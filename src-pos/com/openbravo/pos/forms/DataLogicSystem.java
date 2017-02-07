//    Openbravo POS is a point of sales application designed for touch screens.
//    Copyright (C) 2007-2009 Openbravo, S.L.
//    http://www.openbravo.com/product/pos
//
//    This file is part of Openbravo POS.
//
//    Openbravo POS is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    Openbravo POS is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with Openbravo POS.  If not, see <http://www.gnu.org/licenses/>.

package com.openbravo.pos.forms;

import java.awt.image.BufferedImage;
import java.io.*;
import java.text.ParseException;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import javax.imageio.ImageIO;
import com.openbravo.basic.BasicException;
import com.openbravo.data.loader.*;
import com.openbravo.format.Formats;
import com.openbravo.pos.util.ThumbNailBuilder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;

/**
 *
 * @author adrianromero
 */
public class DataLogicSystem extends BeanFactoryDataSingle {
    private Session s;
    protected String m_sInitScript;
    private SentenceFind m_version;       
    private SentenceExec m_dummy;
    
    protected SentenceList m_peoplevisible;  
    protected SentenceFind m_peoplebycard;  
    protected SerializerRead peopleread;
     protected SentenceFind m_peoplebyname;
    
    private SentenceFind m_rolepermissions; 
    private SentenceExec m_changepassword;    
    private SentenceFind m_locationfind;
    
    private SentenceFind m_resourcebytes;
    private SentenceExec m_resourcebytesinsert;
    private SentenceExec m_resourcebytesupdate;

    protected SentenceFind m_sequencecash;
    protected SentenceFind m_sequenceshow;
    protected SentenceFind m_activecash;
    protected SentenceExec m_insertcash;
    private SentenceExec m_HolidayInsert;
    private SentenceExec m_HolidayInterInsert;
    private SentenceExec m_ShowInsert;
    private SentenceExec m_CancelInsert;
     private SentenceExec m_DiscountInsert;
    private SentenceExec m_ProductInsert;
    private SentenceExec m_ProductCatInsert;
    private SentenceExec m_AnnouncementInsert;
    private SentenceExec m_ShowExtInsert;
    private SentenceExec m_peoplelogInsert;
    protected SentenceFind m_activeShow;
    protected SentenceExec m_insertShow;
    
    private Map<String, byte[]> resourcescache;
    
    /** Creates a new instance of DataLogicSystem */
    public DataLogicSystem() {            
    }
    
    public void init(Session s){

        m_sInitScript = "/com/openbravo/pos/scripts/" + s.DB.getName();

        m_version = new PreparedSentence(s, "SELECT VERSION FROM APPLICATIONS WHERE ID = ?", SerializerWriteString.INSTANCE, SerializerReadString.INSTANCE);
        m_dummy = new StaticSentence(s, "SELECT * FROM PEOPLE WHERE 1 = 0");
         
        final ThumbNailBuilder tnb = new ThumbNailBuilder(32, 32, "com/openbravo/images/yast_sysadmin.png");        
        peopleread = new SerializerRead() {
            public Object readValues(DataRead dr) throws BasicException {
                return new AppUser(
                        dr.getString(1),
                        dr.getString(2),
                        dr.getString(3),
                        dr.getString(4),
                        dr.getString(5),
                        dr.getString(6),
                        new ImageIcon(tnb.getThumbNail(ImageUtils.readImage(dr.getBytes(7)))));
            }
        };

        m_peoplevisible = new StaticSentence(s
            , "SELECT ID, NAME, USERNAME, APPPASSWORD, CARD, ROLE, IMAGE FROM PEOPLE WHERE VISIBLE = " + s.DB.TRUE()
            , null
            , peopleread);

        m_peoplebycard = new PreparedSentence(s
            , "SELECT ID, NAME, USERNAME, APPPASSWORD, CARD, ROLE, IMAGE FROM PEOPLE WHERE CARD = ? AND VISIBLE = " + s.DB.TRUE()
            , SerializerWriteString.INSTANCE
            , peopleread);
         
        m_resourcebytes = new PreparedSentence(s
            , "SELECT CONTENT FROM RESOURCES WHERE NAME = ?"
            , SerializerWriteString.INSTANCE
            , SerializerReadBytes.INSTANCE);
        
        Datas[] resourcedata = new Datas[] {Datas.STRING, Datas.STRING, Datas.INT, Datas.BYTES};
        m_resourcebytesinsert = new PreparedSentence(s
                , "INSERT INTO RESOURCES(ID, NAME, RESTYPE, CONTENT) VALUES (?, ?, ?, ?)"
                , new SerializerWriteBasic(resourcedata));
        m_resourcebytesupdate = new PreparedSentence(s
                , "UPDATE RESOURCES SET NAME = ?, RESTYPE = ?, CONTENT = ? WHERE NAME = ?"
                , new SerializerWriteBasicExt(resourcedata, new int[] {1, 2, 3, 1}));
        
        m_rolepermissions = new PreparedSentence(s
                , "SELECT PERMISSIONS FROM ROLES WHERE ID = ?"
            , SerializerWriteString.INSTANCE
            , SerializerReadBytes.INSTANCE);

        m_peoplebyname = new PreparedSentence(s
            , "SELECT ID, NAME, USERNAME, APPPASSWORD, CARD, ROLE, IMAGE FROM PEOPLE WHERE USERNAME = ? AND VISIBLE = " + s.DB.TRUE()
            , SerializerWriteString.INSTANCE
            , peopleread);
         Datas[] loginDatas = new Datas[] {Datas.STRING, Datas.STRING, Datas.STRING, Datas.STRING, Datas.DOUBLE };
        m_peoplelogInsert = new PreparedSentence(s
                , "INSERT INTO PEOPLELOG (ID, PEOPLE_ID, STARTTIME, REASON, AMOUNT) VALUES (?, ?, ?, ?, ?)"
                , new SerializerWriteBasic(loginDatas));
        m_changepassword = new StaticSentence(s
                , "UPDATE PEOPLE SET APPPASSWORD = ? WHERE ID = ?"
                ,new SerializerWriteBasic(new Datas[] {Datas.STRING, Datas.STRING}));

		Datas[] HolidayDatas = new Datas[] {Datas.STRING, Datas.STRING, Datas.STRING, Datas.STRING, Datas.INT, Datas.STRING };
        m_HolidayInsert = new PreparedSentence(s
                , "INSERT INTO HOLIDAYLIST (ID, DATE, DESCRIPTION, GENERAL, YEAR,HOLIDAYID) VALUES (?, ?, ?, ?, ?, ?)"
                , new SerializerWriteBasic(HolidayDatas));

        Datas[] HolidayInterDatas = new Datas[] {Datas.STRING, Datas.STRING, Datas.STRING, Datas.INT };
        m_HolidayInterInsert = new PreparedSentence(s
                , "INSERT INTO HOLIDAYINTERMEDIATE (ID, WEEK, DAY, YEAR) VALUES (?, ?, ?, ?)"
                , new SerializerWriteBasic(HolidayInterDatas));
        Datas[] ShowDatas = new Datas[] {Datas.STRING, Datas.STRING, Datas.STRING, Datas.STRING, Datas.INT, Datas.STRING, Datas.TIMESTAMP, Datas.STRING, Datas.TIMESTAMP, Datas.STRING };
        m_ShowInsert = new PreparedSentence(s
                , "INSERT INTO SHOWS (ID, SHOWNAME, STARTTIME, ENDTIME, NOOFSEATS, SHOWTYPE, SPECIALSHOWDATE, OPEN, CREATED, CREATEDBY) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
                , new SerializerWriteBasic(ShowDatas));

        Datas[] cancelFeeDatas = new Datas[] {Datas.STRING, Datas.DOUBLE, Datas.INT, Datas.TIMESTAMP, Datas.STRING, Datas.STRING };
        m_CancelInsert = new PreparedSentence(s
                , "INSERT INTO CANCELLATION (ID, CANCELLATIONFEE, TIMELIMIT, CREATED, CREATEDBY, ACTIVE) VALUES (?, ?, ?, ?, ?, ?)"
                , new SerializerWriteBasic(cancelFeeDatas));
Datas[] discountDatas = new Datas[] {Datas.STRING, Datas.DOUBLE, Datas.INT, Datas.TIMESTAMP, Datas.STRING, Datas.STRING };
        m_DiscountInsert = new PreparedSentence(s
                , "INSERT INTO DISCOUNTSETUP (ID, DISCOUNTAMOUNT, NOOFPEOPLE, CREATED, CREATEDBY, ACTIVE) VALUES (?, ?, ?, ?, ?, ?)"
                , new SerializerWriteBasic(discountDatas));


        Datas[] ProductDatas = new Datas[] {Datas.STRING, Datas.STRING, Datas.STRING, Datas.DOUBLE, Datas.STRING, Datas.STRING, Datas.STRING, Datas.DOUBLE, Datas.INT, Datas.IMAGE, Datas.STRING};
        m_ProductInsert = new PreparedSentence(s
                , "INSERT INTO PRODUCTS (ID, CODE, NAME, PRICESELL, CATEGORY, TAXCAT, TICKETGROUP, MINAMOUNT, NOOFPEOPLE, IMAGE, TICKETDISCOUNT) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
                , new SerializerWriteBasic(ProductDatas));
        Datas[] productCatDatas = new Datas[] {Datas.STRING };
        m_ProductCatInsert = new PreparedSentence(s
                , "INSERT INTO PRODUCTS_CAT (PRODUCT) VALUES (?)"
                , new SerializerWriteBasic(productCatDatas));

        Datas[] announcementDatas = new Datas[] {Datas.STRING, Datas.STRING, Datas.STRING, Datas.STRING, Datas.STRING };
        m_AnnouncementInsert = new PreparedSentence(s
                , "INSERT INTO ANNOUNCEMENT (ID, ANNOUNCEMENT, VALIDFROM, VALIDTO, ACTIVE) VALUES (?, ?, ?, ?, ?)"
                , new SerializerWriteBasic(announcementDatas));

        Datas[] ShowExtDatas = new Datas[] {Datas.STRING, Datas.STRING, Datas.TIMESTAMP, Datas.INT, Datas.STRING};
        m_ShowExtInsert = new PreparedSentence(s
                , "INSERT INTO EXTENDEDSEATS (ID, SHOWNAME, DATE, NOOFSEATS, CREATEDBY) VALUES (?, ?, ?, ?, ?)"
                , new SerializerWriteBasic(ShowExtDatas));

        m_sequencecash = new StaticSentence(s,
                "SELECT MAX(HOSTSEQUENCE) FROM CLOSEDCASH WHERE HOST = ?",
                SerializerWriteString.INSTANCE,
                SerializerReadInteger.INSTANCE);
        m_sequenceshow = new StaticSentence(s,
                "SELECT MAX(HOSTSEQUENCE) FROM CLOSEDSHOW WHERE HOST = ? AND ACTIVE= 'Y'",
                SerializerWriteString.INSTANCE,
                SerializerReadInteger.INSTANCE);
        m_activecash = new StaticSentence(s
            , "SELECT HOST, HOSTSEQUENCE, DATESTART, DATEEND FROM CLOSEDCASH WHERE MONEY = ?"
            , SerializerWriteString.INSTANCE
            , new SerializerReadBasic(new Datas[] {Datas.STRING, Datas.INT, Datas.TIMESTAMP, Datas.TIMESTAMP}));            
        m_insertcash = new StaticSentence(s
                , "INSERT INTO CLOSEDCASH(MONEY, HOST, HOSTSEQUENCE, DATESTART, DATEEND) " +
                  "VALUES (?, ?, ?, ?, ?)"
                , new SerializerWriteBasic(new Datas[] {Datas.STRING, Datas.STRING, Datas.INT, Datas.TIMESTAMP, Datas.TIMESTAMP}));
        m_activeShow = new StaticSentence(s
            , "SELECT HOST, HOSTSEQUENCE, DATESTART, DATEEND, SHOWNAME FROM CLOSEDSHOW WHERE MONEY = ?"
            , SerializerWriteString.INSTANCE
            , new SerializerReadBasic(new Datas[] {Datas.STRING, Datas.INT, Datas.TIMESTAMP, Datas.TIMESTAMP, Datas.STRING}));
        m_insertShow = new StaticSentence(s
                , "INSERT INTO CLOSEDSHOW(MONEY, HOST, HOSTSEQUENCE, DATESTART, DATEEND, SHOWNAME, ACTIVE) " +
                  "VALUES (?, ?, ?, ?, ?, ?, ?)"
                , new SerializerWriteBasic(new Datas[] {Datas.STRING, Datas.STRING, Datas.INT, Datas.TIMESTAMP, Datas.TIMESTAMP, Datas.STRING, Datas.STRING}));

        m_locationfind = new StaticSentence(s
                , "SELECT NAME FROM LOCATIONS WHERE ID = ?"
                , SerializerWriteString.INSTANCE
                , SerializerReadString.INSTANCE);   
        
        resetResourcesCache();        
    }

 
    public String getInitScript() {
        return m_sInitScript;
    }
    public final void peoplelogInsert(String Id,String peopleId, String startTime,String reason, Double Amount) throws BasicException {

       Object[] value = new Object[] {Id, peopleId, startTime, reason, Amount};

       m_peoplelogInsert.exec(value);

   }
//    public abstract BaseSentence getShutdown();
    
    public final String findVersion() throws BasicException {
        return (String) m_version.find(AppLocal.APP_ID);
    }
    public final void execDummy() throws BasicException {
        m_dummy.exec();
    }
    public final List listPeopleVisible() throws BasicException {
        return m_peoplevisible.list();
    }      
    public final AppUser findPeopleByCard(String card) throws BasicException {
        return (AppUser) m_peoplebycard.find(card);
    }   
    public final AppUser findPeopleByName(String name) throws BasicException {
        System.out.println("enrtrvvvv---"+name);
        return (AppUser) m_peoplebyname.find(name);
    }
    public final String findRolePermissions(String sRole) {
        
        try {
            return Formats.BYTEA.formatValue(m_rolepermissions.find(sRole));        
        } catch (BasicException e) {
            return null;                    
        }             
    }
    public final void holidayInsert(String Id,String date, String description,String general,int year, String holidayId) throws BasicException {
      
       Object[] value = new Object[] {Id, date, description, general, year, holidayId};

       m_HolidayInsert.exec(value);
    
   }

     public final void holidayInterInsert(String Id,String week, String day,int year) throws BasicException {

       Object[] value = new Object[] {Id, week, day, year};

       m_HolidayInterInsert.exec(value);

   }
    public final void showInsert(String Id,String showName, String startTime,String endTime,int seats,String special,Date specialDate, String groupOpen, Date created, String user) throws BasicException {

       Object[] value = new Object[] {Id, showName, startTime, endTime, seats, special, specialDate, groupOpen, created, user};

       m_ShowInsert.exec(value);

   }
    public final void cancelFeeInsert(String Id, double cancelFee, int timeLimit,Date created, String user, String active) throws BasicException {

       Object[] value = new Object[] {Id, cancelFee, timeLimit, created, user, active};

       m_CancelInsert.exec(value);

   }

    public final void discountInsert(String Id, double discount, int noofpeople,Date created, String user, String active) throws BasicException {

       Object[] value = new Object[] {Id, discount, noofpeople, created, user, active};

       m_DiscountInsert.exec(value);

   }

    public final void ticketCategoryInsert(String Id, String ticketcode, String ticketcategory, double price,String category,String taxcat, String group, double minAmount, int noofpeople, BufferedImage image, String discount) throws BasicException {

       Object[] value = new Object[] {Id, ticketcode, ticketcategory, price, category, taxcat, group, minAmount, noofpeople, image, discount};

       Object[] productCat = new Object[] {Id};

       m_ProductInsert.exec(value);
       m_ProductCatInsert.exec(productCat);

   }

     public final void announcementInsert(String Id,String announcement, String validFrom,String validTo,String active) throws BasicException {

   
       Object[] value = new Object[] {Id, announcement, validFrom, validTo, active};
       m_AnnouncementInsert.exec(value);

   }


       public void showExtInsert(String Id, String showName, Date date, int seats, String user) throws BasicException {
        Object[] value = new Object[] {Id, showName, date, seats, user};
           System.out.println("enrtCurrentdate---"+date);
          m_ShowExtInsert.exec(value);
    }

    
    public final void execChangePassword(Object[] userdata) throws BasicException {
        m_changepassword.exec(userdata);
    }
    
    public final void resetResourcesCache() {
        resourcescache = new HashMap<String, byte[]>();      
    }
    
    private final byte[] getResource(String name) {

        byte[] resource;
        
        resource = resourcescache.get(name);
        
        if (resource == null) {       
            // Primero trato de obtenerlo de la tabla de recursos
            try {
                resource = (byte[]) m_resourcebytes.find(name);
                resourcescache.put(name, resource);
            } catch (BasicException e) {
                resource = null;
            }
        }
        
        return resource;
    }
    
    public final void setResource(String name, int type, byte[] data) {
        
        Object[] value = new Object[] {UUID.randomUUID().toString(), name, new Integer(type), data};
        try {
            if (m_resourcebytesupdate.exec(value) == 0) {
                m_resourcebytesinsert.exec(value);
            }
            resourcescache.put(name, data);
        } catch (BasicException e) {
        }
    }
    
    public final void setResourceAsBinary(String sName, byte[] data) {
        setResource(sName, 2, data);
    }
    
    public final byte[] getResourceAsBinary(String sName) {
        return getResource(sName);
    }
    
    public final String getResourceAsText(String sName) {
        return Formats.BYTEA.formatValue(getResource(sName));
    }
    
    public final String getResourceAsXML(String sName) {
        return Formats.BYTEA.formatValue(getResource(sName));
    }    
    
    public final BufferedImage getResourceAsImage(String sName) {
        try {
            byte[] img = getResource(sName); // , ".png"
            return img == null ? null : ImageIO.read(new ByteArrayInputStream(img));
        } catch (IOException e) {
            return null;
        }
    }
    
    public final void setResourceAsProperties(String sName, Properties p) {
        if (p == null) {
            setResource(sName, 0, null); // texto
        } else {
            try {
                ByteArrayOutputStream o = new ByteArrayOutputStream();
                p.storeToXML(o, AppLocal.APP_NAME, "UTF8");
                setResource(sName, 0, o.toByteArray()); // El texto de las propiedades   
            } catch (IOException e) { // no deberia pasar nunca
            }            
        }
    }
    
    public final Properties getResourceAsProperties(String sName) {
        
        Properties p = new Properties();
        try {
            byte[] img = getResourceAsBinary(sName);
            if (img != null) {
                p.loadFromXML(new ByteArrayInputStream(img));
            }
        } catch (IOException e) {
        }
        return p;
    }

    public final int getSequenceCash(String host) throws BasicException {
        Integer i = (Integer) m_sequencecash.find(host);
        return (i == null) ? 0 : i.intValue();
    }
      public final int getSequenceShow(String host) throws BasicException {
        Integer i = (Integer) m_sequenceshow.find(host);
        return (i == null) ? 0 : i.intValue();
    }

    public final Object[] findActiveCash(String sActiveCashIndex) throws BasicException {
        return (Object[]) m_activecash.find(sActiveCashIndex);
    }
    
    public final void execInsertCash(Object[] cash) throws BasicException {
        m_insertcash.exec(cash);
    } 
    
    public final String findLocationName(String iLocation) throws BasicException {
        return (String) m_locationfind.find(iLocation);
    }
    public final Object[] findActiveShow(String sActiveCashIndex) throws BasicException {
        return (Object[]) m_activeShow.find(sActiveCashIndex);
    }
    public final void execInsertShow(Object[] cash) throws BasicException {
        m_insertShow.exec(cash);
    }



}
