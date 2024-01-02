/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 *
 * @author DELL
 */
public class synclog {
    private int id;
    private String username;
    private String action;
    private String filename;
    private Timestamp time;
    private String details;
            
    private RequestType requestType;

    public RequestType getRequestType() {
        return requestType;
    }

    public synclog(int id, String username, String action, String filename, Timestamp time, String details) {
        this.id = id;
        this.username = username;
        this.action = action;
        this.filename = filename;
        this.time = time;
        this.details = details;
    }
    
    public void setRequestType(RequestType requestType) {
        this.requestType = requestType;
    }
    public synclog() {
    } 

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}
