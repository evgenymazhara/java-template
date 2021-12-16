package edu.spbu.http;

import java.net.*;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.util.Scanner;
import java.io.*;

public class Server {
    public static void main(String[] args) throws Exception{
        int port = 80;
        ServerSocket serverSocket = new ServerSocket(port);
        while(true){
            Socket socket = serverSocket.accept();
            String req = requestReceive(socket);
            requestSend(req, socket);
            socket.close();
        }
    }

    private static String requestReceive(Socket socket){
        try {
            Scanner scanner = new Scanner(socket.getInputStream());
            return scanner.nextLine().split(" ")[1].substring(1);
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return "noSuchFile";
    }

    private static void requestSend(String filename, Socket socket){
        try {
            File file = new File(filename);
            if (file.isFile()) {
                String inside = new String(Files.readAllBytes(Paths.get(filename)));
                String response = "HTTP/1.1 200 OK\n";
                PrintWriter writing = new PrintWriter(socket.getOutputStream(), true);
                writing.print(response + "\r\n");
                writing.print(inside + "\r\n");
                writing.close();
            } else {
                socket.getOutputStream().write("Issue with a file".getBytes());
            }
        }
        catch(IOException | InvalidPathException exc){
            exc.printStackTrace();
        }
    }
}
