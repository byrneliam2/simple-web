package com.byrneliam2.simpleweb;

import java.util.*;
import java.io.*;
import java.net.*;

/**
 * Simple Java implementation of a Web server that responds only to GET commands.
 */
public class JavaSimpleWeb {

    private String htmlDir = "web/html/";
    private String indexPath = htmlDir + "index.html";

    /* -------------------------------------------------------------------------------------------------------------- */

    private void run() {
        try {
            ServerSocket servSocket = new ServerSocket(8080);
            while (true) {
                Socket s = servSocket.accept();
                processNextRequest(s);
                s.close();
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * Process the next request issued by the client.
     */
    private void processNextRequest(Socket socket) throws IOException {
        try {
            String request = readRequest(socket);
            String page = getGetCommandFile(request);
            String response;

            if (page.equals("/")) {
                response = getHTMLIndex();
            } else if (page.startsWith("/page")) {
                response = "something";
            } else {
                response = formHTMLParagraph("Unable to locate requested file");
            }

            sendResponse(httpResponse(response), socket);
        } catch (Exception e) {
            sendResponse(httpResponse(formHTMLParagraph(e.getMessage())), socket);
        }
    }

    /**
     * Process the HTTP request and extract the GET command's requested file. If no
     * GET command is found, a page is returned notifying the user of the issue.
     */
    private String getGetCommandFile(String request) {
        Scanner scanner = new Scanner(request);
        String line;
        while ((line = scanner.nextLine()) != null) {
            if (line.startsWith("GET")) return line.split(" ")[1];
        }
        return formHTMLParagraph("Bad request (no GET command found.)");
    }

    private String readRequest(Socket socket) throws IOException {
        Reader input = new InputStreamReader(new BufferedInputStream(socket.getInputStream()));
        StringBuilder request = new StringBuilder();
        char[] buf = new char[1024];
        int nread;

        do {
            nread = input.read(buf);
            String in = new String(buf, 0, nread);
            request.append(in);
        } while (nread == 1024);

        return request.toString();
    }

    private void sendResponse(String response, Socket s) throws IOException {
        Writer output = new OutputStreamWriter(s.getOutputStream());
        output.write(response);
        output.flush();
    }

    private String httpResponse(String body) {
        return "HTTP/1.1 200 OK\n"
                + "Content-Length: " + body.length() + "\n"
                + "Content-Type: text/html; charset=UTF-8\n\n"
                + body;
    }

    /* -------------------------------------------------------------------------------------------------------------- */

    private String getHTMLIndex() {
        return loadFileIntoString(new File(indexPath));
    }

    /**
     * Load the given file into a String and return for addition into the response.
     */
    private String loadFileIntoString(File file) {
        StringBuilder str = new StringBuilder();

        try {
            Scanner scan = new Scanner(file);
            while (scan.hasNextLine()) str.append(scan.nextLine());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return str.toString();
    }

    /**
     * Create and return a simple HTML page with a single paragraph element.
     */
    private String formHTMLParagraph(String text) {
        return "<!DOCTYPE html><html><body><p>" + text + "</p></body></html>";
    }

    /* -------------------------------------------------------------------------------------------------------------- */

    public static void main(String[] args) {
        new JavaSimpleWeb().run();
    }
}
