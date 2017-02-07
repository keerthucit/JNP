/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.openbravo.pos.panels;

import com.openbravo.basic.BasicException;
import com.openbravo.data.loader.DataRead;
import com.openbravo.data.loader.DataWrite;
import com.openbravo.data.loader.SerializableRead;
import com.openbravo.data.loader.SerializableWrite;
import java.util.Date;

/**
 *
 * @author preethi
 */
public class showInfo implements SerializableRead, SerializableWrite {

    private static final long serialVersionUID = 7640633837719L;
    private String id;
    private String showName;
    private String startTime;
    private String endTime;
    private int noofseats;
    private String special;
    private Date specialShowDate;
     private String groupOpen;
    /** Creates a new instance of DiscountRateinfo */
    public showInfo() {
    }

    public void readValues(DataRead dr) throws BasicException {
        id = dr.getString(1);
        showName = dr.getString(2);
        startTime = dr.getString(3);
        endTime = dr.getString(4);
        noofseats = dr.getInt(5);
        special = dr.getString(6);
        specialShowDate = dr.getTimestamp(7);
        groupOpen = dr.getString(8);
    }
    public void writeValues(DataWrite dp) throws BasicException {
        dp.setString(1, id);
        dp.setString(2, showName);
        dp.setString(3, startTime);
        dp.setString(4, endTime);
        dp.setInt(5, noofseats);
        dp.setString(6, special);
        dp.setTimestamp(7, specialShowDate);
        dp.setString(8, groupOpen);
    }

    public String getId() {
        return id;
    }

    public String getShowName() {
        return showName;
    }
    public void setShowName(String showName) {
        this.showName = showName;
    }

    public String getstartTime() {
        return startTime;
    }
    public void setstartTime(String startTime) {
        this.startTime = startTime;
    }
    public String getendTime() {
        return endTime;
    }

    public void setendTime(String endTime) {
        this.endTime = endTime;
    }
     public int getnoofseats() {
        return noofseats;
    }
    public void setnoofseats(int noofseats) {
        this.noofseats = noofseats;
    }
    public String getspecial() {
        return special;
    }

    public void setspecial(String special) {
        this.special = special;
    }
    public java.util.Date getspecialShowDate() {
        return specialShowDate;
    }

    public void setspecialShowDate(Date specialShowDate) {
        this.specialShowDate = specialShowDate;
    }

    public String getGroupOpen() {
        return groupOpen;
    }

    public void setGroupOpen(String groupOpen) {
        this.groupOpen = groupOpen;
    }
    public String printStarttime(){
        return startTime;
    }
     public String printEndtime(){
        return endTime;
    }
      public String printShowName(){
        return showName;
    }
}
