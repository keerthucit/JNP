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
public class CloseshowInfo implements SerializableRead, SerializableWrite {

    private static final long serialVersionUID = 7640633837719L;
    private String id;
    private String showName;
   
    /** Creates a new instance of DiscountRateinfo */
    public CloseshowInfo() {
    }

    public void readValues(DataRead dr) throws BasicException {
        id = dr.getString(1);
        showName = dr.getString(2);
      
    }
    public void writeValues(DataWrite dp) throws BasicException {
        dp.setString(1, id);
        dp.setString(2, id);
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getshowName() {
        return showName;
    }
    public void setshowName(String id) {
        this.id = showName;
    }

}
