import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        // Declare and initialize socket, input/output streams, and buffered readers/writers
        Socket socket = null;
        InputStreamReader inputStreamReader = null;
        OutputStreamWriter outputStreamWriter = null;
        BufferedReader bufferedReader = null;
        BufferedWriter bufferedWriter = null;

        try {
            // Create a new socket with the server's address and port number
            socket = new Socket("localhost", 4321);
            // Initialize input and output streams to read and write data from/to the server
            inputStreamReader = new InputStreamReader(socket.getInputStream());
            outputStreamWriter = new OutputStreamWriter(socket.getOutputStream());

            // Create buffered readers/writers to read and write data from/to the server
            bufferedReader = new BufferedReader(inputStreamReader);
            bufferedWriter = new BufferedWriter(outputStreamWriter);

            // Create a scanner object to read input from the user
            Scanner scanner = new Scanner(System.in);

            // Loop until the user enters "quit"
            while (true) {
                // Read input from the user and send it to the server
                String message = scanner.nextLine();
                bufferedWriter.write(message);
                bufferedWriter.newLine();
                bufferedWriter.flush();

                // Read the server's response and print it to the console
                System.out.println(bufferedReader.readLine());

                // If the user enters "quit", exit the loop
                if (message.equalsIgnoreCase("quit")) {
                    break;
                }
            }
        } catch (Exception e) {
            // Print any exceptions that occur
            System.out.println(e);
        } finally {
            // Close all open resources
            try {
                if (socket != null) {
                    socket.close();
                }
                if (inputStreamReader != null) {
                    inputStreamReader.close();
                }
                if (outputStreamWriter != null) {
                    outputStreamWriter.close();
                }
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
                if (bufferedWriter != null) {
                    bufferedWriter.close();
                }
            }
            catch (Exception e){
                // Print any exceptions that occur while closing resources
                e.printStackTrace();
            }
        }
    }
}
