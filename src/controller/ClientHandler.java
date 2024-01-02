/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import model.RequestType;
import model.account;
import model.file;
import com.google.gson.JsonElement;
import java.io.DataInputStream;
import java.nio.charset.StandardCharsets;

/**
 *
 * @author DELL
 */
public class ClientHandler implements Runnable {

    private Socket mySocket;
    private String id;
    private InputStream input;
    private MyServerSocket myServer;
    private String path = "";
    private ArrayList<file> files = new ArrayList<>();

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ArrayList<file> getfiles() {
        return this.files;
    }
    private OutputStream output;

    public ClientHandler(Socket mySocket,String path, MyServerSocket myServer) {
        this.mySocket = mySocket;
        this.myServer = myServer;
        this.path = path;
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
            DataInputStream DataInput = new DataInputStream(input);
//            int dataLength = DataInput.readInt();
//            byte[] buffer = new byte[dataLength];
//            System.out.println(String.valueOf(dataLength));
//            int bytesRead;
            String jsonData = "";
//            StringBuilder jsonDataBuilder = new StringBuilder();
            PrintWriter writer = new PrintWriter(this.output, true);
//            while((dataLength = DataInput.readInt()) != -1) {
//                byte[] buffer = new byte[dataLength];
//                int bytesRead = input.read(buffer);
//                if(bytesRead != dataLength) {
//                    throw new IOException("khong doc du du lieu");
//                }
//            while ((bytesRead = input.read(buffer)) != -1) {
            while (true) {
                int dataLength = DataInput.readInt();
                if (dataLength == -1) {
                    break;
                }
                byte[] buffer = new byte[dataLength];
                int bytesRead = 0;
                while (bytesRead < dataLength) {
                    int read = input.read(buffer, bytesRead, dataLength - bytesRead);
                    if (read == -1) {
                        throw new IOException("khong doc du du lieu");
                    }
                    bytesRead += read;
                }
//                jsonData = new String(buffer, 0, bytesRead);
                jsonData = new String(buffer, StandardCharsets.UTF_8);
//                jsonDataBuilder.append(new String(buffer, 0, bytesRead));
                //System.out.println(jsonData);
                if (jsonData.contains("LOGIN")) {
                    processLoginRequest(jsonData, writer);
                }
                if (jsonData.contains("REGISTER")) {
                    handleRegisterRequest(jsonData, writer);
                }
                if (jsonData.contains("GET_ALL_FILES")) {
                    processGetAllFilesRequest(jsonData);
                }
                if (jsonData.contains("SEND_FILES_FROM_CLIENT")) {
                    System.out.println(jsonData);
                    handleReceiveAllFilesResponse(jsonData, new BufferedReader(new InputStreamReader(mySocket.getInputStream())), writer);

//                    System.out.println(jsonDataBuilder.toString());
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
                if (jsonData.contains("SEND_FILES_INFO_FROM_CLIENT")) {
                    files = handleFileInfoRequestFromServer(jsonData);
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

        handleLoginRequest(user, writer);

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
            for (File file : files) {
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
            for (File file : files) {
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
            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
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

    public ArrayList<file> handleFileInfoRequestFromServer(String data) {
        JsonObject jsonFiles = new Gson().fromJson(data, JsonObject.class);
        int fileCount = jsonFiles.get("fileCount").getAsInt();
        JsonArray filesArray = jsonFiles.getAsJsonArray("files");
        ArrayList<file> files = new ArrayList<>();
        for (int i = 0; i < fileCount; i++) {
            JsonObject fileInfo = filesArray.get(i).getAsJsonObject();
            file f = new file();
            String fileName = fileInfo.get("fileName").getAsString();
            f.setFile_name(fileName);
            long fileSize = fileInfo.get("fileSize").getAsLong();
            f.setSize(fileSize);
            String fileMD5 = fileInfo.get("fileMD5").getAsString();
            files.add(f);
        }
        return files;
    }

    private void handleReceiveAllFilesResponse(String data, BufferedReader reader, PrintWriter writer) {
        try {
            System.out.println("handle 10 file co duoc khong");

            JsonObject jsonFiles = new Gson().fromJson(data, JsonObject.class);
            System.out.println("handle 10 file có duoc khong 2");

            int fileCount = jsonFiles.get("fileCount").getAsInt();
            JsonArray filesArray = jsonFiles.getAsJsonArray("files");
            Map<String, String> serverFiles = getServerFiles();
            Map<String, String> markedFiles = new HashMap<>();
            System.out.println("file count o day ne: " + String.valueOf(fileCount));
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
                        log_controller.getInstance().modifyFile(id, fileName);
                    }
                    // Nếu MD5 giống nhau, không cần làm gì cả
                } else {
                    // Nếu file không tồn tại, ghi chú lại thông tin file
                    markedFiles.put(fileName, fileMD5);
                    log_controller.getInstance().createFile(id, fileName);
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
                    log_controller.getInstance().deleteFile(id, serverFileName);
                }
            }
        }
    }

    private Map<String, String> getServerFiles() {
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

                System.out.println("Dang nhan file duoc danh dau tu client: " + fileName);

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
            String filePath = path + File.separator + fileName;

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

   

    private void handleLoginRequest(account user, PrintWriter writer) {
        if (account_controller.getInstance().checkLogin(user)) {
            System.out.println("check login ok111111");
            writer.println("Login successful"); // Gửi phản hồi về client
            this.id = user.getUsername();

        }
        writer.println("login failed");
    }

    private void handleRegisterRequest(String jsonData, PrintWriter writer) {
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
        if (account_controller.getInstance().doRegister(user)) {
            writer.println("Register successful"); // Gửi phản hồi về client

        }
        writer.println("Register failed");
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
            for (File file : files) {
                JsonObject fileInfo = new JsonObject();
                fileInfo.addProperty("fileName", file.getName());
                fileInfo.addProperty("fileSize", file.length());
                System.out.println("file name o trong broadcast ne: " + file.getName());
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

    public void RequestFileInfo() {
        PrintWriter writer = new PrintWriter(this.output, true);
        writer.println("REQUEST_FILE_INFO");
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
