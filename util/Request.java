package util;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.io.File;
import java.io.FileInputStream;

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
    public void action() throws IOException {
        if (this.requestInfo[0].equals("open")) {
            if (requestInfo.length == 1) {
                this.changeBd = true;
                return;
            }
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
                    return;
                }
            }
            System.out.println("Table not open, you cannot use function DB, DB not found");
            return;
        } else if (this.requestInfo[0].equals("create")) {
            Path dir = Files.createDirectories(Paths.get("Tabels"));
            OutputStream out = Files.newOutputStream(dir.resolve(this.requestInfo[0] + ".txt"));
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
        }
        this.changeBd = true;
    }

    public void whatDo() {
        if (!this.changeBd) return;
        if (this.bdPath.isEmpty()) {
            System.out.println("Do not have this BD");
            return;
        }
        cls();
        if (this.requestInfo[0].equals("add")) {
            add();
        } else if (this.requestInfo[0].equals("delete")) {
            delete();
        } else if (this.requestInfo[0].equals("edit")) {
            edit();
        } else if (this.requestInfo[0].equals("show")) {
            show();
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

    }

    private void edit() {

    }

    private void show() {

    }

    private void cls() {
        System.out.println(this.nameColBD);
    }
}
