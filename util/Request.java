package util;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.io.File;
import java.io.FileInputStream;
import java.util.concurrent.ThreadLocalRandom;

public class Request {
    private final ArrayList<String> folders = new ArrayList<>(Arrays.asList("Tables", "TablesResult"));
    private String folderBD = "";
    private String bdPath = "";
    private String nameDB = "";
    private String nameColBD = "";
    public String[] requestInfo;
    public boolean changeBd = false;
    public boolean testing = false;

    //helps methods

    private  void showFolders() {
        System.out.println("Select a folder:");
        for (String key : this.folders) {
            System.out.println(key);
        }
    }
    private int showBD() {
        int count = 0;
        System.out.println("You can open bd:");
        File dir = new File(this.folderBD);
        File[] arrFiles = dir.listFiles();
        List<File> lst = Arrays.asList(arrFiles);
        for (File str : lst) {
            count++;
            System.out.println(str.toString().substring(str.toString().indexOf('/') + 1, str.toString().indexOf('.')));
        }
        return count;
    }
    private int getId() {
        String filePath = this.bdPath;
        int count = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            while ((reader.readLine()) != null) {
                count++;
            }
        } catch (IOException e) {
            System.err.println("Error in reading file: " + e.getMessage());
        }
        return count;
    }

    private int fileLine(String str) {
        final File file = new File(this.bdPath);
         try {
             final LineNumberReader lnr = new LineNumberReader(new FileReader(file));
             int linesCount = 0;
             String readString = lnr.readLine();
             while (null != readString) {
                 if (readString.contains(str))
                     linesCount++;
                 readString = lnr.readLine();
             }
             return linesCount;
         } catch (IOException e) {
             throw new RuntimeException(e);
         }
    }

    private String firstLine() {
        final File file = new File(this.bdPath);
        try {
            final LineNumberReader lnr = new LineNumberReader(new FileReader(file));
            return lnr.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void Error() {
        System.out.println("You entered the wrong command");
    }
    public void action() throws IOException {
        if (this.requestInfo.length == 2 && this.requestInfo[0].equals("open") && this.requestInfo[1].equals("folder")) {
            showFolders();
            Scanner inNew = new Scanner(System.in);
            this.folderBD = inNew.nextLine();
            System.out.println("You open folder " + this.folderBD);
            return;
        } else if (this.requestInfo[0].equals("open")) {
            this.nameDB = "";
            this.bdPath = "";
            int countBd = showBD();

            if (countBd > 0) {
                Scanner inNew = new Scanner(System.in);
                String openBdName = inNew.next();
                this.nameDB = openBdName;
                this.changeBd = false;
                this.bdPath = this.folderBD + "/" + openBdName + ".txt";
                this.nameColBD = firstLine().replace(',', ' ');
                System.out.println("BD open, you can use function");
                return;
            }
            System.out.println("Table not open, DB not found!!!");
            return;
        } else if (this.requestInfo[0].equals("create")) {
            Scanner inNew = new Scanner(System.in);
            System.out.println("Enter name BD");
            this.nameDB = inNew.nextLine();
            Path dir = Files.createDirectories(Paths.get(this.folderBD));
            Files.newOutputStream(dir.resolve(this.nameDB + ".txt"));
            this.bdPath = this.folderBD + "/" + this.nameDB + ".txt";
            this.changeBd = false;

            System.out.println("Need name table col");
            String col = "id," + String.join(",", inNew.nextLine().split(" "));
            this.nameColBD = col.replace(',', ' ');
            try(FileWriter writer = new FileWriter(this.bdPath, false)) {
                writer.write(col + '\n');
                System.out.println("Col was added in BD " + this.nameDB);
                writer.flush();
            }
            catch(IOException ex){
                System.out.println(ex.getMessage());
            }
            System.out.println("BD was created");
            cls();
            return;
        } else if (this.requestInfo.length == 2 && this.requestInfo[0].equals("create") && this.requestInfo[1].equals("folder")) {
            Scanner inNew = new Scanner(System.in);
            System.out.println("Enter name folder");
            String newFolder = inNew.nextLine();
            this.folders.add(newFolder);
            this.folderBD = newFolder;
        }
        this.changeBd = true;
    }

    public void whatDo() throws IOException {
        if (!this.changeBd) return;
        switch (this.requestInfo[0]) {
            case "add" -> add();
            case "delete" -> delete();
            case "edit" -> edit();
            case "show" -> show();
            case "copy" -> copy();
            case "gTesting" -> generateTesting();
            case "gTesting+" -> generateTestingForTeacher();
            case "addMark" -> addMark();
            default -> {
                System.out.println("Not find BD or");
                Error();
            }
        }
    }

    private void add() {
        this.requestInfo[0] = "";
        String addString = String.join(",", this.requestInfo);
        if (fileLine(addString) != 0) {
            System.out.println("Information there is");
            return;
        }
        int id = getId();
        try(FileWriter writer = new FileWriter(this.bdPath, true)) {
            this.requestInfo[0] = String.valueOf(id);
            writer.write(id + addString + "\n");
            System.out.println("Data was added in table " + this.nameDB);
            writer.flush();

            if (this.testing) {
                List<String> result = Files.readAllLines(Paths.get("Tables/Variants.txt"));
                result.remove(0);
                String idVariant = result.get(ThreadLocalRandom.current().nextInt(0, result.size()-1));
                String filePath = "TablesResult/testing_table.txt";
                String text = id + "," + idVariant.substring(0, idVariant.indexOf(",")) + '\n';

                try {
                    FileWriter writ = new FileWriter(filePath, true);
                    BufferedWriter bufferWriter = new BufferedWriter(writ);
                    bufferWriter.write(text);
                    bufferWriter.close();
                }
                catch (IOException e) {
                    System.out.println(e);
                }


                String filePathForTeacher = "TablesResult/testing_table_for_teacher.txt";
                String textForTeacher = addString.substring(1) + "," + idVariant.substring(idVariant.indexOf(",") + 1) + ",\n";

                try {
                    FileWriter writ = new FileWriter(filePathForTeacher, true);
                    BufferedWriter bufferWriter = new BufferedWriter(writ);
                    bufferWriter.write(textForTeacher);
                    bufferWriter.close();
                }
                catch (IOException e) {
                    System.out.println(e);
                }
            }
        }
        catch(IOException ex){
            System.out.println(ex.getMessage());
        }
    }

    private void delete() {
        this.requestInfo[0] = "";
        String infoForDelete;
        if (this.requestInfo.length == 2) {
            infoForDelete = this.requestInfo[1] + ",";
        } else {
            infoForDelete = String.join(",", this.requestInfo);
        }


        Scanner newIn = new Scanner(System.in);
        try {
            String charset = "UTF-8";
            File file = new File(this.bdPath);
            File temp = File.createTempFile(this.nameDB, ".txt", file.getParentFile());
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), charset));
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(temp), charset));
            for (String line; (line = reader.readLine()) != null;) {
                if (line.contains(infoForDelete)) {
                    System.out.println("Found data: " + line.replace(",", " "));
                    if (newIn.next().equals("yes")) {
                        String[] data = line.split(",");

                        File file1 = new File("TablesResult/testing_table.txt");
                        File file2 = new File("TablesResult/testing_table_for_teacher.txt");

                        StringBuilder sb = new StringBuilder();
                        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file1)))) {
                            String strLine;
                            while ((strLine = br.readLine()) != null) {
                                String[] line_in_id = strLine.split(",");
                                if (line_in_id[0].equals(data[0])) {
                                    continue;
                                }
                                sb.append(strLine).append("\n");
                            }
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                        try (FileWriter fileWriter = new FileWriter(file1)) {
                            fileWriter.write(sb.toString());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                        StringBuilder sb_teacher = new StringBuilder();
                        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file2)))) {
                            String strLine;
                            while ((strLine = br.readLine()) != null) {
                                String[] line_in_id = strLine.split(",");
                                if (line_in_id[0].equals(data[1] + " " + data[2])) {
                                    continue;
                                }
                                sb_teacher.append(strLine).append("\n");
                            }
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }


                        try (FileWriter fileWriter = new FileWriter(file2)) {
                            fileWriter.write(sb_teacher.toString());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                        System.out.println("Was deleted");
                        continue;
                    }
                }
                writer.println(line);
            }
            reader.close();
            writer.close();
            file.delete();
            temp.renameTo(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void edit() {
        String infoForEdit;
        this.requestInfo[0] = "";
        if (this.requestInfo.length == 2) {
            infoForEdit = this.requestInfo[1] + ",";
        } else {
            infoForEdit = String.join(",", this.requestInfo);
        }

        Scanner newIn = new Scanner(System.in);
        try {
            String charset = "UTF-8";
            File file = new File(this.bdPath);
            File temp = File.createTempFile(this.nameDB, ".txt", file.getParentFile());
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), charset));
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(temp), charset));
            for (String line; (line = reader.readLine()) != null;) {
                if (line.contains(infoForEdit)) {
                    System.out.println("Found data: " + line.replace(",", " "));
                    if (newIn.nextLine().equals("yes")) {
                        String[] data = line.split(",");
                        String rep = line.substring(line.indexOf(',') + 1);
                        String newLine;
                        while (true) {
                            newLine = newIn.nextLine().replace(' ', ',');
                            if (fileLine(newLine) == 0) {
                                break;
                            }
                            System.out.println("Information there is");
                        }
                        line = line.replace(rep, newLine);
                        writer.println(line);


                        File file2 = new File("TablesResult/testing_table_for_teacher.txt");
                        StringBuilder sb = new StringBuilder();
                        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file2)))) {
                            String strLine;
                            while ((strLine = br.readLine()) != null) {
                                if (strLine.contains(data[1] + " " + data[2])) {
                                    sb.append(strLine.replace(data[1] + " " + data[2], newLine)).append("\n");
                                }
                                sb.append(strLine).append("\n");
                            }
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                        try (FileWriter fileWriter = new FileWriter(file2)) {
                            fileWriter.write(sb.toString());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                        System.out.println("Was edited");
                        continue;
                    }
                }
                writer.println(line);
            }
            reader.close();
            writer.close();
            file.delete();
            temp.renameTo(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void show() {
        int start = 0;
        int count = Integer.MAX_VALUE;
        if (this.requestInfo.length == 2) {
            count = Integer.parseInt(this.requestInfo[1]);
        } else if (this.requestInfo.length == 3) {
            count = Integer.parseInt(this.requestInfo[1]);
            start = Integer.parseInt(this.requestInfo[2]);
        }
        final File file = new File(this.bdPath);
        Scanner newIn = new Scanner(System.in);

        try {
            final LineNumberReader lnr = new LineNumberReader(new FileReader(file));
            while (lnr.getLineNumber() != start) {
                lnr.readLine();
            }
            String str = lnr.readLine();
            while (str != null && Objects.equals(newIn.next(), "next")) {
                for (int i = 0; i < count && str != null; i++) {
                    System.out.println(str.replace(',', ' '));
                    str = lnr.readLine();
                }
            }
            System.out.println("Not data. It is all");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void copy() {
        try(FileWriter writer = new FileWriter(this.bdPath, true)) {
            String charset = "UTF-8";
            File file = new File("copyData/" + this.requestInfo[1] + ".txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), charset));
            int id = getId();
            for (String line; (line = reader.readLine()) != null;) {
                if (fileLine("," + line.replace(' ', ',')) != 0) {
                    continue;
                }
                line = id++ + "," + line.replace(' ', ',') + '\n';
                writer.write(line);
            }
            System.out.println("Data was added in table " + this.nameDB);
            writer.flush();
            reader.close();
        }
        catch(IOException ex){
            System.out.println(ex.getMessage());
        }
    }

    private void generateTesting() throws IOException {
        String charset = "UTF-8";
        String path = "TablesResult",
               nameFile = "testing_table.txt",
               col = "student_id,variant_id\n";
        Path dir = Files.createDirectories(Paths.get(path));
        Files.newOutputStream(dir.resolve(nameFile));
        try(FileWriter writer = new FileWriter(path + "/" + nameFile, false)) {
            writer.write(col);
            File fileOne = new File("Tables/Students.txt");
            List<String> result = Files.readAllLines(Paths.get("Tables/Variants.txt"));
            result.remove(0);
            BufferedReader readerOne = new BufferedReader(new InputStreamReader(new FileInputStream(fileOne), charset));
            readerOne.readLine();
            for (String line; (line = readerOne.readLine()) != null;) {
                line = line.substring(0, line.indexOf(',')) + "," + result.get(ThreadLocalRandom.current().nextInt(0, result.size()-1)).substring(0, line.indexOf(',')) + '\n';
                writer.write(line);
            }
            writer.flush();
            System.out.println("testing_table was created");
        }
        catch(IOException ex){
            System.out.println(ex.getMessage());
        }
        generateTestingForTeacher();
    }

    private void generateTestingForTeacher() throws IOException {
        String testing_table_path = "TablesResult/testing_table.txt",
               bdStudents = "Tables/Students.txt",
               bdVariants = "Tables/Variants.txt";

        String charset = "UTF-8";
        String path = "TablesResult",
                nameFile = "testing_table_for_teacher.txt",
                col = "full_name,path_to_file,mark\n";

        Path dir = Files.createDirectories(Paths.get(path));
        Files.newOutputStream(dir.resolve(nameFile));
        try(FileWriter writer = new FileWriter(path + "/" + nameFile, false)) {
            writer.write(col);
            File testing_table = new File(testing_table_path);
            List<String> resultUsers = Files.readAllLines(Paths.get(bdStudents));
            List<String> resultVariants = Files.readAllLines(Paths.get(bdVariants));
            resultUsers.remove(0); resultVariants.remove(0);
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(testing_table), charset));
            reader.readLine();

            for (String line; (line = reader.readLine()) != null;) {
                String idUser = line.substring(0, line.indexOf(',') + 1);
                String idVariant = line.substring(line.indexOf(',') + 1) + ",";
                String newString = "";

                for (String strTables : resultUsers) {
                    if (strTables.contains(idUser)) {
                        newString = strTables.substring(strTables.indexOf(',') + 1).replace(',', ' ')  + ",";
                        break;
                    }
                }
                for (String strTables : resultVariants) {
                    if (strTables.contains(idVariant)) {
                        newString += strTables.substring(strTables.indexOf(',') + 1).replace(',', ' ') + ",\n";
                        break;
                    }
                }
                writer.write(newString);
            }
            writer.flush();
            System.out.println("testing_table_for_teacher was created");
        }
        catch(IOException ex){
            System.out.println(ex.getMessage());
        }
    }

    private void addMark() {
        System.out.println("Enter full name and mark using ,");
        Scanner inNew = new Scanner(System.in);
        String[] info = inNew.nextLine().split(",");
        String fullName = info[0];
        String mark  = info[1];
        File file = new File(this.bdPath);

        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
            String strLine;
            while ((strLine = br.readLine()) != null) {
                String[] line = strLine.split(",");
                if (line[0].equals(fullName)) {
                    sb.append(line[0]).append(",").append(line[1]).append(",").append(mark).append("\n");
                    continue;
                }
                sb.append(strLine).append("\n");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try (FileWriter fileWriter = new FileWriter(file)) {
            fileWriter.write(sb.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public int fileLen() {
        final File file = new File("TablesResult/testing_table.txt");
        try {
            final LineNumberReader lnr = new LineNumberReader(new FileReader(file));
            int linesCount = 0;
            while (lnr.readLine() != null) {
                linesCount++;
            }
            return linesCount;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void cls() {
        System.out.println(this.nameColBD);
    }
}
