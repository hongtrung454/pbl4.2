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
import dao.accountDAO;
import java.util.Map;
import model.account;
/**
 *
 * @author DELL
 */
public class account_controller {
     public static account_controller getInstance() {
        return new account_controller();
    }
     public boolean checkLogin(account t) {
         Map<String, String> userCredentials = accountDAO.getInstance().SelectAllToMap();
         if(userCredentials.containsKey(t.getUsername())) {
             String storedPassword = userCredentials.get(t.getUsername());
             return storedPassword.equals(t.getPassword());
         }
         return false;
     }
}
