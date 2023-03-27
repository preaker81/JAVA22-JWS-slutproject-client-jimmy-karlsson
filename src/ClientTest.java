import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ClientTest {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final ByteArrayInputStream inContent;

    public ClientTest() {
        String input = "1\n4\n";
        inContent = new ByteArrayInputStream(input.getBytes());
    }

    @BeforeEach
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
        System.setIn(inContent);
    }

    @AfterEach
    public void restoreStreams() {
        System.setOut(originalOut);
        System.setIn(System.in);
    }

    @Test
    public void testGetAllBooks() throws IOException {
        Socket socket = new Socket("localhost", 10000);

        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(socket.getOutputStream());
        BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
        InputStreamReader inputStreamReader = new InputStreamReader(socket.getInputStream());
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        String getRequest = "GET / HTTP/1.1\r\n";
        getRequest += "Connection: close\r\n";
        getRequest += "\r\n";
        bufferedWriter.write(getRequest);
        bufferedWriter.flush();

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

        assertEquals("200 OK", response, "Server response should be '200 OK'");
    }

    @Test
    public void testGetSpecificBook() throws IOException {
        Socket socket = new Socket("localhost", 10000);

        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(socket.getOutputStream());
        BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
        InputStreamReader inputStreamReader = new InputStreamReader(socket.getInputStream());
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        String getRequest = "GET /book?title=test HTTP/1.1\r\n";
        getRequest += "Connection: close\r\n";
        getRequest += "\r\n";
        bufferedWriter.write(getRequest);
        bufferedWriter.flush();

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

        assertEquals("200 OK", response, "Server response should be '200 OK'");
    }

    @Test
    public void testPostRequest() throws IOException {
        Socket socket = new Socket("localhost", 10000);

        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(socket.getOutputStream());
        BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
        InputStreamReader inputStreamReader = new InputStreamReader(socket.getInputStream());
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        String jsonInput = "{\"title\":\"JUnit Test Book\",\"category\":\"Testing\",\"author\":\"John Doe\",\"words\":123}";

        String postRequest = "POST / HTTP/1.1\r\n";
        postRequest += "Content-Type: application/json\r\n";
        postRequest += "Content-Length: " + jsonInput.length() + "\r\n";
        postRequest += "Connection: close\r\n";
        postRequest += "\r\n";
        postRequest += jsonInput;
        bufferedWriter.write(postRequest);
        bufferedWriter.flush();

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

        assertEquals("201 Created", response, "Server response should be '201 Created'");
    }
}
