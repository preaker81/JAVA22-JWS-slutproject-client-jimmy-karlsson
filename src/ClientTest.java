import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.assertEquals;

// Test class for the Client
public class ClientTest {
    // Initialize variables for output and input stream manipulation
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final ByteArrayInputStream inContent;

    // Constructor for the test class
    public ClientTest() {
        // Define the input to be used for the tests
        String input = "1\n4\n";
        inContent = new ByteArrayInputStream(input.getBytes());
    }

    // Set up the streams before each test
    @BeforeEach
    public void setUpStreams() {
        // Redirect the output and input streams
        System.setOut(new PrintStream(outContent));
        System.setIn(inContent);
    }

    // Restore the streams after each test
    @AfterEach
    public void restoreStreams() {
        // Restore the original output and input streams
        System.setOut(originalOut);
        System.setIn(System.in);
    }

    // Test the GET all books request
    @Test
    public void testGetAllBooks() throws IOException {
        // Create a socket and connect to the server
        Socket socket = new Socket("localhost", 10000);

        // Initialize input and output streams
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(socket.getOutputStream());
        BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
        InputStreamReader inputStreamReader = new InputStreamReader(socket.getInputStream());
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        // Create the GET request
        String getRequest = "GET / HTTP/1.1\r\n";
        getRequest += "Connection: close\r\n";
        getRequest += "\r\n";
        bufferedWriter.write(getRequest);
        bufferedWriter.flush();

        // Read the server response
        String line;
        String response = null;

        while ((line = bufferedReader.readLine()) != null) {
            if (line.startsWith("HTTP/1.1")) {
                response = line.substring("HTTP/1.1".length()).trim();
            }

            if (line.isEmpty()) {
                break;
            }
        }

        // Close the socket
        socket.close();

        // Assert that the server response is as expected
        assertEquals("200 OK", response, "Server response should be '200 OK'");
    }

    // Test the GET specific book request
    @Test
    public void testGetSpecificBook() throws IOException {
        // Create a socket and connect to the server
        Socket socket = new Socket("localhost", 10000);

        // Initialize input and output streams
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(socket.getOutputStream());
        BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
        InputStreamReader inputStreamReader = new InputStreamReader(socket.getInputStream());
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        // Create the GET request for a specific book using the HTTP/1.1 protocol
        String getRequest = "GET /book?title=test HTTP/1.1\r\n";
        getRequest += "Connection: close\r\n";
        getRequest += "\r\n";
        bufferedWriter.write(getRequest);
        bufferedWriter.flush();

        // Read the server response line by line and store the status line in the 'response' variable
        String line;
        String response = null;

        while ((line = bufferedReader.readLine()) != null) {
            if (line.startsWith("HTTP/1.1")) {
                response = line.substring("HTTP/1.1".length()).trim();
            }

            if (line.isEmpty()) {
                break;
            }
        }

        socket.close();

        // Check if the server response is '200 OK'
        assertEquals("200 OK", response, "Server response should be '200 OK'");

    }

    @Test
    public void testPostRequest() throws IOException {
        // Create a socket and connect to the server
        Socket socket = new Socket("localhost", 10000);

        // Initialize input and output streams for communication with the server
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(socket.getOutputStream());
        BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
        InputStreamReader inputStreamReader = new InputStreamReader(socket.getInputStream());
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        // Define the JSON input for the POST request
        String jsonInput = "{\"title\":\"JUnit Test Book\",\"category\":\"Testing\",\"author\":\"John Doe\",\"words\":123}";

        // Create the POST request using the HTTP/1.1 protocol, including headers for content type, content length, and connection
        String postRequest = "POST / HTTP/1.1\r\n";
        postRequest += "Content-Type: application/json\r\n";
        postRequest += "Content-Length: " + jsonInput.length() + "\r\n";
        postRequest += "Connection: close\r\n";
        postRequest += "\r\n";
        postRequest += jsonInput;
        bufferedWriter.write(postRequest);
        bufferedWriter.flush();

        // Read the server response line by line and store the status line in the 'response' variable
        String line;
        String response = null;

        while ((line = bufferedReader.readLine()) != null) {
            if (line.startsWith("HTTP/1.1")) {
                response = line.substring("HTTP/1.1".length()).trim();
            }

            if (line.isEmpty()) {
                break;
            }
        }

        socket.close();

        // Check if the server response is '201 Created'
        assertEquals("201 Created", response, "Server response should be '201 Created'");
    }
}
