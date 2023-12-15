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
                System.out.println("New client connected" + clientSocket.getInetAddress().getHostAddress());
                NetworkInterface networkInterface = NetworkInterface.getByInetAddress(clientSocket.getInetAddress());
                if (networkInterface != null) {
                    byte [] macAddressBytes = networkInterface.getHardwareAddress();
                    if(macAddressBytes != null) {
                        StringBuilder macAddressStringBuilder = new StringBuilder();
                        for (byte b : macAddressBytes) {
                            macAddressStringBuilder.append(String.format("%02X:", b));
                        }
                        String macAddress = macAddressStringBuilder.toString().substring(0, macAddressStringBuilder.length() - 1);
                        System.out.println("MAC address : " + macAddress);
                        
                    }
                    else {
                        System.out.println("Mac address is not available for the given ip address");
                    }
                }
                else {
                    System.out.println("network interface not found for the given ip address");
                }
                
                ClientHandler clientHandler = new ClientHandler(clientSocket, System.currentTimeMillis()+ "", this);
                clients.add(clientHandler);
                new Thread(clientHandler).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
