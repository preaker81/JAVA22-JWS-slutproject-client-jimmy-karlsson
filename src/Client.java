import java.io.*;
import java.net.Socket;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {

        Scanner scanner = null;

        try {
            Socket clientSocket = new Socket("localhost", 10000);

            InputStreamReader inputStreamReader = new InputStreamReader(clientSocket.getInputStream());
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(clientSocket.getOutputStream());
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);

            scanner = new Scanner(System.in);

            while (true) {

                System.out.println("What do you want to do?");
                System.out.println("1: Get all books. (GET)");
                System.out.println("2: Get a specific book. (GET)");
                System.out.println("3: Add a book to the list. (POST)");
                System.out.println("4: Quit.");

                int selectionInput = scanner.nextInt();
                scanner.nextLine();

                if (selectionInput == 4) {
                    break;
                }

                switch (selectionInput) {
                    case 1 -> {
                        String getRequest = "GET / HTTP/1.1\r\n";
                        getRequest += "Connection: keep-alive\r\n";
                        getRequest += "\r\n";
                        bufferedWriter.write(getRequest);
                        bufferedWriter.flush();
                    }
                    case 2 -> {
                        System.out.println("What book do you want?");
                        String bookQuery = scanner.nextLine();

                        String encodedBookQuery = encodeValue(bookQuery);

                        String getRequest = "GET /book?title=" + encodedBookQuery + " HTTP/1.1\r\n";
                        getRequest += "Connection: keep-alive\r\n";
                        getRequest += "\r\n";
                        bufferedWriter.write(getRequest);
                        bufferedWriter.flush();
                    }
                    case 3 -> {
                        System.out.println("String - Enter the title:");
                        String title = scanner.nextLine();
                        System.out.println("String - Enter the series. (n/a if not available):");
                        String seriesName = scanner.nextLine();
                        System.out.println("int - Enter the order of serie. (0 if not available):");
                        int seriesID = scanner.nextInt();
                        scanner.nextLine();
                        System.out.println("String - Enter release date. (xxxx-xx-xx):");
                        String release = scanner.nextLine();
                        System.out.println("String - Enter the category:");
                        String category = scanner.nextLine();
                        System.out.println("String - Enter the Author:");
                        String author = scanner.nextLine();
                        System.out.println("int - Pages paperback:");
                        int pagesPB = scanner.nextInt();
                        scanner.nextLine();
                        System.out.println("int - Pages hardback:");
                        int pagesHB = scanner.nextInt();
                        scanner.nextLine();
                        System.out.println("Word count:");
                        int words = scanner.nextInt();
                        scanner.nextLine();
                        System.out.println("Audiobook length (String xxhxxm, n/a if not available):");
                        String audio = scanner.nextLine();

                        String jsonInput = String.format("{\"title\": \"%s\", \"seriesName\": \"%s\", \"seriesID\": \"%d\", \"release\": \"%s\", \"category\": \"%s\", \"author\": \"%s\", \"pagesPB\": \"%d\", \"pagesHB\": \"%d\", \"words\": \"%d\", \"audio\": \"%s\",}", title, seriesName, seriesID, release, category, author, pagesPB, pagesHB, words, audio);

                        String postRequest = "POST / HTTP/1.1\r\n";
                        postRequest += "Content-Type: application/json\r\n";
                        postRequest += "Content-Length: " + jsonInput.length() + "\r\n";
                        postRequest += "Connection: keep-alive\r\n";
                        postRequest += "\r\n";
                        postRequest += jsonInput;
                        bufferedWriter.write(postRequest);
                        bufferedWriter.flush();
                    }
                    default -> System.out.println("Invalid input, try again.");
                }


                String line;
                StringBuilder response = new StringBuilder();

                int contentLength = -1;

                while ((line = bufferedReader.readLine()) != null) {
                    if (line.startsWith("Content-Length:")) {
                        contentLength = Integer.parseInt(line.substring("Content-Length:".length()).trim());
                    }

                    if (line.isEmpty()) {
                        break;
                    }
                }

                if (contentLength > 0) {
                    char[] contentBuffer = new char[contentLength];
                    bufferedReader.read(contentBuffer);
                    response.append(new String(contentBuffer));
                }
                System.out.println(response);

                response.setLength(0);
            }

            closeAll(clientSocket, inputStreamReader, outputStreamWriter, bufferedReader, bufferedWriter);

        } catch (Exception e) {
            System.out.println(e);
        } finally {
            if (scanner != null) {
                scanner.close();
            }
        }
    }

    private static String encodeValue(String value) {
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex.getCause());
        }
    }

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
}
