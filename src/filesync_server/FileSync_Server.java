/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package filesync_server;

import controller.MyServerSocket;


/**
 *
 * @author DELL
 */
public class FileSync_Server {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        MyServerSocket myserver = new MyServerSocket();
        myserver.startServer();
    }
    
}
