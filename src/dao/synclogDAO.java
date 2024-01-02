/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.util.ArrayList;
import model.synclog;
import database.JDBCUtil;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.time.LocalDateTime;
import java.sql.Timestamp;


/**
 *
 * @author DELL
 */
public class synclogDAO implements DAOInterface<synclog>{

    private static synclogDAO instance;
    public static synchronized synclogDAO getInstance() {
        if (instance == null) {
            instance = new synclogDAO();
        }
        return instance;
    }

    @Override
    public int insert(synclog syncLog) {
        int ketQua = 0;
        try {
            Connection con = JDBCUtil.getConnection();

            String sql = "INSERT INTO synclog(username, action, filename, details) VALUES (?, ?, ?, ?)";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, syncLog.getUsername());
            pst.setString(2, syncLog.getAction());
            pst.setString(3, syncLog.getFilename());
            pst.setString(4, syncLog.getDetails());

            ketQua = pst.executeUpdate();

            JDBCUtil.closeConnection(con);
        } catch (Exception e) {
            // Xử lý lỗi (hiện đang bỏ trống)
        }
        return ketQua;

    }
    public int insertByServer(synclog syncLog) {
        int ketQua = 0;
        try {
            Connection con = JDBCUtil.getConnection();

            String sql = "INSERT INTO synclog( action, filename, details) VALUES ( ?, ?, ?)";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, syncLog.getAction());
            pst.setString(2, syncLog.getFilename());
            pst.setString(3, syncLog.getDetails());

            ketQua = pst.executeUpdate();

            JDBCUtil.closeConnection(con);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ketQua;
    }

    @Override
    public int update(synclog t) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public int delete(synclog t) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public ArrayList<synclog> selectAll() {
        ArrayList<synclog> ketQua = new ArrayList<synclog>();
        try {
            Connection con = JDBCUtil.getConnection();
            String sql = "select * from synclog";
            PreparedStatement st = con.prepareStatement(sql);

            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String username = rs.getString("username");
                String action = rs.getString("action");
                String fileName = rs.getString("filename");
                Timestamp time = rs.getTimestamp("time");
                String details = rs.getString("details");
                
                synclog log = new synclog(id, username, action, fileName, time, details);
                ketQua.add(log);
            }

        } catch (Exception e) {
        }
        return ketQua;
    }

    @Override
    public synclog selectByID(synclog t) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public ArrayList<synclog> selectByCondition(String condition) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
}
