/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
 
package com.openbravo.pos.forms;

import com.openbravo.basic.BasicException;
import com.openbravo.data.loader.DataRead;
import com.openbravo.data.loader.DataWrite;
import com.openbravo.data.loader.SerializableRead;
import com.openbravo.data.loader.SerializableWrite;
import java.util.List;

/**
 *
 * @author preethi 
 */
public class cancellationInfo implements SerializableRead, SerializableWrite {
    private long serialVersionUID = 123456789L;
    private double cancelFee;
    private int timeLimit;
    
    
// private List<announcementInfo> m_aLines;

    public cancellationInfo() {
    }

    /**
     * @return the id
     */
    public double getcancelFee() {
        return cancelFee;
    }

    /**
     * @param id the id to set
     */
    public void setcancelFee(double cancelFee) {
        this.cancelFee = cancelFee;
    }
    public int getTimeLimit() {
        return timeLimit;
    }

    /**
     * @param id the id to set
     */
    public void setTimeLimit(int timeLimit) {
        this.timeLimit = timeLimit;
    }



    public void readValues(DataRead dr) throws BasicException {
       cancelFee = dr.getDouble(1);
       timeLimit = dr.getInt(2);
     }

    public void writeValues(DataWrite dp) throws BasicException {
        dp.setDouble(1, cancelFee);
        dp.setInt(2, timeLimit);

    }


}
