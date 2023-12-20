/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import dao.machineDAO;
import model.machine;

/**
 *
 * @author DELL
 */
public class machine_controller {
    public static machine_controller getInstance() {
        return new machine_controller();
    }
    public machine checkFingerprint(machine t) {
        return machineDAO.getInstance().selectByID(t);
    }
    public int insertDevice(machine t) {
        return machineDAO.getInstance().insert(t);
    }
} 
 