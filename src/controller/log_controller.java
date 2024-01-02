/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import dao.synclogDAO;
import java.io.File;
import java.util.ArrayList;
import model.synclog;

/**
 *
 * @author DELL
 */
public class log_controller {
    private static log_controller instance;
    public static synchronized log_controller getInstance() {
        if (instance == null) {
            instance = new log_controller();
        }
        return instance;
    }

    private String path;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
    public void initFolder() {
        File folder = new File(path);
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                synclog initLog = new synclog();
                initLog.setAction("Create");
                initLog.setFilename(file.getName());
                initLog.setDetails("Start server");
                synclogDAO.getInstance().insertByServer(initLog);
            }
        }
        
    }
    public void deleteFile(String username, String fileName) {
        synclog log = new synclog();
        log.setAction("Delete");
        log.setFilename(fileName);
        log.setUsername(username);
        log.setDetails("User delete file");
        synclogDAO.getInstance().insert(log);
    }
    public void createFile(String username, String fileName) {
        synclog log = new synclog();
        log.setAction("Create");
        log.setFilename(fileName);
        log.setUsername(username);
        log.setDetails("User create file");
        synclogDAO.getInstance().insert(log);
    }
    public void modifyFile(String username, String fileName) {
        synclog log = new synclog();
        log.setAction("Modify");
        log.setFilename(fileName);
        log.setUsername(username);
        log.setDetails("User modify file");
        synclogDAO.getInstance().insert(log);
    }
    public ArrayList<synclog> getLogList() {
        return synclogDAO.getInstance().selectAll();
    }
}
