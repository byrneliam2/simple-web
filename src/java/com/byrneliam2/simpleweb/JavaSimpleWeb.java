package com.byrneliam2.simpleweb;

import java.util.*;
import java.io.*;
import java.net.*;

public class JavaSimpleWeb {

    private void run() {
        try {
            ServerSocket ss = new ServerSocket(8080);
            while (true) {
                Socket s = ss.accept();
                processRequest(s);
                s.close();
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    private void processRequest(Socket s) throws IOException {
        try {
            String request = readRequest(s);
            String httpCommand = stripHttpGetCommand(request);
            assert httpCommand != null;
            String page = httpCommand.split(" ")[1];
            String response;

            if (page.equals("/")) {
                response = mainPage();
            } else if (page.startsWith("/page")) {
                response = "something";
            } else {
                response = htmlHeader() + "Unrecognised Request" + htmlFooter();
            }

            sendResponse(httpResponse(response), s);
        } catch (Exception e) {
            sendResponse(httpResponse(e.getMessage()), s);
        }
    }

    public String redirect(String target) {
        String r = htmlHeader();
        r += "<script type=\"text/javascript\">location.href='" + target + "'</script>";
        return r + htmlFooter();
    }

    private Map<String, String> splitParameters(String str) {
        int idx = str.indexOf('?');
        if (idx == -1 || idx >= str.length()) {
            return new HashMap<>();
        } else {
            Map fields = new HashMap<String, String>();
            String[] params = str.substring(idx + 1).split("&");
            for (String p : params) {
                String[] pair = p.split("=");
                if (pair.length > 1) {
                    fields.put(pair[0], pair[1].replace("+", " ").replace("%0D%0A", "\n"));
                } else if (pair.length > 0) {
                    fields.put(pair[0], "");
                }
            }
            return fields;
        }
    }

    private String stripHttpGetCommand(String request) throws IOException {
        BufferedReader r = new BufferedReader(new StringReader(request));
        String line;
        while ((line = r.readLine()) != null) {
            if (line.startsWith("GET")) {
                return line;
            }
        }
        return null;
    }

    private String readRequest(Socket s) throws IOException {
        Reader input = new InputStreamReader(new BufferedInputStream(s.getInputStream()));
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
        return "";
    }

    private String htmlFooter() {
        return "";
    }

    /* -------------------------------------------------------------------------------------------------------------- */

    private String mainPage() {
        return "";
    }

    /* -------------------------------------------------------------------------------------------------------------- */

    public static void main(String[] args) {
        new JavaSimpleWeb().run();
    }
}
