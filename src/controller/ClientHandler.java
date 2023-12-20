/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import model.RequestType;
import static model.RequestType.LOGIN;
import static model.RequestType.REGISTER;
import model.account;
import model.machine;

/**
 *
 * @author DELL
 */
public class ClientHandler implements Runnable {

    private Socket mySocket;
    private String id;
    private InputStream input;
    private MyServerSocket myServer;
    private String path = "D:\\Folder Chung";

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
            // Mở luồng đọc và ghi với client
            byte[] buffer = new byte[1024];
            int bytesRead;
            String jsonData = "";
            PrintWriter writer = new PrintWriter(this.output, true);
            while ((bytesRead = input.read(buffer)) != -1) {
                jsonData = new String(buffer, 0, bytesRead);
                //System.out.println(jsonData);
                if (jsonData.contains("LOGIN")) {
                    processLoginRequest(jsonData, writer);
                }
                if (jsonData.contains("CHECK_FINGERPRINT") || jsonData.contains("INSERT_DEVICE")) {
                    processCheckFingerprintRequest(jsonData, writer);
                }
                if (jsonData.contains("GET_ALL_FILES")) {
                    processGetAllFilesRequest(jsonData);
                }
            }

            System.out.println("Received data from client: " + jsonData);

            // Xử lý yêu cầu từ client
            // Đóng kết nối với client
//            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processLoginRequest(String jsonData, PrintWriter writer) {
        // Giả sử có một hàm để chuyển đổi JSON thành object
        // và có các model như User, RequestType
        account user = new account();
        try {
            // Dữ liệu JSON nhận được từ server
            String receivedJsonData = jsonData; // Thay bằng dữ liệu JSON nhận được từ server;

            // Chuyển đổi từ JSON sang đối tượng account
            Gson gson = new Gson();
            user = gson.fromJson(receivedJsonData, account.class);

        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(user.toString());
        RequestType requestType = user.getRequestType();
        System.out.println(requestType);

        // Xử lý yêu cầu tùy thuộc vào requestType
        switch (requestType) {
            case LOGIN:
                handleLoginRequest(user, writer);
                break;
            case REGISTER:
                handleRegisterRequest(user, writer);
                break;
            // Thêm các trường hợp khác nếu cần
            default:
                // Xử lý khi requestType không hợp lệ
                break;
        }
    }

    private void processCheckFingerprintRequest(String jsonData, PrintWriter writer) {
        machine machine1 = new machine();
        try {
            // Dữ liệu JSON nhận được từ server
            String receivedJsonData = jsonData; // Thay bằng dữ liệu JSON nhận được từ server;

            // Chuyển đổi từ JSON sang đối tượng account
            Gson gson = new Gson();
            machine1 = gson.fromJson(receivedJsonData, machine.class);

        } catch (Exception e) {
            e.printStackTrace();
        }
        switch (machine1.getRequestType()) {
            case CHECK_FINGERPRINT:
                handleCheckFingerprintRequest(machine1, writer);
                break;
            case INSERT_DEVICE:
                handleInsertDeviceRequest(machine1, writer);
                break;
            // Thêm các trường hợp khác nếu cần
            default:
                // Xử lý khi requestType không hợp lệ
                break;
        }
    }

    private void processGetAllFilesRequest(String receivedData) {
        try {
            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(receivedData, JsonObject.class);
            // xử lý thông tin user và thiết bị của user (lưu vào db, ghi log)
            account user1 = gson.fromJson(jsonObject.getAsJsonObject("account"), account.class);
            machine machine1 = gson.fromJson(jsonObject.getAsJsonObject("machine"), machine.class);
            
            //gửi file
            File directory = new File(path);
            File[] files = directory.listFiles();
            
            //tao doi tuong JSON de bieu dien list cac tap tin
            JsonObject jsonFiles = new JsonObject();
            jsonFiles.addProperty("requestType", "GET_ALL_FILES_RESPONSE");
            jsonFiles.addProperty("fileCount", files.length);
            
            // tao mot array luu thong tin cho moi file
            JsonArray filesArray = new JsonArray();
            for (File file: files) {
                JsonObject fileInfo = new JsonObject();
                fileInfo.addProperty("fileName", file.getName());
                fileInfo.addProperty("fileSize", file.length());
                filesArray.add(fileInfo);
                
            }
            jsonFiles.add("files", filesArray);
            
            String jsonData = new Gson().toJson(jsonFiles);
            PrintWriter writer = new PrintWriter(this.output, true);
            writer.println(jsonData);
            
            // gui moi file cho client 
            for(File file: files) {
                sendFileToClient(file);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void sendFileToClient(File file) {
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            byte[] fileData = new byte[(int) file.length()];
            fileInputStream.read(fileData);
            fileInputStream.close();
            // gui file size cho client
            //PrintWriter writer = new PrintWriter(output, true);
            //writer.println(fileData.length);
            
            // gui file content cho client
            this.output.write(fileData);
            this.output.flush();
        } catch (Exception e) {
        }
    }

    private void handleCheckFingerprintRequest(machine machine1, PrintWriter writer) {
        if ((machine_controller.getInstance().checkFingerprint(machine1)) != null) {
            writer.println("Fingerprint right");
        }
        writer.println("Fingerprint not found");
    }

    private void handleInsertDeviceRequest(machine machine1, PrintWriter writer) {
        if (machine_controller.getInstance().insertDevice(machine1) > 0) {
            writer.println("hehe, da them mot may");
        }
    }

    private void handleLoginRequest(account user, PrintWriter writer) {
        // Xử lý yêu cầu đăng nhập
        System.out.println("check login ok");

        if (account_controller.getInstance().checkLogin(user)) {
            System.out.println("check login ok111111");
            writer.println("Login successful"); // Gửi phản hồi về client

        }
        writer.println("login failed");
    }

    private void handleRegisterRequest(account user, PrintWriter writer) {
        // Xử lý yêu cầu đăng ký
        // ...
        writer.println("Registration successful"); // Gửi phản hồi về client
    }

}
