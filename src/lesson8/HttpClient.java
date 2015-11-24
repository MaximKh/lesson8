package lesson8;

import java.io.*;
import java.net.Socket;

public class HttpClient {

    private static final int DEFAULT_PORT = 8080;
    private static final String DEFAULT_HOST = "localhost";
    private static final String DEFAULT_FILE_REQUEST = "Request.txt";

    public static void main(String[] args) {
        String host = DEFAULT_HOST;
        String fileRequest = DEFAULT_FILE_REQUEST;
        int port = DEFAULT_PORT;
        switch (args.length) {
            case 3:
                fileRequest = args[2];
            case 2:
                try {
                    port = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    System.out.println(e.getMessage());
                    System.exit(-1);
                }
            case 1:
                host = args[0];
        }
        try (Socket socket = new Socket(host, port);
             OutputStream out = socket.getOutputStream();
             InputStream in = socket.getInputStream()) {
            sendHeader(out, fileRequest);
            System.out.println(receiveHeader(in));
        } catch (IllegalArgumentException | SecurityException | IOException e) {
            System.out.println(e.getMessage());
            System.exit(-2);
        }


    }

    public static String receiveHeader(InputStream in) {
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
            String line;
            while (true) {
                try {
                    line = bufferedReader.readLine();
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                    break;
                }
                if (line == null) break;
                sb.append(line + "\n");
            }
        } catch (IllegalArgumentException | SecurityException e) {
            System.out.println(e.getMessage());
            System.exit(-3);
        }
        return sb.toString();
    }

    public static void sendHeader(OutputStream out, String fileRequest) {

        try (FileReader fileReader = new FileReader(new File(fileRequest).getAbsoluteFile())) {

            BufferedReader br = new BufferedReader(fileReader);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(out));
            String line;
            while ((line = br.readLine()) != null) {
                bw.write(line + "\n");
                bw.flush();
            }
            bw.write("\n");
            bw.flush();

        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.exit(-4);
        }
    }
}
