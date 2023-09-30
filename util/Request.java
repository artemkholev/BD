package util;

import java.awt.event.KeyEvent;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.io.File;
import java.io.FileInputStream;

import static java.lang.Integer.parseInt;

public class Request {
    private String bdPath;
    private String nameDB;
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
        if (this.requestInfo.length == 1 && this.nameDB.isEmpty()) {
            Error();
            return;
        }
        if (this.requestInfo[0].equals("open")) {
            this.nameDB = "";
            this.bdPath = "";
            File dir = new File("Tabels");
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
            Path dir = Files.createDirectories(Paths.get("Tabels"));
            OutputStream out = Files.newOutputStream(dir.resolve(this.requestInfo[1] + ".txt"));
            this.bdPath = "Tabels/" + this.requestInfo[1] + ".txt";
            this.nameDB = this.requestInfo[1];
            Scanner newIn = new Scanner(System.in);
            System.out.println("You need name table col");
            String col = "id," + String.join(",", newIn.nextLine().split(" "));
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

    public void whatDo() {
        if (!this.changeBd) return;
        if (this.bdPath.isEmpty()) {
            System.out.println("Do not have this BD");
            return;
        }
        if (this.requestInfo[0].equals("add")) {
            add();
        } else if (this.requestInfo[0].equals("delete")) {
            delete();
        } else if (this.requestInfo[0].equals("edit")) {
            edit();
        } else if (this.requestInfo[0].equals("show")) {
            show();
        } else if (this.requestInfo[0].equals("copy")) {

        } else {
            System.out.println("Not find BD");
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
        try {
            File inFile = new File(this.bdPath);

            if (!inFile.isFile()) {
                System.out.println("Parameter is not an existing file");
                return;
            }

            //Construct the new file that will later be renamed to the original filename.
            File tempFile = new File(inFile.getAbsolutePath() + ".tmp");

            BufferedReader br = new BufferedReader(new FileReader(file));
            PrintWriter pw = new PrintWriter(new FileWriter(tempFile));

            String line = null;

            //Read from the original file and write to the new
            //unless content matches data to be removed.
            while ((line = br.readLine()) != null) {

                if (!line.trim().equals(lineToRemove)) {

                    pw.println(line);
                    pw.flush();
                }
            }
            pw.close();
            br.close();

            //Delete the original file
            if (!inFile.delete()) {
                System.out.println("Could not delete file");
                return;
            }

            //Rename the new file to the filename the original file had.
            if (!tempFile.renameTo(inFile))
                System.out.println("Could not rename file");

        }
        catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void edit() {

    }

    private void show() {
        final File file = new File(this.bdPath);
        try {
            final LineNumberReader lnr = new LineNumberReader(new FileReader(file));
            System.out.println();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void copy(String path) {

    }

    private void cls() {
        System.out.println(this.nameColBD);
    }
}
