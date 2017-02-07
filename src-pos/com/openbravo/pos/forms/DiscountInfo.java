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
public class DiscountInfo implements SerializableRead, SerializableWrite {
    private long serialVersionUID = 123456789L;
    private double discountAmt;
    private int noofPeople;
    
    
// private List<announcementInfo> m_aLines;

    public DiscountInfo() {
    }

    /**
     * @return the id
     */
    public double getDiscountAmt() {
        return discountAmt;
    }

    /**
     * @param id the id to set
     */
    public void setcancelFee(double discountAmt) {
        this.discountAmt = discountAmt;
    }
    public int getNoofPeople() {
        return noofPeople;
    }

    /**
     * @param id the id to set
     */
    public void setNoofPeople(int noofPeople) {
        this.noofPeople = noofPeople;
    }



    public void readValues(DataRead dr) throws BasicException {
       discountAmt = dr.getDouble(1);
       noofPeople = dr.getInt(2);
     }

    public void writeValues(DataWrite dp) throws BasicException {
        dp.setDouble(1, discountAmt);
        dp.setInt(2, noofPeople);

    }


}
