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
public class holidayInfo implements SerializableRead, SerializableWrite {
    private long serialVersionUID = 123456789L;
    private String id;
    private String holidayDate;
    private String description;
 private List<holidayInfo> m_aLines;

    public holidayInfo() {
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }
    public String getHolidayDate() {
        return holidayDate;
    }

    /**
     * @param id the id to set
     */
    public void setHolidayDate(String holidayDate) {
        this.holidayDate = holidayDate;
    }

        public String getHolidayDesc() {
        return description;
    }

    /**
     * @param id the id to set
     */
    public void setHolidayDesc(String description) {
        this.description = description;
    }
  public String printHolidayDesc() {
        return description;
    }
  public String printHolidayDate() {
        return holidayDate;
    }

    /**
     * @return the employeeid
     */

 public List<holidayInfo> getLines() {
//        System.out.println("enrtrr---m_aLines"+this.m_aLines.size());
        return this.m_aLines;
    }

    public void setLines(List<holidayInfo> value) {

        m_aLines = value;

    }
    public void readValues(DataRead dr) throws BasicException {
       id = dr.getString(1);
       holidayDate = dr.getString(2);
       description = dr.getString(3);

    }

    public void writeValues(DataWrite dp) throws BasicException {
        dp.setString(1, id);
        dp.setString(2, holidayDate);
        dp.setString(3, description);

    }


}
