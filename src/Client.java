import org.json.simple.JSONObject;
import java.io.*;
import java.net.Socket;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {

        // Initialize the scanner for user input
        Scanner scanner = null;

        try {
            // Connect to the server using a socket with localhost and port 10000
            Socket socket = new Socket("localhost", 10000);

            // Set up input and output streams for the socket
            InputStreamReader inputStreamReader = new InputStreamReader(socket.getInputStream());
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(socket.getOutputStream());
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);

            // Initialize the scanner for user input
            scanner = new Scanner(System.in);

            // Main loop for user interaction
            while (true) {

                // Display the menu for the user
                System.out.println("""
                        ==================================
                        What do you want to do?
                        1: Get all books. (GET)
                        2: Get a specific book. (GET)
                        3: Add a book to the list. (POST)
                        4: Quit. (Closing client)
                        ==================================
                        """);

                // Get the user's choice
                System.out.print("INPUT: ");
                int selectionInput = scanner.nextInt();
                scanner.nextLine();

                // Exit the loop if the user chooses to quit
                if (selectionInput == 4) {
                    break;
                }

                // Process user's choice
                switch (selectionInput) {
                    // Get all books
                    case 1 -> {
                        String getRequest = "GET / HTTP/1.1\r\n";
                        getRequest += "Connection: keep-alive\r\n";
                        getRequest += "\r\n";
                        bufferedWriter.write(getRequest);
                        bufferedWriter.flush();
                    }
                    case 2 -> {
                        // Get a specific book
                        System.out.println("What book do you want?");
                        String bookQuery = scanner.nextLine();

                        // Encode the book title for use in a URL
                        String encodedBookQuery = encodeValue(bookQuery);

                        String getRequest = "GET /book?title=" + encodedBookQuery + " HTTP/1.1\r\n";
                        getRequest += "Connection: keep-alive\r\n";
                        getRequest += "\r\n";
                        bufferedWriter.write(getRequest);
                        bufferedWriter.flush();
                    }
                    // Add a book to the list
                    case 3 -> {
                        String jsonInput = getJsonInput();

                        String postRequest = "POST / HTTP/1.1\r\n";
                        postRequest += "Content-Type: application/json\r\n";
                        postRequest += "Content-Length: " + jsonInput.length() + "\r\n";
                        postRequest += "Connection: keep-alive\r\n";
                        postRequest += "\r\n";
                        postRequest += jsonInput;
                        bufferedWriter.write(postRequest);
                        bufferedWriter.flush();
                    }
                    // Invalid input
                    default -> System.out.println("Invalid input, try again.");
                }

                // Handle the server response
                String line;
                StringBuilder response = new StringBuilder();

                int contentLength = -1;

                while ((line = bufferedReader.readLine()) != null) {
                    String serverResponse;

                    if (line.startsWith("HTTP/1.1 201")) {
                        serverResponse = line.substring("HTTP/1.1".length()).trim();
                        System.out.println("");
                        System.out.println("RESPONSE: " + serverResponse);
                        System.out.println("");
                    }

                    if (line.startsWith("Content-Length:")) {
                        contentLength = Integer.parseInt(line.substring("Content-Length:".length()).trim());
                    }

                    if (line.isEmpty()) {
                        break;
                    }
                }

                // Read and display the response content if the content length is greater than 0
                if (contentLength > 0) {
                    char[] contentBuffer = new char[contentLength];
                    bufferedReader.read(contentBuffer);
                    response.append(new String(contentBuffer));
                }

                System.out.println("");
                System.out.println("RESPONSE: " + response);
                System.out.println("");

                // Reset the response string builder for the next iteration
                response.setLength(0);
            }

            // Close all resources
            closeAll(socket, inputStreamReader, outputStreamWriter, bufferedReader, bufferedWriter);

        } catch (Exception e) {
            System.out.println(e);
        } finally {
            if (scanner != null) {
                scanner.close();
            }
        }
    }

    // Function - Encode the given value using URL encoding with UTF-8
    private static String encodeValue(String value) {
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex.getCause());
        }
    }

    // Function - Close all resources
    private static void closeAll(Socket socket, InputStreamReader inputStreamReader, OutputStreamWriter outputStreamWriter, BufferedReader bufferedReader, BufferedWriter bufferedWriter) throws IOException {
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

    // Function - Get JSON input for the book information
    public static String getJsonInput() {
        Scanner scanner = new Scanner(System.in);
        String title = getValidStringInput(scanner, "String - Enter the title:");
        String category = getValidStringInput(scanner, "String - Enter the category:");
        String author = getValidStringInput(scanner, "String - Enter the Author:");
        int words = getValidIntInput(scanner, "Word count:");

        // Create a JSON object with the book information
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("title", title);
        jsonObject.put("category", category);
        jsonObject.put("author", author);
        jsonObject.put("words", words);

        // Return the JSON object as a string
        return jsonObject.toString();
    }

    // Function - Get valid string input from the user
    private static String getValidStringInput(Scanner scanner, String prompt) {
        String input;
        System.out.println(prompt);
        do {
            input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                System.out.println("Please enter a non-empty value:");
            }
        } while (input.isEmpty());
        return input;
    }

    // Function - Get valid integer input from the user
    private static int getValidIntInput(Scanner scanner, String prompt) {
        int input = 0;
        boolean isValid = false;
        System.out.println(prompt);
        while (!isValid) {
            try {
                input = scanner.nextInt();
                isValid = true;
            } catch (InputMismatchException e) {
                System.out.println("Please enter a valid integer:");
            } finally {
                scanner.nextLine();
            }
        }
        return input;
    }
}
