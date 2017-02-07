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

package com.openbravo.pos.panels;

import java.util.*;
import javax.swing.table.AbstractTableModel;
import com.openbravo.basic.BasicException;
import com.openbravo.data.loader.*;
import com.openbravo.format.Formats;
import com.openbravo.pos.forms.AppLocal;
import com.openbravo.pos.forms.AppView;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 *
 * @author adrianromero
 */
public class PaymentsModel {

    private String m_sHost;
    private int m_iSeq;
    private Date m_dDateStart;
    private Date m_dDateEnd;       
            
    private Integer m_iPayments;
    private Double m_dPaymentsTotal;
    private Double  m_dRefPaymentsTotal;
    private Double m_dCancellationFee;
    private java.util.List<PaymentsLine> m_lpayments;
    private java.util.List<AdvSalesLine> m_lAdvsales;
    private java.util.List<RefSalesLine> m_lRefsales;
    private java.util.List<DiscountSalesLine> m_lDisasales;
    private java.util.List<GroupSalesLine> m_lGroupsales;
    private java.util.List<GroupAdvSalesLine> m_lGroupAdvsales;
    private final static String[] PAYMENTHEADERS = {"Label.Payment", "label.totalcash"};
    private static String m_dActive;
    private Double m_dDiscount;
    private Double m_dDiscountAmt;
    private Double m_dNorTotalTicket;
    private Double m_dNorDiscount;
    private Double m_dAdvTotalTicket;
    private Double m_dAdvDiscount;
    private Integer m_iSales; 
    private Double m_dSalesBase;
    private Double m_dSalesTaxes;
    private static String sysDate;
     private Double m_dTotalUnits;
    private java.util.List<SalesLine> m_lsales;
    private static Date createdDate = new Date();
    private static DateFormat format = new SimpleDateFormat("dd-MM-yyyy");
    private final static String[] SALEHEADERS = {"label.taxcash", "label.totalcash"};

    private PaymentsModel() {
         sysDate = format.format(createdDate);
    }    
    
    public static PaymentsModel emptyInstance() {
        
        PaymentsModel p = new PaymentsModel();
        
        p.m_iPayments = new Integer(0);
        p.m_dPaymentsTotal = new Double(0.0);
        p.m_dRefPaymentsTotal=new Double(0.0);
          p.m_dCancellationFee = new Double(0.0);
        p.m_lpayments = new ArrayList<PaymentsLine>();
           p.m_dDiscount = null;
        p.m_iSales = null;
        p.m_dSalesBase = null;
        p.m_dSalesTaxes = null;
        sysDate = format.format(createdDate);
        p.m_lsales = new ArrayList<SalesLine>();
         p.m_dActive = "Y";
        return p;
    }
    
    public static PaymentsModel loadInstance(AppView app) throws BasicException {
        
        PaymentsModel p = new PaymentsModel();
        
        // Propiedades globales
        p.m_sHost = app.getProperties().getHost();
        p.m_iSeq = app.getActiveCashSequence();
        p.m_dDateStart = app.getActiveCashDateStart();
        p.m_dDateEnd = null;
         p.m_dActive = "Y";
        
        // Pagos
        Object[] valtickets = (Object []) new StaticSentence(app.getSession()
            , "SELECT COUNT(*),  COALESCE(SUM(PAYMENTS.TOTAL),0) " +
              "FROM PAYMENTS, RECEIPTS " +
              "WHERE PAYMENTS.RECEIPT = RECEIPTS.ID AND RECEIPTS.MONEY = ? AND PAYMENTS.PAYMENT='cash'"
            , SerializerWriteString.INSTANCE
            , new SerializerReadBasic(new Datas[] {Datas.INT, Datas.DOUBLE}))
            .find(app.getActiveCashIndex());
            
        if (valtickets == null) {
            p.m_iPayments = new Integer(0);
            p.m_dPaymentsTotal = new Double(0.0);
        } else {
            p.m_iPayments = (Integer) valtickets[0];
            p.m_dPaymentsTotal = (Double) valtickets[1];
        }  
        Object[] valReftickets = (Object []) new StaticSentence(app.getSession()
            , "SELECT SUM(TICKETLINES.PRICE) " +
               "FROM RECEIPTS, TICKETLINES, PRODUCTS, TICKETS WHERE RECEIPTS.ID = TICKETS.ID AND TICKETS.ID = TICKETLINES.TICKET AND PRODUCTS.ID= TICKETLINES.PRODUCT " +
                "AND TICKETS.TICKETTYPE=1 AND RECEIPTS.MONEY = ? "
            , SerializerWriteString.INSTANCE
            , new SerializerReadBasic(new Datas[] {Datas.DOUBLE}))
            .find(app.getActiveCashIndex());

        if (valReftickets == null) {
             p.m_dRefPaymentsTotal = new Double(0.0);
        } else {
            p.m_dRefPaymentsTotal = (Double) valReftickets[0];
        }
          Object[] valDisNormaltickets = (Object []) new StaticSentence(app.getSession()
            , "SELECT COALESCE(SUM(TICKETS.DISCOUNT),0) FROM TICKETS, RECEIPTS WHERE RECEIPTS.ID = TICKETS.ID AND RECEIPTS.ADVANCEBOOKING = 'N' " +
                "AND RECEIPTS.MONEY = ? "
            , SerializerWriteString.INSTANCE
            , new SerializerReadBasic(new Datas[] {Datas.DOUBLE}))
            .find(app.getActiveCashIndex());

        if (valDisNormaltickets == null) {
             p.m_dNorDiscount = new Double(0.0);
        } else {
            p.m_dNorDiscount = (Double) valDisNormaltickets[0];
        }
         Object[] valDisAdvtickets = (Object []) new StaticSentence(app.getSession()
            , "SELECT COALESCE(SUM(TICKETS.DISCOUNT),0) FROM TICKETS, RECEIPTS WHERE RECEIPTS.ID = TICKETS.ID AND RECEIPTS.ADVANCEBOOKING = 'Y' " +
                "AND RECEIPTS.MONEY = ? "
            , SerializerWriteString.INSTANCE
            , new SerializerReadBasic(new Datas[] {Datas.DOUBLE}))
            .find(app.getActiveCashIndex());

        if (valDisAdvtickets == null) {
             p.m_dAdvDiscount = new Double(0.0);
        } else {
            p.m_dAdvDiscount = (Double) valDisAdvtickets[0];
        }
        Object[] valCancelFee = (Object []) new StaticSentence(app.getSession()
            , "SELECT COALESCE(SUM(TICKETS.CANCELLATIONFEE),0) " +
              "FROM TICKETS, RECEIPTS " +
              "WHERE TICKETS.ID = RECEIPTS.ID AND RECEIPTS.MONEY = ? "
            , SerializerWriteString.INSTANCE
            , new SerializerReadBasic(new Datas[] {Datas.DOUBLE}))
            .find(app.getActiveCashIndex());

        if (valCancelFee == null) {
             p.m_dCancellationFee = new Double(0.0);
        } else {
            p.m_dCancellationFee = (Double) valCancelFee[0];
        }
        List l = new StaticSentence(app.getSession()            
            , "SELECT PAYMENTS.PAYMENT, SUM(PAYMENTS.TOTAL) " +
              "FROM PAYMENTS, RECEIPTS " +
              "WHERE PAYMENTS.RECEIPT = RECEIPTS.ID AND RECEIPTS.MONEY = ? " +
              "GROUP BY PAYMENTS.PAYMENT"
            , SerializerWriteString.INSTANCE
            , new SerializerReadClass(PaymentsModel.PaymentsLine.class)) //new SerializerReadBasic(new Datas[] {Datas.STRING, Datas.DOUBLE}))
            .list(app.getActiveCashIndex()); 
        
        if (l == null) {
            p.m_lpayments = new ArrayList();
        } else {
            p.m_lpayments = l;
        }        
        
        // Sales
        Object[] recsales = (Object []) new StaticSentence(app.getSession(),
            "SELECT COUNT(DISTINCT RECEIPTS.ID), SUM(TICKETLINES.PRICE) " +
            "FROM RECEIPTS, TICKETLINES WHERE RECEIPTS.ID = TICKETLINES.TICKET AND RECEIPTS.MONEY = ?",
            SerializerWriteString.INSTANCE,
            new SerializerReadBasic(new Datas[] {Datas.INT, Datas.DOUBLE}))
            .find(app.getActiveCashIndex());
        if (recsales == null) {
            p.m_iSales = null;
            p.m_dSalesBase = null;
        } else {
            p.m_iSales = (Integer) recsales[0];
            p.m_dSalesBase = (Double) recsales[1];
        }             
        
        // Taxes
        Object[] rectaxes = (Object []) new StaticSentence(app.getSession(),
            "SELECT SUM(TAXLINES.AMOUNT) " +
            "FROM RECEIPTS, TAXLINES WHERE RECEIPTS.ID = TAXLINES.RECEIPT AND RECEIPTS.MONEY = ?"
            , SerializerWriteString.INSTANCE
            , new SerializerReadBasic(new Datas[] {Datas.DOUBLE}))
            .find(app.getActiveCashIndex());            
        if (rectaxes == null) {
            p.m_dSalesTaxes = null;
        } else {
            p.m_dSalesTaxes = (Double) rectaxes[0];
        } 
          Object[] valTotalUnits = (Object []) new StaticSentence(app.getSession()
            , "SELECT sum(TICKETLINES.UNITS) " +
              "FROM TICKETLINES,TICKETS,RECEIPTS " +
              "WHERE TICKETS.ID = TICKETLINES.TICKET AND TICKETS.ID=RECEIPTS.ID AND RECEIPTS.MONEY = ? "
            , SerializerWriteString.INSTANCE
            , new SerializerReadBasic(new Datas[] {Datas.DOUBLE}))
            .find(app.getActiveCashIndex());

        if (valTotalUnits == null) {
             p.m_dTotalUnits = new Double(0.0);
        } else {
            p.m_dTotalUnits = (Double) valTotalUnits[0];
        }
        List<SalesLine> asales = new StaticSentence(app.getSession(),
               "SELECT PRODUCTS.NAME, SUM(TICKETLINES.UNITS), SUM(PRODUCTS.PRICESELL*TICKETLINES.UNITS) " +
                "FROM RECEIPTS, TICKETLINES, PRODUCTS WHERE RECEIPTS.ID = TICKETLINES.TICKET AND PRODUCTS.ID= TICKETLINES.PRODUCT AND PRODUCTS.TICKETGROUP='N' " +
                "AND RECEIPTS.ADVANCEBOOKING = 'N' AND RECEIPTS.MONEY = ? " +
                "GROUP BY PRODUCTS.NAME"
                , SerializerWriteString.INSTANCE
                , new SerializerReadClass(PaymentsModel.SalesLine.class))
                .list(app.getActiveCashIndex());
        if (asales == null) {
            p.m_lsales = new ArrayList<SalesLine>();
        } else {
            p.m_lsales = asales;
        }

        List<AdvSalesLine> Advasales = new StaticSentence(app.getSession(),
              "SELECT PRODUCTS.NAME, SUM(TICKETLINES.UNITS), SUM(PRODUCTS.PRICESELL*TICKETLINES.UNITS) " +
                "FROM RECEIPTS, TICKETLINES, PRODUCTS, TICKETS WHERE RECEIPTS.ID = TICKETS.ID AND TICKETS.ID = TICKETLINES.TICKET AND PRODUCTS.ID= TICKETLINES.PRODUCT AND PRODUCTS.TICKETGROUP='N' " +
                "AND RECEIPTS.ADVANCEBOOKING = 'Y' AND TICKETS.TICKETTYPE=0 AND RECEIPTS.MONEY = ? " +
                "GROUP BY PRODUCTS.NAME"
                , SerializerWriteString.INSTANCE
                , new SerializerReadClass(PaymentsModel.AdvSalesLine.class))
                .list(app.getActiveCashIndex());
        if (Advasales == null) {
            p.m_lAdvsales = new ArrayList<AdvSalesLine>();
        } else {
            p.m_lAdvsales = Advasales;
        }
    List<GroupSalesLine> aGroupsales = new StaticSentence(app.getSession(),
                "SELECT PRODUCTS.NAME, SUM(TICKETLINES.UNITS), SUM(TICKETLINES.PRICE) " +
                "FROM RECEIPTS, TICKETLINES, PRODUCTS WHERE RECEIPTS.ID = TICKETLINES.TICKET AND PRODUCTS.ID= TICKETLINES.PRODUCT AND PRODUCTS.TICKETGROUP='Y' " +
                "AND RECEIPTS.ADVANCEBOOKING = 'N' AND RECEIPTS.MONEY = ? " +
                "GROUP BY PRODUCTS.NAME"
                , SerializerWriteString.INSTANCE
                , new SerializerReadClass(PaymentsModel.GroupSalesLine.class))
                .list(app.getActiveCashIndex());
        if (asales == null) {
            p.m_lGroupsales = new ArrayList<GroupSalesLine>();
        } else {
            p.m_lGroupsales = aGroupsales;
        }




    List<GroupAdvSalesLine> GroupAdvasales = new StaticSentence(app.getSession(),
                "SELECT PRODUCTS.NAME, SUM(TICKETLINES.UNITS), SUM(TICKETLINES.PRICE) " +
                "FROM RECEIPTS, TICKETLINES, PRODUCTS, TICKETS WHERE RECEIPTS.ID = TICKETS.ID AND TICKETS.ID = TICKETLINES.TICKET AND PRODUCTS.ID= TICKETLINES.PRODUCT AND PRODUCTS.TICKETGROUP='Y' " +
                "AND RECEIPTS.ADVANCEBOOKING = 'Y' AND TICKETS.TICKETTYPE=0 AND RECEIPTS.MONEY = ? " +
                "GROUP BY PRODUCTS.NAME"
                , SerializerWriteString.INSTANCE
                , new SerializerReadClass(PaymentsModel.GroupAdvSalesLine.class))
                .list(app.getActiveCashIndex());
        if (Advasales == null) {
            p.m_lGroupAdvsales = new ArrayList<GroupAdvSalesLine>();
        } else {
            p.m_lGroupAdvsales = GroupAdvasales;
        }

          List<RefSalesLine> Refasales = new StaticSentence(app.getSession(),
                "SELECT PRODUCTS.NAME, SUM(TICKETLINES.UNITS), SUM(TICKETLINES.PRICE) " +
                "FROM RECEIPTS, TICKETLINES, PRODUCTS, TICKETS WHERE RECEIPTS.ID = TICKETS.ID AND TICKETS.ID = TICKETLINES.TICKET AND PRODUCTS.ID= TICKETLINES.PRODUCT " +
                "AND TICKETS.TICKETTYPE=1 AND RECEIPTS.MONEY = ? " +
                "GROUP BY PRODUCTS.NAME"
                , SerializerWriteString.INSTANCE
                , new SerializerReadClass(PaymentsModel.RefSalesLine.class))
                .list(app.getActiveCashIndex());
        if (Refasales == null) {
            p.m_lRefsales = new ArrayList<RefSalesLine>();
        } else {
            p.m_lRefsales = Refasales;
        }
        Object[] valDiscounttickets = (Object []) new StaticSentence(app.getSession()
            , "SELECT SUM(TICKETS.DISCOUNT) FROM TICKETS, RECEIPTS WHERE RECEIPTS.ID = TICKETS.ID " +
                "AND RECEIPTS.MONEY = ? "
            , SerializerWriteString.INSTANCE
            , new SerializerReadBasic(new Datas[] {Datas.DOUBLE}))
            .find(app.getActiveCashIndex());

        if (valDiscounttickets == null) {
             p.m_dDiscount = new Double(0.0);
        } else {
            p.m_dDiscount = (Double) valDiscounttickets[0];
        }
         List<DiscountSalesLine> Disasales = new StaticSentence(app.getSession(),
                "SELECT PRODUCTS.NAME, SUM(TICKETLINES.UNITS) " +
                "FROM RECEIPTS, TICKETLINES, PRODUCTS, TICKETS WHERE RECEIPTS.ID = TICKETS.ID AND TICKETS.ID = TICKETLINES.TICKET AND PRODUCTS.ID=TICKETLINES.PRODUCT  " +
                "AND TICKETS.DISCOUNT!=0 AND RECEIPTS.MONEY = ? " +
                "GROUP BY PRODUCTS.NAME"
                , SerializerWriteString.INSTANCE
                , new SerializerReadClass(PaymentsModel.DiscountSalesLine.class))
                .list(app.getActiveCashIndex());
        if (Disasales == null) {
            p.m_lDisasales = new ArrayList<DiscountSalesLine>();
        } else {
            p.m_lDisasales = Disasales;
        }
         Object[] valDiscountAmt = (Object []) new StaticSentence(app.getSession()
            , "SELECT DISCOUNTAMOUNT FROM DISCOUNTSETUP WHERE ACTIVE= ? "
            , SerializerWriteString.INSTANCE
            , new SerializerReadBasic(new Datas[] {Datas.DOUBLE}))
            .find(m_dActive);

        if (valDiscountAmt == null) {
             p.m_dDiscountAmt = new Double(0.0);
        } else {
            p.m_dDiscountAmt = (Double) valDiscountAmt[0];
        }
        return p;
    }

    public int getPayments() {
        return m_iPayments.intValue();
    }
    public double getTotal() {
        return m_dPaymentsTotal.doubleValue();
    }
    
     public double getCancelFee() {
        return  m_dCancellationFee.doubleValue();
    }


    public String getHost() {
        return m_sHost;
    }
    public int getSequence() {
        return m_iSeq;
    }
    public Date getDateStart() {
        return m_dDateStart;
    }
    public void setDateEnd(Date dValue) {
        m_dDateEnd = dValue;
    }
    public Date getDateEnd() {
        return m_dDateEnd;
    }
     public String printSysDate() {

        return sysDate;
     }
    
    public String printHost() {
        return m_sHost;
    }
    public String printSequence() {
        return Formats.INT.formatValue(m_iSeq);
    }
    public String printDateStart() {
        return Formats.TIMESTAMP.formatValue(m_dDateStart);
    }
    public String printDateEnd() {
        return Formats.TIMESTAMP.formatValue(m_dDateEnd);
    }  
    
    public String printPayments() {
        return Formats.INT.formatValue(m_iPayments);
    }
    public String printDiscount() {
        return Formats.CURRENCY.formatValue(m_dDiscount);
    }
     public String printNorDiscount() {
        return Formats.CURRENCY.formatValue(m_dNorDiscount);
    }
    public String printNormalDisTicket() {

        if(m_dDiscountAmt!=0){
            m_dNorTotalTicket =  m_dNorDiscount/m_dDiscountAmt;
        }else{
            m_dNorTotalTicket = 0.0;
        }
        return Formats.INT.formatValue(m_dNorTotalTicket);
    }
    public String printAdvDisTicket() {

        if(m_dDiscountAmt!=0){
           m_dAdvTotalTicket =  m_dAdvDiscount/m_dDiscountAmt;
        }else{
          m_dAdvTotalTicket = 0.0;
        }
        return Formats.INT.formatValue(m_dAdvTotalTicket);
    }

    public String printAdvDiscount() {
        return Formats.CURRENCY.formatValue(m_dAdvDiscount);
    }
     public String printPaymentsTotal() {
        try{
            return Formats.CURRENCY.formatValue(m_dPaymentsTotal+m_dRefPaymentsTotal+m_dCancellationFee);
        }catch(Exception e){
            return Formats.CURRENCY.formatValue(m_dPaymentsTotal+0+m_dCancellationFee);
        }
    }
     public String printTotalUnits(){
           return Formats.INT.formatValue(m_dTotalUnits);
       }
    public String printCancelFee() {
            return Formats.CURRENCY.formatValue(m_dCancellationFee);
    }
    
    public List<PaymentsLine> getPaymentLines() {
        return m_lpayments;
    }
    
    public int getSales() {
        return m_iSales == null ? 0 : m_iSales.intValue();
    }    
    public String printSales() {
        return Formats.INT.formatValue(m_iSales);
    }
    public String printSalesBase() {
        return Formats.CURRENCY.formatValue(m_dSalesBase);
    }     
    public String printSalesTaxes() {
        return Formats.CURRENCY.formatValue(m_dSalesTaxes);
    }     
    public String printSalesTotal() {            
        return Formats.CURRENCY.formatValue((m_dSalesBase == null || m_dSalesTaxes == null)
                ? null
                : m_dSalesBase + m_dSalesTaxes);
    }     
    public List<SalesLine> getSaleLines() {
        return m_lsales;
    }
     public List<AdvSalesLine> getAdvSaleLines() {
        return m_lAdvsales;
    }
     public List<RefSalesLine> getRefSaleLines() {
        return m_lRefsales;
    }
     public List<DiscountSalesLine> getDisSaleLines() {
        return m_lDisasales;
    }
    public List<GroupSalesLine> getGroupSaleLines() {
        return m_lGroupsales;
    }
      public List<GroupAdvSalesLine> getGroupAdvSaleLines() {
        return m_lGroupAdvsales;
    }
     public static class AdvSalesLine implements SerializableRead {

        private String m_AdvSalesProductName;
        private Double m_AdvSalesProduct;
        private int m_AdvSalesProductCount;

        public void readValues(DataRead dr) throws BasicException {
            m_AdvSalesProductName = dr.getString(1);
            m_AdvSalesProductCount = dr.getInt(2);
            m_AdvSalesProduct = dr.getDouble(3);
        }
        public String printAdvProductName() {
            return m_AdvSalesProductName;
        }
        public String printAdvProduct() {
            return Formats.CURRENCY.formatValue(m_AdvSalesProduct);
        }
        public String printAdvProductCount() {
            return Formats.INT.formatValue(m_AdvSalesProductCount);
        }
        public String getAdvProductName() {
            return m_AdvSalesProductName;
        }
        public Double getAdvProduct() {
            return m_AdvSalesProduct;
        }
         public int getAdvproductCount(){
            return m_AdvSalesProductCount;
        }
    }
      public AbstractTableModel getAdvSalesModel() {
        return new AbstractTableModel() {
            public String getColumnName(int column) {
                return AppLocal.getIntString(SALEHEADERS[column]);
            }
            public int getRowCount() {
                return m_lAdvsales.size();
            }
            public int getColumnCount() {
                return SALEHEADERS.length;
            }
            public Object getValueAt(int row, int column) {
                AdvSalesLine l = m_lAdvsales.get(row);
                switch (column) {
                case 0: return l.getAdvProductName();
                case 1:return l.getAdvproductCount();
                case 2: return l.getAdvProduct();
                default: return null;
                }
            }
        };
    }

    
    public AbstractTableModel getPaymentsModel() {
        return new AbstractTableModel() {
            public String getColumnName(int column) {
                return AppLocal.getIntString(PAYMENTHEADERS[column]);
            }
            public int getRowCount() {
                return m_lpayments.size();
            }
            public int getColumnCount() {
                return PAYMENTHEADERS.length;
            }
            public Object getValueAt(int row, int column) {
                PaymentsLine l = m_lpayments.get(row);
                switch (column) {
                case 0: return l.getType();
                case 1: return l.getValue();
                default: return null;
                }
            }  
        };
    }
    
    public static class SalesLine implements SerializableRead {
        
        private String m_SalesProductName;
        private Double m_SalesProduct;
        private int m_SalesProductCount;
        
        public void readValues(DataRead dr) throws BasicException {
            m_SalesProductName = dr.getString(1);
            m_SalesProductCount = dr.getInt(2);
            m_SalesProduct = dr.getDouble(3);
            System.out.println ("entr prodcount"+m_SalesProductCount);
        }
        
        public String printProductName() {
            return m_SalesProductName;
        }      
        public String printProduct() {
            return Formats.CURRENCY.formatValue(m_SalesProduct);
        }
        public String printProductCount() {
            return Formats.INT.formatValue(m_SalesProductCount);
        }
        public String getProductName() {
            return m_SalesProductName;
        }
        public Double getProduct() {
            return m_SalesProduct;
        }
         public int getproductCount(){
            return m_SalesProductCount;
        }
    }
public static class GroupSalesLine implements SerializableRead {

        private String m_GroupSalesProductName;
        private Double m_GroupSalesProduct;
        private int m_GroupSalesProductCount;

        public void readValues(DataRead dr) throws BasicException {
            m_GroupSalesProductName = dr.getString(1);
            m_GroupSalesProductCount = dr.getInt(2);
            m_GroupSalesProduct = dr.getDouble(3);
        }
        public String printGroupProductName() {
            return m_GroupSalesProductName;
        }
        public String printGroupProduct() {
            return Formats.CURRENCY.formatValue(m_GroupSalesProduct);
        }
        public String printGroupProductCount() {
            return Formats.INT.formatValue(m_GroupSalesProductCount);
        }
        public String getGroupProductName() {
            return m_GroupSalesProductName;
        }
        public Double getGroupProduct() {
            return m_GroupSalesProduct;
        }
         public int getGroupproductCount(){
            return m_GroupSalesProductCount;
        }
    }
    public static class GroupAdvSalesLine implements SerializableRead {

        private String m_GroupAdvSalesProductName;
        private Double m_GroupAdvSalesProduct;
        private int m_GroupAdvSalesProductCount;

        public void readValues(DataRead dr) throws BasicException {
            m_GroupAdvSalesProductName = dr.getString(1);
            m_GroupAdvSalesProductCount = dr.getInt(2);
            m_GroupAdvSalesProduct = dr.getDouble(3);
        }
        public String printGroupAdvProductName() {
            return m_GroupAdvSalesProductName;
        }
        public String printGroupAdvProduct() {
            return Formats.CURRENCY.formatValue(m_GroupAdvSalesProduct);
        }
        public String printGroupAdvProductCount() {
            return Formats.INT.formatValue(m_GroupAdvSalesProductCount);
        }
        public String getGroupAdvProductName() {
            return m_GroupAdvSalesProductName;
        }
        public Double getGroupAdvProduct() {
            return m_GroupAdvSalesProduct;
        }
         public int getGroupAdvproductCount(){
            return m_GroupAdvSalesProductCount;
        }
    }

    public AbstractTableModel getSalesModel() {
        return new AbstractTableModel() {
            public String getColumnName(int column) {
                return AppLocal.getIntString(SALEHEADERS[column]);
            }
            public int getRowCount() {
                return m_lsales.size();
            }
            public int getColumnCount() {
                return SALEHEADERS.length;
            }
            public Object getValueAt(int row, int column) {
                SalesLine l = m_lsales.get(row);
                switch (column) {
                case 0: return l.getProductName();
                case 1:return l.getproductCount();
                case 2: return l.getProduct();
                default: return null;
                }
            }  
        };
    }
public AbstractTableModel getRefSalesModel() {
        return new AbstractTableModel() {
            public String getColumnName(int column) {
                return AppLocal.getIntString(SALEHEADERS[column]);
            }
            public int getRowCount() {
                return m_lRefsales.size();
            }
            public int getColumnCount() {
                return SALEHEADERS.length;
            }
            public Object getValueAt(int row, int column) {
                RefSalesLine l = m_lRefsales.get(row);
                switch (column) {
                case 0: return l.getRefProductName();
                case 1:return l.getRefproductCount();
                case 2: return l.getRefProduct();
                default: return null;
                }
            }
        };
    }
    public static class RefSalesLine implements SerializableRead {

        private String m_RefSalesProductName;
        private Double m_RefSalesProduct;
        private int m_RefSalesProductCount;

        public void readValues(DataRead dr) throws BasicException {
            m_RefSalesProductName = dr.getString(1);
            m_RefSalesProductCount = dr.getInt(2);
            m_RefSalesProduct = dr.getDouble(3);
        }
        public String printRefProductName() {
            return m_RefSalesProductName;
        }
        public String printRefProduct() {
            return Formats.CURRENCY.formatValue(m_RefSalesProduct);
        }
        public String printRefProductCount() {
            return Formats.INT.formatValue(m_RefSalesProductCount);
        }
        public String getRefProductName() {
            return m_RefSalesProductName;
        }
        public Double getRefProduct() {
            return m_RefSalesProduct;
        }
         public int getRefproductCount(){
            return m_RefSalesProductCount;
        }
    }
public static class DiscountSalesLine implements SerializableRead {

        private String m_DisSalesProductName;
        private int m_DisSalesProductCount;

        public void readValues(DataRead dr) throws BasicException {
            m_DisSalesProductName = dr.getString(1);
            m_DisSalesProductCount = dr.getInt(2);

        }
        public String printDisProductName() {
            return m_DisSalesProductName;
        }

        public String printDisProductCount() {
            return Formats.INT.formatValue(m_DisSalesProductCount);
        }
        public String getDisProductName() {
            return m_DisSalesProductName;
        }

         public int getDisproductCount(){
            return m_DisSalesProductCount;
        }
    }

     public AbstractTableModel getDisSalesModel() {
        return new AbstractTableModel() {
            public String getColumnName(int column) {
                return AppLocal.getIntString(SALEHEADERS[column]);
            }
            public int getRowCount() {
                return m_lDisasales.size();
            }
            public int getColumnCount() {
                return SALEHEADERS.length;
            }
            public Object getValueAt(int row, int column) {
                DiscountSalesLine l = m_lDisasales.get(row);
                switch (column) {
                case 0: return l.getDisProductName();
                case 1:return l.getDisproductCount();
                default: return null;
                }
            }
        };
    }
      public AbstractTableModel getGroupSalesModel() {
        return new AbstractTableModel() {
            public String getColumnName(int column) {
                return AppLocal.getIntString(SALEHEADERS[column]);
            }
            public int getRowCount() {
                return m_lGroupsales.size();
            }
            public int getColumnCount() {
                return SALEHEADERS.length;
            }
            public Object getValueAt(int row, int column) {
                GroupSalesLine l = m_lGroupsales.get(row);
                switch (column) {
                case 0: return l.getGroupProductName();
                case 1:return l.getGroupproductCount();
                case 2: return l.getGroupProduct();
                default: return null;
                }
            }
        };
    }

     public AbstractTableModel getGroupAdvSalesModel() {
        return new AbstractTableModel() {
            public String getColumnName(int column) {
                return AppLocal.getIntString(SALEHEADERS[column]);
            }
            public int getRowCount() {
                return m_lGroupAdvsales.size();
            }
            public int getColumnCount() {
                return SALEHEADERS.length;
            }
            public Object getValueAt(int row, int column) {
                GroupAdvSalesLine l = m_lGroupAdvsales.get(row);
                switch (column) {
                case 0: return l.getGroupAdvProductName();
                case 1:return l.getGroupAdvproductCount();
                case 2: return l.getGroupAdvProduct();
                default: return null;
                }
            }
        };
    }

    public static class PaymentsLine implements SerializableRead {
        
        private String m_PaymentType;
        private Double m_PaymentValue;
        
        public void readValues(DataRead dr) throws BasicException {
            m_PaymentType = dr.getString(1);
            m_PaymentValue = dr.getDouble(2);
        }
        
        public String printType() {
            return AppLocal.getIntString("transpayment." + m_PaymentType);
        }
        public String getType() {
            return m_PaymentType;
        }
        public String printValue() {
            return Formats.CURRENCY.formatValue(m_PaymentValue);
        }
        public Double getValue() {
            return m_PaymentValue;
        }        
    }
}    