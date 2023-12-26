/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;
import model.RequestType;
import model.account;
import java.net.NetworkInterface;


/**
 *
 * @author DELL
 */
public class MyServerSocket {

    public MyServerSocket() {
    }
    private static final int PORT = 8000;
    private List<ClientHandler> clients = new ArrayList<>();
    public void startServer() {
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            System.out.println("Server started, listnening on port:" + PORT);
            //clients connect to server
            while(true) {
                Socket clientSocket =  serverSocket.accept();
                System.out.println("New client connected " + clientSocket.getInetAddress().getHostAddress());
                
                
                ClientHandler clientHandler = new ClientHandler(clientSocket, System.currentTimeMillis()+ "", this);
                clients.add(clientHandler);
                new Thread(clientHandler).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void broadCastMessage(String id) {
        for(ClientHandler client: clients) {
            if(!client.getId().equals(id))
                client.SendUpdate();
        }
    }
}
