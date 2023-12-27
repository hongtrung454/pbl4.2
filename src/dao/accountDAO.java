/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import com.mysql.cj.xdevapi.PreparableStatement;
import database.JDBCUtil;
import java.sql.Connection;
import java.util.ArrayList;
import model.account;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author DELL
 */
public class accountDAO implements DAOInterface<account>{

    public static accountDAO getInstance() {
        return new accountDAO();
    }
    public boolean getUserActiveStatus(String username) {
        try {
            Connection con = JDBCUtil.getConnection();
            String sqlQuery = "select is_active from account where username =?";
            PreparedStatement pst = con.prepareStatement(sqlQuery);
            pst.setString(1, username);
            ResultSet resultSet = pst.executeQuery();
            if(resultSet.next()) {
                int isActive = resultSet.getInt("is_active");
                return isActive == 1;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    public boolean getUserBlockStatus(String username) {
        try {
            Connection con = JDBCUtil.getConnection();
            String sqlQuery = "select is_blocked from account where username =?";
            PreparedStatement pst = con.prepareStatement(sqlQuery);
            pst.setString(1, username);
            ResultSet resultSet = pst.executeQuery();
            if(resultSet.next()) {
                int isBlocked = resultSet.getInt("is_blocked");
                return isBlocked == 1;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    public Map<String, String> SelectAllToMap() {
        Map<String, String> userCredentials = new HashMap<>();
        try {
            Connection con = JDBCUtil.getConnection();
            String sql = "select username, password_hash from account";
            PreparedStatement pst = con.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();

            while(rs.next()) {
            String username = rs.getString("username");
            String password = rs.getString("password_hash");
            userCredentials.put(username, password);
        }
        
    } catch (Exception e) {
        e.printStackTrace(); // handle the exception appropriately
    }
    
    return userCredentials;
    }
    @Override
    public int insert(account t) {
        int ketQua = 0;
        try {
            
            int active = (t.isIs_active() == true) ?  1 : 0;
            Connection con = JDBCUtil.getConnection();
            
            String sql = "INSERT INTO account( username, password_hash, is_active) "+
                        "VALUES ( ?, ?, ?)";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, t.getUsername());
            pst.setString(2, t.getPassword());
            pst.setString(3, String.valueOf(active));

            ketQua = pst.executeUpdate();
            
            JDBCUtil.closeConnection(con);

        } catch (Exception e) {
        }
        return ketQua;
        
    }

    @Override
    public int update(account t) {
        int ketQua = 0;
        try {
            int active = (t.isIs_active() == true) ?  1 : 0;
            Connection con = JDBCUtil.getConnection();
            
            String sql = "update account "+
                    " set "+ 
                    
                     ", password_hash = ? "
                    + ", is_active = ?"
                    +"where username =?";
            PreparedStatement pst = con.prepareStatement(sql);
            
            pst.setString(1, t.getPassword());
            pst.setString(2, String.valueOf(active));
            pst.setString(3, String.valueOf(t.getUsername()));
            ketQua = pst.executeUpdate();
            
            JDBCUtil.closeConnection(con);
        } catch (Exception e) {
        }
        return ketQua;
    }
    public int setIsActive (account t) {
        int ketQua = 0;
        try {
           int active = (t.isIs_active() == true) ?  1 : 0;
            Connection con = JDBCUtil.getConnection();
             String sql = "update account "+
                    " set "+ 
                     "is_active = ?"
                   
                    
                    +"where username =?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, String.valueOf(active));
            pst.setString(2, t.getUsername());

            ketQua = pst.executeUpdate();
            
            JDBCUtil.closeConnection(con);
        } catch (Exception e) {
        }
                return ketQua;

    }
    public int setIsBlocked (String username, boolean blocked) {
        int ketQua = 0;
        try {
           int doBlock = (blocked == true) ?  0 : 1;
            Connection con = JDBCUtil.getConnection();
             String sql = "update account "+
                    " set "+ 
                     "is_blocked = ?"
                   
                    
                    +"where username =?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, String.valueOf(doBlock));
            pst.setString(2, username);

            ketQua = pst.executeUpdate();
            
            JDBCUtil.closeConnection(con);
        } catch (Exception e) {
        }
                return ketQua;

    }
    public ArrayList<account> SearchClients(String searchText) {
        ArrayList<account> searchResults = new ArrayList<>();
        try {
            Connection con = JDBCUtil.getConnection();
            String sql = "select * from account where username like ?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, "%" + searchText + "%");
            ResultSet rs = pst.executeQuery();
            while(rs.next()) {
                String foundUsername = rs.getString("username");
                int is_active = rs.getInt("is_active");
                int is_blocked = rs.getInt("is_blocked");
                
                boolean active = (is_active == 1);
                boolean blocked = (is_blocked == 1);
                account account1 = new account(foundUsername,  active, blocked);
                searchResults.add(account1);
            }
        } catch (Exception e) {
        }
        return searchResults;
    }
    @Override
    public int delete(account t) {
        int ketQua = 0;
        try {
            Connection con = JDBCUtil.getConnection();
            
            String sql = "delete from account " +
                    "where username = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, String.valueOf(t.getUsername()));
           
            ketQua = pst.executeUpdate();
            
            JDBCUtil.closeConnection(con);
        } catch (Exception e) {
        }
        return ketQua;
    }

    @Override
    public ArrayList<account> selectAll() {
        ArrayList<account> ketQua = new ArrayList<account>();
        try {
            Connection con = JDBCUtil.getConnection();
            String sql = "select * from account";
            PreparedStatement st = con.prepareStatement(sql);
            
            ResultSet rs = st.executeQuery();
            while(rs.next()) {
                //int user_id = rs.getInt("user_id");
                String username = rs.getString("username");
                String password = rs.getString("password_hash");
                int is_active = rs.getInt("is_active");
                int is_blocked = rs.getInt("is_blocked");

                boolean active = (is_active == 1) ? true: false;
                boolean blocked = (is_blocked == 1) ? true: false;
                account account = new account( username, password, active, blocked);
                ketQua.add(account);
            }
            
        } catch (Exception e) {
        }
        return ketQua;
    }
    

    
    @Override
    public account selectByID(account t) {
        account ketQua = null;
        try {
            Connection con = JDBCUtil.getConnection();
            
            String sql = "select * from account where username = ?";
            PreparedStatement st = con.prepareStatement(sql);
            st.setString(1, String.valueOf(t.getUsername()));
            ResultSet rs = st.executeQuery();
            
            while(rs.next()) {
                String username = rs.getString("username");
                String password = rs.getString("password_hash");
                int is_active = rs.getInt("is_active");
                boolean active = (is_active == 1) ? true: false;
                ketQua = new account( username, password, active);
                
            }
            JDBCUtil.closeConnection(con);
        } catch (Exception e) {
        }
        return ketQua;
    }

    @Override
    public ArrayList<account> selectByCondition(String condition) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
}
