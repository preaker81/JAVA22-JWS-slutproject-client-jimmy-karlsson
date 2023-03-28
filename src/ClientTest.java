import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.assertEquals;

// Test class for the Client
public class ClientTest {
    // ByteArrayOutputStream and PrintStream for capturing and redirecting console output
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    // ByteArrayInputStream for simulating user input
    private final ByteArrayInputStream inContent;
    private Socket socket;
    private BufferedWriter bufferedWriter;
    private BufferedReader bufferedReader;

    // Constructor for the test class
    public ClientTest() {
        // Prepare the simulated user input
        String input = "1\n4\n";
        inContent = new ByteArrayInputStream(input.getBytes());
    }

    // Set up the streams before each test
    @BeforeEach
    public void setUpStreams() {
        // Redirect the output and input streams to the custom streams
        System.setOut(new PrintStream(outContent));
        System.setIn(inContent);
    }

    // Set up the socket and its associated streams before each test
    @BeforeEach
    public void setUpSocketAndStreams() throws IOException {
        // Create a socket and connect to the server
        socket = new Socket("localhost", 10000);
        // Initialize the BufferedWriter for writing to the socket
        bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        // Initialize the BufferedReader for reading from the socket
        bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    // Restore the streams after each test
    @AfterEach
    public void restoreStreams() {
        // Restore the original output and input streams
        System.setOut(originalOut);
        System.setIn(System.in);
    }

    // Close the socket and its associated streams after each test
    @AfterEach
    public void closeSocketAndStreams() throws IOException {
        // Close the BufferedReader
        if (bufferedReader != null) {
            bufferedReader.close();
        }
        // Close the BufferedWriter
        if (bufferedWriter != null) {
            bufferedWriter.close();
        }
        // Close the socket
        if (socket != null) {
            socket.close();
        }
    }

    // Test the GET all books request
    @Test
    public void testGetAllBooks() throws IOException {
        // Prepare the GET request for all books
        String getRequest = "GET / HTTP/1.1\r\n";
        getRequest += "Connection: close\r\n";
        getRequest += "\r\n";

        // Send the GET request
        bufferedWriter.write(getRequest);
        bufferedWriter.flush();

        String line;
        String response = null;

        // Read the server response and extract the status line
        while ((line = bufferedReader.readLine()) != null) {
            if (line.startsWith("HTTP/1.1")) {
                response = line.substring("HTTP/1.1".length()).trim();
            }

            if (line.isEmpty()) {
                break;
            }
        }

        // Assert that the server response is '200 OK'
        assertEquals("200 OK", response, "Server response should be '200 OK'");
    }

    // Test the GET request to retrieve a specific book by title
    @Test
    public void testGetSpecificBook() throws IOException {
        // Create the GET request for a specific book using the HTTP/1.1 protocol
        String getRequest = "GET /book?title=test HTTP/1.1\r\n";
        getRequest += "Connection: close\r\n";
        getRequest += "\r\n";

        // Send the GET request to the server
        bufferedWriter.write(getRequest);
        bufferedWriter.flush();

        // Read the server response line by line
        // Store the status line in the 'response' variable
        String line;
        String response = null;

        while ((line = bufferedReader.readLine()) != null) {
            // Check if the line starts with "HTTP/1.1"
            // If so, extract the status code and reason phrase
            if (line.startsWith("HTTP/1.1")) {
                response = line.substring("HTTP/1.1".length()).trim();
            }

            // If the line is empty, stop reading the response
            if (line.isEmpty()) {
                break;
            }
        }

        // Check if the server response is '200 OK'
        assertEquals("200 OK", response, "Server response should be '200 OK'");
    }

    // Test the POST request to add a new book
    @Test
    public void testPostRequest() throws IOException {
        // Define the JSON input for the POST request
        String jsonInput = "{\"title\":\"JUnit Test Book\",\"category\":\"Testing\",\"author\":\"John Doe\",\"words\":123}";

        // Create the POST request using the HTTP/1.1 protocol
        // Include headers for content type, content length, and connection
        String postRequest = "POST / HTTP/1.1\r\n";
        postRequest += "Content-Type: application/json\r\n";
        postRequest += "Content-Length: " + jsonInput.length() + "\r\n";
        postRequest += "Connection: close\r\n";
        postRequest += "\r\n";
        postRequest += jsonInput;

        // Send the POST request to the server
        bufferedWriter.write(postRequest);
        bufferedWriter.flush();

        // Read the server response line by line
        // Store the status line in the 'response' variable
        String line;
        String response = null;

        while ((line = bufferedReader.readLine()) != null) {
            // Check if the line starts with "HTTP/1.1"
            // If so, extract the status code and reason phrase
            if (line.startsWith("HTTP/1.1")) {
                response = line.substring("HTTP/1.1".length()).trim();
            }

            // If the line is empty, stop reading the response
            if (line.isEmpty()) {
                break;
            }
        }

        // Check if the server response is '201 Created'
        assertEquals("201 Created", response, "Server response should be '201 Created'");
    }
}
