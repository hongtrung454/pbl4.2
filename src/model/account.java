/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author DELL
 */
public class account {
    private String username;
    private String password;
    private RequestType requestType;
    private boolean is_blocked;

    public boolean isIs_blocked() {
        return is_blocked;
    }

    public void setIs_blocked(boolean is_blocked) {
        this.is_blocked = is_blocked;
    }

    public RequestType getRequestType() {
        return requestType;
    }

    public void setRequestType(RequestType requestType) {
        this.requestType = requestType;
    }

    @Override
    public String toString() {
        return "account [ , username =" + username + ", password = " + password + ", isactive = " + is_active; // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/OverriddenMethodBody
    }

    public account() {
    }

    public account(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public account(String username, String password,   boolean is_active,boolean is_blocked) {
        this.username = username;
        this.password = password;
        this.is_blocked = is_blocked;
        this.is_active = is_active;
    }

    
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isIs_active() {
        return is_active;
    }

    public void setIs_active(boolean is_active) {
        this.is_active = is_active;
    }

    public account(String username, String password, boolean is_active) {
        this.username = username;
        this.password = password;
        this.is_active = is_active;
    }

    public account(String username, boolean is_blocked, boolean is_active) {
        this.username = username;
        this.is_blocked = is_blocked;
        this.is_active = is_active;
    }
    private boolean is_active;
}
