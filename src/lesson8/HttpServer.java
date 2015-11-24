package lesson8;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.IllegalBlockingModeException;

public class HttpServer {

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(8080)) {
            while (true) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("Клиент подключился");
                    new Thread(new SocketSession(clientSocket)).start();
                } catch (IllegalThreadStateException | IllegalBlockingModeException | SecurityException e) {
                    e.printStackTrace();
                    System.exit(-1);
                }
            }
        } catch (IllegalArgumentException | SecurityException | IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }

    }

    private static class SocketSession implements Runnable {

        private Socket clientSocket;
        private InputStream input;
        private OutputStream output;
        private int languageType = 0; //default English, 1 - Russian

        public SocketSession(Socket clientSocket) {
            this.clientSocket = clientSocket;
            try {
                this.input = clientSocket.getInputStream();
                this.output = clientSocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(-1);
            }

        }

        @Override
        public void run() {
            try {
                parseHeaders();
                if (languageType == 1) {
                    writeHeaders("<html><body><h1>Ответ моего сервера</h1></body></html>");
                } else {
                    writeHeaders("<html><body><h1>Response From MyServer</h1></body></html>");
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(-1);
            } finally {
                try {
                    this.clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    System.exit(-1);
                }
            }
//            System.out.println("Request finished");
        }

        private void parseHeaders() throws IOException {
            BufferedReader br = new BufferedReader(new InputStreamReader(input));
            StringBuilder sb = new StringBuilder();
            while (true) {
                String line = br.readLine();
                sb.append(line + "\n");
                if (line == null || line.trim().length() == 0) {
                    break;
                }
                String[] parts = line.split(": ");
                if ("Accept-Language".equals(parts[0])) {
//                    System.out.println(parts[1]);
                    if ("ru".equals(parts[1])) {
//                        System.out.println("Yes");
                        languageType = 1;
                    }
                }

            }
            System.out.println("Запрос клиента:");
            System.out.println(sb.toString());
        }

        private void writeHeaders(String s) throws IOException {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(output));
            String response = "HTTP/1.1 200 OK\r\n" +
                    "Server: MyServer/2015-11-08\r\n" +
                    "Content-Type: text/html\r\n" +
                    "Content-Length: " + s.length() + "\r\n" +
                    "Connection: close\r\n\r\n";
            String result = response + s;
            bw.write(result);
            bw.write("");
            bw.flush();
            System.out.println("Ответ клиенту:");
            System.out.println(result);
            System.out.println();
        }

    }

}
