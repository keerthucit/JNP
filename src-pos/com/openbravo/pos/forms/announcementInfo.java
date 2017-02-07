/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
 
package com.openbravo.pos.forms;

import com.mysql.jdbc.StringUtils;
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
public class announcementInfo implements SerializableRead, SerializableWrite {
    private long serialVersionUID = 123456789L;
    private String id;
    private String announcement;
    private String validfrom;
    private String validTo;
    private String active;
    
 private List<announcementInfo> m_aLines;

    public announcementInfo() {
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
    public String getAnnouncement() {
        return announcement;
    }

    /**
     * @param id the id to set
     */
    public void setAnnouncement(String announcement) {
        this.announcement = announcement;
    }

        public String getValidfrom() {
        return validfrom;
    }

    /**
     * @param id the id to set
     */
    public void setValidfrom(String validfrom) {
        this.validfrom = validfrom;
    }
        public String getValidto() {
        return validTo;
    }
     public String getActive() {
        return active;
    }

    /**
     * @param id the id to set
     */
    public void setActive(String active) {
        this.active = active;
    }
    /**
     * @param id the id to set
     */
    public void setValidto(String validto) {
        this.validTo = validTo;
    }
  public String printAnnouncement() {
        return announcement == null ? " " : announcement;
    }

  public String printAnnouceFirstLine() {
      String announcement =  getAnnouncement();
      int count = announcement.indexOf("*");
      int announcementCount = 0;
      if(count==-1){
           announcementCount = 65;
      }else{
           announcementCount = count;
      }
      if(announcement.length()>announcementCount){
      String announcement1 = announcement.substring(0, announcementCount);
      return announcement1 == null ? "" : announcement1;
      }
      return announcement == null ? "" : announcement;
  }
  public String printAnnouceSecondLine() {
      String announcement2 = null;
      int count = announcement.indexOf("*");
      int announcementCount = 0;
      if(count==-1){
          announcementCount = 65;
      }else{
          announcementCount = count;
      }

      String announcement =  getAnnouncement();
      if(announcement.length()>announcementCount){
      if(announcementCount==65){
           announcement2 = announcement.substring(announcementCount, announcement.length());
      }else{
           announcement2 = announcement.substring(announcementCount+1, announcement.length());
      }
      return announcement2 == null ? "" : announcement2;
      }
     return announcement2 == null ? "" : announcement2;
  }

  public String printValidTo() {
        return validTo;
    }
    public String printValidFrom() {
        return validfrom;
    }
     public String printActive() {
        return active;
    }

    /**
     * @return the employeeid
     */

  public List<announcementInfo> getLines() {
//        System.out.println("enrtrr---m_aLines"+this.m_aLines.size());
        return this.m_aLines;
    }

    public void setLines(List<announcementInfo> value) {

        m_aLines = value;

    }
    public void readValues(DataRead dr) throws BasicException {
       id = dr.getString(1);
       announcement = dr.getString(2);
       validfrom = dr.getString(3);
       validTo = dr.getString(4);
       active = dr.getString(5);

    }

    public void writeValues(DataWrite dp) throws BasicException {
        dp.setString(1, id);
        dp.setString(2, announcement);
        dp.setString(3, validfrom);
        dp.setString(4, validTo);
        dp.setString(5, active);

    }


}
