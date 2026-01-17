package com.iprody;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.iprody.utils.ContentUtils.*;

@Slf4j
public class HttpServer {
    private static final int PORT = 8080;
    private static final String CRLF = "\r\n";
    private static final String ROOT_PATH = "/";
    private static final String STATUS_LINE_TEMPLATE = "HTTP/1.1 %s";

    private static String BASE_FILE_FOLDER;

    static void main(String[] args) throws IOException {
        setUpBaseFileFolder(args);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            log.info("Server started at http://localhost:" + PORT);
            while (true) {
                try (Socket clientSocket = serverSocket.accept()) {
                    List<String> requestLines = readHttpRequestHeaders(clientSocket);
                    writeHttpRequestHeadersToConsole(requestLines);
                    sendHttpResponse(clientSocket, requestLines);
                }
            }
        }
    }

    private static void setUpBaseFileFolder(String[] args) {
        if (args == null || args.length == 0) {
            throw new IllegalArgumentException("args can not be empty");
        }

        log.info("base file folder will be setting up with first argument from command line");

        BASE_FILE_FOLDER = args[0];
    }

    private static List<String> readHttpRequestHeaders(Socket socket) throws IOException {
        BufferedReader in =
                new BufferedReader(
                        new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));

        List<String> requestLines = new ArrayList<>();

        String line;
        while ((line = in.readLine()) != null && !line.isEmpty()) {
            requestLines.add(line);
        }

        return Collections.unmodifiableList(requestLines);
    }

    private static void writeHttpRequestHeadersToConsole(List<String> requestLines) {
        requestLines.forEach(log::info);
    }

    private static void sendHttpResponse(Socket socket, List<String> requestLines) throws IOException {
        final Optional<String> pathOpt = extractLocationAfterServerUrl(requestLines);

        if (pathOpt.isEmpty()) {
            sendNotFoundResponse(socket);
            return;
        }

        final String path = pathOpt.get();

        if (path.equals(ROOT_PATH)) {
            sendRootContent(socket);
            return;
        }

        final Path absolutePath = Path.of(BASE_FILE_FOLDER, path).toAbsolutePath();

        if (!isValidFile(absolutePath)) {
            sendNotFoundResponse(socket);
            return;
        }

        sendFileContent(socket, absolutePath);
    }

    private static void sendRootContent(Socket socket) throws IOException {
        String response = "<html><body><h1>Hello from server!</h1></body></html>";
        writeHttpResponse(
                socket,
                STATUS_LINE_TEMPLATE.formatted("200 OK"),
                "text/html",
                response.getBytes(StandardCharsets.UTF_8));
    }

    private static void sendFileContent(Socket socket, Path filePath) throws IOException {
        final byte[] content = Files.readAllBytes(filePath);
        final String contentType = defineContentType(filePath);
        writeHttpResponse(
                socket,
                STATUS_LINE_TEMPLATE.formatted("200 OK"),
                contentType,
                content);
    }

    private static void sendNotFoundResponse(Socket socket) throws IOException {
        String response = "<html><body><h1>404 Not Found</h1></body></html>";
        writeHttpResponse(
                socket,
                STATUS_LINE_TEMPLATE.formatted("404 Not Found"),
                "text/html",
                response.getBytes(StandardCharsets.UTF_8));
    }

    private static void writeHttpResponse(Socket socket,
                                          String statusLine,
                                          String contentType,
                                          byte[] responseBytes) throws IOException {
        BufferedWriter out =
                new BufferedWriter(
                        new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));

        out.write(statusLine + CRLF);
        out.write("Content-Type: %s; charset=UTF-8".formatted(contentType) + CRLF);
        out.write("Content-Length: " + responseBytes.length + CRLF);
        out.write(CRLF);
        out.write(new String(responseBytes, StandardCharsets.UTF_8));

        out.flush();
    }
}
