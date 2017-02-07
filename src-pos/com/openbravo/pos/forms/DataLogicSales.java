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

import com.openbravo.pos.ticket.CategoryInfo;
import com.openbravo.pos.ticket.ProductInfoExt;
import com.openbravo.pos.ticket.TaxInfo;
import com.openbravo.pos.ticket.TicketInfo;
import com.openbravo.pos.ticket.TicketLineInfo;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import com.openbravo.data.loader.*;
import com.openbravo.format.Formats;
import com.openbravo.basic.BasicException;
import com.openbravo.data.model.Field;
import com.openbravo.data.model.Row;
import com.openbravo.pos.customers.CustomerInfoExt;
import com.openbravo.pos.inventory.AttributeSetInfo;
import com.openbravo.pos.inventory.TaxCustCategoryInfo;
import com.openbravo.pos.inventory.LocationInfo;
import com.openbravo.pos.inventory.MovementReason;
import com.openbravo.pos.inventory.TaxCategoryInfo;
import com.openbravo.pos.mant.FloorsInfo;
import com.openbravo.pos.panels.CloseshowInfo;
import com.openbravo.pos.panels.showInfo;
import com.openbravo.pos.panels.ticketCategoryInfo;
import com.openbravo.pos.payment.PaymentInfo;
import com.openbravo.pos.payment.PaymentInfoTicket;
import com.openbravo.pos.sales.CurrentSeatInfo;
import com.openbravo.pos.ticket.FindTicketsInfo;
import com.openbravo.pos.ticket.TicketTaxInfo;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author adrianromero
 */
public class DataLogicSales extends BeanFactoryDataSingle {

    protected Session s;

    protected Datas[] auxiliarDatas;
    protected Datas[] stockdiaryDatas;
    // protected Datas[] productcatDatas;
    protected Datas[] paymenttabledatas;
    protected Datas[] stockdatas;

    protected Row productsRow;

    /** Creates a new instance of SentenceContainerGeneric */
    public DataLogicSales() {
        stockdiaryDatas = new Datas[] {Datas.STRING, Datas.TIMESTAMP, Datas.INT, Datas.STRING, Datas.STRING, Datas.STRING, Datas.DOUBLE, Datas.DOUBLE};
        paymenttabledatas = new Datas[] {Datas.STRING, Datas.STRING, Datas.TIMESTAMP, Datas.STRING, Datas.STRING, Datas.DOUBLE};
        stockdatas = new Datas[] {Datas.STRING, Datas.STRING, Datas.STRING, Datas.DOUBLE, Datas.DOUBLE, Datas.DOUBLE};
        auxiliarDatas = new Datas[] {Datas.STRING, Datas.STRING, Datas.STRING, Datas.STRING, Datas.STRING, Datas.STRING};

        productsRow = new Row(
                new Field("ID", Datas.STRING, Formats.STRING),
                new Field(AppLocal.getIntString("label.prodref"), Datas.STRING, Formats.STRING, true, true, true),
                new Field(AppLocal.getIntString("label.prodbarcode"), Datas.STRING, Formats.STRING, false, true, true),
                new Field(AppLocal.getIntString("label.prodname"), Datas.STRING, Formats.STRING, true, true, true),
                new Field("ISCOM", Datas.BOOLEAN, Formats.BOOLEAN),
                new Field("ISSCALE", Datas.BOOLEAN, Formats.BOOLEAN),
                new Field(AppLocal.getIntString("label.prodpricebuy"), Datas.DOUBLE, Formats.CURRENCY, false, true, true),
                new Field(AppLocal.getIntString("label.prodpricesell"), Datas.DOUBLE, Formats.CURRENCY, false, true, true),
                new Field(AppLocal.getIntString("label.prodcategory"), Datas.STRING, Formats.STRING, false, false, true),
                new Field(AppLocal.getIntString("label.taxcategory"), Datas.STRING, Formats.STRING, false, false, true),
                new Field(AppLocal.getIntString("label.attributeset"), Datas.STRING, Formats.STRING, false, false, true),
                new Field("IMAGE", Datas.IMAGE, Formats.NULL),
                new Field("STOCKCOST", Datas.DOUBLE, Formats.CURRENCY),
                new Field("STOCKVOLUME", Datas.DOUBLE, Formats.DOUBLE),
                new Field("ISCATALOG", Datas.BOOLEAN, Formats.BOOLEAN),
                new Field("CATORDER", Datas.INT, Formats.INT),
                //new Field("GROUP", Datas.STRING, Formats.STRING),
                new Field(AppLocal.getIntString("label.prodminamt"), Datas.DOUBLE, Formats.CURRENCY, false, true, true),
              //  new Field(AppLocal.getIntString("label.noofpeople"), Datas.INT, Formats.INT),
                new Field("PROPERTIES", Datas.BYTES, Formats.NULL));

    }

    public void init(Session s){
        this.s = s;
    }

    public final Row getProductsRow() {
        return productsRow;
    }

    // Utilidades de productos
    public final ProductInfoExt getProductInfo(String id) throws BasicException {
        return (ProductInfoExt) new PreparedSentence(s
            , "SELECT ID, REFERENCE, CODE, NAME, ISCOM, ISSCALE, PRICEBUY, PRICESELL, TAXCAT, CATEGORY, ATTRIBUTESET_ID, IMAGE, ATTRIBUTES, MINAMOUNT, TICKETGROUP, NOOFPEOPLE, TICKETDISCOUNT " +
              "FROM PRODUCTS WHERE ID = ?"
            , SerializerWriteString.INSTANCE
            , ProductInfoExt.getSerializerRead()).find(id);
    }
    public final ProductInfoExt getProductInfoByCode(String sCode) throws BasicException {
        return (ProductInfoExt) new PreparedSentence(s
            , "SELECT ID, REFERENCE, CODE, NAME, ISCOM, ISSCALE, PRICEBUY, PRICESELL, TAXCAT, CATEGORY, ATTRIBUTESET_ID, IMAGE, ATTRIBUTES  MINAMOUNT, TICKETGROUP, NOOFPEOPLE, TICKETDISCOUNT " +
              "FROM PRODUCTS WHERE CODE = ?"
            , SerializerWriteString.INSTANCE
            , ProductInfoExt.getSerializerRead()).find(sCode);
    }
    public final ProductInfoExt getProductInfoByReference(String sReference) throws BasicException {
        return (ProductInfoExt) new PreparedSentence(s
            , "SELECT ID, REFERENCE, CODE, NAME, ISCOM, ISSCALE, PRICEBUY, PRICESELL, TAXCAT, CATEGORY, ATTRIBUTESET_ID, IMAGE, ATTRIBUTES  MINAMOUNT, TICKETGROUP, NOOFPEOPLE, TICKETDISCOUNT " +
              "FROM PRODUCTS WHERE REFERENCE = ?"
            , SerializerWriteString.INSTANCE
            , ProductInfoExt.getSerializerRead()).find(sReference);
    }

    // Catalogo de productos
    public final List<CategoryInfo> getRootCategories() throws BasicException {
        return new PreparedSentence(s
            , "SELECT ID, NAME, IMAGE FROM CATEGORIES WHERE PARENTID IS NULL ORDER BY NAME"
            , null
            , CategoryInfo.getSerializerRead()).list();
    }
    public final List<CategoryInfo> getSubcategories(String category) throws BasicException  {
        return new PreparedSentence(s
            , "SELECT ID, NAME, IMAGE FROM CATEGORIES WHERE PARENTID = ? ORDER BY NAME"
            , SerializerWriteString.INSTANCE
            , CategoryInfo.getSerializerRead()).list(category);
    }
    public List<ProductInfoExt> getProductCatalog(String category) throws BasicException  {
        return new PreparedSentence(s
            , "SELECT P.ID, P.REFERENCE, P.CODE, P.NAME, P.ISCOM, P.ISSCALE, P.PRICEBUY, P.PRICESELL, P.TAXCAT, P.CATEGORY, P.ATTRIBUTESET_ID, P.IMAGE, P.ATTRIBUTES,  P.MINAMOUNT, P.TICKETGROUP, P.NOOFPEOPLE, P.TICKETDISCOUNT " +
              "FROM PRODUCTS P, PRODUCTS_CAT O WHERE P.ID = O.PRODUCT AND P.CATEGORY = ? AND P.ACTIVE='Y' " +
              "ORDER BY O.CATORDER, P.NAME"
            , SerializerWriteString.INSTANCE
            , ProductInfoExt.getSerializerRead()).list(category);
    }
    public List<ProductInfoExt> getProductGroupCatalog(String category, String ticketGroup) throws BasicException  {
        return new PreparedSentence(s
            , "SELECT P.ID, P.REFERENCE, P.CODE, P.NAME, P.ISCOM, P.ISSCALE, P.PRICEBUY, P.PRICESELL, P.TAXCAT, P.CATEGORY, P.ATTRIBUTESET_ID, P.IMAGE, P.ATTRIBUTES, P.MINAMOUNT, P.TICKETGROUP, P.NOOFPEOPLE, P.TICKETDISCOUNT " +
              "FROM PRODUCTS P, PRODUCTS_CAT O WHERE P.ID = O.PRODUCT AND P.TICKETGROUP='"+ticketGroup+"' AND P.CATEGORY = ? AND P.ACTIVE='Y' " +
              "ORDER BY O.CATORDER, P.NAME"
            , SerializerWriteString.INSTANCE
            , ProductInfoExt.getSerializerRead()).list(category);
    }

    public List<ProductInfoExt> getProductComments(String id) throws BasicException {
        return new PreparedSentence(s
            , "SELECT P.ID, P.REFERENCE, P.CODE, P.NAME, P.ISCOM, P.ISSCALE, P.PRICEBUY, P.PRICESELL, P.TAXCAT, P.CATEGORY, P.ATTRIBUTESET_ID, P.IMAGE, P.ATTRIBUTES, P.MINAMOUNT, P.TICKETGROUP, P.NOOFPEOPLE, P.TICKETDISCOUNT " +
              "FROM PRODUCTS P, PRODUCTS_CAT O, PRODUCTS_COM M WHERE P.ID = O.PRODUCT AND P.ID = M.PRODUCT2 AND M.PRODUCT = ? " +
              "AND P.ISCOM = " + s.DB.TRUE() + " " +
              "ORDER BY O.CATORDER, P.NAME"
            , SerializerWriteString.INSTANCE
            , ProductInfoExt.getSerializerRead()).list(id);
    }
  
    // Products list
    public final SentenceList getProductList() {
        return new StaticSentence(s
            , new QBFBuilder(
              "SELECT ID, REFERENCE, CODE, NAME, ISCOM, ISSCALE, PRICEBUY, PRICESELL, TAXCAT, CATEGORY, ATTRIBUTESET_ID, IMAGE, ATTRIBUTES, MINAMOUNT, TICKETGROUP,NOOFPEOPLE, TICKETDISCOUNT   " +
              "FROM PRODUCTS WHERE ACTIVE='Y' AND ?(QBF_FILTER) ORDER BY REFERENCE", new String[] {"NAME", "CODE"})
            , new SerializerWriteBasic(new Datas[] {Datas.OBJECT, Datas.STRING, Datas.OBJECT, Datas.STRING})
            , ProductInfoExt.getSerializerRead());
    }
    
    // Products list
    public SentenceList getProductListNormal() {
        return new StaticSentence(s
            , new QBFBuilder(
              "SELECT ID, REFERENCE, CODE, NAME, ISCOM, ISSCALE, PRICEBUY, PRICESELL, TAXCAT, CATEGORY, ATTRIBUTESET_ID, IMAGE, ATTRIBUTES, MINAMOUNT, TICKETGROUP, NOOFPEOPLE, TICKETDISCOUNT " +
              "FROM PRODUCTS WHERE ACTIVE='Y' AND ISCOM = " + s.DB.FALSE() + " AND ?(QBF_FILTER) ORDER BY REFERENCE", new String[] {"NAME", "PRICEBUY", "PRICESELL", "CATEGORY", "CODE"})
            , new SerializerWriteBasic(new Datas[] {Datas.OBJECT, Datas.STRING, Datas.OBJECT, Datas.DOUBLE, Datas.OBJECT, Datas.DOUBLE, Datas.OBJECT, Datas.STRING, Datas.OBJECT, Datas.STRING})
            , ProductInfoExt.getSerializerRead());
    }
    
    //Auxiliar list for a filter
    public SentenceList getProductListAuxiliar() {
         return new StaticSentence(s
            , new QBFBuilder(
              "SELECT ID, REFERENCE, CODE, NAME, ISCOM, ISSCALE, PRICEBUY, PRICESELL, TAXCAT, CATEGORY, ATTRIBUTESET_ID, IMAGE, ATTRIBUTES " +
              "FROM PRODUCTS WHERE ISCOM = " + s.DB.TRUE() + " AND ?(QBF_FILTER) ORDER BY REFERENCE", new String[] {"NAME", "PRICEBUY", "PRICESELL", "CATEGORY", "CODE"})
            , new SerializerWriteBasic(new Datas[] {Datas.OBJECT, Datas.STRING, Datas.OBJECT, Datas.DOUBLE, Datas.OBJECT, Datas.DOUBLE, Datas.OBJECT, Datas.STRING, Datas.OBJECT, Datas.STRING})
            , ProductInfoExt.getSerializerRead());
    }
    
    //Tickets and Receipt list
    public SentenceList getTicketsList() {
         Date convertToDate = null;
        String advanceDate = null;
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat time = new SimpleDateFormat("HH:mm");
        String CurrentDateTime = format.format(new Date()).toString();
        advanceDate = dateFormat.format(new Date()).toString();

        try {
            convertToDate = format.parse(CurrentDateTime);
        } catch (ParseException ex) {
            Logger.getLogger(DataLogicSales.class.getName()).log(Level.SEVERE, null, ex);
        }

         return new StaticSentence(s
            , new QBFBuilder(
            "SELECT T.TICKETID, T.TICKETTYPE, R.DATENEW, P.NAME, C.NAME, SUM(PM.TOTAL), T.SHOWNAME, R.ADVANCEDATE "+
            "FROM RECEIPTS R JOIN TICKETS T ON R.ID = T.ID AND T.REFUND='N' LEFT OUTER JOIN PAYMENTS PM ON R.ID = PM.RECEIPT LEFT OUTER JOIN CUSTOMERS C ON C.ID = T.CUSTOMER LEFT OUTER JOIN PEOPLE P ON T.PERSON = P.ID " +
            "WHERE  R.DATENEW >=(SELECT DATESTART FROM CLOSEDSHOW WHERE DATEEND IS NULL) OR R.ADVANCEDATE>='"+advanceDate+"' AND ?(QBF_FILTER) GROUP BY T.ID, T.TICKETID, T.TICKETTYPE, R.DATENEW, P.NAME, C.NAME ORDER BY R.DATENEW DESC, T.TICKETID", new String[] {"T.TICKETID", "T.TICKETTYPE", "PM.TOTAL", "R.DATENEW", "P.NAME"})
            , new SerializerWriteBasic(new Datas[] {Datas.OBJECT, Datas.INT, Datas.OBJECT, Datas.INT, Datas.OBJECT, Datas.DOUBLE, Datas.OBJECT, Datas.TIMESTAMP, Datas.OBJECT, Datas.STRING})
            , new SerializerReadClass(FindTicketsInfo.class));

      
    }
    
    //User list
    public final SentenceList getUserList() {
        return new StaticSentence(s
            , "SELECT ID, NAME FROM PEOPLE ORDER BY NAME"
            , null
            , new SerializerRead() { public Object readValues(DataRead dr) throws BasicException {
                return new TaxCategoryInfo(
                        dr.getString(1), 
                        dr.getString(2));
            }});
    }
   
    // Listados para combo
    public final SentenceList getTaxList() {
        return new StaticSentence(s
            , "SELECT ID, NAME, CATEGORY, CUSTCATEGORY, PARENTID, RATE, RATECASCADE, RATEORDER FROM TAXES ORDER BY NAME"
            , null
            , new SerializerRead() { public Object readValues(DataRead dr) throws BasicException {
                return new TaxInfo(
                        dr.getString(1), 
                        dr.getString(2),
                        dr.getString(3),
                        dr.getString(4),
                        dr.getString(5),
                        dr.getDouble(6).doubleValue(),
                        dr.getBoolean(7).booleanValue(),
                        dr.getInt(8));
            }});
    }
    public final SentenceList getCategoriesList() {
        return new StaticSentence(s
            , "SELECT ID, NAME, IMAGE FROM CATEGORIES ORDER BY NAME"
            , null
            , CategoryInfo.getSerializerRead());
    }
    public final SentenceList getTaxCustCategoriesList() {
        return new StaticSentence(s
            , "SELECT ID, NAME FROM TAXCUSTCATEGORIES ORDER BY NAME"
            , null
            , new SerializerRead() { public Object readValues(DataRead dr) throws BasicException {
                return new TaxCustCategoryInfo(dr.getString(1), dr.getString(2));
            }});
    }
    public final SentenceList getTaxCategoriesList() {
        return new StaticSentence(s
            , "SELECT ID, NAME FROM TAXCATEGORIES ORDER BY NAME"
            , null
            , new SerializerRead() { public Object readValues(DataRead dr) throws BasicException {
                return new TaxCategoryInfo(dr.getString(1), dr.getString(2));
            }});
    }
    public final SentenceList getAttributeSetList() {
        return new StaticSentence(s
            , "SELECT ID, NAME FROM ATTRIBUTESET ORDER BY NAME"
            , null
            , new SerializerRead() { public Object readValues(DataRead dr) throws BasicException {
                return new AttributeSetInfo(dr.getString(1), dr.getString(2));
            }});
    }
    public final SentenceList getLocationsList() {
        return new StaticSentence(s
            , "SELECT ID, NAME, ADDRESS FROM LOCATIONS ORDER BY NAME"
            , null
            , new SerializerReadClass(LocationInfo.class));
    }
    public final SentenceList getFloorsList() {
        return new StaticSentence(s
            , "SELECT ID, NAME FROM FLOORS ORDER BY NAME"
            , null
            , new SerializerReadClass(FloorsInfo.class));
    }

    public CustomerInfoExt findCustomerExt(String card) throws BasicException {
        return (CustomerInfoExt) new PreparedSentence(s
                , "SELECT ID, TAXID, SEARCHKEY, NAME, CARD, TAXCATEGORY, NOTES, MAXDEBT, VISIBLE, CURDATE, CURDEBT" +
                  ", FIRSTNAME, LASTNAME, EMAIL, PHONE, PHONE2, FAX" +
                  ", ADDRESS, ADDRESS2, POSTAL, CITY, REGION, COUNTRY" +
                  " FROM CUSTOMERS WHERE CARD = ? AND VISIBLE = " + s.DB.TRUE()
                , SerializerWriteString.INSTANCE
                , new CustomerExtRead()).find(card);
    }

    public CustomerInfoExt loadCustomerExt(String id) throws BasicException {
        return (CustomerInfoExt) new PreparedSentence(s
                , "SELECT ID, TAXID, SEARCHKEY, NAME, CARD, TAXCATEGORY, NOTES, MAXDEBT, VISIBLE, CURDATE, CURDEBT" +
                  ", FIRSTNAME, LASTNAME, EMAIL, PHONE, PHONE2, FAX" +
                  ", ADDRESS, ADDRESS2, POSTAL, CITY, REGION, COUNTRY" +
                " FROM CUSTOMERS WHERE ID = ?"
                , SerializerWriteString.INSTANCE
                , new CustomerExtRead()).find(id);
    }

    public final boolean isCashActive(String id) throws BasicException {

        return new PreparedSentence(s,
                "SELECT MONEY FROM CLOSEDCASH WHERE DATEEND IS NULL AND MONEY = ?",
                SerializerWriteString.INSTANCE,
                SerializerReadString.INSTANCE).find(id)
            != null;
    }

    public final TicketInfo loadTicket(final int tickettype, final int ticketid) throws BasicException {
        TicketInfo ticket = (TicketInfo) new PreparedSentence(s
                , "SELECT T.ID, T.TICKETTYPE, T.TICKETID, R.DATENEW, R.MONEY, R.ATTRIBUTES, P.ID, P.NAME, T.CUSTOMER, T.SHOWNAME, R.ADVANCEDATE FROM RECEIPTS R JOIN TICKETS T ON R.ID = T.ID LEFT OUTER JOIN PEOPLE P ON T.PERSON = P.ID WHERE T.TICKETTYPE = ? AND T.TICKETID = ?"
                , SerializerWriteParams.INSTANCE
                , new SerializerReadClass(TicketInfo.class))
                .find(new DataParams() { public void writeValues() throws BasicException {
                    setInt(1, tickettype);
                    setInt(2, ticketid);
                }});
        if (ticket != null) {

            String customerid = ticket.getCustomerId();
            ticket.setCustomer(customerid == null
                    ? null
                    : loadCustomerExt(customerid));

            ticket.setLines(new PreparedSentence(s
                , "SELECT L.TICKET, L.LINE, L.PRODUCT, L.ATTRIBUTESETINSTANCE_ID, L.UNITS, P.PRICESELL, T.ID, T.NAME, T.CATEGORY, T.CUSTCATEGORY, T.PARENTID, T.RATE, T.RATECASCADE, T.RATEORDER, L.ATTRIBUTES, P.MINAMOUNT, P.TICKETGROUP, P.NOOFPEOPLE, P.NAME, P.TICKETDISCOUNT " +
                  "FROM TICKETLINES L, TAXES T, PRODUCTS P WHERE L.TAXID = T.ID AND P.ID=L.PRODUCT AND L.TICKET = ? ORDER BY L.LINE"
                , SerializerWriteString.INSTANCE
                , new SerializerReadClass(TicketLineInfo.class)).list(ticket.getId()));
            ticket.setPayments(new PreparedSentence(s
                , "SELECT PAYMENT, TOTAL, TRANSID FROM PAYMENTS WHERE RECEIPT = ?"
                , SerializerWriteString.INSTANCE
                , new SerializerReadClass(PaymentInfoTicket.class)).list(ticket.getId()));
        }
        return ticket;
    }

    public final synchronized void saveTicket(final TicketInfo ticket, final String location, final String show, final int availableSeats, final String advBook, final String advDate, final String refund) throws BasicException {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
       
        Date createdDate = new Date();
        String currentTime = sdf.format(createdDate);
        final String sysDate;
        if(!advDate.equals("")){
        sysDate = advDate;
            
        }else{
            sysDate = df.format(createdDate);
        }
        final String showId = getShowId(currentTime);
        final String cashId = getCashId(currentTime);
        Transaction t = new Transaction(s) {

            public Object transact() throws BasicException {

                // Set Receipt Id
                if (ticket.getTicketId() == 0) {
                    switch (ticket.getTicketType()) {
                        case TicketInfo.RECEIPT_NORMAL:
                            ticket.setTicketId(getNextTicketIndex().intValue());
                          
                            break;
                        case TicketInfo.RECEIPT_REFUND:
                            final int ticketId = ticket.getRefundId();
                               SentenceExec updateTicket = new PreparedSentence(s, "UPDATE TICKETS SET REFUND = ? WHERE TICKETID=?", SerializerWriteParams.INSTANCE);
                               updateTicket.exec(new DataParams() {

                                @Override
                                    public void writeValues() throws BasicException {
                                        setString(1, "Y");
                                        setInt(2, ticketId);

                                    }
                                });
                           
                            ticket.setTicketId(getNextTicketRefundIndex().intValue());
                            break;
                        case TicketInfo.RECEIPT_PAYMENT:
                            ticket.setTicketId(getNextTicketPaymentIndex().intValue());
                            break;
                        default:
                            throw new BasicException();
                    }
                }
                // new receipt
                new PreparedSentence(s
                    , "INSERT INTO RECEIPTS (ID, MONEY, DATENEW, MONEYSHOW, ATTRIBUTES, ADVANCEBOOKING,ADVANCEDATE) VALUES (?, ?, ?, ?, ?, ?, ?)"
                    , SerializerWriteParams.INSTANCE
                    ).exec(new DataParams() { public void writeValues() throws BasicException {
                        setString(1, ticket.getId());
                     //   setString(2, ticket.getActiveCash());
                        setString(2, cashId);
                        
                        setTimestamp(3, ticket.getDate());
                      // setString(4, ticket.getActiveShow());
                        setString(4, showId);

                        try {
                            ByteArrayOutputStream o = new ByteArrayOutputStream();
                            ticket.getProperties().storeToXML(o, AppLocal.APP_NAME, "UTF-8");
                            setBytes(5, o.toByteArray());
                        } catch (IOException e) {
                            setBytes(5, null);
                        }
                        setString(6, advBook);
                        setString(7, advDate);

                    }});
                    final double discountTotal;
                    if((ticket.getTicketType()==TicketInfo.RECEIPT_REFUND)){
                        discountTotal =0;
                    }else{
                            discountTotal=ticket.getDiscountValue();
                    }
                // new ticket
                new PreparedSentence(s
                    , "INSERT INTO TICKETS (ID, TICKETTYPE, TICKETID, PERSON, CUSTOMER, SHOWNAME, CANCELLATIONFEE,DISCOUNT,REFUND) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)"
                    , SerializerWriteParams.INSTANCE
                    ).exec(new DataParams() { public void writeValues() throws BasicException {
                        setString(1, ticket.getId());
                        setInt(2, ticket.getTicketType());
                        setInt(3, ticket.getTicketId());
                        setString(4, ticket.getUser().getId());
                        setString(5, ticket.getCustomerId());
                        setString(6, show);
                        setDouble(7, ticket.getCancelFee());
                        setDouble(8, discountTotal);
                        setString(9, refund);
                    }});
     for (final TicketLineInfo l : ticket.getLines()) {
     final double price;
 
     if((ticket.getTicketType()==TicketInfo.RECEIPT_REFUND) ){
        if(((-1)*ticket.getDisArticlesCount()>=l.getDiscountQty()) && l.getTicketDiscount().equals("Y")){
                 price = l.getSubValue() -(l.getMultiply()* l.getDiscountPrice());
        }else{
             price = l.getSubValue();
        }
     }else{
     if((ticket.getDisArticlesCount()>=l.getDiscountQty()) && l.getTicketDiscount().equals("Y")){
         price = l.getSubValue() - (l.getMultiply()* l.getDiscountPrice());
     }else{
         price = l.getSubValue();
     }
     }
                  //  ticketlineinsert.exec(l);
                    new PreparedSentence(s
                    , "INSERT INTO TICKETLINES (TICKET, LINE, PRODUCT, ATTRIBUTESETINSTANCE_ID, UNITS, PRICE, TAXID, ATTRIBUTES) VALUES (?, ?, ?, ?, ?, ?, ?, ?)"
                    , SerializerWriteParams.INSTANCE
                    ).exec(new DataParams() { public void writeValues() throws BasicException {
                       setString(1, ticket.getId());
                        setInt(2, l.getTicketLine());
                        setString(3,l.getProductID());
                        setString(4, l.getProductAttSetInstId());
                        setDouble(5, l.getMultiply());
                        setDouble(6, price);
                        setString(7, l.getTaxInfo().getId());
                        setString(8, l.getProductAttSetId());

                    }});
                SentenceExec ticketlineinsert = new PreparedSentence(s
                    , "INSERT INTO TICKETLINES (TICKET, LINE, PRODUCT, ATTRIBUTESETINSTANCE_ID, UNITS, PRICE, TAXID, ATTRIBUTES) VALUES (?, ?, ?, ?, ?, ?, ?, ?)"
                    , SerializerWriteBuilder.INSTANCE);

               
                    if (l.getProductID() != null)  {
                        // update the stock
                        getStockDiaryInsert().exec(new Object[] {
                            UUID.randomUUID().toString(),
                            ticket.getDate(),
                            l.getMultiply() < 0.0
                                ? MovementReason.IN_REFUND.getKey()
                                : MovementReason.OUT_SALE.getKey(),
                            location,
                            l.getProductID(),
                            l.getProductAttSetInstId(),
                            new Double(-l.getMultiply()),
                            new Double(l.getPrice())
                        });
                    }
                }

                SentenceExec paymentinsert = new PreparedSentence(s
                    , "INSERT INTO PAYMENTS (ID, RECEIPT, PAYMENT, TOTAL, TRANSID, RETURNMSG) VALUES (?, ?, ?, ?, ?, ?)"
                    , SerializerWriteParams.INSTANCE);
                
                for (final PaymentInfo p : ticket.getPayments()) {
                    final Double total;
                    if(p.getName().equals("cashrefund") && ticket.getCancelFee()!=0){
                        total = p.getTotal() + ticket.getCancelFee();
                    }else{
                        total = p.getTotal();
                    }
                    paymentinsert.exec(new DataParams() { public void writeValues() throws BasicException {
                        setString(1, UUID.randomUUID().toString());
                        setString(2, ticket.getId());
                        setString(3, p.getName());
                        setDouble(4, total);
                        setString(5, ticket.getTransactionID());
                        setBytes(6, (byte[]) Formats.BYTEA.parseValue(ticket.getReturnMessage()));
                    }});

                    if ("debt".equals(p.getName()) || "debtpaid".equals(p.getName())) {

                        // udate customer fields...
                        ticket.getCustomer().updateCurDebt(p.getTotal(), ticket.getDate());

                        // save customer fields...
                        getDebtUpdate().exec(new DataParams() { public void writeValues() throws BasicException {
                            setDouble(1, ticket.getCustomer().getCurdebt());
                            setTimestamp(2, ticket.getCustomer().getCurdate());
                            setString(3, ticket.getCustomer().getId());
                        }});
                    }
                }

                SentenceExec taxlinesinsert = new PreparedSentence(s
                        , "INSERT INTO TAXLINES (ID, RECEIPT, TAXID, BASE, AMOUNT)  VALUES (?, ?, ?, ?, ?)"
                        , SerializerWriteParams.INSTANCE);
                if (ticket.getTaxes() != null) {
                    for (final TicketTaxInfo tickettax: ticket.getTaxes()) {
                        taxlinesinsert.exec(new DataParams() { public void writeValues() throws BasicException {
                            setString(1, UUID.randomUUID().toString());
                            setString(2, ticket.getId());
                            setString(3, tickettax.getTaxInfo().getId());
                            setDouble(4, tickettax.getSubTotal());
                            setDouble(5, tickettax.getTax());
                        }});
                    }
                }
 
                SentenceExec updateSeats = new PreparedSentence(s, "UPDATE AVAILABLESEATS SET SEATS = ? WHERE SHOWNAME=? AND DATE = ?", SerializerWriteParams.INSTANCE);
                updateSeats.exec(new DataParams() {

                @Override
                    public void writeValues() throws BasicException {
                        setInt(1, availableSeats);
                        setString(2, show);
                        setString(3, sysDate);

                    }
                 });

                return null;
            }
        };
        t.execute();
    }

    public final void deleteTicket(final TicketInfo ticket, final String location) throws BasicException {

        Transaction t = new Transaction(s) {
            public Object transact() throws BasicException {

                // update the inventory
                Date d = new Date();
                for (int i = 0; i < ticket.getLinesCount(); i++) {
                    if (ticket.getLine(i).getProductID() != null)  {
                        // Hay que actualizar el stock si el hay producto
                        getStockDiaryInsert().exec( new Object[] {
                            UUID.randomUUID().toString(),
                            d,
                            ticket.getLine(i).getMultiply() >= 0.0
                                ? MovementReason.IN_REFUND.getKey()
                                : MovementReason.OUT_SALE.getKey(),
                            location,
                            ticket.getLine(i).getProductID(),
                            ticket.getLine(i).getProductAttSetInstId(),
                            new Double(ticket.getLine(i).getMultiply()),
                            new Double(ticket.getLine(i).getPrice())
                        });
                    }
                }

                // update customer debts
                for (PaymentInfo p : ticket.getPayments()) {
                    if ("debt".equals(p.getName()) || "debtpaid".equals(p.getName())) {

                        // udate customer fields...
                        ticket.getCustomer().updateCurDebt(-p.getTotal(), ticket.getDate());

                         // save customer fields...
                        getDebtUpdate().exec(new DataParams() { public void writeValues() throws BasicException {
                            setDouble(1, ticket.getCustomer().getCurdebt());
                            setTimestamp(2, ticket.getCustomer().getCurdate());
                            setString(3, ticket.getCustomer().getId());
                        }});
                    }
                }

                // and delete the receipt
                new StaticSentence(s
                    , "DELETE FROM TAXLINES WHERE RECEIPT = ?"
                    , SerializerWriteString.INSTANCE).exec(ticket.getId());
                new StaticSentence(s
                    , "DELETE FROM PAYMENTS WHERE RECEIPT = ?"
                    , SerializerWriteString.INSTANCE).exec(ticket.getId());
                new StaticSentence(s
                    , "DELETE FROM TICKETLINES WHERE TICKET = ?"
                    , SerializerWriteString.INSTANCE).exec(ticket.getId());
                new StaticSentence(s
                    , "DELETE FROM TICKETS WHERE ID = ?"
                    , SerializerWriteString.INSTANCE).exec(ticket.getId());
                new StaticSentence(s
                    , "DELETE FROM RECEIPTS WHERE ID = ?"
                    , SerializerWriteString.INSTANCE).exec(ticket.getId());
                return null;
            }
        };
        t.execute();
    }

    public final Integer getNextTicketIndex() throws BasicException {
        return (Integer) s.DB.getSequenceSentence(s, "TICKETSNUM").find();
    }

    public final Integer getNextTicketRefundIndex() throws BasicException {
        return (Integer) s.DB.getSequenceSentence(s, "TICKETSNUM_REFUND").find();
    }

    public final Integer getNextTicketPaymentIndex() throws BasicException {
        return (Integer) s.DB.getSequenceSentence(s, "TICKETSNUM_PAYMENT").find();
    }

    public final SentenceList getProductCatQBF() {
        return new StaticSentence(s
            , new QBFBuilder(
                "SELECT P.ID, P.REFERENCE, P.CODE, P.NAME, P.ISCOM, P.ISSCALE, P.PRICEBUY, P.PRICESELL, P.CATEGORY, P.TAXCAT, P.ATTRIBUTESET_ID, P.IMAGE, P.STOCKCOST, P.STOCKVOLUME, CASE WHEN C.PRODUCT IS NULL THEN " + s.DB.FALSE() + " ELSE " + s.DB.TRUE() + " END, C.CATORDER, P.ATTRIBUTES, P.MINAMOUNT " +
                "FROM PRODUCTS P LEFT OUTER JOIN PRODUCTS_CAT C ON P.ID = C.PRODUCT " +
                "WHERE ?(QBF_FILTER) " +
                "ORDER BY P.REFERENCE", new String[] {"P.NAME", "P.PRICEBUY", "P.PRICESELL", "P.CATEGORY", "P.CODE"})
            , new SerializerWriteBasic(new Datas[] {Datas.OBJECT, Datas.STRING, Datas.OBJECT, Datas.DOUBLE, Datas.OBJECT, Datas.DOUBLE, Datas.OBJECT, Datas.STRING, Datas.OBJECT, Datas.STRING})
            , productsRow.getSerializerRead());
    }

    public final SentenceExec getProductCatInsert() {
        return new SentenceExecTransaction(s) {
            public int execInTransaction(Object params) throws BasicException {
                Object[] values = (Object[]) params;
                int i = new PreparedSentence(s
                    , "INSERT INTO PRODUCTS (ID, CODE, NAME, PRICESELL, CATEGORY, TAXCAT) VALUES (?, ?, ?, ?, ?, ?, ?, ?)"
                    , new SerializerWriteBasicExt(productsRow.getDatas(), new int[]{0, 2, 3, 7, 8, 9, 17, 18 })).exec(params);
             //   if (i > 0 && ((Boolean)values[14]).booleanValue()) {
                    return new PreparedSentence(s
                        , "INSERT INTO PRODUCTS_CAT (PRODUCT, CATORDER) VALUES (?, ?)"
                        , new SerializerWriteBasicExt(productsRow.getDatas(), new int[] {0, 15})).exec(params);
              //  } else {
                  //  return i;
               // }
            }
        };
    }

    public final SentenceExec getProductCatUpdate() {
        return new SentenceExecTransaction(s) {
            public int execInTransaction(Object params) throws BasicException {
                Object[] values = (Object[]) params;
                return new PreparedSentence(s
                    , "UPDATE PRODUCTS SET ID = ?, CODE = ?, NAME = ?,  PRICESELL = ?, CATEGORY = ?, TAXCAT = ? WHERE ID = ?"
                    , new SerializerWriteBasicExt(productsRow.getDatas(), new int[]{0, 2, 3, 7, 8, 9, 0})).exec(params);
                /*if (i > 0) {
//                    if (((Boolean)values[14]).booleanValue()) {
                        if (new PreparedSentence(s
                                , "UPDATE PRODUCTS_CAT SET CATORDER = ? WHERE PRODUCT = ?"
                                , new SerializerWriteBasicExt(productsRow.getDatas(), new int[] {15, 0})).exec(params) == 0) {
                            new PreparedSentence(s
                                , "INSERT INTO PRODUCTS_CAT (PRODUCT, CATORDER) VALUES (?, ?)"
                                , new SerializerWriteBasicExt(productsRow.getDatas(), new int[] {0, 15})).exec(params);
                        }
                   /* } else {
                        new PreparedSentence(s
                            , "DELETE FROM PRODUCTS_CAT WHERE PRODUCT = ?"
                            , new SerializerWriteBasicExt(productsRow.getDatas(), new int[] {0})).exec(params);
                    }
                }*/
                //return i;
            }
        };
    }

    public final SentenceExec getProductCatDelete() {
        return new SentenceExecTransaction(s) {
            public int execInTransaction(Object params) throws BasicException {
                new PreparedSentence(s
                    , "DELETE FROM PRODUCTS_CAT WHERE PRODUCT = ?"
                    , new SerializerWriteBasicExt(productsRow.getDatas(), new int[] {0})).exec(params);
                return new PreparedSentence(s
                    , "DELETE FROM PRODUCTS WHERE ID = ?"
                    , new SerializerWriteBasicExt(productsRow.getDatas(), new int[] {0})).exec(params);
            }
        };
    }

    public final SentenceExec getDebtUpdate() {

        return new PreparedSentence(s
                , "UPDATE CUSTOMERS SET CURDEBT = ?, CURDATE = ? WHERE ID = ?"
                , SerializerWriteParams.INSTANCE);
    }

    public final SentenceExec getStockDiaryInsert() {
        return new SentenceExecTransaction(s) {
            public int execInTransaction(Object params) throws BasicException {
                int updateresult = ((Object[]) params)[5] == null // si ATTRIBUTESETINSTANCE_ID is null
                    ? new PreparedSentence(s
                        , "UPDATE STOCKCURRENT SET UNITS = (UNITS + ?) WHERE LOCATION = ? AND PRODUCT = ? AND ATTRIBUTESETINSTANCE_ID IS NULL"
                        , new SerializerWriteBasicExt(stockdiaryDatas, new int[] {6, 3, 4})).exec(params)
                    : new PreparedSentence(s
                        , "UPDATE STOCKCURRENT SET UNITS = (UNITS + ?) WHERE LOCATION = ? AND PRODUCT = ? AND ATTRIBUTESETINSTANCE_ID = ?"
                        , new SerializerWriteBasicExt(stockdiaryDatas, new int[] {6, 3, 4, 5})).exec(params);

                if (updateresult == 0) {
                    new PreparedSentence(s
                        , "INSERT INTO STOCKCURRENT (LOCATION, PRODUCT, ATTRIBUTESETINSTANCE_ID, UNITS) VALUES (?, ?, ?, ?)"
                        , new SerializerWriteBasicExt(stockdiaryDatas, new int[] {3, 4, 5, 6})).exec(params);
                }
                return new PreparedSentence(s
                    , "INSERT INTO STOCKDIARY (ID, DATENEW, REASON, LOCATION, PRODUCT, ATTRIBUTESETINSTANCE_ID, UNITS, PRICE) VALUES (?, ?, ?, ?, ?, ?, ?, ?)"
                    , new SerializerWriteBasicExt(stockdiaryDatas, new int[] {0, 1, 2, 3, 4, 5, 6, 7})).exec(params);
            }
        };
    }

    public final SentenceExec getStockDiaryDelete() {
        return new SentenceExecTransaction(s) {
            public int execInTransaction(Object params) throws BasicException {
                int updateresult = ((Object[]) params)[5] == null // if ATTRIBUTESETINSTANCE_ID is null
                        ? new PreparedSentence(s
                            , "UPDATE STOCKCURRENT SET UNITS = (UNITS - ?) WHERE LOCATION = ? AND PRODUCT = ? AND ATTRIBUTESETINSTANCE_ID IS NULL"
                            , new SerializerWriteBasicExt(stockdiaryDatas, new int[] {6, 3, 4})).exec(params)
                        : new PreparedSentence(s
                            , "UPDATE STOCKCURRENT SET UNITS = (UNITS - ?) WHERE LOCATION = ? AND PRODUCT = ? AND ATTRIBUTESETINSTANCE_ID = ?"
                            , new SerializerWriteBasicExt(stockdiaryDatas, new int[] {6, 3, 4, 5})).exec(params);

                if (updateresult == 0) {
                    new PreparedSentence(s
                        , "INSERT INTO STOCKCURRENT (LOCATION, PRODUCT, ATTRIBUTESETINSTANCE_ID, UNITS) VALUES (?, ?, ?, -(?))"
                        , new SerializerWriteBasicExt(stockdiaryDatas, new int[] {3, 4, 5, 6})).exec(params);
                }
                return new PreparedSentence(s
                    , "DELETE FROM STOCKDIARY WHERE ID = ?"
                    , new SerializerWriteBasicExt(stockdiaryDatas, new int[] {0})).exec(params);
            }
        };
    }

    public final SentenceExec getPaymentMovementInsert() {
        return new SentenceExecTransaction(s) {
            public int execInTransaction(Object params) throws BasicException {
                new PreparedSentence(s
                    , "INSERT INTO RECEIPTS (ID, MONEY, DATENEW) VALUES (?, ?, ?)"
                    , new SerializerWriteBasicExt(paymenttabledatas, new int[] {0, 1, 2})).exec(params);
                return new PreparedSentence(s
                    , "INSERT INTO PAYMENTS (ID, RECEIPT, PAYMENT, TOTAL) VALUES (?, ?, ?, ?)"
                    , new SerializerWriteBasicExt(paymenttabledatas, new int[] {3, 0, 4, 5})).exec(params);
            }
        };
    }

    public final SentenceExec getPaymentMovementDelete() {
        return new SentenceExecTransaction(s) {
            public int execInTransaction(Object params) throws BasicException {
                new PreparedSentence(s
                    , "DELETE FROM PAYMENTS WHERE ID = ?"
                    , new SerializerWriteBasicExt(paymenttabledatas, new int[] {3})).exec(params);
                return new PreparedSentence(s
                    , "DELETE FROM RECEIPTS WHERE ID = ?"
                    , new SerializerWriteBasicExt(paymenttabledatas, new int[] {0})).exec(params);
            }
        };
    }

    public java.util.List<holidayInfo> getAllHoliday() {
       
        List<holidayInfo> lines = new ArrayList<holidayInfo>();
        String query = "SELECT ID, DATE, DESCRIPTION FROM HOLIDAYLIST WHERE GENERAL='Y' ";
             
        try {
            lines = new StaticSentence(s, query, null, new SerializerReadClass(holidayInfo.class)).list();
        } catch (BasicException ex) {
            Logger.getLogger(DataLogicSales.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lines;
    }
     public java.util.List<holidayInfo> getAllWeekHoliday() {

        List<holidayInfo> lines = new ArrayList<holidayInfo>();
        String query = "SELECT HL.ID, HL.DATE, HL.DESCRIPTION,HL.YEAR,HI.WEEK,HI.DAY FROM HOLIDAYLIST HL, HOLIDAYINTERMEDIATE HI WHERE HL.HOLIDAYID= HI.ID AND HL.GENERAL='N' ";

        try {
            lines = new StaticSentence(s, query, null, new SerializerReadClass(holidayInfo.class)).list();
        } catch (BasicException ex) {
            Logger.getLogger(DataLogicSales.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lines;
    }


    public java.util.List<announcementInfo> getAllAnnouncement() {
       
        List<announcementInfo> lines = new ArrayList<announcementInfo>();
        String query = "SELECT ID, ANNOUNCEMENT, VALIDFROM, VALIDTO, ACTIVE FROM ANNOUNCEMENT ";
             
        try {
            lines = new StaticSentence(s, query, null, new SerializerReadClass(announcementInfo.class)).list();
        } catch (BasicException ex) {
            Logger.getLogger(DataLogicSales.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lines;
    }

    public java.util.List<cancellationInfo> getCancellationFee() {

        List<cancellationInfo> lines = new ArrayList<cancellationInfo>();
        String query = "SELECT CANCELLATIONFEE, TIMELIMIT FROM CANCELLATION WHERE ACTIVE='Y' ";

        try {
            lines = new StaticSentence(s, query, null, new SerializerReadClass(cancellationInfo.class)).list();
        } catch (BasicException ex) {
            Logger.getLogger(DataLogicSales.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lines;
    }

     public java.util.List<DiscountInfo> getDiscountAmt() {

        List<DiscountInfo> lines = new ArrayList<DiscountInfo>();
        String query = "SELECT DISCOUNTAMOUNT, NOOFPEOPLE FROM DISCOUNTSETUP WHERE ACTIVE='Y' ";

        try {
            lines = new StaticSentence(s, query, null, new SerializerReadClass(DiscountInfo.class)).list();
        } catch (BasicException ex) {
            Logger.getLogger(DataLogicSales.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lines;
    }
      public int getShowOpenCount() throws BasicException {

       Object[] record = ( Object[]) new StaticSentence(s
                    , "SELECT COUNT(DISTINCT SHOWNAME) FROM CLOSEDSHOW WHERE DATEEND IS NULL "
                    , SerializerWriteString.INSTANCE
                    , new SerializerReadBasic(new Datas[] {Datas.STRING})).find();
            int i = Integer.parseInt(record[0].toString());
            return (i == 0 ? 0 : i);


    }
 public int getSequenceNo() throws BasicException {

       Object[] record = ( Object[]) new StaticSentence(s
                    , " SELECT MAX(HOSTSEQUENCE) FROM CLOSEDSHOW "
                    , SerializerWriteString.INSTANCE
                    , new SerializerReadBasic(new Datas[] {Datas.STRING})).find();
            int i = Integer.parseInt(record[0].toString());
            return (i == 0 ? 0 : i);


    }
 public String getCloseShowId(String showName, String dateStart, String dateEnd) throws BasicException {
       Object[] record = ( Object[]) new StaticSentence(s
                    , "SELECT MONEY FROM CLOSEDSHOW WHERE DATESTART > '"+dateStart+"' AND DATEEND<= '"+dateEnd+"' AND SHOWNAME= ? "
                    , SerializerWriteString.INSTANCE
                    , new SerializerReadBasic(new Datas[] {Datas.STRING})).find(showName);
            return (String) (record == null? null : record[0]);


    }
  public java.util.List<CloseshowInfo> getOpenShowId() {
        List<CloseshowInfo> lines = null;
        String query = "SELECT MONEY, SHOWNAME FROM CLOSEDSHOW WHERE DATEEND IS NULL ";
                try {
            lines = new StaticSentence(s, query, null, new SerializerReadClass(CloseshowInfo.class)).list();
        } catch (BasicException ex) {
            Logger.getLogger(DataLogicSales.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lines;
    }
         public final SentenceExec deleteOpenShow(String id) {
        try {
            new StaticSentence(s, "DELETE FROM CLOSEDSHOW WHERE MONEY = ?", SerializerWriteString.INSTANCE).exec(id);
        } catch (BasicException ex) {
            Logger.getLogger(DataLogicSales.class.getName()).log(Level.SEVERE, null, ex);
        }

    return null;
     }
     public List<announcementInfo> getAnnouncement(String date) throws BasicException {

        return (List<announcementInfo>) new StaticSentence(s, "SELECT ID, ANNOUNCEMENT, VALIDFROM, VALIDTO, ACTIVE FROM ANNOUNCEMENT WHERE VALIDTO >='"+date+"' AND ACTIVE ='Y' ORDER BY VALIDFROM asc", null, new SerializerReadClass(announcementInfo.class)).list();
    }
     public List<showInfo> getShowList() throws BasicException {

        return (List<showInfo>) new StaticSentence(s, "SELECT ID, SHOWNAME, STARTTIME, ENDTIME, NOOFSEATS, SHOWTYPE, SPECIALSHOWDATE, OPEN FROM SHOWS WHERE ACTIVE='Y' ORDER BY SHOWNAME asc", null, new SerializerReadClass(showInfo.class)).list();
    }
      public List<showInfo> getextShowList(String createDate) throws BasicException {

        return (List<showInfo>) new StaticSentence(s, "SELECT ID, SHOWNAME, STARTTIME, ENDTIME, NOOFSEATS, SHOWTYPE, SPECIALSHOWDATE, OPEN FROM SHOWS WHERE SHOWNAME NOT IN (SELECT SHOWNAME FROM CLOSEDSHOW WHERE (DATEEND >= '"+createDate+"')) AND (SPECIALSHOWDATE IS NULL OR SPECIALSHOWDATE = '"+createDate+"') AND ACTIVE='Y' ORDER BY STARTTIME asc", null, new SerializerReadClass(showInfo.class)).list();
    }
      public List<showInfo> getCurrentShowList(String sysdate) throws BasicException {

        return (List<showInfo>) new StaticSentence(s, "SELECT ID, SHOWNAME, STARTTIME, ENDTIME, NOOFSEATS, SHOWTYPE, SPECIALSHOWDATE, OPEN FROM SHOWS WHERE (SPECIALSHOWDATE IS NULL OR SPECIALSHOWDATE = '"+sysdate+"')  AND ACTIVE='Y'", null, new SerializerReadClass(showInfo.class)).list();
    }
      public List<showInfo> getAdvanceShowList(String sysdate, String showname) throws BasicException {

        return (List<showInfo>) new StaticSentence(s, "SELECT S.ID, S.SHOWNAME, S.STARTTIME, S.ENDTIME, S.NOOFSEATS, S.SHOWTYPE, S.SPECIALSHOWDATE, S.OPEN FROM SHOWS S WHERE S.STARTTIME > (SELECT S.STARTTIME FROM SHOWS S WHERE  S.SHOWNAME = '"+showname+"' AND ACTIVE='Y') AND (SPECIALSHOWDATE IS NULL OR SPECIALSHOWDATE = '"+sysdate+"')  AND ACTIVE='Y'", null, new SerializerReadClass(showInfo.class)).list();
    }
      public List<ticketCategoryInfo> getticketcategoryList() throws BasicException {

        return (List<ticketCategoryInfo>) new StaticSentence(s, "SELECT ID, CODE, NAME, PRICESELL, TICKETGROUP, MINAMOUNT, NOOFPEOPLE, IMAGE, TICKETDISCOUNT FROM PRODUCTS WHERE ACTIVE='Y' ORDER BY NAME asc", null, new SerializerReadClass(ticketCategoryInfo.class)).list();
    }
    public void insertAvailableSeats(final String showName, final int noofSeats, final Date sysDate) throws BasicException {
       
     new PreparedSentence(s
                    , "INSERT INTO AVAILABLESEATS (ID, SHOWNAME, SEATS,DATE) VALUES (?, ?, ?, ?)"
                    , SerializerWriteParams.INSTANCE
                    ).exec(new DataParams() { public void writeValues() throws BasicException {
                        setString(1, UUID.randomUUID().toString());
                        setString(2, showName);
                        setInt(3, noofSeats);
                        setDate(4, sysDate);
                    }});
    }
    public java.util.List<showInfo> getShowDetails(String showName) {
        List<showInfo> lines = null;
        String query = "SELECT ID, SHOWNAME, STARTTIME, ENDTIME, NOOFSEATS, SHOWTYPE, SPECIALSHOWDATE, OPEN "
                + " FROM SHOWS WHERE SHOWNAME ='"+showName+"' AND ACTIVE='Y'";
        try {
            lines = new StaticSentence(s, query, null, new SerializerReadClass(showInfo.class)).list();
        } catch (BasicException ex) {
            Logger.getLogger(DataLogicSales.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lines;
    }
    public java.util.List<showInfo> getCurrentShow(String sysDate) {
        List<showInfo> lines = null;
        String query = "SELECT ID, SHOWNAME, STARTTIME, ENDTIME, NOOFSEATS, SHOWTYPE, SPECIALSHOWDATE, OPEN "
                + " FROM SHOWS WHERE (SPECIALSHOWDATE IS NULL OR SPECIALSHOWDATE = '"+sysDate+"')  AND ACTIVE='Y'  ORDER BY STARTTIME asc  LIMIT 1";
        try {
            lines = new StaticSentence(s, query, null, new SerializerReadClass(showInfo.class)).list();
        } catch (BasicException ex) {
            Logger.getLogger(DataLogicSales.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lines;
    }
    //starttime >= '"+currentTime+"'
     public java.util.List<showInfo> getNextShow(String endTime, String currentStartDateTime) {
        List<showInfo> lines = null;
        String query = "SELECT ID, SHOWNAME, STARTTIME, ENDTIME, NOOFSEATS, SHOWTYPE, SPECIALSHOWDATE, OPEN "
                + " FROM SHOWS WHERE ENDTIME > '"+endTime+"' AND  (SPECIALSHOWDATE IS NULL OR SPECIALSHOWDATE = '"+currentStartDateTime+"')  AND ACTIVE='Y' ORDER BY STARTTIME asc  LIMIT 1";
        try {
            lines = new StaticSentence(s, query, null, new SerializerReadClass(showInfo.class)).list();
        } catch (BasicException ex) {
            Logger.getLogger(DataLogicSales.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lines;
    }
 

 public int getShowCountValue(String currentTime) throws BasicException {
       Object[] record = ( Object[]) new StaticSentence(s
                    , "SELECT count(*) FROM SHOWS WHERE STARTTIME >= ?  AND ACTIVE='Y' ORDER BY STARTTIME asc  LIMIT 1"
                    , SerializerWriteString.INSTANCE
                    , new SerializerReadBasic(new Datas[] {Datas.STRING})).find(currentTime);
            int i = Integer.parseInt(record[0].toString());
            return (i == 0 ? 0 : i);


    }

  public int getClosedShowCount(String showName, String startDate, String endDate) throws BasicException {
       Object[] record = ( Object[]) new StaticSentence(s
                    , "SELECT count(*) FROM CLOSEDSHOW WHERE SHOWNAME = ? AND DATESTART>'"+startDate+"' AND DATESTART<='"+endDate+"' "
                    , SerializerWriteString.INSTANCE
                    , new SerializerReadBasic(new Datas[] {Datas.STRING})).find(showName);
            int i = Integer.parseInt(record[0].toString());
            return (i == 0 ? 0 : i);



    }

  public int getClosedCashCount() throws BasicException {
       Object[] record = ( Object[]) new StaticSentence(s
                    , "SELECT count(*) FROM CLOSEDCASH  WHERE DATEEND IS NULL "
                    , SerializerWriteString.INSTANCE
                    , new SerializerReadBasic(new Datas[] {Datas.STRING})).find();
            int i = Integer.parseInt(record[0].toString());
            return (i == 0 ? 0 : i);



    }
 
  public int getopenShowCountValue(String showName) throws BasicException {
       Object[] record = ( Object[]) new StaticSentence(s
                    , "SELECT COUNT(*) FROM CLOSEDSHOW WHERE DATEEND IS NULL AND SHOWNAME=? "
                    , SerializerWriteString.INSTANCE
                    , new SerializerReadBasic(new Datas[] {Datas.STRING})).find(showName);
            int i = Integer.parseInt(record[0].toString());
            return (i == 0 ? 0 : i);


    }

    public int getGroupShowCountValue(String showName, String sysDate) throws BasicException {
       Object[] record = ( Object[]) new StaticSentence(s
                    , "SELECT COUNT(*) FROM TICKETS T,RECEIPTS R WHERE R.ID=T.ID AND R.ADVANCEDATE>='"+sysDate+"' AND T.SHOWNAME=? "
                    , SerializerWriteString.INSTANCE
                    , new SerializerReadBasic(new Datas[] {Datas.STRING})).find(showName);
            int i = Integer.parseInt(record[0].toString());
            return (i == 0 ? 0 : i);


    }



 public int getTicketCodeCount(String code) throws BasicException {
       Object[] record = ( Object[]) new StaticSentence(s
                    , "SELECT count(*) FROM PRODUCTS WHERE CODE= ? "
                    , SerializerWriteString.INSTANCE
                    , new SerializerReadBasic(new Datas[] {Datas.STRING})).find(code);
            int i = Integer.parseInt(record[0].toString());
            return (i == 0 ? 0 : i);


    }
 /*public int getDateEnd(String sysDate) throws BasicException {

       Object[] record = ( Object[]) new StaticSentence(s
                    , "SELECT COUNT(*) FROM CLOSEDSHOW WHERE DATE(DATESTART)=? "
                    , SerializerWriteString.INSTANCE
                    , new SerializerReadBasic(new Datas[] {Datas.STRING})).find(sysDate);
            int i = Integer.parseInt(record[0].toString());
            return (i == 0 ? 0 : i);


    }*/

 public int getDateEnd() throws BasicException {

       Object[] record = ( Object[]) new StaticSentence(s
                    , "SELECT COUNT(*) FROM CLOSEDSHOW WHERE DATEEND IS NULL "
                    , SerializerWriteString.INSTANCE
                    , new SerializerReadBasic(new Datas[] {Datas.STRING})).find();
            int i = Integer.parseInt(record[0].toString());
            return (i == 0 ? 0 : i);


    }


  public String getDateStart(String money) throws BasicException {
       Object[] record = ( Object[]) new StaticSentence(s
                    , "SELECT DATESTART FROM CLOSEDSHOW WHERE MONEY=? AND DATEEND IS NULL "
                    , SerializerWriteString.INSTANCE
                    , new SerializerReadBasic(new Datas[] {Datas.STRING})).find(money);
          //  int i = Integer.parseInt(record[0].toString());
      
            return (String) (record == null? null : record[0]);


    }
  public int getweekHolidayCount(String currentTime) throws BasicException {
       Object[] record = ( Object[]) new StaticSentence(s
                    , "SELECT count(*) FROM HOLIDAYLIST WHERE GENERAL ='N' AND DATE = ?"
                    , SerializerWriteString.INSTANCE
                    , new SerializerReadBasic(new Datas[] {Datas.STRING})).find(currentTime);
            int i = Integer.parseInt(record[0].toString());
            return (i == 0 ? 0 : i);


    }
 public int getAnnounceCount(String active, String validfrom, String validto) throws BasicException {
       Object[] record = ( Object[]) new StaticSentence(s
                    , "SELECT count(*) FROM ANNOUNCEMENT WHERE (VALIDFROM <='"+validfrom+"' AND VALIDTO >='"+validto+"') OR (VALIDFROM BETWEEN '"+validfrom+"' AND '"+validto+"') AND ACTIVE=? "
                    , SerializerWriteString.INSTANCE
                    , new SerializerReadBasic(new Datas[] {Datas.STRING})).find(active);
            int i = Integer.parseInt(record[0].toString());
            return (i == 0 ? 0 : i);


    }

 public int getCloseShowcount(String showName) throws BasicException {
       Object[] record = ( Object[]) new StaticSentence(s
                    , "SELECT count(*) FROM CLOSEDSHOW WHERE SHOWNAME = ? AND DATEEND IS NULL"
                    , SerializerWriteString.INSTANCE
                    , new SerializerReadBasic(new Datas[] {Datas.STRING})).find(showName);      
            int i = Integer.parseInt(record[0].toString());
            return (i == 0 ? 0 : i);



    }
   public java.util.List<showInfo> getClosedShow(String money) {
        List<showInfo> lines = null;
        String query = "SELECT SS.ID, SS.SHOWNAME, SS.STARTTIME, SS.ENDTIME, SS.NOOFSEATS, SS.SHOWTYPE, SS.SPECIALSHOWDATE, SS.OPEN "
                + " FROM SHOWS SS, CLOSEDSHOW CS WHERE SS.SHOWNAME = CS.SHOWNAME AND CS.MONEY='"+money+"' AND SS.ACTIVE='Y' ";
        try {
            lines = new StaticSentence(s, query, null, new SerializerReadClass(showInfo.class)).list();
        } catch (BasicException ex) {
            Logger.getLogger(DataLogicSales.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lines;
    }

       public java.util.List<showInfo> getClosedShowDetails() {
        List<showInfo> lines = null;
        String query = "SELECT SS.ID, SS.SHOWNAME, SS.STARTTIME, SS.ENDTIME, SS.NOOFSEATS, SS.SHOWTYPE, SS.SPECIALSHOWDATE, SS.OPEN "
                + " FROM SHOWS SS, CLOSEDSHOW CS WHERE SS.SHOWNAME = CS.SHOWNAME AND SS.ACTIVE='Y' AND CS.DATEEND IS NULL ";
                try {
            lines = new StaticSentence(s, query, null, new SerializerReadClass(showInfo.class)).list();
        } catch (BasicException ex) {
            Logger.getLogger(DataLogicSales.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lines;
    }
           public java.util.List<showInfo> getLastClosedDetails() {
        List<showInfo> lines = null;
        String query = "SELECT SS.ID, SS.SHOWNAME, SS.STARTTIME, SS.ENDTIME, SS.NOOFSEATS, SS.SHOWTYPE, SS.SPECIALSHOWDATE, SS.OPEN "
                + " FROM SHOWS SS, CLOSEDSHOW CS WHERE SS.SHOWNAME = CS.SHOWNAME AND SS.ACTIVE='Y' ORDER BY SS.ENDTIME DESC LIMIT 1 ";
                try {
            lines = new StaticSentence(s, query, null, new SerializerReadClass(showInfo.class)).list();
        } catch (BasicException ex) {
            Logger.getLogger(DataLogicSales.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lines;
    }
 public int getClosecount(String dateStart) throws BasicException {
   
     Object[] record = ( Object[]) new StaticSentence(s
                    , "SELECT count(*) FROM CLOSEDSHOW WHERE DATESTART <= ? AND DATEEND IS NULL"
                    , SerializerWriteString.INSTANCE
                    , new SerializerReadBasic(new Datas[] {Datas.STRING})).find(dateStart);
       
            int i = Integer.parseInt(record[0].toString());
            return (i == 0 ? 0 : i);



    }
 public int getShowNo(String dateStart) throws BasicException {

     Object[] record = ( Object[]) new StaticSentence(s
                    , "SELECT HOSTSEQUENCE FROM CLOSEDSHOW WHERE DATESTART <= ? AND DATEEND IS NULL"
                    , SerializerWriteString.INSTANCE
                    , new SerializerReadBasic(new Datas[] {Datas.STRING})).find(dateStart);

            int i = Integer.parseInt(record[0].toString());
            return (i == 0 ? 0 : i);



    }

 
 public String getShowName(String dateStart) throws BasicException {

     Object[] record = ( Object[]) new StaticSentence(s
                    , "SELECT SHOWNAME FROM CLOSEDSHOW WHERE DATESTART <= ? AND DATEEND IS NULL"
                    , SerializerWriteString.INSTANCE
                    , new SerializerReadBasic(new Datas[] {Datas.STRING})).find(dateStart);

            String i = record[0].toString();
            return (i == "" ? "" : i);



    }

  public String getShowId(String dateStart) throws BasicException {

     Object[] record = ( Object[]) new StaticSentence(s
                    , "SELECT MONEY FROM CLOSEDSHOW WHERE DATESTART <= ? AND DATEEND IS NULL"
                    , SerializerWriteString.INSTANCE
                    , new SerializerReadBasic(new Datas[] {Datas.STRING})).find(dateStart);

            String i = record[0].toString();
            return (i == null ? null : i);



    }
  
  public String getCashId(String dateStart) throws BasicException {

     Object[] record = ( Object[]) new StaticSentence(s
                    , "SELECT MONEY FROM CLOSEDCASH WHERE DATESTART <= ? AND DATEEND IS NULL"
                    , SerializerWriteString.INSTANCE
                    , new SerializerReadBasic(new Datas[] {Datas.STRING})).find(dateStart);

            String i = record[0].toString();
            return (i == null ? null : i);



    }

 public int getCountAfterClose(String dateStart, String sysDate, String showName) throws BasicException {

     Object[] record = ( Object[]) new StaticSentence(s
                    , "SELECT count(*) FROM CLOSEDSHOW WHERE DATESTART >= ? AND DATEEND <'"+sysDate+"' AND SHOWNAME='"+showName+"'"
                    , SerializerWriteString.INSTANCE
                    , new SerializerReadBasic(new Datas[] {Datas.STRING})).find(dateStart);

            int i = Integer.parseInt(record[0].toString());
            return (i == 0 ? 0 : i);



    }
  public int getmonthShowCount(String dateStart) throws BasicException {

     Object[] record = ( Object[]) new StaticSentence(s
                    , "SELECT count(*) FROM CLOSEDSHOW WHERE DATESTART LIKE ?%"
                    , SerializerWriteString.INSTANCE
                    , new SerializerReadBasic(new Datas[] {Datas.STRING})).find(dateStart);
      
            int i = Integer.parseInt(record[0].toString());
            return (i == 0 ? 0 : i);

    }
  public int getResetCount(String dateStart) throws BasicException {

     Object[] record = ( Object[]) new StaticSentence(s
                    , "SELECT count(*) FROM CLOSEDSHOW WHERE DATESTART >=?"
                    , SerializerWriteString.INSTANCE
                    , new SerializerReadBasic(new Datas[] {Datas.STRING})).find(dateStart);
       
            int i = Integer.parseInt(record[0].toString());
            return (i == 0 ? 0 : i);
    }
   public void updateShows(String id, String showName, String starttime, String endTime, int noofSeats, String special, Date specialShowDate, String groupOpen, Date created, String user ) throws BasicException {

        Object[] values = new Object[]{id, showName, starttime, endTime, noofSeats, special, specialShowDate, groupOpen, created, user};
        Datas[] datas = new Datas[]{Datas.STRING, Datas.STRING, Datas.STRING, Datas.STRING, Datas.INT, Datas.STRING, Datas.TIMESTAMP, Datas.STRING, Datas.TIMESTAMP, Datas.STRING};
        new PreparedSentence(s, "UPDATE SHOWS SET SHOWNAME = ?, STARTTIME = ?, ENDTIME= ?, NOOFSEATS = ?, SHOWTYPE = ?, SPECIALSHOWDATE = ?, OPEN = ?, CREATED = ?, CREATEDBY = ? WHERE ID= ? ", new SerializerWriteBasicExt(datas, new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 0})).exec(values);

    }

     public void updateTicketCategory(String id, String ticketCode, String productname, double price, String group, double minAmount, int people, BufferedImage image, String discount) throws BasicException {

        Object[] values = new Object[]{id, ticketCode, productname, price, group, minAmount, people, image, discount};
        Datas[] datas = new Datas[]{Datas.STRING, Datas.STRING, Datas.STRING, Datas.DOUBLE, Datas.STRING, Datas.DOUBLE, Datas.INT, Datas.IMAGE, Datas.STRING};
        new PreparedSentence(s, "UPDATE PRODUCTS SET CODE = ?, NAME = ?, PRICESELL= ?, TICKETGROUP = ?, MINAMOUNT = ?, NOOFPEOPLE = ?, IMAGE =?, TICKETDISCOUNT = ? WHERE ID= ? ", new SerializerWriteBasicExt(datas, new int[]{1, 2, 3, 4, 5, 6, 7, 8, 0})).exec(values);

    }

     public void updateProducts(String ID) throws BasicException {

        Object[] values = new Object[]{ ID};
        Datas[] datas = new Datas[]{Datas.STRING};
        new PreparedSentence(s, "UPDATE PRODUCTS SET ACTIVE = 'N' WHERE ID= ? ", new SerializerWriteBasicExt(datas, new int[]{0})).exec(values);

    }

      public void updateActiveShow(String ID) throws BasicException {

        Object[] values = new Object[]{ ID};
        Datas[] datas = new Datas[]{Datas.STRING};
        new PreparedSentence(s, "UPDATE SHOWS SET ACTIVE = 'N' WHERE ID= ? ", new SerializerWriteBasicExt(datas, new int[]{0})).exec(values);

    }


   public void updateAvailableSeats(Date createdDate, String showName, int noofSeats ) throws BasicException {

        Object[] values = new Object[]{createdDate, showName, noofSeats};
        Datas[] datas = new Datas[]{Datas.TIMESTAMP, Datas.STRING, Datas.INT};
        new PreparedSentence(s, "UPDATE AVAILABLESEATS SET SEATS = ? WHERE SHOWNAME=? AND DATE = ? ", new SerializerWriteBasicExt(datas, new int[]{2, 1, 0})).exec(values);

    }

   public final SentenceExec updateAnnouncement() throws BasicException {
       SentenceExec updateSeats = new PreparedSentence(s, "UPDATE ANNOUNCEMENT SET ACTIVE =  ?", SerializerWriteParams.INSTANCE);
                updateSeats.exec(new DataParams() {

                @Override
                    public void writeValues() throws BasicException {
                        setString(1, "N");
                              }
                 });
    return null;
  
    }

   public final SentenceExec updateCancellationFee() throws BasicException {
       SentenceExec updateCancelFee = new PreparedSentence(s, "UPDATE CANCELLATION SET ACTIVE =  ?", SerializerWriteParams.INSTANCE);
                updateCancelFee.exec(new DataParams() {
                    public void writeValues() throws BasicException {
                        setString(1, "N");
                    }
                 });
       return null;

    }

    public final SentenceExec updateDiscount() throws BasicException {
       SentenceExec updateCancelFee = new PreparedSentence(s, "UPDATE DISCOUNTSETUP SET ACTIVE =  ?", SerializerWriteParams.INSTANCE);
                updateCancelFee.exec(new DataParams() {
                    public void writeValues() throws BasicException {
                        setString(1, "N");
                    }
                 });
       return null;

    }

   public int getAvailableSeats(String showName, Date sysDate) throws BasicException {
      
       DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
       String systemDate = df.format(sysDate);
         Object[] record = (Object[]) new StaticSentence(s
                    , "SELECT SEATS FROM AVAILABLESEATS WHERE SHOWNAME =? AND DATE = '"+systemDate+"'"
                    , SerializerWriteString.INSTANCE
                    , new SerializerReadBasic(new Datas[] {Datas.STRING})).find(showName);
            int i = Integer.parseInt(record[0].toString());
            return (i == 0 ? 0 : i);

    }

   public int getExtSeats(String showName, String sysDate, String endTime) throws BasicException {
         Object[] record = ( Object[]) new StaticSentence(s
                    , "SELECT NOOFSEATS FROM EXTENDEDSEATS WHERE SHOWNAME =? AND DATE>='"+sysDate+"' AND DATE<'"+endTime+"'"
                    , SerializerWriteString.INSTANCE
                    , new SerializerReadBasic(new Datas[] {Datas.STRING})).find(showName);
            int i = Integer.parseInt(record[0].toString());
            return (i == 0 ? 0 : i);

    }
  
   public int getExtSeatscount(String showName, String sysDate, String endTime) throws BasicException {
        int i = 0;
         Object[] record = ( Object[]) new StaticSentence(s
                    , "SELECT count(*) FROM EXTENDEDSEATS WHERE SHOWNAME =? AND DATE>'"+sysDate+"' AND DATE<'"+endTime+"'"
                    , SerializerWriteString.INSTANCE
                    , new SerializerReadBasic(new Datas[] {Datas.STRING})).find(showName);

            i = Integer.parseInt(record[0].toString());
            return (i == 0 ? 0 : i);
    }

    public int getDateCount(String date) throws BasicException {


            Object[] record = ( Object[]) new StaticSentence(s
                    , "SELECT count(*) FROM HOLIDAYLIST WHERE DATE =?"
                    , SerializerWriteString.INSTANCE
                    , new SerializerReadBasic(new Datas[] {Datas.STRING})).find(date);
           int i = Integer.parseInt(record[0].toString());
            return (i == 0 ? 0 : i);

    }
    public final double findProductStock(String warehouse, String id, String attsetinstid) throws BasicException {

        PreparedSentence p = attsetinstid == null
                ? new PreparedSentence(s, "SELECT UNITS FROM STOCKCURRENT WHERE LOCATION = ? AND PRODUCT = ? AND ATTRIBUTESETINSTANCE_ID IS NULL"
                    , new SerializerWriteBasic(Datas.STRING, Datas.STRING)
                    , SerializerReadDouble.INSTANCE)
                : new PreparedSentence(s, "SELECT UNITS FROM STOCKCURRENT WHERE LOCATION = ? AND PRODUCT = ? AND ATTRIBUTESETINSTANCE_ID = ?"
                    , new SerializerWriteBasic(Datas.STRING, Datas.STRING, Datas.STRING)
                    , SerializerReadDouble.INSTANCE);

        Double d = (Double) p.find(warehouse, id, attsetinstid);
        return d == null ? 0.0 : d.doubleValue();
    }

    public final SentenceExec getCatalogCategoryAdd() {
        return new StaticSentence(s
                , "INSERT INTO PRODUCTS_CAT(PRODUCT, CATORDER) SELECT ID, " + s.DB.INTEGER_NULL() + " FROM PRODUCTS WHERE CATEGORY = ?"
                , SerializerWriteString.INSTANCE);
    }
    public final SentenceExec getCatalogCategoryDel() {
        return new StaticSentence(s
                , "DELETE FROM PRODUCTS_CAT WHERE PRODUCT = ANY (SELECT ID FROM PRODUCTS WHERE CATEGORY = ?)"
                , SerializerWriteString.INSTANCE);
    }
     public final SentenceExec deleteHoliday(String id) {
        try {
            new StaticSentence(s, "DELETE FROM HOLIDAYLIST WHERE ID = ?", SerializerWriteString.INSTANCE).exec(id);
        } catch (BasicException ex) {
            Logger.getLogger(DataLogicSales.class.getName()).log(Level.SEVERE, null, ex);
        }

    return null;
     }

   public final SentenceExec deleteAnnouncement(String id) {
        try {
            new StaticSentence(s, "DELETE FROM ANNOUNCEMENT WHERE ID = ?", SerializerWriteString.INSTANCE).exec(id);
        } catch (BasicException ex) {
            Logger.getLogger(DataLogicSales.class.getName()).log(Level.SEVERE, null, ex);
        }

    return null;
     }
        public final SentenceExec deleteSharedTickets() {
        try {
            new StaticSentence(s, "DELETE FROM SHAREDTICKETS ", SerializerWriteString.INSTANCE).exec();
        } catch (BasicException ex) {
            Logger.getLogger(DataLogicSales.class.getName()).log(Level.SEVERE, null, ex);
        }
    return null;
     }


   public int getAvailableCount(String showName, Date sysDate) throws BasicException {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String systemDate = df.format(sysDate);

            Object[] record = ( Object[]) new StaticSentence(s
                    , "SELECT count(*) FROM AVAILABLESEATS WHERE SHOWNAME =? AND DATE = '"+systemDate+"'"
                    , SerializerWriteString.INSTANCE
                    , new SerializerReadBasic(new Datas[] {Datas.STRING})).find(showName);
           int i = Integer.parseInt(record[0].toString());
            return (i == 0 ? 0 : i);

    }

    public int getShowNameCount(String showName) throws BasicException {


            Object[] record = ( Object[]) new StaticSentence(s
                    , "SELECT count(*) FROM SHOWS WHERE SHOWNAME =? AND ACTIVE='Y' "
                    , SerializerWriteString.INSTANCE
                    , new SerializerReadBasic(new Datas[] {Datas.STRING})).find(showName);
           int i = Integer.parseInt(record[0].toString());
            return (i == 0 ? 0 : i);

    }
    public int getReceiptCount(String money) throws BasicException {


            Object[] record = ( Object[]) new StaticSentence(s
                    , "SELECT count(*) FROM RECEIPTS WHERE MONEYSHOW =?"
                    , SerializerWriteString.INSTANCE
                    , new SerializerReadBasic(new Datas[] {Datas.STRING})).find(money);
           int i = Integer.parseInt(record[0].toString());
            return (i == 0 ? 0 : i);

    }

     public int getNorReceiptCount(String money, String currentDate, String showName) throws BasicException {

String advanceDate = "";
            Object[] record = ( Object[]) new StaticSentence(s
                    , "SELECT COALESCE(SUM(TL.UNITS),0) FROM RECEIPTS RP INNER JOIN TICKETS T ON RP.ID = T.ID INNER JOIN TICKETLINES TL ON T.ID = TL.TICKET WHERE  RP.ADVANCEDATE ='"+advanceDate+"' AND T.SHOWNAME = '"+showName+"' and MONEYSHOW =?"
                    , SerializerWriteString.INSTANCE
                    , new SerializerReadBasic(new Datas[] {Datas.STRING})).find(money);
           int i = Integer.parseInt(record[0].toString());
            return (i == 0 ? 0 : i);
    }
//(RP.ADVANCEDATE ='"+advanceDate+"' OR  RP.ADVANCEDATE = '"+currentDate+"') AND
      public int getAdvanceReceiptCount(String currentDate, String showname) throws BasicException {


            Object[] record = ( Object[]) new StaticSentence(s
                    , "SELECT COALESCE(SUM(TL.UNITS),0) FROM RECEIPTS RP INNER JOIN TICKETS T ON RP.ID = T.ID INNER JOIN TICKETLINES TL ON T.ID = TL.TICKET WHERE RP.ADVANCEDATE = ? AND T.SHOWNAME = '"+showname+"'"
                    , SerializerWriteString.INSTANCE
                    , new SerializerReadBasic(new Datas[] {Datas.STRING})).find(currentDate);
           int i = Integer.parseInt(record[0].toString());
            return (i == 0 ? 0 : i);

    }
    
     public int getCategoryNameCount(String categoryName) throws BasicException {


            Object[] record = ( Object[]) new StaticSentence(s
                    , "SELECT count(*) FROM PRODUCTS WHERE NAME =?"
                    , SerializerWriteString.INSTANCE
                    , new SerializerReadBasic(new Datas[] {Datas.STRING})).find(categoryName);
           int i = Integer.parseInt(record[0].toString());
            return (i == 0 ? 0 : i);

    }
     public int getShowextCount(String showName, String created, String dateStart) throws BasicException {


            Object[] record = ( Object[]) new StaticSentence(s
                    , "SELECT count(*) FROM EXTENDEDSEATS WHERE SHOWNAME =? and DATE>='"+dateStart+"' AND DATE<'"+created+"'"
                    , SerializerWriteString.INSTANCE
                    , new SerializerReadBasic(new Datas[] {Datas.STRING})).find(showName);
           int i = Integer.parseInt(record[0].toString());
            return (i == 0 ? 0 : i);

    }


    /*public int getShowSpecialCount(String startTime, String EndTime, String sysDate) throws BasicException {

            Object[] record = (Object[]) new StaticSentence(s
                    , "SELECT COUNT(*) FROM SHOWS WHERE (STARTTIME <= ? AND ENDTIME >='"+EndTime+"') OR (STARTTIME BETWEEN '"+startTime+"' AND '"+EndTime+"') AND (SPECIALSHOWDATE IS NULL OR SPECIALSHOWDATE = '"+sysDate+"') AND ACTIVE='Y' "
                    , SerializerWriteString.INSTANCE
                    , new SerializerReadBasic(new Datas[] {Datas.STRING})).find(startTime);
           int i = Integer.parseInt(record[0].toString());
            return (i == 0 ? 0 : i);

    }

      public int getShowCount(String startTime, String EndTime, String sysDate) throws BasicException {

            Object[] record = (Object[]) new StaticSentence(s
                    , "SELECT COUNT(*) FROM SHOWS WHERE (STARTTIME <= ? AND ENDTIME >='"+EndTime+"') OR (STARTTIME BETWEEN '"+startTime+"' AND '"+EndTime+"') AND (SPECIALSHOWDATE IS NULL OR SPECIALSHOWDATE = '"+sysDate+"') AND ACTIVE='Y' "
                    , SerializerWriteString.INSTANCE
                    , new SerializerReadBasic(new Datas[] {Datas.STRING})).find(startTime);
           int i = Integer.parseInt(record[0].toString());
            return (i == 0 ? 0 : i);

    }*/

    public int getShowSpecialCount(String startTime, String EndTime, String sysDate) throws BasicException {

            Object[] record = (Object[]) new StaticSentence(s
                    , "SELECT COUNT(*) FROM SHOWS WHERE ACTIVE='Y' AND (STARTTIME BETWEEN ? AND '"+EndTime+"') AND (SPECIALSHOWDATE IS NULL OR SPECIALSHOWDATE = '"+sysDate+"') "
                    , SerializerWriteString.INSTANCE
                    , new SerializerReadBasic(new Datas[] {Datas.STRING})).find(startTime);
           int i = Integer.parseInt(record[0].toString());
            return (i == 0 ? 0 : i);

    }

public int getRgularShowCount(String startTime, String EndTime) throws BasicException {

            Object[] record = ( Object[]) new StaticSentence(s
                    , "SELECT count(*) FROM SHOWS  WHERE ACTIVE='Y' AND (STARTTIME <= ? AND ENDTIME >='"+EndTime+"' OR STARTTIME BETWEEN '"+startTime+"' AND '"+EndTime+"')"
                    , SerializerWriteString.INSTANCE
                    , new SerializerReadBasic(new Datas[] {Datas.STRING})).find(startTime);
           int i = Integer.parseInt(record[0].toString());
            return (i == 0 ? 0 : i);

    }


    public int getShowCount(String startTime, String EndTime, String sysDate) throws BasicException {

            Object[] record = (Object[]) new StaticSentence(s
                    , "SELECT COUNT(*) FROM SHOWS WHERE ACTIVE='Y' AND (STARTTIME <= ? AND ENDTIME >='"+EndTime+"') OR (STARTTIME BETWEEN '"+startTime+"' AND '"+EndTime+"') AND (SPECIALSHOWDATE IS NULL OR SPECIALSHOWDATE = '"+sysDate+"') "
                    , SerializerWriteString.INSTANCE
                    , new SerializerReadBasic(new Datas[] {Datas.STRING})).find(startTime);
           int i = Integer.parseInt(record[0].toString());
            return (i == 0 ? 0 : i);

    }

     public int getweekHolidayCount(String day, String week, int year) throws BasicException {

            Object[] record = ( Object[]) new StaticSentence(s
                    , "SELECT COUNT(*) FROM HOLIDAYINTERMEDIATE HI,HOLIDAYLIST HL WHERE HL.HOLIDAYID= HI.ID AND HI.DAY ='"+day+"' AND HI.YEAR = '"+year+"' AND HI.WEEK=? "
                    , SerializerWriteString.INSTANCE
                    , new SerializerReadBasic(new Datas[] {Datas.STRING})).find(week);
           int i = Integer.parseInt(record[0].toString());
            return (i == 0 ? 0 : i);

    }
   
    public final TableDefinition getTableCategories() {
        return new TableDefinition(s,
            "CATEGORIES"
            , new String[] {"ID", "NAME", "PARENTID", "IMAGE"}
            , new String[] {"ID", AppLocal.getIntString("Label.Name"), "", AppLocal.getIntString("label.image")}
            , new Datas[] {Datas.STRING, Datas.STRING, Datas.STRING, Datas.IMAGE}
            , new Formats[] {Formats.STRING, Formats.STRING, Formats.STRING, Formats.NULL}
            , new int[] {0}
        );
    }
    public final TableDefinition getTableTaxes() {
        return new TableDefinition(s,
            "TAXES"
            , new String[] {"ID", "NAME", "CATEGORY", "CUSTCATEGORY", "PARENTID", "RATE", "RATECASCADE", "RATEORDER"}
            , new String[] {"ID", AppLocal.getIntString("Label.Name"), AppLocal.getIntString("label.taxcategory"), AppLocal.getIntString("label.custtaxcategory"), AppLocal.getIntString("label.taxparent"), AppLocal.getIntString("label.dutyrate"), AppLocal.getIntString("label.cascade"), AppLocal.getIntString("label.order")}
            , new Datas[] {Datas.STRING, Datas.STRING, Datas.STRING, Datas.STRING, Datas.STRING, Datas.DOUBLE, Datas.BOOLEAN, Datas.INT}
            , new Formats[] {Formats.STRING, Formats.STRING, Formats.STRING, Formats.STRING, Formats.STRING, Formats.PERCENT, Formats.BOOLEAN, Formats.INT}
            , new int[] {0}
        );
    }

    public final TableDefinition getTableTaxCustCategories() {
        return new TableDefinition(s,
            "TAXCUSTCATEGORIES"
            , new String[] {"ID", "NAME"}
            , new String[] {"ID", AppLocal.getIntString("Label.Name")}
            , new Datas[] {Datas.STRING, Datas.STRING}
            , new Formats[] {Formats.STRING, Formats.STRING}
            , new int[] {0}
        );
    }
    public final TableDefinition getTableTaxCategories() {
        return new TableDefinition(s,
            "TAXCATEGORIES"
            , new String[] {"ID", "NAME"}
            , new String[] {"ID", AppLocal.getIntString("Label.Name")}
            , new Datas[] {Datas.STRING, Datas.STRING}
            , new Formats[] {Formats.STRING, Formats.STRING}
            , new int[] {0}
        );
    }

    public final TableDefinition getTableLocations() {
        return new TableDefinition(s,
            "LOCATIONS"
            , new String[] {"ID", "NAME", "ADDRESS"}
            , new String[] {"ID", AppLocal.getIntString("label.locationname"), AppLocal.getIntString("label.locationaddress")}
            , new Datas[] {Datas.STRING, Datas.STRING, Datas.STRING}
            , new Formats[] {Formats.STRING, Formats.STRING, Formats.STRING}
            , new int[] {0}
        );
    }


    protected static class CustomerExtRead implements SerializerRead {
        public Object readValues(DataRead dr) throws BasicException {
            CustomerInfoExt c = new CustomerInfoExt(dr.getString(1));
            c.setTaxid(dr.getString(2));
            c.setSearchkey(dr.getString(3));
            c.setName(dr.getString(4));
            c.setCard(dr.getString(5));
            c.setTaxCustomerID(dr.getString(6));
            c.setNotes(dr.getString(7));
            c.setMaxdebt(dr.getDouble(8));
            c.setVisible(dr.getBoolean(9).booleanValue());
            c.setCurdate(dr.getTimestamp(10));
            c.setCurdebt(dr.getDouble(11));
            c.setFirstname(dr.getString(12));
            c.setLastname(dr.getString(13));
            c.setEmail(dr.getString(14));
            c.setPhone(dr.getString(15));
            c.setPhone2(dr.getString(16));
            c.setFax(dr.getString(17));
            c.setAddress(dr.getString(18));
            c.setAddress2(dr.getString(19));
            c.setPostal(dr.getString(20));
            c.setCity(dr.getString(21));
            c.setRegion(dr.getString(22));
            c.setCountry(dr.getString(23));

            return c;
        }
    }
    public List<CurrentSeatInfo> getCurrentShowId() throws BasicException {
        Date sysdate = new Date();
        SimpleDateFormat time = new SimpleDateFormat("HH:mm:ss");
        String currentTime = time.format(sysdate);
        return (List<CurrentSeatInfo>) new PreparedSentence(s, "SELECT ID,STARTTIME,ENDTIME FROM SHOWS  WHERE ACTIVE='Y'   AND STARTTIME<='" + currentTime + "'  AND ENDTIME>'" + currentTime + "' ", null, new SerializerReadClass(CurrentSeatInfo.class)).list();
    }
}
