/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import static controller.calculateMD5.calculateMD5OfFile;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
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
                if (jsonData.contains("SEND_FILES_FROM_CLIENT")){
                    handleReceiveAllFilesResponse(jsonData, new BufferedReader(new InputStreamReader(mySocket.getInputStream())), writer);
                    System.out.println(jsonData);
                }
                if (jsonData.contains("SEND_MARKED_FILES")) {
                    handleReceiveMarkedFiles(jsonData);
                }
                if (jsonData.contains("MARKED_FILES_INFO_FROM_CLIENT")) {
                    try {
                        handleMarkedFilesInfoFromClient(new Gson().fromJson(jsonData, JsonObject.class), writer);
 
                    } catch (Exception e) {
                    }
                }
                if (jsonData.contains("DOWNLOAD")) {
                    SendUpdate();
                }
                if (jsonData.contains("LOG_OUT")) {
                    handleLogoutRequest(jsonData);
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
    private void handleLogoutRequest(String jsonData) {
        account user = new account();
        try {
             String receivedJsonData = jsonData; // Thay bằng dữ liệu JSON nhận được từ server;

            // Chuyển đổi từ JSON sang đối tượng account
            Gson gson = new Gson();
            user = gson.fromJson(receivedJsonData, account.class);
            account_controller.getInstance().setLogOut(user);

        } catch (Exception e) {
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
            this.id = machine1.getDevice_fingerprint();

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
//            machine machine1 = gson.fromJson(jsonObject.getAsJsonObject("machine"), machine.class);
            
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
            writer.println(jsonData); // đây là gửi thông tin file
            
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
            byte[] buffer = new byte[8192]; //đlà gửi file :f  
            int bytesRead;
            while((bytesRead = fileInputStream.read(buffer)) != -1) {
                this.output.write(buffer, 0, bytesRead);
                this.output.flush();
            }
            fileInputStream.close();
//            fileInputStream.read(fileData);
//            fileInputStream.close();
//            this.output.write(fileData);
//            this.output.flush();
        } catch (Exception e) {
        }
    }
    private void handleReceiveAllFilesResponse(String data, BufferedReader reader, PrintWriter writer) {
        try {                
            JsonObject jsonFiles = new Gson().fromJson(data, JsonObject.class);
                                        

            int fileCount = jsonFiles.get("fileCount").getAsInt();
            JsonArray filesArray = jsonFiles.getAsJsonArray("files");
            Map<String, String> serverFiles = getServerFiles();
            Map<String, String> markedFiles = new HashMap<>();
            System.out.println("file count o day ne: " +String.valueOf(fileCount));
            for (int i = 0; i < fileCount; i++) {
                JsonObject fileInfo = filesArray.get(i).getAsJsonObject();
                String fileName = fileInfo.get("fileName").getAsString();
                System.out.println(fileName + "test nhan file o server");
                long fileSize = fileInfo.get("fileSize").getAsLong();
                String fileMD5 = fileInfo.get("fileMD5").getAsString();
                // Kiểm tra file trên server
                if (serverFiles.containsKey(fileName)) {
                    String serverFileMD5 = serverFiles.get(fileName);

                    if (!serverFileMD5.equals(fileMD5)) {
                        // Nếu MD5 khác nhau, ghi chú lại thông tin file
                        markedFiles.put(fileName, fileMD5);
                    }
                    // Nếu MD5 giống nhau, không cần làm gì cả
                } else {
                    // Nếu file không tồn tại, ghi chú lại thông tin file
                    markedFiles.put(fileName, fileMD5);
                }
            }
            cleanupServerFiles(filesArray);
            // Gửi thông tin về các file đã được ghi chú lại cho client
            sendMarkedFilesInfoToClient(markedFiles, writer);
//            myServer.broadCastMessage(id);
            // Nhận các file từ client và thực hiện ghi đè hoặc tải về
//            receiveFilesFromClient(reader, markedFiles);
        } catch (Exception e) {
        }
    }
    private void cleanupServerFiles(JsonArray clientFilesArray) {
        File serverDirectory = new File(path);
        File[] serverFiles = serverDirectory.listFiles();

        if (serverFiles != null) {
            for (File serverFile : serverFiles) {
                String serverFileName = serverFile.getName();
                boolean found = false;

                // Kiểm tra xem file trên server có trong danh sách gửi từ client không
                for (JsonElement clientFileElement : clientFilesArray) {
                    String clientFileName = clientFileElement.getAsJsonObject().get("fileName").getAsString();
                    if (serverFileName.equals(clientFileName)) {
                        found = true;
                        break;
                    }
                }

                // Nếu không tìm thấy trong danh sách gửi từ client, xóa file trên server
                if (!found) {
                    serverFile.delete();
                }
            }
        }
    }
    private  Map<String, String> getServerFiles() {
        Map<String, String> serverFiles = new HashMap<>();
        File serverDirectory = new File(path);
        File[] files = serverDirectory.listFiles();

        if (files != null) {
            for (File file : files) {
                String md5 = calculateMD5.calculateMD5OfFile(file.getAbsolutePath());
                serverFiles.put(file.getName(), md5);
            }
        }

        return serverFiles;
    }
    private void sendMarkedFilesInfoToClient(Map<String, String> markedFiles, PrintWriter writer) {
        JsonObject markedFilesInfo = new JsonObject();
        markedFilesInfo.addProperty("responseType", "MARKED_FILES_INFO");

        JsonArray markedFilesArray = new JsonArray();
        for (Map.Entry<String, String> entry : markedFiles.entrySet()) {
            JsonObject fileInfo = new JsonObject();
            fileInfo.addProperty("fileName", entry.getKey());
            fileInfo.addProperty("fileMD5", entry.getValue());
            markedFilesArray.add(fileInfo);
        }

        markedFilesInfo.add("markedFiles", markedFilesArray);

        writer.println(new Gson().toJson(markedFilesInfo));
    }
    private void handleReceiveMarkedFiles(String data) {
        try {
            JsonObject jsonFiles = new Gson().fromJson(data, JsonObject.class);
                                        System.out.println("aloaloaloaloa1111");

            int fileCount = jsonFiles.get("fileCount").getAsInt();
            JsonArray filesArray = jsonFiles.getAsJsonArray("files");
            System.out.println(String.valueOf(fileCount));
            // nhan moi file tu server
            for (int i = 0; i < fileCount; i++) {
                JsonObject fileInfo = filesArray.get(i).getAsJsonObject();
                String fileName = fileInfo.get("fileName").getAsString();
                
                System.out.println("Dang nhan file duoc danh dau tu client: " +fileName);

                long fileSize = fileInfo.get("fileSize").getAsLong();
                                System.out.println(String.valueOf(fileSize));


                receiveFile(mySocket.getInputStream(), fileName, fileSize);
            }
            // gui thong tin cho cac client khac 
            System.out.println("Da broadcast chua ?");
            myServer.broadCastMessage(id);
        } catch (Exception e) {
        }
    }
    private void receiveFile(InputStream inputStream, String fileName, long fileSize) {
        try {
            // Read file data into a byte array
            byte[] fileData = new byte[(int) fileSize];
            int bytesRead;
            int offset = 0;

            while (offset < fileSize && (bytesRead = inputStream.read(fileData, offset, (int) (fileSize - offset))) != -1) {
                offset += bytesRead;
            }

            // Save the file
            saveFile(fileData, fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveFile(byte[] fileData, String fileName) throws IOException {
        FileOutputStream fileOutputStream = null;

        try {
            // Construct the file path
            String filePath = path + "\\" + fileName;

            // Create a FileOutputStream to save the file
            fileOutputStream = new FileOutputStream(filePath);

            // Write the file data to the FileOutputStream
            fileOutputStream.write(fileData);

            System.out.println("File saved to: " + filePath);

        } finally {
            // Close the FileOutputStream
            if (fileOutputStream != null) {
                fileOutputStream.close();
            }
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
            this.id = user.getUsername();

        }
        writer.println("login failed");
    }

    private void handleRegisterRequest(account user, PrintWriter writer) {
        // Xử lý yêu cầu đăng ký
        // ...
        writer.println("Registration successful"); // Gửi phản hồi về client
    }
    public void SendUpdate() {
       
        try {
            System.out.println("gui update cho cac client");
            
            //gửi file
            
            File directory = new File(path);
            File[] files = directory.listFiles();
            
            //tao doi tuong JSON de bieu dien list cac tap tin
            JsonObject jsonFiles = new JsonObject();
            jsonFiles.addProperty("requestType", "SEND_UPDATE_FROM_SERVER");
            jsonFiles.addProperty("fileCount", files.length);
            
            // tao mot array luu thong tin cho moi file
            JsonArray filesArray = new JsonArray();
            for (File file: files) {
                JsonObject fileInfo = new JsonObject();
                fileInfo.addProperty("fileName", file.getName());
                fileInfo.addProperty("fileSize", file.length());
                System.out.println("file name o trong broadcast ne: "+ file.getName());
                fileInfo.addProperty("fileMD5", calculateMD5OfFile(file.getAbsolutePath()));
                filesArray.add(fileInfo);
                
            }
            jsonFiles.add("files", filesArray);
            
            String jsonData = new Gson().toJson(jsonFiles);
            PrintWriter writer = new PrintWriter(mySocket.getOutputStream(), true);
            writer.println(jsonData); // đây là gửi thông tin file
            
            // gui moi file cho client 
//            for(File file: files) {
//                sendFileToClient(file);
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    
    }
    private void handleMarkedFilesInfoFromClient(JsonObject receivedData, PrintWriter writer) {
        try {
            JsonArray markedFilesArray = receivedData.getAsJsonArray("markedFiles");

            // Tạo danh sách các file đã được ghi chú lại từ server
            JsonObject jsonFiles = new JsonObject();
            Map<String, String> markedFiles = new HashMap<>();
            JsonArray fileDetailsArray = new JsonArray(); // Tạo một mảng mới cho thông tin về các file

            for (int i = 0; i < markedFilesArray.size(); i++) {
                JsonObject fileInfo = markedFilesArray.get(i).getAsJsonObject();
                String fileName = fileInfo.get("fileName").getAsString();
                System.out.println(fileName + "namenamenmanenamnanamanananamanenaenemena");
                String fileMD5 = fileInfo.get("fileMD5").getAsString();
                String filePath = Paths.get(path, fileName).toString();

                File file = new File(filePath);
                JsonObject fileDetails = new JsonObject();
                fileDetails.addProperty("fileName", fileName);
                fileDetails.addProperty("fileSize", file.length());
                fileDetailsArray.add(fileDetails);
                markedFiles.put(fileName, fileMD5);
                System.out.println("testttttttttttttttttttttttttttt000000000");
            }

            System.out.println("testttttttttttttttttttttttttttt");
            jsonFiles.addProperty("requestType", "SEND_MARKED_FILES_FROM_SERVER");
            jsonFiles.addProperty("fileCount", markedFilesArray.size());
            jsonFiles.add("files", fileDetailsArray);

            String jsonData = new Gson().toJson(jsonFiles);
            writer.println(jsonData);
            writer.flush();
            sendMarkedFilesToClient(path, markedFiles);
            // Gửi các file đã được ghi chú lại đến server để nhận từ server
            // sendFilesToServer(reader, writer, markedFiles);
        } catch (Exception e) {
            // Xử lý các exception phù hợp
            e.printStackTrace();
        }
    }
    private void sendMarkedFilesToClient(String path, Map<String, String> markedFiles) {
        for (Map.Entry<String, String> entry : markedFiles.entrySet()) {
            String fileName = entry.getKey();
            String fileMD5 = entry.getValue();
            
            // Construct the full path of the file
            String filePath = Paths.get(path, fileName).toString();

            // Check if the file exists
            File file = new File(filePath);
            if (file.exists()) {
                System.out.println("Sending file: " + fileName);
               // sendFileToServer(file, fileName);
                sendFileToClient(file);
            } else {
                System.out.println("File not found: " + fileName);
            }
        }
    }
//    private void sendFileToServer(File file, String fileName) {
//        try {
//            FileInputStream fileInputStream = new FileInputStream(file);
//            byte[] fileData = new byte[(int) file.length()]; //đlà gửi file :f  
//            fileInputStream.read(fileData);
//            fileInputStream.close();
//            
//            this.mySocket.getOutputStream().write(fileData);
//            this.mySocket.getOutputStream().flush();
//        } catch (Exception e) {
//        }
//    }

}
