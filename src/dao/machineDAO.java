/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.util.ArrayList;
import model.machine;
import database.JDBCUtil;
import java.sql.Connection;

import com.mysql.cj.xdevapi.PreparableStatement;
import database.JDBCUtil;
import java.sql.Connection;
import java.util.ArrayList;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
/**
 *
 * @author DELL
 */
public class machineDAO implements DAOInterface<machine>{

    public static machineDAO getInstance() {
        return new machineDAO();
    }
    @Override
    public int insert(machine t) {
        int rowsAffected = 0;
        try {
            Connection con = JDBCUtil.getConnection();

            String sql = "INSERT INTO machine (device_fingerprint, machine_name, is_active, folder_path) VALUES (?, ?, ?, ?)";
            PreparedStatement st = con.prepareStatement(sql);

            // Set values for the prepared statement
            st.setString(1, t.getDevice_fingerprint());
            st.setString(2, t.getMachine_name());
            st.setInt(3, t.isIs_active()? 1 : 0);
            st.setString(4, t.getFolder_path());

            // Execute the insert statement
             rowsAffected = st.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Insert successful.");
                return rowsAffected;
            } else {
                System.out.println("Insert failed.");

            }

            JDBCUtil.closeConnection(con);
        } catch (Exception e) {
            e.printStackTrace();
            // Handle exceptions or log as needed
        }
        return rowsAffected;
    }

    @Override
    public int update(machine t) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public int delete(machine t) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public ArrayList<machine> selectAll() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public machine selectByID(machine t) {
        machine ketQua = null;
    try {
        Connection con = JDBCUtil.getConnection();
        
        String sql = "SELECT * FROM machine WHERE device_fingerprint = ?";
        PreparedStatement st = con.prepareStatement(sql);
        st.setString(1, t.getDevice_fingerprint());
        ResultSet rs = st.executeQuery();
        
        while (rs.next()) {
            String device_fingerprint = rs.getString("device_fingerprint");
            String machine_name = rs.getString("machine_name");
            int is_active = rs.getInt("is_active");
            boolean active = (is_active == 1) ? true : false;
            String folder_path = rs.getString("folder_path");
            
            ketQua = new machine(device_fingerprint, machine_name,  active, folder_path);
        }
        
        JDBCUtil.closeConnection(con);
    } catch (Exception e) {
        e.printStackTrace();
        // Xử lý ngoại lệ hoặc logging tùy theo yêu cầu
    }
    return ketQua;
    }

    @Override
    public ArrayList<machine> selectByCondition(String condition) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
}
