package util;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.io.File;
import java.io.FileInputStream;
import java.util.concurrent.ThreadLocalRandom;

import static java.lang.Integer.parseInt;

public class Request {
    private String bdPath = "";
    private String nameDB = "";
    private String nameColBD = "";
    public String[] requestInfo;
    public boolean changeBd = false;

    //helps methods
    private int getId() {
        String filePath = this.bdPath;
        int count = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                count++;
            }
        } catch (IOException e) {
            System.err.println("Ошибка при чтении файла: " + e.getMessage());
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
         } catch (FileNotFoundException e) {
             throw new RuntimeException(e);
         } catch (IOException e) {
             throw new RuntimeException(e);
         }
    }

    private String firstLine() {
        final File file = new File(this.bdPath);
        try {
            final LineNumberReader lnr = new LineNumberReader(new FileReader(file));
            return lnr.readLine();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void Error() {
        System.out.println("You entered the wrong command");
    }
    public void action() throws IOException {
        if (this.requestInfo[0].equals("open")) {
            this.nameDB = "";
            this.bdPath = "";
            File dir = new File("Tables");
            File[] arrFiles = dir.listFiles();
            List<File> lst = Arrays.asList(arrFiles);
            for (int i = 0; i < lst.size(); i++) {
                if (lst.get(i).toString().contains(this.requestInfo[1] + ".")) {
                    this.nameDB = this.requestInfo[1];
                    System.out.println("Table open, you can use function DB");
                    this.changeBd = false;
                    this.bdPath = lst.get(i).toString();
                    this.nameColBD = firstLine().replace(',', ' ');
                    cls();
                    return;
                }
            }
            System.out.println("Table not open, you cannot use function DB, DB not found");
            return;
        } else if (this.requestInfo[0].equals("create")) {
            String path = "Tables";
            Path dir = Files.createDirectories(Paths.get(path));
            OutputStream out = Files.newOutputStream(dir.resolve(this.requestInfo[1] + ".txt"));
            this.bdPath = path + "/" + this.requestInfo[1] + ".txt";
            this.nameDB = this.requestInfo[1];
            Scanner newIn = new Scanner(System.in);
            System.out.println("You need name table col");
            String col = this.requestInfo.length == 3 ? "" : "id," + String.join(",", newIn.nextLine().split(" "));
            try(FileWriter writer = new FileWriter(this.bdPath, false)) {
                writer.write(String.join(",", col) + '\n');
                System.out.println("Col was added in BD " + this.nameDB);
                writer.flush();
            }
            catch(IOException ex){
                System.out.println(ex.getMessage());
            }
            System.out.println("BD was created");
            this.nameColBD = firstLine().replace(',', ' ');
            cls();
            return;
        }
        this.changeBd = true;
    }

    public void whatDo() throws IOException {
        if (!this.changeBd) return;
        if (this.requestInfo[0].equals("add")) {
            add();
        } else if (this.requestInfo[0].equals("delete")) {
            delete();
        } else if (this.requestInfo[0].equals("edit")) {
            edit();
        } else if (this.requestInfo[0].equals("show")) {
            show();
        } else if (this.requestInfo[0].equals("copy")) {
            copy();
        } else if (this.requestInfo[0].equals("gTesting")) {
            generateTesting();
        } else {
            System.out.println("Not find BD or");
            Error();
        }
    }

    private void add() {
        this.requestInfo[0] = "";
        String addString = String.join(",", this.requestInfo);
        if (fileLine(addString) != 0) {
            System.out.println("Inforamtion there is");
            return;
        }
        addString = String.valueOf(getId()) + addString + '\n';
        try(FileWriter writer = new FileWriter(this.bdPath, true)) {
            this.requestInfo[0] = String.valueOf(getId());
            writer.write(addString);
            System.out.println("Data was added in table " + this.nameDB);
            writer.flush();
        }
        catch(IOException ex){
            System.out.println(ex.getMessage());
        }
    }

    private void delete() {
        this.requestInfo[0] = "";
        String infoForDelete = null;
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
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void edit() {
        this.requestInfo[0] = "";
        String infoForEdit = null;
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
                        String rep = line.substring(line.indexOf(',') + 1);
                        String newLine = null;
                        while (true) {
                            newLine = newIn.nextLine().replace(' ', ',');
                            if (fileLine(newLine) == 0) {
                                break;
                            }
                            System.out.println("Inforamtion there is");
                        }
                        line = line.replace(rep, newLine);
                        writer.println(line);
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
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void show() {
        int start = 0;
        Integer count = Integer.MAX_VALUE;
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
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
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
        OutputStream out = Files.newOutputStream(dir.resolve(nameFile));
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
    }

    private void cls() {
        System.out.println(this.nameColBD);
    }
}
