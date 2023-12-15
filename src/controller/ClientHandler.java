/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 *
 * @author DELL
 */

public class ClientHandler implements Runnable{
    private Socket mySocket;
    private String id;
    private InputStream input;
    private MyServerSocket myServer;
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    private OutputStream output;

    public ClientHandler(Socket mySocket, String id, MyServerSocket myServer) {
        this.mySocket = mySocket;
        this.id = id;
        this.myServer = myServer;
        try {
            this.input = mySocket.getInputStream();
            this.output = mySocket.getOutputStream();
        } catch (Exception e) {
        }
    }

    @Override
    public void run() {
        try {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while((bytesRead = input.read(buffer)) != -1){
                String message = new String(buffer, 0, bytesRead);
            }
        } catch (Exception e) {
        }
    }
    
    
    
}