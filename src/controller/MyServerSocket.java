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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import model.RequestType;
import model.account;
import java.net.NetworkInterface;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import model.file;
import view.MainForm;


/**
 *
 * @author DELL
 */
public class MyServerSocket {

    public MyServerSocket() {
    }
    private static final int PORT = 8000;
    private List<ClientHandler> clients = new ArrayList<>();
    // phòng trừ trường hợp biến isSyncing đang được thay đổi thì có biến khác đọc nó thì ta dùng lock
    private String path = "";

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
    
    public void startServer() {
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            System.out.println("Server started, listnening on port:" + PORT);
            //clients connect to server
            MainForm f = new MainForm(this);
            f.show();
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected " + clientSocket.getInetAddress().getHostAddress());
                ClientHandler clientHandler = new ClientHandler(clientSocket, path,  this);
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
    ArrayList<file> files = new ArrayList<>();

    public ArrayList<file> RequestFileInfo(String id) {
        for (ClientHandler client: clients) {
            if(client.getId().equals(id))
                client.RequestFileInfo();
            final Timer timer = new Timer(1500, null);
            timer.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    files = client.getfiles();
                    if(!files.isEmpty())
                    {
                        
                        timer.stop();
                    }
                    else {        
                        JOptionPane.showMessageDialog(null, "Folder của client rỗng", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                        timer.stop();

                    }
                }
            });
                    
            timer.start();
        }
        return files;
    }
}
