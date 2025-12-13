package server;

import java.io.*;
import java.net.*;

public class ServerMain {
    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(5000)) {
            System.out.println("Сервер запущен на порту 5000...");
            Socket client = serverSocket.accept();
            System.out.println("Клиент подключился!");
            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            PrintWriter out = new PrintWriter(client.getOutputStream(), true);

            String msg = in.readLine();
            System.out.println("Клиент пишет: " + msg);
            out.println("Привет от сервера!");
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
