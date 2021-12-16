package edu.spbu.http;

import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(String args[])throws Exception{
        //String address = "math.spbu.ru";
        String address = "localhost";
        int port = 80;
        Socket socket = new Socket(InetAddress.getByName(address), port);
        String req = "GET /index.html\r\n";
        socket.getOutputStream().write(req.getBytes());
        socket.getOutputStream().flush();
        Scanner scanner = new Scanner(socket.getInputStream());
        //System.out.println(scanner.hasNextLine());
        while(scanner.hasNextLine()){
            System.out.print(scanner.nextLine());
        }
    }
}
