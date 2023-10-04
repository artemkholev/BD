import util.Request;

import java.io.IOException;
import java.util.Scanner;

public class Database {
    public static void main(String[] args) throws IOException {
        Request request = new Request();

        Scanner in = new Scanner(System.in);
        String inputData;

        System.out.println("BD strated");
        inputData = in.nextLine();
        while (!inputData.equals("end")) {
            request.requestInfo = inputData.split(" ");
            request.action();
            request.whatDo();
            request.changeBd = false;
            inputData = in.nextLine();
        }
        System.out.println("Program was closed.");
        in.close();
    }
}