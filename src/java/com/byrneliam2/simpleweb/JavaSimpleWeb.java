package com.byrneliam2.simpleweb;

import java.util.*;
import java.io.*;
import java.net.*;

/**
 * Simple Java implementation of a Web server that responds only to GET commands.
 */
public class JavaSimpleWeb {

    private String indexPath = "src/web/index.html";

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
            String getCommand = stripHttpGetCommand(request);
            assert getCommand != null;

            String page = getCommand.split(" ")[1];
            String response;

            if (page.equals("/")) {
                response = getHTMLIndex();
            } else if (page.startsWith("/page")) {
                response = "something";
            } else {
                response = htmlHeader() + "Unrecognised request." + htmlFooter();
            }

            sendResponse(httpResponse(response), socket);
        } catch (Exception e) {
            sendResponse(httpResponse(e.getMessage()), socket);
        }
    }

    /**
     * Process the HTTP request and extract the GET command.
     */
    private String stripHttpGetCommand(String request) throws IOException {
        BufferedReader r = new BufferedReader(new StringReader(request));
        String line;
        while ((line = r.readLine()) != null) {
            if (line.startsWith("GET")) return line;
        }
        return null;
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

    private String htmlHeader() {
        return "<!DOCTYPE html><html><body>";
    }

    private String htmlFooter() {
        return "</body></html>";
    }

    /* -------------------------------------------------------------------------------------------------------------- */

    /**
     * Load the index page into a String and return for addition into the response.
     */
    private String getHTMLIndex() {
        File f = new File(indexPath);
        StringBuilder str = new StringBuilder();

        try {
            Scanner scan = new Scanner(f);
            while (scan.hasNextLine()) str.append(scan.nextLine());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return str.toString();
    }

    /* -------------------------------------------------------------------------------------------------------------- */

    public static void main(String[] args) {
        new JavaSimpleWeb().run();
    }
}
