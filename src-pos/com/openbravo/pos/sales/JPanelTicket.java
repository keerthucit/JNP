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
package com.openbravo.pos.sales;

import com.openbravo.pos.forms.AppUser;
import java.text.ParseException;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Date;

import com.openbravo.data.gui.ComboBoxValModel;
import com.openbravo.data.gui.MessageInf;
import com.openbravo.pos.printer.*;

import com.openbravo.pos.forms.JPanelView;
import com.openbravo.pos.forms.AppView;
import com.openbravo.pos.forms.AppLocal;
import com.openbravo.pos.panels.JProductFinder;
import com.openbravo.pos.scale.ScaleException;
import com.openbravo.pos.payment.JPaymentSelect;
import com.openbravo.basic.BasicException;
import com.openbravo.beans.JCalendarDialog;
import com.openbravo.data.gui.ListKeyed;
import com.openbravo.data.loader.Datas;
import com.openbravo.data.loader.SentenceList;
import com.openbravo.data.loader.SerializerWriteBasic;
import com.openbravo.data.loader.StaticSentence;
import com.openbravo.pos.catalog.JCatalog;
import com.openbravo.pos.customers.CustomerInfoExt;
import com.openbravo.pos.customers.DataLogicCustomers;
import com.openbravo.pos.customers.JCustomerFinder;
import com.openbravo.pos.scripting.ScriptEngine;
import com.openbravo.pos.scripting.ScriptException;
import com.openbravo.pos.scripting.ScriptFactory;
import com.openbravo.pos.forms.DataLogicSystem;
import com.openbravo.pos.forms.DataLogicSales;
import com.openbravo.pos.forms.BeanFactoryApp;
import com.openbravo.pos.forms.BeanFactoryException;
import com.openbravo.pos.forms.DiscountInfo;
import com.openbravo.pos.forms.JRootApp;
import com.openbravo.pos.forms.announcementInfo;
import com.openbravo.pos.forms.cancellationInfo;
import com.openbravo.pos.inventory.TaxCategoryInfo;
import com.openbravo.pos.panels.CloseshowInfo;
import com.openbravo.pos.panels.PaymentsHandOverModel;
import com.openbravo.pos.panels.PaymentsModel;
import com.openbravo.pos.panels.PaymentsShowModel;
import com.openbravo.pos.panels.showInfo;
import com.openbravo.pos.payment.JPaymentSelectReceipt;
import com.openbravo.pos.payment.JPaymentSelectRefund;
import com.openbravo.pos.printer.printer.ImagePrinter;
import com.openbravo.pos.printer.printer.TicketLineConstructor;
import com.openbravo.pos.sales.shared.JTicketsBagShared;
import com.openbravo.pos.ticket.ProductInfoExt;
import com.openbravo.pos.ticket.TaxInfo;
import com.openbravo.pos.ticket.TicketInfo;
import com.openbravo.pos.ticket.TicketLineInfo;
import com.openbravo.pos.util.JRPrinterAWT300;
import com.openbravo.pos.util.ReportUtils;
import java.awt.print.PrinterException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.UUID;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.print.PrintService;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRMapArrayDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

/**
 *
 * @author adrianromero
 */
public abstract class JPanelTicket extends JPanel implements JPanelView, BeanFactoryApp, TicketsEditor {

    // Variable numerica
    private final static int NUMBERZERO = 0;
    private final static int NUMBERVALID = 1;
    private final static int NUMBER_INPUTZERO = 0;
    private final static int NUMBER_INPUTZERODEC = 1;
    private final static int NUMBER_INPUTINT = 2;
    private final static int NUMBER_INPUTDEC = 3;
    private final static int NUMBER_PORZERO = 4;
    private final static int NUMBER_PORZERODEC = 5;
    private final static int NUMBER_PORINT = 6;
    private final static int NUMBER_PORDEC = 7;
    private JRootApp m_RootApp;
    protected JTicketLines m_ticketlines;
    protected TicketsEditor ticketEditor;
    // private Template m_tempLine;
    private TicketParser m_TTP;
    public JPanelTicketSales jPanelTicketSales;
    protected TicketInfo m_oTicket;
    protected Object m_oTicketExt;
    // Estas tres variables forman el estado...
    private int m_iNumberStatus;
    private int m_iNumberStatusInput;
    private int m_iNumberStatusPor;
    private StringBuffer m_sBarcode;
    private JTicketsBag m_ticketsbag;
    private SentenceList senttax;
    private ListKeyed taxcollection;
    // private ComboBoxValModel m_TaxModel;
    private SentenceList senttaxcategories;
    private ListKeyed taxcategoriescollection;
    private ComboBoxValModel taxcategoriesmodel;
    public java.util.List<announcementInfo> announcement;
    public java.util.List<cancellationInfo> cancellation;
    private ListKeyed announcementValues;
    private TaxesLogic taxeslogic;
    private boolean dateMode = false;
    protected JPanelButtons m_jbtnconfig;
    protected AppView m_App;
    protected DataLogicSystem dlSystem;
    protected DataLogicSales dlSales;
    protected DataLogicCustomers dlCustomers;
    private JPaymentSelect paymentdialogreceipt;
    private JPaymentSelect paymentdialogrefund;
    public java.util.List<showInfo> showDetails = null;
    public java.util.List<showInfo> currentShow = null;
    public java.util.List<showInfo> closedshowDetails = null;
    int availableSeats = 0;
    int extSeats = 0;
    int seats = 0;
    String endTime;
    public String currentShowName = null;
    private String loggedUserId = null;
    String nextShowName = null;
    private PaymentsShowModel m_PaymentsToClose;
    private PaymentsModel m_PaymentsToCloseCash;
    private PaymentsHandOverModel m_PaymentsToMoneyTransfer;
    public static final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";
    public String sysShowName = null;
    private JCatalog catalog;
    private SentenceList showDetailslist;
    String advShow = null;
    public String advanceShowName = "";
    String refundShowDate;
    String advanceShowDate = null;
    public java.util.List<DiscountInfo> discountDetails;
    public double noOfSeats=0;
    /**
     * Creates new form JTicketView
     */
    public JPanelTicket() {

        initComponents();

    }

    public void init(AppView app) throws BeanFactoryException {

        m_App = app;
        dlSystem = (DataLogicSystem) m_App.getBean("com.openbravo.pos.forms.DataLogicSystem");
        dlSales = (DataLogicSales) m_App.getBean("com.openbravo.pos.forms.DataLogicSales");
        dlCustomers = (DataLogicCustomers) m_App.getBean("com.openbravo.pos.customers.DataLogicCustomers");

        // borramos el boton de bascula si no hay bascula conectada
        if (!m_App.getDeviceScale().existsScale()) {
//            m_jbtnScale.setVisible(false);
        }

        m_ticketsbag = getJTicketsBag();

        m_jPanelBag.add(m_ticketsbag.getBagComponent(), BorderLayout.LINE_START);
        add(m_ticketsbag.getNullComponent(), "null");

        m_ticketlines = new JTicketLines(dlSystem.getResourceAsXML("Ticket.Line"));


        m_jPanelCentral.add(m_ticketlines, java.awt.BorderLayout.CENTER);

        m_TTP = new TicketParser(m_App.getDeviceTicket(), dlSystem);

        // Los botones configurables...
        m_jbtnconfig = new JPanelButtons("Ticket.Buttons", this);
        m_jButtonsExt.add(m_jbtnconfig);

        // El panel de los productos o de las lineas...        
        catcontainer.add(getSouthComponent(), BorderLayout.CENTER);

        // El modelo de impuestos
        senttax = dlSales.getTaxList();
        senttaxcategories = dlSales.getTaxCategoriesList();

        taxcategoriesmodel = new ComboBoxValModel();

        // ponemos a cero el estado
        stateToZero();

        // inicializamos
        m_oTicket = null;
        m_oTicketExt = null;
        m_jTxtDate.setVisible(false);
        m_jbtndate.setVisible(false);
        m_jLblDate.setVisible(false);
        m_jTxtDate.setText("");
        m_jCboShowlist.setEnabled(false);
       
        m_jEnter.setVisible(false);


    }

    public Object getBean() {
        return this;
    }

    public JComponent getComponent() {
        return this;
    }

    public void activate() throws BasicException {
        getBean();
        getComponent();
        paymentdialogreceipt = JPaymentSelectReceipt.getDialog(this);
        paymentdialogreceipt.init(m_App);
        paymentdialogrefund = JPaymentSelectRefund.getDialog(this);
        paymentdialogrefund.init(m_App);
       
        // impuestos incluidos seleccionado ?
        m_jaddtax.setSelected("true".equals(m_jbtnconfig.getProperty("taxesincluded")));

        // Inicializamos el combo de los impuestos.
        java.util.List<TaxInfo> taxlist = senttax.list();
        taxcollection = new ListKeyed<TaxInfo>(taxlist);
        java.util.List<TaxCategoryInfo> taxcategorieslist = senttaxcategories.list();
        taxcategoriescollection = new ListKeyed<TaxCategoryInfo>(taxcategorieslist);

        taxcategoriesmodel = new ComboBoxValModel(taxcategorieslist);
        m_jTax.setModel(taxcategoriesmodel);

        String taxesid = m_jbtnconfig.getProperty("taxcategoryid");
        if (taxesid == null) {
            if (m_jTax.getItemCount() > 0) {
                m_jTax.setSelectedIndex(0);
            }
        } else {
            taxcategoriesmodel.setSelectedKey(taxesid);
        }

        taxeslogic = new TaxesLogic(taxlist);

        if (m_App.getAppUserView().getUser().hasPermission("sales.ChangeTaxOptions")) {
            m_jTax.setVisible(true);
            m_jaddtax.setVisible(true);
        } else {
            m_jTax.setVisible(false);
            m_jaddtax.setVisible(false);
        }
       

        m_jDelete.setEnabled(m_App.getAppUserView().getUser().hasPermission("sales.EditLines"));
        m_jNumberKeys.setMinusEnabled(m_App.getAppUserView().getUser().hasPermission("sales.EditLines"));
        m_jNumberKeys.setEqualsEnabled(m_App.getAppUserView().getUser().hasPermission("sales.Total"));
        m_jbtnconfig.setPermissions(m_App.getAppUserView().getUser());

        m_ticketsbag.activate();
    }

    public boolean deactivate() {

        return m_ticketsbag.deactivate();
    }

    protected abstract JTicketsBag getJTicketsBag();

    protected abstract Component getSouthComponent();

    protected abstract void resetSouthComponent();

    public void setActiveTicket(TicketInfo oTicket, Object oTicketExt) {
        m_oTicket = oTicket;
        m_oTicketExt = oTicketExt;

        if (m_oTicket != null) {
            noOfSeats=0;
            // Asign preeliminary properties to the receipt
            m_oTicket.setUser(m_App.getAppUserView().getUser().getUserInfo());
            m_oTicket.setActiveCash(m_App.getActiveCashIndex());
            m_oTicket.setActiveShow(m_App.getActiveShowIndex());
            m_oTicket.setDate(new Date()); // Set the edition date.

        }

        executeEvent(m_oTicket, m_oTicketExt, "ticket.show");
        dlSales.deleteSharedTickets();
        refreshTicket();
    }

    public TicketInfo getActiveTicket() {
        return m_oTicket;
    }

    private void populateShow(String date) {
        Vector<String> shows = getShowNames(date);
        m_jCboShowlist.setModel(new DefaultComboBoxModel(shows));
    }

    private Vector<String> getShowNames(String date) {

        Vector<String> showList = new Vector<String>();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat dt = new SimpleDateFormat("dd-MM-yyyy");
        Date createdDate = new Date();
        String sysDate = null;
        sysDate = format.format(createdDate);
        String advanceDate = m_jTxtDate.getText();
        if (!advanceDate.equals("")) {
            Date advShowDate = null;
            try {
                advShowDate = dt.parse(advanceDate);
            } catch (ParseException ex) {
                Logger.getLogger(JPanelTicket.class.getName()).log(Level.SEVERE, null, ex);
            }
            advanceDate = format.format(advShowDate);
        }
        if (sysDate.equals(advanceDate)) {
            try {
                showDetails = dlSales.getAdvanceShowList(date, sysShowName);
            } catch (BasicException ex) {
                Logger.getLogger(JPanelTicket.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            try {
                showDetails = dlSales.getCurrentShowList(date);
            } catch (BasicException ex) {
                Logger.getLogger(JPanelTicket.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        showList.add(0, "");
        for (int i = 0; i < showDetails.size(); i++) {
            showList.add(i + 1, showDetails.get(i).getShowName());
        }
        return showList;
    }

    private void refreshTicket() {

        int showCount = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        SimpleDateFormat datetime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
        SimpleDateFormat dfst = new SimpleDateFormat("dd-MM-yyyy");
        DateFormat dfet = new SimpleDateFormat("yyyy-MM-dd 23:59:59");
        String CurrentTime = sdf.format(new Date()).toString();
        String sysDateTime = datetime.format(new Date()).toString();
        String specialShowDate = dt.format(new Date()).toString();
        Date createdDate = new Date();
        Date convertToDate = null;
        Date currentDate = null;
        String advShowname = null;
        String advshowDate = m_jTxtDate.getText();

        try {
            currentDate = datetime.parse(sysDateTime);
        } catch (ParseException ex) {
            Logger.getLogger(JPanelTicket.class.getName()).log(Level.SEVERE, null, ex);
        }

        CardLayout cl = (CardLayout) (getLayout());

        if (m_oTicket == null) {
            m_ticketlines.clearTicketLines();
            m_jTotalEuros.setText(null);

            stateToZero();

            // Muestro el panel de nulos.
            cl.show(this, "null");
            resetSouthComponent();

        } else {
            if (m_oTicket.getTicketType() == TicketInfo.RECEIPT_REFUND) {

                //Make disable Search and Edit Buttons
                m_jCloseDay.setVisible(false);
                refundShowDate = m_oTicket.getadvanceDate();
                m_jCloseShow.setVisible(false);
                m_jCboAdvbook.setVisible(false);
                m_jLblDate.setVisible(false);
                m_jTxtDate.setVisible(false);
                m_jbtndate.setVisible(false);
                m_jPanTotals.setVisible(true);
                m_jPanRefundTotals.setVisible(true);
                m_jlblAvailSeats.setVisible(false);
                m_jAvailSeats.setVisible(false);
                m_jTotalEuros.setVisible(false);
                m_jlblDiscount.setVisible(false);
                m_jTxtDiscount.setVisible(false);
                m_jLblTotalEuros1.setVisible(false);
                m_jTxtCancelFee.setText("");
                m_jUp.setEnabled(false);
                m_jDown.setEnabled(false);
                m_jDelete.setEnabled(false);
                m_jList.setEnabled(false);
                m_jEditLine.setEnabled(false);
                m_jNumberKeys.setEnabled(false);
                m_jNumberKeys.setEqualsButton();
                m_jReset.setVisible(false);
                m_jMoneyHandover.setVisible(false);
            } else {
                m_jCboAdvbook.setSelected(false);
                m_jLblDate.setVisible(false);
                m_jbtndate.setVisible(false);
                m_jTxtDate.setVisible(false);
                m_jTxtDate.setText("");
                m_jCboShowlist.setEnabled(false);
                m_jPanTotals.setVisible(true);
                m_jPanRefundTotals.setVisible(false);

            }


            m_ticketlines.clearTicketLines();

            for (int i = 0; i < m_oTicket.getLinesCount(); i++) {
                m_ticketlines.addTicketLine(m_oTicket.getLine(i));
            }
            printPartialTotals();
            stateToZero();

            // Muestro el panel de tickets.
            cl.show(this, "ticket");
            resetSouthComponent();

            // activo el tecleador...
            m_jKeyFactory.setText(null);
            java.awt.EventQueue.invokeLater(new Runnable() {
                public void run() {
                    m_jKeyFactory.requestFocus();
                }
            });

            //       m_jTxtDate.setText(advshowDate);
            // if(m_jCboAdvbook.isSelected()==false){
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            // DateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");


            String sysDate = null;

            sysDate = format.format(createdDate);

            if (m_oTicket.getTicketType() == TicketInfo.RECEIPT_REFUND) {

                if (!refundShowDate.equals("")) {
                    try {
                        convertToDate = format.parse(refundShowDate);
                    } catch (ParseException ex) {
                        Logger.getLogger(JPanelTicket.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    refundShowDate = sysDate;
                    try {
                        convertToDate = format.parse(sysDate);
                    } catch (ParseException ex) {
                        Logger.getLogger(JPanelTicket.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

                String refundShowName = m_oTicket.getShowName();

                int availableCount = 0;
                int extSeatscount = 0;
                if (refundShowName.equals(null) || refundShowName.equals("")) {
                    m_jCboShowlist.setSelectedIndex(-1);

                } else {
                    try {
                        availableSeats = dlSales.getAvailableSeats(refundShowName, convertToDate);
                    } catch (BasicException ex) {
                        Logger.getLogger(JPanelTicket.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    populateShow(refundShowDate);
                    m_jAvailSeats.setText(Integer.toString(availableSeats));
                    m_jCboShowlist.setSelectedItem(m_oTicket.getShowName());

                }
            } else {

                if (m_jCboAdvbook.isSelected() == false) {


                    try {
                        showCount = dlSales.getClosecount(sysDateTime);
                    } catch (BasicException ex) {
                        Logger.getLogger(JPanelTicket.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    if (showCount == 1) {
                        currentShow = dlSales.getClosedShowDetails();

                    } else {
                        currentShow = dlSales.getCurrentShow(specialShowDate);
                    }
                    if (currentShow.size() != 0) {
                        int countAfrClose = 0;

                        if (!advanceShowName.equals("")) {
                            //currentShowName = advanceShowName;

                            Date advDate = null;
                            currentShowName = advanceShowName;

                            currentShow = dlSales.getShowDetails(currentShowName);
                            seats = currentShow.get(0).getnoofseats();
                            endTime = currentShow.get(0).getendTime();
                            String currentStartDateTime = dt.format(createdDate);
                            String currentEndDateTime = dfet.format(createdDate);
                            try {
                                advDate = dfst.parse(advanceShowDate);
                            } catch (ParseException ex) {
                                Logger.getLogger(JPanelTicket.class.getName()).log(Level.SEVERE, null, ex);
                            }

                            currentStartDateTime = dt.format(advDate);
                            populateShow(currentStartDateTime);
                            m_jCboShowlist.setSelectedItem(currentShowName);
                            int availableCount = 0;
                            int extSeatscount = 0;


                            try {
                                convertToDate = dfst.parse(advanceShowDate);
                            } catch (ParseException ex) {
                                Logger.getLogger(JPanelTicket.class.getName()).log(Level.SEVERE, null, ex);
                            }

                            if (currentShowName == null) {
                                m_jCboShowlist.setSelectedIndex(-1);
                            } else {

                                m_oTicket.setShowName(currentShowName);
                                m_oTicket.setEndTime(endTime);

                                try {
                                    availableCount = dlSales.getAvailableCount(currentShowName, convertToDate);
                                } catch (BasicException ex) {
                                    Logger.getLogger(JPanelTicket.class.getName()).log(Level.SEVERE, null, ex);
                                }
                                if (availableCount == 0) {
                                    try {
                                        dlSales.insertAvailableSeats(currentShowName, seats, convertToDate);
                                    } catch (BasicException ex) {
                                        Logger.getLogger(JPanelTicket.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                }
                                try {
                                    availableSeats = dlSales.getAvailableSeats(currentShowName, convertToDate);
                                } catch (BasicException ex) {
                                    Logger.getLogger(JPanelTicket.class.getName()).log(Level.SEVERE, null, ex);
                                }

                                m_jAvailSeats.setText(Integer.toString(availableSeats));
                                m_jCboAdvbook.setSelected(true);
                                m_jCboShowlist.setEnabled(true);
                                m_jLblDate.setVisible(true);
                                m_jbtndate.setVisible(true);
                                m_jTxtDate.setVisible(true);
                                m_jTxtDate.setText(advanceShowDate);

                            }

                        } else {
                            currentShowName = currentShow.get(0).getShowName();
                            String currentStartDateTime = dt.format(createdDate);
                            String currentEndDateTime = dfet.format(createdDate);
                            populateShow(currentStartDateTime);
                            m_jCboShowlist.setSelectedItem(currentShowName);

                            try {
                                countAfrClose = dlSales.getCountAfterClose(specialShowDate, sysDateTime, currentShowName);
                            } catch (BasicException ex) {
                                Logger.getLogger(JPanelTicket.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            if (countAfrClose != 0) {
                                m_jCboShowlist.setSelectedIndex(-1);
                                seats = 0;
                            } else {
                                if (currentShowName != null) {

                                    sysShowName = currentShowName;

                                    seats = currentShow.get(0).getnoofseats();
                                    endTime = currentShow.get(0).getendTime();
                                  

                                 
                                    int availableCount = 0;
                                    int extSeatscount = 0;


                                    try {
                                        convertToDate = format.parse(sysDate);
                                    } catch (ParseException ex) {
                                        Logger.getLogger(JPanelTicket.class.getName()).log(Level.SEVERE, null, ex);
                                    }

                                    if (currentShowName == null) {
                                        m_jCboShowlist.setSelectedIndex(-1);
                                    } else {

                                        m_oTicket.setShowName(sysShowName);
                                        m_oTicket.setEndTime(endTime);

                                        try {
                                            availableCount = dlSales.getAvailableCount(sysShowName, convertToDate);
                                        } catch (BasicException ex) {
                                            Logger.getLogger(JPanelTicket.class.getName()).log(Level.SEVERE, null, ex);
                                        }
                                        if (availableCount == 0) {
                                            try {
                                                dlSales.insertAvailableSeats(sysShowName, seats, createdDate);
                                            } catch (BasicException ex) {
                                                Logger.getLogger(JPanelTicket.class.getName()).log(Level.SEVERE, null, ex);
                                            }
                                        }
                                        try {
                                            availableSeats = dlSales.getAvailableSeats(sysShowName, convertToDate);
                                        } catch (BasicException ex) {
                                            Logger.getLogger(JPanelTicket.class.getName()).log(Level.SEVERE, null, ex);
                                        }

                                        m_jAvailSeats.setText(Integer.toString(availableSeats));
                                    }

                                } else {
                                    m_jCboShowlist.setSelectedIndex(-1);
                                    seats = 0;
                                }
                            }
                        }
                    } else {
                        m_jCboShowlist.setSelectedIndex(-1);
                        seats = 0;
                    }
                }
            }
        }
    }

    public void closeButton() {
        m_jCloseShow.setEnabled(true);
        m_jCloseDay.setEnabled(true);
    }

    private void printPartialTotals() {
        if (m_oTicket.getTicketType() == TicketInfo.RECEIPT_REFUND) {
            if (m_oTicket.getLinesCount() == 0) {
                m_jTotalRefundEuros.setText(null);
            } else {
                Date ticketDate = m_oTicket.getTicketDate();
                Date sysDate = new Date();

                SimpleDateFormat dt = new SimpleDateFormat("yy/MM/dd HH:mm:ss");
                String refticketDate = dt.format(ticketDate);
                String curDate = dt.format(sysDate);
                try {
                    ticketDate = dt.parse(refticketDate);
                    sysDate = dt.parse(curDate);
                } catch (ParseException ex) {
                    Logger.getLogger(JPanelTicket.class.getName()).log(Level.SEVERE, null, ex);
                }
                long diff = sysDate.getTime() - ticketDate.getTime();
                long diffMinutes = diff / (60 * 1000);
                java.util.List<cancellationInfo> cancellationInfo = new java.util.ArrayList<cancellationInfo>();
                cancellation = dlSales.getCancellationFee();
                cancellationInfo = cancellation;
                if (cancellationInfo.size() != 0) {
                    double cancellationFee = cancellationInfo.get(0).getcancelFee();
                    int timelimit = cancellationInfo.get(0).getTimeLimit();

                    if (timelimit > diffMinutes) {
                        m_jTotalRefundEuros.setText(m_oTicket.printTotal());
                    } else {
                        m_oTicket.setCancelFee(cancellationFee);

                        Double total = m_oTicket.getTotal() + cancellationFee;
                        String totalValue = Double.toString(total);
                        m_jTotalRefundEuros.setText(totalValue);
                        m_jTxtCancelFee.setText(Double.toString(cancellationFee));
                    }

                } else {
                    m_jTotalRefundEuros.setText(m_oTicket.printTotal());
                }
            }
        } else {
            if (m_oTicket.getLinesCount() == 0) {
                m_jTotalEuros.setText(null);
                m_jTxtDiscount.setText(null);
            } else {
                m_jTotalEuros.setText(m_oTicket.printTotal());
                m_jTxtDiscount.setText(m_oTicket.printDiscountTotal());
            }
        }
    }

    private void paintTicketLine(int index, TicketLineInfo oLine) {
        getDiscountDetails(oLine);
        if (executeEventAndRefresh("ticket.setline", new ScriptArg("index", index), new ScriptArg("line", oLine)) == null) {

            m_oTicket.setLine(index, oLine);
            m_ticketlines.setTicketLine(index, oLine);
            m_ticketlines.setSelectedIndex(index);

            visorTicketLine(oLine); // Y al visor tambien...
            printPartialTotals();
            stateToZero();

            // event receipt
            executeEventAndRefresh("ticket.change");
        }
    }

    private void addTicketLine(ProductInfoExt oProduct, double dMul, double dPrice) {
        TaxInfo tax = taxeslogic.getTaxInfo(oProduct.getTaxCategoryID(), m_oTicket.getCustomer());
        System.out.println("m_oTicket.getSeats"+m_oTicket.getSeats()+ "noOfSeats "+noOfSeats);
        if (!oProduct.getName().equals("") && (m_oTicket.getSeats()>noOfSeats)) {
            noOfSeats=noOfSeats+1;
            addTicketLine(new TicketLineInfo(oProduct, dMul, dPrice, tax, (java.util.Properties) (oProduct.getProperties().clone())));
        }
    }

    protected void addTicketLine(TicketLineInfo oLine) {

        getDiscountDetails(oLine);
        if (executeEventAndRefresh("ticket.addline", new ScriptArg("line", oLine)) == null) {

            if (oLine.isProductCom()) {

                // Comentario entonces donde se pueda
                int i = m_ticketlines.getSelectedIndex();

                // me salto el primer producto normal...
                if (i >= 0 && !m_oTicket.getLine(i).isProductCom()) {
                    i++;
                }

                // me salto todos los productos auxiliares...
                while (i >= 0 && i < m_oTicket.getLinesCount() && m_oTicket.getLine(i).isProductCom()) {
                    i++;
                }

                if (i >= 0) {
                    m_oTicket.insertLine(i, oLine);
                    m_ticketlines.insertTicketLine(i, oLine); // Pintamos la linea en la vista...                 
                } else {
                    Toolkit.getDefaultToolkit().beep();
                }
            } else {

                // Producto normal, entonces al finalnewline.getMultiply()
                m_oTicket.addLine(oLine);

                m_ticketlines.addTicketLine(oLine); // Pintamos la linea en la vista... 
            }

            visorTicketLine(oLine);
            printPartialTotals();
            stateToZero();

            // event receipt
            executeEventAndRefresh("ticket.change");
        }
    }

    private void removeTicketLine(int i) {

        if (executeEventAndRefresh("ticket.removeline", new ScriptArg("index", i)) == null) {

            if (m_oTicket.getLine(i).isProductCom()) {
                // Es un producto auxiliar, lo borro y santas pascuas.
                m_oTicket.removeLine(i);
                m_ticketlines.removeTicketLine(i);
            } else {

                // Es un producto normal, lo borro.


                m_oTicket.removeLine(i);
                m_ticketlines.removeTicketLine(i);

                // Y todos lo auxiliaries que hubiera debajo.
                while (i < m_oTicket.getLinesCount() && m_oTicket.getLine(i).isProductCom()) {
                    m_oTicket.removeLine(i);
                    m_ticketlines.removeTicketLine(i);
                }

            }

            visorTicketLine(null); // borro el visor 
            printPartialTotals(); // pinto los totales parciales...                           
            stateToZero(); // Pongo a cero    

            // event receipt
            executeEventAndRefresh("ticket.change");

        }
    }

    private ProductInfoExt getInputProduct() {
        ProductInfoExt oProduct = new ProductInfoExt(); // Es un ticket
        oProduct.setReference(null);
        oProduct.setCode(null);
        oProduct.setName("");
        oProduct.setTaxCategoryID(((TaxCategoryInfo) taxcategoriesmodel.getSelectedItem()).getID());

        oProduct.setPriceSell(includeTaxes(oProduct.getTaxCategoryID(), getInputValue()));

        return oProduct;
    }

    private double includeTaxes(String tcid, double dValue) {
        if (m_jaddtax.isSelected()) {
            TaxInfo tax = taxeslogic.getTaxInfo(tcid, m_oTicket.getCustomer());
            double dTaxRate = tax == null ? 0.0 : tax.getRate();
            return dValue / (1.0 + dTaxRate);
        } else {
            return dValue;
        }
    }

    private double getInputValue() {
        try {
            return Double.parseDouble(m_jPrice.getText());
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    private double getPorValue() {
        try {
            return Double.parseDouble(m_jPor.getText().substring(1));
        } catch (NumberFormatException e) {
            return 1.0;
        } catch (StringIndexOutOfBoundsException e) {
            return 1.0;
        }
    }

    private void stateToZero() {
        m_jPor.setText("");
        m_jPrice.setText("");
        m_sBarcode = new StringBuffer();

        m_iNumberStatus = NUMBER_INPUTZERO;
        m_iNumberStatusInput = NUMBERZERO;
        m_iNumberStatusPor = NUMBERZERO;
    }

    private void incProductByCode(String sCode) {
        // precondicion: sCode != null

        try {
            ProductInfoExt oProduct = dlSales.getProductInfoByCode(sCode);
            if (oProduct == null) {
                Toolkit.getDefaultToolkit().beep();
                new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.noproduct")).show(this);
                stateToZero();
            } else {
                // Se anade directamente una unidad con el precio y todo
                incProduct(oProduct);
            }
        } catch (BasicException eData) {
            stateToZero();
            new MessageInf(eData).show(this);
        }
    }

    private void incProductByCodePrice(String sCode, double dPriceSell) {
        // precondicion: sCode != null

        try {
            ProductInfoExt oProduct = dlSales.getProductInfoByCode(sCode);
            if (oProduct == null) {
                Toolkit.getDefaultToolkit().beep();
                new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.noproduct")).show(this);
                stateToZero();
            } else {
                // Se anade directamente una unidad con el precio y todo
                if (m_jaddtax.isSelected()) {
                    // debemos quitarle los impuestos ya que el precio es con iva incluido...
                    TaxInfo tax = taxeslogic.getTaxInfo(oProduct.getTaxCategoryID(), m_oTicket.getCustomer());
                    addTicketLine(oProduct, 1.0, dPriceSell / (1.0 + tax.getRate()));
                } else {
                    addTicketLine(oProduct, 1.0, dPriceSell);
                }
            }
        } catch (BasicException eData) {
            stateToZero();
            new MessageInf(eData).show(this);
        }
    }

    private void incProduct(ProductInfoExt prod) {

        if (prod.isScale() && m_App.getDeviceScale().existsScale()) {
            try {
                Double value = m_App.getDeviceScale().readWeight();
                if (value != null) {
                    incProduct(value.doubleValue(), prod);
                }
            } catch (ScaleException e) {
                Toolkit.getDefaultToolkit().beep();
                new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.noweight"), e).show(this);
                stateToZero();
            }
        } else {
            // No es un producto que se pese o no hay balanza
            incProduct(1.0, prod);
        }
    }

    private void incProduct(double dPor, ProductInfoExt prod) {
        // precondicion: prod != null
        addTicketLine(prod, dPor, prod.getPriceSell());
    }

    protected void buttonTransition(ProductInfoExt prod) {
        // precondicion: prod != null

        String showname = null;

        showname = m_jCboShowlist.getSelectedItem().toString();
        String advDate = m_jTxtDate.getText();
        if (m_iNumberStatusInput == NUMBERZERO && m_iNumberStatusPor == NUMBERZERO) {

            incProduct(prod);

        } else if (m_iNumberStatusInput == NUMBERVALID && m_iNumberStatusPor == NUMBERZERO) {

            incProduct(getInputValue(), prod);
        } else {
            Toolkit.getDefaultToolkit().beep();
        }

        if (!advDate.equals("")) {
            m_jCboAdvbook.setSelected(false);
            m_jLblDate.setVisible(false);
            m_jbtndate.setVisible(false);
            m_jTxtDate.setVisible(false);
            m_jTxtDate.setText("");
            m_jCboShowlist.setEnabled(false);
            m_jCboShowlist.setSelectedItem(showname);
            m_jCboShowlist.setEnabled(true);

            m_jCboAdvbook.setSelected(true);
            m_jLblDate.setVisible(true);
            m_jbtndate.setVisible(true);
            m_jTxtDate.setVisible(true);
            m_jTxtDate.setText(advDate);
            Date convertToDate = null;
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            DateFormat dst = new SimpleDateFormat("dd-MM-yyyy");
            Date createdDate = new Date();
            String sysDate = null;

            if (!advDate.equals("")) {
                sysDate = advDate;

            } else {
                sysDate = dst.format(createdDate);
            }
            try {
                convertToDate = dst.parse(sysDate);
            } catch (ParseException ex) {
                Logger.getLogger(JPanelTicket.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                availableSeats = dlSales.getAvailableSeats(showname, convertToDate);
            } catch (BasicException ex) {
                Logger.getLogger(JPanelTicket.class.getName()).log(Level.SEVERE, null, ex);
            }
            m_jAvailSeats.setText(Integer.toString(availableSeats));

        } else {

            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            Date createdDate = new Date();
            String sysDate = null;
            sysDate = df.format(createdDate);
            populateShow(sysDate);
            m_jCboShowlist.setSelectedItem(sysShowName);
            m_jCboShowlist.setEnabled(false);

            m_jCboAdvbook.setSelected(false);
            m_jLblDate.setVisible(false);
            m_jbtndate.setVisible(false);
            m_jTxtDate.setVisible(false);
            m_jTxtDate.setText("");
        }

    }

    private void stateTransition(char cTrans) {
        String advanceDate = m_jTxtDate.getText();
        String showName = m_jCboShowlist.getSelectedItem().toString();
        if (cTrans == '\n') {
            // Codigo de barras introducido
            if (m_sBarcode.length() > 0) {
                String sCode = m_sBarcode.toString();
                if (sCode.startsWith("c")) {
                    // barcode of a customers card
                    try {
                        CustomerInfoExt newcustomer = dlSales.findCustomerExt(sCode);
                        if (newcustomer == null) {
                            Toolkit.getDefaultToolkit().beep();
                            new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.nocustomer")).show(this);
                        } else {
                            m_oTicket.setCustomer(newcustomer);
//                            m_jTicketId.setText(m_oTicket.getName(m_oTicketExt));
                        }
                    } catch (BasicException e) {
                        Toolkit.getDefaultToolkit().beep();
                        new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.nocustomer"), e).show(this);
                    }
                    stateToZero();
                } else if (sCode.length() == 13 && sCode.startsWith("250")) {
                    // barcode of the other machine
                    ProductInfoExt oProduct = new ProductInfoExt(); // Es un ticket
                    oProduct.setReference(null); // para que no se grabe
                    oProduct.setCode(sCode);
                    oProduct.setName("Ticket " + sCode.substring(3, 7));
                    oProduct.setPriceSell(Double.parseDouble(sCode.substring(7, 12)) / 100);
                    oProduct.setTaxCategoryID(((TaxCategoryInfo) taxcategoriesmodel.getSelectedItem()).getID());
                    // Se anade directamente una unidad con el precio y todo
                    addTicketLine(oProduct, 1.0, includeTaxes(oProduct.getTaxCategoryID(), oProduct.getPriceSell()));
                } else if (sCode.length() == 13 && sCode.startsWith("210")) {
                    // barcode of a weigth product
                    incProductByCodePrice(sCode.substring(0, 7), Double.parseDouble(sCode.substring(7, 12)) / 100);
                } else {
                    incProductByCode(sCode);
                }
            } else {
                Toolkit.getDefaultToolkit().beep();
            }
        } else {
            // otro caracter
            // Esto es para el codigo de barras...
            m_sBarcode.append(cTrans);

            // Esto es para el los productos normales...
            if (cTrans == '\u007f') {
                stateToZero();

            } else if ((cTrans == '0')
                    && (m_iNumberStatus == NUMBER_INPUTZERO)) {
                m_jPrice.setText("0");
            } else if ((cTrans == '1' || cTrans == '2' || cTrans == '3' || cTrans == '4' || cTrans == '5' || cTrans == '6' || cTrans == '7' || cTrans == '8' || cTrans == '9')
                    && (m_iNumberStatus == NUMBER_INPUTZERO)) {
                // Un numero entero


                m_jPrice.setText(Character.toString(cTrans));
                m_iNumberStatus = NUMBER_INPUTINT;
                m_iNumberStatusInput = NUMBERVALID;

            } else if ((cTrans == '0' || cTrans == '1' || cTrans == '2' || cTrans == '3' || cTrans == '4' || cTrans == '5' || cTrans == '6' || cTrans == '7' || cTrans == '8' || cTrans == '9')
                    && (m_iNumberStatus == NUMBER_INPUTINT)) {

                // Un numero entero
                m_jPrice.setText(m_jPrice.getText() + cTrans);

            } else if (cTrans == '.' && m_iNumberStatus == NUMBER_INPUTZERO) {
                m_jPrice.setText("0.");
                m_iNumberStatus = NUMBER_INPUTZERODEC;
            } else if (cTrans == '.' && m_iNumberStatus == NUMBER_INPUTINT) {
                m_jPrice.setText(m_jPrice.getText() + ".");
                m_iNumberStatus = NUMBER_INPUTDEC;

            } else if ((cTrans == '0')
                    && (m_iNumberStatus == NUMBER_INPUTZERODEC || m_iNumberStatus == NUMBER_INPUTDEC)) {
                // Un numero decimal
                m_jPrice.setText(m_jPrice.getText() + cTrans);
            } else if ((cTrans == '1' || cTrans == '2' || cTrans == '3' || cTrans == '4' || cTrans == '5' || cTrans == '6' || cTrans == '7' || cTrans == '8' || cTrans == '9')
                    && (m_iNumberStatus == NUMBER_INPUTZERODEC || m_iNumberStatus == NUMBER_INPUTDEC)) {
                // Un numero decimal
                m_jPrice.setText(m_jPrice.getText() + cTrans);
                m_iNumberStatus = NUMBER_INPUTDEC;
                m_iNumberStatusInput = NUMBERVALID;

            } else if (cTrans == '*'
                    && (m_iNumberStatus == NUMBER_INPUTINT || m_iNumberStatus == NUMBER_INPUTDEC)) {
                m_jPor.setText("x");
                m_iNumberStatus = NUMBER_PORZERO;
            } else if (cTrans == '*'
                    && (m_iNumberStatus == NUMBER_INPUTZERO || m_iNumberStatus == NUMBER_INPUTZERODEC)) {
                m_jPrice.setText("0");
                m_jPor.setText("x");
                m_iNumberStatus = NUMBER_PORZERO;

            } else if ((cTrans == '0')
                    && (m_iNumberStatus == NUMBER_PORZERO)) {
                m_jPor.setText("x0");
            } else if ((cTrans == '1' || cTrans == '2' || cTrans == '3' || cTrans == '4' || cTrans == '5' || cTrans == '6' || cTrans == '7' || cTrans == '8' || cTrans == '9')
                    && (m_iNumberStatus == NUMBER_PORZERO)) {
                // Un numero entero
                m_jPor.setText("x" + Character.toString(cTrans));
                m_iNumberStatus = NUMBER_PORINT;
                m_iNumberStatusPor = NUMBERVALID;
            } else if ((cTrans == '0' || cTrans == '1' || cTrans == '2' || cTrans == '3' || cTrans == '4' || cTrans == '5' || cTrans == '6' || cTrans == '7' || cTrans == '8' || cTrans == '9')
                    && (m_iNumberStatus == NUMBER_PORINT)) {
                // Un numero entero
                m_jPor.setText(m_jPor.getText() + cTrans);

            } else if (cTrans == '.' && m_iNumberStatus == NUMBER_PORZERO) {
                m_jPor.setText("x0.");
                m_iNumberStatus = NUMBER_PORZERODEC;
            } else if (cTrans == '.' && m_iNumberStatus == NUMBER_PORINT) {
                m_jPor.setText(m_jPor.getText() + ".");
                m_iNumberStatus = NUMBER_PORDEC;

            } else if ((cTrans == '0')
                    && (m_iNumberStatus == NUMBER_PORZERODEC || m_iNumberStatus == NUMBER_PORDEC)) {
                // Un numero decimal
                m_jPor.setText(m_jPor.getText() + cTrans);
            } else if ((cTrans == '1' || cTrans == '2' || cTrans == '3' || cTrans == '4' || cTrans == '5' || cTrans == '6' || cTrans == '7' || cTrans == '8' || cTrans == '9')
                    && (m_iNumberStatus == NUMBER_PORZERODEC || m_iNumberStatus == NUMBER_PORDEC)) {
                // Un numero decimal
                m_jPor.setText(m_jPor.getText() + cTrans);
                m_iNumberStatus = NUMBER_PORDEC;
                m_iNumberStatusPor = NUMBERVALID;

            } else if (cTrans == '\u00a7'
                    && m_iNumberStatusInput == NUMBERVALID && m_iNumberStatusPor == NUMBERZERO) {
                // Scale button pressed and a number typed as a price
                if (m_App.getDeviceScale().existsScale() && m_App.getAppUserView().getUser().hasPermission("sales.EditLines")) {
                    try {
                        Double value = m_App.getDeviceScale().readWeight();
                        if (value != null) {
                            ProductInfoExt product = getInputProduct();
                            addTicketLine(product, value.doubleValue(), product.getPriceSell());
                        }
                    } catch (ScaleException e) {
                        Toolkit.getDefaultToolkit().beep();
                        new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.noweight"), e).show(this);
                        stateToZero();
                    }
                } else {
                    // No existe la balanza;
                    Toolkit.getDefaultToolkit().beep();
                }
            } else if (cTrans == '\u00a7'
                    && m_iNumberStatusInput == NUMBERZERO && m_iNumberStatusPor == NUMBERZERO) {
                // Scale button pressed and no number typed.
                int i = m_ticketlines.getSelectedIndex();
                if (i < 0) {
                    Toolkit.getDefaultToolkit().beep();
                } else if (m_App.getDeviceScale().existsScale()) {
                    try {
                        Double value = m_App.getDeviceScale().readWeight();
                        if (value != null) {
                            TicketLineInfo newline = new TicketLineInfo(m_oTicket.getLine(i));
                            newline.setMultiply(value.doubleValue());
                            newline.setPrice(Math.abs(newline.getPrice()));
                            paintTicketLine(i, newline);
                        }
                    } catch (ScaleException e) {
                        // Error de pesada.
                        Toolkit.getDefaultToolkit().beep();
                        new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.noweight"), e).show(this);
                        stateToZero();
                    }
                } else {
                    // No existe la balanza;
                    Toolkit.getDefaultToolkit().beep();
                }

                // Add one product more to the selected line
            } else if (cTrans == '+'
                    && m_iNumberStatusInput == NUMBERZERO && m_iNumberStatusPor == NUMBERZERO) {
                int i = m_ticketlines.getSelectedIndex();
                 if (i < 0) {
                    Toolkit.getDefaultToolkit().beep();
                } else {
                    TicketLineInfo newline = new TicketLineInfo(m_oTicket.getLine(i));
                    //If it's a refund + button means one unit less
                    if (m_oTicket.getTicketType() == TicketInfo.RECEIPT_REFUND) {
                        newline.setMultiply(newline.getMultiply() - 1.0);
                        paintTicketLine(i, newline);
                    } else {
                        if(m_oTicket.getSeats()>noOfSeats){
                        noOfSeats=noOfSeats+1;
                        // add one unit to the selected line
                        newline.setMultiply(newline.getMultiply() + 1.0);
                        paintTicketLine(i, newline);
                        // advanceFunction(advanceDate,showName);
                        }


                    }

                }

                // Delete one product of the selected line
            } else if (cTrans == '-'
                    && m_iNumberStatusInput == NUMBERZERO && m_iNumberStatusPor == NUMBERZERO
                    && m_App.getAppUserView().getUser().hasPermission("sales.EditLines")) {

                int i = m_ticketlines.getSelectedIndex();
                if (i < 0) {
                    Toolkit.getDefaultToolkit().beep();
                } else {
                    TicketLineInfo newline = new TicketLineInfo(m_oTicket.getLine(i));
                    //If it's a refund - button means one unit more
                    if (m_oTicket.getTicketType() == TicketInfo.RECEIPT_REFUND) {
                        newline.setMultiply(newline.getMultiply() + 1.0);
                        if (newline.getMultiply() >= 0) {
                            removeTicketLine(i);
                        } else {
                            paintTicketLine(i, newline);
                        }
                    } else {
                        // substract one unit to the selected line
                        newline.setMultiply(newline.getMultiply() - 1.0);
                        if (newline.getMultiply() <= 0.0) {
                            removeTicketLine(i); // elimino la linea
                        } else {
                            paintTicketLine(i, newline);
                            //advanceFunction(advanceDate,showName);
                        }
                    }
                }

                // Set n products to the selected line
            } else if (cTrans == '+'
                    && m_iNumberStatusInput == NUMBERZERO && m_iNumberStatusPor == NUMBERVALID) {
                int i = m_ticketlines.getSelectedIndex();

                if (i < 0) {
                    Toolkit.getDefaultToolkit().beep();
                } else {
                    double dPor = getPorValue();
                    TicketLineInfo newline = new TicketLineInfo(m_oTicket.getLine(i));
                    if (m_oTicket.getTicketType() == TicketInfo.RECEIPT_REFUND) {
                        newline.setMultiply(-dPor);
                        newline.setPrice(Math.abs(newline.getPrice()));
                        paintTicketLine(i, newline);
                    } else {
                        newline.setMultiply(dPor);
                        newline.setPrice(Math.abs(newline.getPrice()));
                        paintTicketLine(i, newline);
                    }
                }

                // Set n negative products to the selected line
            } else if (cTrans == '-'
                    && m_iNumberStatusInput == NUMBERZERO && m_iNumberStatusPor == NUMBERVALID
                    && m_App.getAppUserView().getUser().hasPermission("sales.EditLines")) {

                int i = m_ticketlines.getSelectedIndex();
                if (i < 0) {
                    Toolkit.getDefaultToolkit().beep();
                } else {
                    double dPor = getPorValue();
                    TicketLineInfo newline = new TicketLineInfo(m_oTicket.getLine(i));
                    if (m_oTicket.getTicketType() == TicketInfo.RECEIPT_NORMAL) {
                        newline.setMultiply(dPor);
                        newline.setPrice(-Math.abs(newline.getPrice()));
                        paintTicketLine(i, newline);
                    }
                }

                // Anadimos 1 producto
            } else if (cTrans == '+'
                    && m_iNumberStatusInput == NUMBERVALID && m_iNumberStatusPor == NUMBERZERO
                    && m_App.getAppUserView().getUser().hasPermission("sales.EditLines")) {
                 
                ProductInfoExt product = getInputProduct();
                addTicketLine(product, 1.0, product.getPriceSell());

                // Anadimos 1 producto con precio negativo
            } else if (cTrans == '-'
                    && m_iNumberStatusInput == NUMBERVALID && m_iNumberStatusPor == NUMBERZERO
                    && m_App.getAppUserView().getUser().hasPermission("sales.EditLines")) {
                ProductInfoExt product = getInputProduct();
                addTicketLine(product, 1.0, -product.getPriceSell());

                // Anadimos n productos
            } else if (cTrans == '+'
                    && m_iNumberStatusInput == NUMBERVALID && m_iNumberStatusPor == NUMBERVALID
                    && m_App.getAppUserView().getUser().hasPermission("sales.EditLines")) {
             
                ProductInfoExt product = getInputProduct();
                addTicketLine(product, getPorValue(), product.getPriceSell());

                // Anadimos n productos con precio negativo ?
            } else if (cTrans == '-'
                    && m_iNumberStatusInput == NUMBERVALID && m_iNumberStatusPor == NUMBERVALID
                    && m_App.getAppUserView().getUser().hasPermission("sales.EditLines")) {
                ProductInfoExt product = getInputProduct();
                addTicketLine(product, getPorValue(), -product.getPriceSell());

                // Totals() Igual;
            } else if (cTrans == ' ' || cTrans == '=') {

                if (m_oTicket.getLinesCount() > 0) {
                    if (availableSeats < m_oTicket.getArticlesCount()) {
                        JOptionPane.showMessageDialog(JPanelTicket.this, "Available seats is less than the seats selected for the show");
                    } else {

                        if (closeTicket(m_oTicket, m_oTicketExt)) {
                            // Ends edition of current receipt
                            m_ticketsbag.deleteTicket();
                        } else {
                            // repaint current ticket
                            refreshTicket();
                        }
                    }
                } else {

                    Toolkit.getDefaultToolkit().beep();
                }
            }
        }
    }

    public void getDiscountDetails(TicketLineInfo oLine) {
        java.util.List<DiscountInfo> DiscountInfo = new java.util.ArrayList<DiscountInfo>();

        double discountAmt = 0.0;
        int noOfQty = 0;
        try {
            discountDetails = dlSales.getDiscountAmt();
        } catch (NullPointerException ex) {
        }
        DiscountInfo = discountDetails;
        if (discountDetails.size() != 0) {
            discountAmt = DiscountInfo.get(0).getDiscountAmt();
            int discount = (int) discountAmt;
            noOfQty = DiscountInfo.get(0).getNoofPeople();
            oLine.setDiscountPrice(discountAmt);
            oLine.setDiscountQty(noOfQty);
        }
    }

    public void advanceFunction(String advanceDate, String showName) {


        if (!advanceDate.equals("")) {
            m_jCboShowlist.setSelectedItem(showName);
            m_jCboShowlist.setEnabled(true);

            m_jCboAdvbook.setSelected(true);
            m_jLblDate.setVisible(true);
            m_jbtndate.setVisible(true);
            m_jTxtDate.setVisible(true);
            m_jTxtDate.setText(advanceDate);
            Date convertToDate = null;
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            DateFormat dst = new SimpleDateFormat("dd-MM-yyyy");
            Date createdDate = new Date();
            String sysDate = null;
            if (!advanceDate.equals("")) {
                sysDate = advanceDate;

            } else {
                sysDate = dst.format(createdDate);
            }
            try {
                convertToDate = dst.parse(sysDate);
            } catch (ParseException ex) {
                Logger.getLogger(JPanelTicket.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                availableSeats = dlSales.getAvailableSeats(showName, convertToDate);
            } catch (BasicException ex) {
                Logger.getLogger(JPanelTicket.class.getName()).log(Level.SEVERE, null, ex);
            }
            m_jAvailSeats.setText(Integer.toString(availableSeats));

        } else {

            m_jCboShowlist.setSelectedItem(sysShowName);
            m_jCboShowlist.setEnabled(false);

            m_jCboAdvbook.setSelected(false);
            m_jLblDate.setVisible(false);
            m_jbtndate.setVisible(false);
            m_jTxtDate.setVisible(false);
            m_jTxtDate.setText("");

        }
    }

    private boolean closeTicket(TicketInfo ticket, Object ticketext) {

        boolean resultok = false;


        if (m_App.getAppUserView().getUser().hasPermission("sales.Total")) {

            try {
                // reset the payment info
                taxeslogic.calculateTaxes(ticket);
                if (ticket.getTotal() >= 0.0) {
                    ticket.resetPayments(); //Only reset if is sale
                }

                if (executeEvent(ticket, ticketext, "ticket.total") == null) {

                    // Muestro el total
                    //printTicket("Printer.TicketTotal", ticket, ticketext);


                    // Select the Payments information
                    JPaymentSelect paymentdialog = ticket.getTicketType() == TicketInfo.RECEIPT_NORMAL
                            ? paymentdialogreceipt
                            : paymentdialogrefund;
                    paymentdialog.setPrintSelected("true".equals(m_jbtnconfig.getProperty("printselected", "true")));

                    paymentdialog.setTransactionID(ticket.getTransactionID());

                    if (paymentdialog.showDialog(ticket.getTotal(), ticket.getCustomer())) {

                        // assign the payments selected and calculate taxes.         
                        ticket.setPayments(paymentdialog.getSelectedPayments());

                        // Asigno los valores definitivos del ticket...
                        ticket.setUser(m_App.getAppUserView().getUser().getUserInfo()); // El usuario que lo cobra
                        ticket.setActiveCash(m_App.getActiveCashIndex());
                        ticket.setActiveShow(m_App.getActiveShowIndex());
                        ticket.setDate(new Date()); // Le pongo la fecha de cobro

                        if (executeEvent(ticket, ticketext, "ticket.save") == null) {
                            String show = null;

                            show = m_jCboShowlist.getSelectedItem().toString();


                            int totalQty = (int) (ticket.getArticlesCount());
                            String advBook = null;
                            String advDate = null;
                            String reason = null;
                            String printer;
                            String showDate = null;
                              String refund;
                            double grandTotal = 0;
                            int currentSeats = 0;
                            if (ticket.getTicketType() == TicketInfo.RECEIPT_REFUND) {
                                ticket.setActiveShow(m_App.getActiveShowIndex());
                                advDate = m_oTicket.getadvanceDate();
                                SimpleDateFormat dtf = new SimpleDateFormat("yyyy-MM-dd");
                                if (advDate.equals("")) {
                                    Date createdDate = new Date();
                                    showDate = dtf.format(createdDate);
                                    advDate = "";
                                    m_oTicket.setshowDate(showDate);
                                } else {
                                    advDate = m_oTicket.getadvanceDate();
                                    m_oTicket.setshowDate(advDate);
                                }
                                advBook = "";
                                reason = "Refund";
                                

                                printer = "Printer.TicketRefund";
                                if (m_jTxtCancelFee.getText() != "") {
                                    double ticketCancelFee = Double.parseDouble(m_jTxtCancelFee.getText());
                                    grandTotal = m_oTicket.getTotal() + ticketCancelFee;
                                } else {
                                    grandTotal = m_oTicket.getTotal();
                                }
                                refund = "Y";
                            } else {
                                SimpleDateFormat dtf = new SimpleDateFormat("yyyy-MM-dd");
                                SimpleDateFormat dt = new SimpleDateFormat("dd-MM-yyyy");
                                Date advShowDate = null;

                                if (m_jCboAdvbook.isSelected() == true) {
                                    advBook = "Y";
                                    advDate = m_jTxtDate.getText();
                                    try {
                                        advShowDate = dt.parse(advDate);
                                    } catch (ParseException ex) {
                                        Logger.getLogger(JPanelTicket.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                    advDate = dtf.format(advShowDate);
                                    reason = "Advance Booking";
                                    m_oTicket.setshowDate(advDate);
                                } else {
                                    advBook = "N";
                                    advDate = "";
                                    reason = "Ticketing";

                                    Date createdDate = new Date();
                                    showDate = dtf.format(createdDate);
                                    m_oTicket.setshowDate(showDate);
                                }
                                printer = "Printer.Ticket";
                                grandTotal = m_oTicket.getTotal();
                                refund = "N";
                            }
                            currentSeats = availableSeats - totalQty;

                            // Save the receipt and assign a receipt number
                            if (!show.equals("")) {
                                try {
                                    dlSales.saveTicket(ticket, m_App.getInventoryLocation(), show, currentSeats, advBook, advDate, refund);
                                } catch (BasicException eData) {
                                    MessageInf msg = new MessageInf(MessageInf.SGN_NOTICE, AppLocal.getIntString("message.nosaveticket"), eData);
                                    msg.show(this);
                                }

                                try {
                                    dlSystem.peoplelogInsert(UUID.randomUUID().toString(), (m_oTicket.getUser()).getId(), m_RootApp.now(), reason, grandTotal);
                                    // TODO add your handling code here:;

                                } catch (BasicException ex) {
                                    Logger.getLogger(JPanelTicket.class.getName()).log(Level.SEVERE, null, ex);
                                }

                              
                                executeEvent(ticket, ticketext, "ticket.close", new ScriptArg("print", paymentdialog.isPrintSelected()));

                                // Print receipt.
                                printTicket(paymentdialog.isPrintSelected()
                                        ? printer
                                        : "Printer.Ticket2", ticket, ticketext);
                                resultok = true;
                                m_jCboAdvbook.setSelected(false);
                                m_jTxtDate.setVisible(false);
                                m_jTxtDate.setText("");
                                m_jbtndate.setVisible(false);
                                m_jLblDate.setVisible(false);
                                m_jCboShowlist.setEnabled(false);
                                m_jCloseShow.setEnabled(true);
                                m_jCloseDay.setEnabled(true);
                                advanceShowName = "";
                                advanceShowDate = "";
                                refreshTicket();
                            }
                        } else {
                            showMessage(this, "Please select the show");
                            // }

                        }
                    }
                }
            } catch (TaxesException e) {
                MessageInf msg = new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.cannotcalculatetaxes"));
                msg.show(this);
                resultok = false;
            }

            // reset the payment info
            m_oTicket.resetTaxes();
            m_oTicket.resetPayments();
        }

        // cancelled the ticket.total script
        // or canceled the payment dialog
        // or canceled the ticket.close script
        return resultok;
    }

    private void printTicket(String sresourcename, TicketInfo ticket, Object ticketext) {
        java.util.List<showInfo> showInfo = new java.util.ArrayList<showInfo>();
        java.util.List<announcementInfo> announcementInfo = new java.util.ArrayList<announcementInfo>();
        String showName;
        String advanceSysdate = null;
        SimpleDateFormat dt = new SimpleDateFormat("dd-MM-yyyy");
        Date sysdate = new Date();
        String currentDate = dt.format(sysdate);
       
       
        if (ticket.getTicketType() == TicketInfo.RECEIPT_REFUND) {
             showName = (String) m_jCboShowlist.getSelectedItem();
        }else{
            if (!m_jTxtDate.getText().equals("")) {
                advanceSysdate = m_jTxtDate.getText();
                showName = (String) m_jCboShowlist.getSelectedItem();
            } else {
                advanceSysdate="";
                showName = sysShowName;
            }
        }
        showDetails = dlSales.getShowDetails(showName);
        showInfo = showDetails;
        try {
            announcement = dlSales.getAnnouncement(currentDate);
        } catch (BasicException ex) {
            Logger.getLogger(JPanelTicket.class.getName()).log(Level.SEVERE, null, ex);
        }

        announcementInfo = announcement;

       String sresource = dlSystem.getResourceAsXML(sresourcename);
       /* if (sresource == null) {
         MessageInf msg = new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.cannotprintticket"));
         msg.show(JPanelTicket.this);
         } else {
         try {
         ScriptEngine script = ScriptFactory.getScriptEngine(ScriptFactory.VELOCITY);
         script.put("taxes", taxcollection);
         script.put("taxeslogic", taxeslogic);
         script.put("ticket", ticket);
         script.put("place", ticketext);
         script.put("showinfo", (showInfo)showInfo.get(0));
                  
         if(announcement.size()!=0){
         script.put("announcement", (announcementInfo)announcementInfo.get(0));
         }
                  
         m_TTP.printTicket(script.eval(sresource).toString());
         //CDrawer cr = new CDrawer();
         //  cr.open1();
         //  cr.opendr1();
         //   cr.close1();


         } catch (ScriptException e) {
         MessageInf msg = new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.cannotprintticket"), e);
         msg.show(JPanelTicket.this);
         } catch (TicketPrinterException e) {
         MessageInf msg = new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.cannotprintticket"), e);
         msg.show(JPanelTicket.this);
         }


         }*/

       if (sresourcename.equals("Printer.Ticket")) {
            //call custom printer service
            java.util.List<TicketLineConstructor> allLines = getAllLines(ticket, showInfo, announcement, advanceSysdate);
            com.openbravo.pos.printer.printer.ImagePrinter printer = new ImagePrinter();
            try {
                printer.print(ticket.getShowName(), allLines);
            } catch (PrinterException ex) {
                Logger.getLogger(JPanelTicket.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            java.util.List<TicketLineConstructor> allLines = getRefundLines(ticket, showInfo, announcement);
            com.openbravo.pos.printer.printer.ImagePrinter printer = new ImagePrinter();
            try {
                printer.print(ticket.getShowName(), allLines);
            } catch (PrinterException ex) {
                Logger.getLogger(JPanelTicket.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private java.util.List<TicketLineConstructor> getAllLines(TicketInfo ticket, java.util.List<showInfo> showInfo, java.util.List<announcementInfo> announcement, String advanceSysDate) {
        String advSysDate = "";
        if(advanceSysDate.equals("")){
            advSysDate = ticket.printSysDate();
        }else{
            advSysDate = advanceSysDate;
        }

        java.util.List<TicketLineConstructor> allLines = new ArrayList<TicketLineConstructor>();
        String line1 = ticket.printSysDate() + getSpaces(7) + ticket.printSysTime() + getSpaces(18) + advSysDate + getSpaces(6) + showInfo.get(0).printStarttime();

        String line2 = getDottedLine(80);
        String line3 = getDottedLine(80);
        String line4 = getDottedLine(80);
        String line5 = getDottedLine(80);

        allLines.add(new TicketLineConstructor(line1));
        allLines.add(new TicketLineConstructor(line2));
        allLines.add(new TicketLineConstructor(line3));
        allLines.add(new TicketLineConstructor(line4));
         allLines.add(new TicketLineConstructor(line5));
       
        for (TicketLineInfo tLine : ticket.getLines()) {
            String prodName = tLine.printName();
            String qty = tLine.printMultiply();
            String subValue = tLine.printPriceValue();
            allLines.add(new TicketLineConstructor(prodName + getSpaces(34 - prodName.length()) + qty + getSpaces(24 - qty.length() + 7 - subValue.length()) + subValue));
        }
        allLines.add(new TicketLineConstructor(getDottedLine(80)));
        if (ticket.getDiscountValue() != 0) {
            allLines.add(new TicketLineConstructor("Discount Total " + getSpaces(41 + 7 - ticket.printDiscountTotal().length()) + ("-"+ticket.printDiscountTotal())));
           // allLines.add(new TicketLineConstructor("Discount Total. " + getSpaces(27 - ticket.printDiscountTotal().length()) + ticket.printDiscountTotal()));
        }
        String aCount = ticket.printTicketCount();
        allLines.add(new TicketLineConstructor("Total. " + getSpaces(27) + aCount + getSpaces(24 - aCount.length() + 7 - ticket.printTotal().length()) + ticket.printTotal()));
       
        int linesCount = ticket.getLinesCount();
        if (ticket.getDiscountTotal() == 0 && linesCount <= 4) {

            allLines.add(new TicketLineConstructor(getDottedLine(80)));
        }
        if (ticket.getDiscountValue() != 0) {
            addlinesaddBlankLines(5 - linesCount, allLines);
        } else {
            addlinesaddBlankLines(6 - linesCount, allLines);
        }
        String announce1 = "";
        String announce2 = "";
        String announce3 = "";
        if (announcement.size() > 0) {
            if (announcement.get(0).getAnnouncement() != null) {
                announce1 = announcement.get(0).printAnnouceFirstLine();
                announce2 = announcement.get(0).printAnnouceSecondLine();
                //announce3 = announcement.get(0).printAnnouceThirdLine();
            }
        }
        allLines.add(new TicketLineConstructor(announce1));
        allLines.add(new TicketLineConstructor(announce2));
        //allLines.add(new TicketLineConstructor(announce3));
        return allLines;
    }

    private java.util.List<TicketLineConstructor> getRefundLines(TicketInfo ticket, java.util.List<showInfo> showInfo, java.util.List<announcementInfo> announcement) {

        java.util.List<TicketLineConstructor> allLines = new ArrayList<TicketLineConstructor>();
 
        String line1 = ticket.printSysDate() + getSpaces(7) + ticket.printTime() + getSpaces(21) + ticket.printShowDate() + getSpaces(6) + showInfo.get(0).printStarttime();
        String line2 = getDottedLine(1);
        String line3 = getDottedLine(1);
        String line4 = getDottedLine(1);
        String line5 = getDottedLine(1);
        allLines.add(new TicketLineConstructor(line1));
        allLines.add(new TicketLineConstructor(line2));
        allLines.add(new TicketLineConstructor(line3));
        allLines.add(new TicketLineConstructor(line4));
        allLines.add(new TicketLineConstructor(line5));
      
        for (TicketLineInfo tLine : ticket.getLines()) {
            String prodName = tLine.printName();
            String qty = tLine.printMultiply();
            String subValue = tLine.printSubValue();
            allLines.add(new TicketLineConstructor(prodName + getSpaces(34 - prodName.length()) + qty + getSpaces(22 - qty.length() + 7 - subValue.length()) + subValue));
        }
        allLines.add(new TicketLineConstructor(getDottedLine(80)));
        String aCount = ticket.printTicketCount();
        if (ticket.getCancelFee() != 0) {
            allLines.add(new TicketLineConstructor("Processing Fee " + getSpaces(41 + 7 - ticket.printCancelFee().length()) + ticket.printCancelFee()));
        }
        allLines.add(new TicketLineConstructor("Total. " + getSpaces(27) + aCount + getSpaces(22 - aCount.length() + 7 - ticket.printRefTotal().length()) + ticket.printRefTotal()));
        int linesCount = ticket.getLinesCount();
        if (ticket.getCancelFee() == 0 && linesCount <= 5) {

            allLines.add(new TicketLineConstructor(getDottedLine(80)));
        }

        if (ticket.getCancelFee() != 0) {
            addlinesaddBlankLines(5 - linesCount, allLines);
        } else {
            addlinesaddBlankLines(6 - linesCount, allLines);
        }

        String announce1 = "";
        String announce2 = "";
        if (announcement.size() > 0) {
            if (announcement.get(0).getAnnouncement() != null) {
                announce1 = announcement.get(0).printAnnouceFirstLine();
                announce2 = announcement.get(0).printAnnouceSecondLine();
            }
        }
        allLines.add(new TicketLineConstructor(announce1));
        allLines.add(new TicketLineConstructor(announce2));
        return allLines;
    }

    private String getDottedLine(int len) {
        String dotLine = "";
        for (int i = 0; i < len; i++) {
            dotLine = dotLine + " ";
        }
        return dotLine;
    }

    private String getSpaces(int len) {
        String spaces = "";
        for (int i = 0; i < len; i++) {
            spaces = spaces + " ";
        }
        return spaces;
    }

    private void printReport(String resourcefile, TicketInfo ticket, Object ticketext) {

        try {

            JasperReport jr;

            InputStream in = getClass().getResourceAsStream(resourcefile + ".ser");
            if (in == null) {
                // read and compile the report
                JasperDesign jd = JRXmlLoader.load(getClass().getResourceAsStream(resourcefile + ".jrxml"));
                jr = JasperCompileManager.compileReport(jd);
            } else {
                // read the compiled reporte
                ObjectInputStream oin = new ObjectInputStream(in);
                jr = (JasperReport) oin.readObject();
                oin.close();
            }

            // Construyo el mapa de los parametros.
            Map reportparams = new HashMap();
            // reportparams.put("ARG", params);
            try {
                reportparams.put("REPORT_RESOURCE_BUNDLE", ResourceBundle.getBundle(resourcefile + ".properties"));
            } catch (MissingResourceException e) {
            }
            reportparams.put("TAXESLOGIC", taxeslogic);

            Map reportfields = new HashMap();
            reportfields.put("TICKET", ticket);
            reportfields.put("PLACE", ticketext);

            JasperPrint jp = JasperFillManager.fillReport(jr, reportparams, new JRMapArrayDataSource(new Object[]{reportfields}));

            PrintService service = ReportUtils.getPrintService(m_App.getProperties().getProperty("machine.printername"));

            JRPrinterAWT300.printPages(jp, 0, jp.getPages().size() - 1, service);

        } catch (Exception e) {
            MessageInf msg = new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.cannotloadreport"), e);
            msg.show(this);
        }
    }

    private void visorTicketLine(TicketLineInfo oLine) {
        if (oLine == null) {
            m_App.getDeviceTicket().getDeviceDisplay().clearVisor();
        } else {
            try {
                ScriptEngine script = ScriptFactory.getScriptEngine(ScriptFactory.VELOCITY);
                script.put("ticketline", oLine);
                m_TTP.printTicket(script.eval(dlSystem.getResourceAsXML("Printer.TicketLine")).toString());
            } catch (ScriptException e) {
                MessageInf msg = new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.cannotprintline"), e);
                msg.show(JPanelTicket.this);
            } catch (TicketPrinterException e) {
                MessageInf msg = new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.cannotprintline"), e);
                msg.show(JPanelTicket.this);
            }
        }
    }

    private Object evalScript(ScriptObject scr, String resource, ScriptArg... args) {

        // resource here is guaratied to be not null
        try {
            scr.setSelectedIndex(m_ticketlines.getSelectedIndex());
            return scr.evalScript(dlSystem.getResourceAsXML(resource), args);
        } catch (ScriptException e) {
            MessageInf msg = new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.cannotexecute"), e);
            msg.show(this);
            return msg;
        }
    }

    public void evalScriptAndRefresh(String resource, ScriptArg... args) {

        if (resource == null) {
            MessageInf msg = new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.cannotexecute"));
            msg.show(this);
        } else {
            ScriptObject scr = new ScriptObject(m_oTicket, m_oTicketExt);
            scr.setSelectedIndex(m_ticketlines.getSelectedIndex());
            evalScript(scr, resource, args);
            refreshTicket();
            setSelectedIndex(scr.getSelectedIndex());
        }
    }

    public void printTicket(String resource) {
        printTicket(resource, m_oTicket, m_oTicketExt);
    }

    private Object executeEventAndRefresh(String eventkey, ScriptArg... args) {
        String advanceDate = m_jTxtDate.getText();
        String showName = m_jCboShowlist.getSelectedItem().toString();

        String resource = m_jbtnconfig.getEvent(eventkey);
        if (resource == null) {
            return null;
        } else {
            ScriptObject scr = new ScriptObject(m_oTicket, m_oTicketExt);
            scr.setSelectedIndex(m_ticketlines.getSelectedIndex());
            Object result = evalScript(scr, resource, args);
            refreshTicket();

            setSelectedIndex(scr.getSelectedIndex());
            return result;
        }
    }

    private Object executeEvent(TicketInfo ticket, Object ticketext, String eventkey, ScriptArg... args) {

        String resource = m_jbtnconfig.getEvent(eventkey);
        if (resource == null) {
            return null;
        } else {
            ScriptObject scr = new ScriptObject(ticket, ticketext);
            return evalScript(scr, resource, args);
        }
    }

    public String getResourceAsXML(String sresourcename) {
        return dlSystem.getResourceAsXML(sresourcename);
    }

    public BufferedImage getResourceAsImage(String sresourcename) {
        return dlSystem.getResourceAsImage(sresourcename);
    }

    private void setSelectedIndex(int i) {

        if (i >= 0 && i < m_oTicket.getLinesCount()) {
            m_ticketlines.setSelectedIndex(i);
        } else if (m_oTicket.getLinesCount() > 0) {
            m_ticketlines.setSelectedIndex(m_oTicket.getLinesCount() - 1);
        }
    }

    private void addlinesaddBlankLines(int count, java.util.List<TicketLineConstructor> allLines) {
        for (int i = 0; i < count; i++) {
            allLines.add(new TicketLineConstructor(""));
        }
    }

    public static class ScriptArg {

        private String key;
        private Object value;

        public ScriptArg(String key, Object value) {
            this.key = key;
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public Object getValue() {
            return value;
        }
    }

    public class ScriptObject {

        private TicketInfo ticket;
        private Object ticketext;
        private int selectedindex;

        private ScriptObject(TicketInfo ticket, Object ticketext) {
            this.ticket = ticket;
            this.ticketext = ticketext;
        }

        public double getInputValue() {
            if (m_iNumberStatusInput == NUMBERVALID && m_iNumberStatusPor == NUMBERZERO) {
                return JPanelTicket.this.getInputValue();
            } else {
                return 0.0;
            }
        }

        public int getSelectedIndex() {
            return selectedindex;
        }

        public void setSelectedIndex(int i) {
            selectedindex = i;
        }

        public void printReport(String resourcefile) {
            JPanelTicket.this.printReport(resourcefile, ticket, ticketext);
        }

        public void printTicket(String sresourcename) {
            JPanelTicket.this.printTicket(sresourcename, ticket, ticketext);
        }

        public Object evalScript(String code, ScriptArg... args) throws ScriptException {

            ScriptEngine script = ScriptFactory.getScriptEngine(ScriptFactory.BEANSHELL);
            script.put("ticket", ticket);
            script.put("place", ticketext);
            script.put("taxes", taxcollection);
            script.put("taxeslogic", taxeslogic);
            script.put("user", m_App.getAppUserView().getUser());
            script.put("sales", this);

            // more arguments
            for (ScriptArg arg : args) {
                script.put(arg.getKey(), arg.getValue());
            }

            return script.eval(code);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the FormEditor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        m_jPanContainer = new javax.swing.JPanel();
        m_jOptions = new javax.swing.JPanel();
        m_jPanelScripts = new javax.swing.JPanel();
        m_jButtonsExt = new javax.swing.JPanel();
        m_jPanelBag = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        m_jCloseShow = new javax.swing.JButton();
        m_jCloseDay = new javax.swing.JButton();
        m_jReset = new javax.swing.JButton();
        m_jMoneyHandover = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        m_jCboShowlist = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        m_jLblDate = new javax.swing.JLabel();
        m_jTxtDate = new javax.swing.JTextField();
        m_jbtndate = new javax.swing.JButton();
        m_jCboAdvbook = new javax.swing.JCheckBox();
        m_jPanTicket = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        m_jUp = new javax.swing.JButton();
        m_jDown = new javax.swing.JButton();
        m_jDelete = new javax.swing.JButton();
        m_jList = new javax.swing.JButton();
        m_jEditLine = new javax.swing.JButton();
        m_jPanelCentral = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        m_jPanTotals = new javax.swing.JPanel();
        m_jPanRefundTotals = new javax.swing.JPanel();
        m_jlblCancelFee = new javax.swing.JLabel();
        m_jTxtCancelFee = new javax.swing.JLabel();
        m_jLblTotalRefundEuros = new javax.swing.JLabel();
        m_jTotalRefundEuros = new javax.swing.JTextField();
        m_jTotalEuros = new javax.swing.JLabel();
        m_jlblAvailSeats = new javax.swing.JLabel();
        m_jAvailSeats = new javax.swing.JLabel();
        m_jLblTotalEuros1 = new javax.swing.JLabel();
        m_jlblDiscount = new javax.swing.JLabel();
        m_jTxtDiscount = new javax.swing.JTextField();
        m_jContEntries = new javax.swing.JPanel();
        m_jPanEntries = new javax.swing.JPanel();
        m_jNumberKeys = new com.openbravo.beans.JNumberKeys();
        jPanel9 = new javax.swing.JPanel();
        m_jPrice = new javax.swing.JLabel();
        m_jPor = new javax.swing.JLabel();
        m_jEnter = new javax.swing.JButton();
        m_jTax = new javax.swing.JComboBox();
        m_jaddtax = new javax.swing.JToggleButton();
        m_jKeyFactory = new javax.swing.JTextField();
        catcontainer = new javax.swing.JPanel();

        setBackground(new java.awt.Color(255, 204, 153));
        setLayout(new java.awt.CardLayout());

        m_jPanContainer.setLayout(new java.awt.BorderLayout());

        m_jOptions.setLayout(new java.awt.BorderLayout());

        m_jPanelScripts.setPreferredSize(new java.awt.Dimension(400, 0));
        m_jPanelScripts.setLayout(new java.awt.BorderLayout());

        m_jButtonsExt.setLayout(new javax.swing.BoxLayout(m_jButtonsExt, javax.swing.BoxLayout.LINE_AXIS));
        m_jPanelScripts.add(m_jButtonsExt, java.awt.BorderLayout.LINE_END);

        m_jOptions.add(m_jPanelScripts, java.awt.BorderLayout.LINE_END);

        m_jPanelBag.setLayout(new java.awt.BorderLayout());

        jPanel1.setPreferredSize(new java.awt.Dimension(280, 47));

        m_jCloseShow.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/CloseShow.png"))); // NOI18N
        m_jCloseShow.setToolTipText("Close Booking");
        m_jCloseShow.setFocusPainted(false);
        m_jCloseShow.setFocusable(false);
        m_jCloseShow.setMargin(new java.awt.Insets(8, 14, 8, 14));
        m_jCloseShow.setRequestFocusEnabled(false);
        m_jCloseShow.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jCloseShowActionPerformed(evt);
            }
        });

        m_jCloseDay.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/CloseDay.png"))); // NOI18N
        m_jCloseDay.setToolTipText("Hand Over");
        m_jCloseDay.setFocusPainted(false);
        m_jCloseDay.setFocusable(false);
        m_jCloseDay.setMargin(new java.awt.Insets(8, 14, 8, 14));
        m_jCloseDay.setRequestFocusEnabled(false);
        m_jCloseDay.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jCloseDayActionPerformed(evt);
            }
        });

        m_jReset.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/reload.png"))); // NOI18N
        m_jReset.setToolTipText("Reset");
        m_jReset.setFocusPainted(false);
        m_jReset.setFocusable(false);
        m_jReset.setMargin(new java.awt.Insets(8, 14, 8, 14));
        m_jReset.setRequestFocusEnabled(false);
        m_jReset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jResetActionPerformed(evt);
            }
        });

        m_jMoneyHandover.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/money_handover.png"))); // NOI18N
        m_jMoneyHandover.setToolTipText("Money Handover"); // NOI18N
        m_jMoneyHandover.setFocusPainted(false);
        m_jMoneyHandover.setFocusable(false);
        m_jMoneyHandover.setMargin(new java.awt.Insets(8, 14, 8, 14));
        m_jMoneyHandover.setRequestFocusEnabled(false);
        m_jMoneyHandover.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jMoneyHandoverActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(2, 2, 2)
                .add(m_jCloseShow)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(m_jCloseDay)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(m_jReset)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(m_jMoneyHandover)
                .addContainerGap(317, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(m_jCloseShow)
                    .add(m_jCloseDay)
                    .add(m_jReset)
                    .add(m_jMoneyHandover))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        m_jPanelBag.add(jPanel1, java.awt.BorderLayout.CENTER);

        m_jOptions.add(m_jPanelBag, java.awt.BorderLayout.CENTER);

        jPanel3.setPreferredSize(new java.awt.Dimension(745, 40));

        m_jCboShowlist.setFont(new java.awt.Font("TakaoPGothic", 1, 16));
        m_jCboShowlist.setForeground(new java.awt.Color(255, 0, 51));
        m_jCboShowlist.setPreferredSize(new java.awt.Dimension(28, 25));
        m_jCboShowlist.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jCboShowlistActionPerformed(evt);
            }
        });

        jLabel1.setText("Show Name");

        m_jLblDate.setText("Date");

        m_jTxtDate.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(javax.swing.UIManager.getDefaults().getColor("Button.darkShadow")), javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1)));
        m_jTxtDate.setPreferredSize(new java.awt.Dimension(6, 25));

        m_jbtndate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/date.png"))); // NOI18N
        m_jbtndate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jbtndateActionPerformed(evt);
            }
        });

        m_jCboAdvbook.setText("Advance Booking");
        m_jCboAdvbook.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jCboAdvbookActionPerformed(evt);
            }
        });
        m_jCboAdvbook.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                m_jCboAdvbookFocusLost(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel3Layout = new org.jdesktop.layout.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .add(5, 5, 5)
                .add(jLabel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 110, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(m_jCboShowlist, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 165, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(m_jCboAdvbook, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 146, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(m_jLblDate, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 43, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(m_jTxtDate, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 157, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(m_jbtndate, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 40, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(105, 105, 105))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(m_jbtndate, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 28, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(m_jTxtDate, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(m_jLblDate, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 20, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(m_jCboAdvbook)
                        .add(m_jCboShowlist, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jPanel3Layout.createSequentialGroup()
                        .add(2, 2, 2)
                        .add(jLabel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 20, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(12, Short.MAX_VALUE))
        );

        m_jOptions.add(jPanel3, java.awt.BorderLayout.PAGE_START);

        m_jPanContainer.add(m_jOptions, java.awt.BorderLayout.NORTH);

        m_jPanTicket.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        m_jPanTicket.setLayout(new java.awt.BorderLayout());

        jPanel5.setLayout(new java.awt.BorderLayout());

        jPanel2.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 5, 0, 5));
        jPanel2.setLayout(new java.awt.GridLayout(0, 1, 5, 5));

        m_jUp.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/1uparrow22.png"))); // NOI18N
        m_jUp.setFocusPainted(false);
        m_jUp.setFocusable(false);
        m_jUp.setMargin(new java.awt.Insets(8, 14, 8, 14));
        m_jUp.setRequestFocusEnabled(false);
        m_jUp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jUpActionPerformed(evt);
            }
        });
        jPanel2.add(m_jUp);

        m_jDown.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/1downarrow22.png"))); // NOI18N
        m_jDown.setFocusPainted(false);
        m_jDown.setFocusable(false);
        m_jDown.setMargin(new java.awt.Insets(8, 14, 8, 14));
        m_jDown.setRequestFocusEnabled(false);
        m_jDown.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jDownActionPerformed(evt);
            }
        });
        jPanel2.add(m_jDown);

        m_jDelete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/locationbar_erase.png"))); // NOI18N
        m_jDelete.setFocusPainted(false);
        m_jDelete.setFocusable(false);
        m_jDelete.setMargin(new java.awt.Insets(8, 14, 8, 14));
        m_jDelete.setRequestFocusEnabled(false);
        m_jDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jDeleteActionPerformed(evt);
            }
        });
        jPanel2.add(m_jDelete);

        m_jList.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/search22.png"))); // NOI18N
        m_jList.setFocusPainted(false);
        m_jList.setFocusable(false);
        m_jList.setMargin(new java.awt.Insets(8, 14, 8, 14));
        m_jList.setRequestFocusEnabled(false);
        m_jList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jListActionPerformed(evt);
            }
        });
        jPanel2.add(m_jList);

        m_jEditLine.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/color_line.png"))); // NOI18N
        m_jEditLine.setFocusPainted(false);
        m_jEditLine.setFocusable(false);
        m_jEditLine.setMargin(new java.awt.Insets(8, 14, 8, 14));
        m_jEditLine.setRequestFocusEnabled(false);
        m_jEditLine.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jEditLineActionPerformed(evt);
            }
        });
        jPanel2.add(m_jEditLine);

        jPanel5.add(jPanel2, java.awt.BorderLayout.NORTH);

        m_jPanTicket.add(jPanel5, java.awt.BorderLayout.LINE_END);

        m_jPanelCentral.setLayout(new java.awt.BorderLayout());

        jPanel4.setLayout(new java.awt.BorderLayout());

        m_jPanTotals.setPreferredSize(new java.awt.Dimension(550, 90));
        m_jPanTotals.setVerifyInputWhenFocusTarget(false);

        m_jPanRefundTotals.setPreferredSize(new java.awt.Dimension(550, 50));
        m_jPanRefundTotals.setVerifyInputWhenFocusTarget(false);

        m_jlblCancelFee.setFont(new java.awt.Font("Tahoma", 1, 18));
        m_jlblCancelFee.setForeground(new java.awt.Color(255, 0, 51));
        m_jlblCancelFee.setText("Processing Fee");

        m_jTxtCancelFee.setFont(new java.awt.Font("Tahoma", 1, 18));
        m_jTxtCancelFee.setForeground(new java.awt.Color(255, 0, 51));

        m_jLblTotalRefundEuros.setText("Total");

        m_jTotalRefundEuros.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(javax.swing.UIManager.getDefaults().getColor("Button.darkShadow")), javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1)));

        org.jdesktop.layout.GroupLayout m_jPanRefundTotalsLayout = new org.jdesktop.layout.GroupLayout(m_jPanRefundTotals);
        m_jPanRefundTotals.setLayout(m_jPanRefundTotalsLayout);
        m_jPanRefundTotalsLayout.setHorizontalGroup(
            m_jPanRefundTotalsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(m_jPanRefundTotalsLayout.createSequentialGroup()
                .add(73, 73, 73)
                .add(m_jlblCancelFee, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 190, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(m_jTxtCancelFee, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 63, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(m_jLblTotalRefundEuros, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 43, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(m_jTotalRefundEuros, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 113, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(47, 47, 47))
        );
        m_jPanRefundTotalsLayout.setVerticalGroup(
            m_jPanRefundTotalsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(m_jPanRefundTotalsLayout.createSequentialGroup()
                .add(11, 11, 11)
                .add(m_jPanRefundTotalsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, m_jPanRefundTotalsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(m_jLblTotalRefundEuros, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(m_jTotalRefundEuros, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(org.jdesktop.layout.GroupLayout.LEADING, m_jlblCancelFee, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 28, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, m_jTxtCancelFee, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 28, Short.MAX_VALUE))
                .add(12, 12, 12))
        );

        m_jTotalEuros.setBackground(java.awt.Color.white);
        m_jTotalEuros.setFont(new java.awt.Font("Dialog", 1, 14));
        m_jTotalEuros.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        m_jTotalEuros.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(javax.swing.UIManager.getDefaults().getColor("Button.darkShadow")), javax.swing.BorderFactory.createEmptyBorder(1, 4, 1, 4)));
        m_jTotalEuros.setOpaque(true);
        m_jTotalEuros.setPreferredSize(new java.awt.Dimension(150, 25));
        m_jTotalEuros.setRequestFocusEnabled(false);

        m_jlblAvailSeats.setFont(new java.awt.Font("Tahoma", 1, 18));
        m_jlblAvailSeats.setForeground(new java.awt.Color(255, 0, 51));
        m_jlblAvailSeats.setText("Available Seats");

        m_jAvailSeats.setFont(new java.awt.Font("Tahoma", 1, 18));
        m_jAvailSeats.setForeground(new java.awt.Color(255, 0, 51));
        m_jAvailSeats.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        m_jLblTotalEuros1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        m_jLblTotalEuros1.setText("Total");

        m_jlblDiscount.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        m_jlblDiscount.setText("Discount");

        m_jTxtDiscount.setEditable(false);
        m_jTxtDiscount.setFont(new java.awt.Font("Dialog", 1, 14));
        m_jTxtDiscount.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        m_jTxtDiscount.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(javax.swing.UIManager.getDefaults().getColor("Button.darkShadow")), javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1)));
        m_jTxtDiscount.setPreferredSize(new java.awt.Dimension(150, 25));

        org.jdesktop.layout.GroupLayout m_jPanTotalsLayout = new org.jdesktop.layout.GroupLayout(m_jPanTotals);
        m_jPanTotals.setLayout(m_jPanTotalsLayout);
        m_jPanTotalsLayout.setHorizontalGroup(
            m_jPanTotalsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(m_jPanTotalsLayout.createSequentialGroup()
                .add(65, 65, 65)
                .add(m_jlblAvailSeats, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 175, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(m_jAvailSeats, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 48, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(18, 18, 18)
                .add(m_jlblDiscount, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 77, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .add(m_jPanTotalsLayout.createSequentialGroup()
                .add(400, 400, 400)
                .add(m_jTxtDiscount, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 113, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .add(m_jPanTotalsLayout.createSequentialGroup()
                .add(400, 400, 400)
                .add(m_jTotalEuros, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 113, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .add(m_jPanRefundTotals, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 550, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .add(m_jPanTotalsLayout.createSequentialGroup()
                .add(311, 311, 311)
                .add(m_jLblTotalEuros1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 73, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );
        m_jPanTotalsLayout.setVerticalGroup(
            m_jPanTotalsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(m_jPanTotalsLayout.createSequentialGroup()
                .add(m_jPanTotalsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(m_jPanTotalsLayout.createSequentialGroup()
                        .add(10, 10, 10)
                        .add(m_jPanTotalsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(m_jlblDiscount, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 20, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(m_jAvailSeats, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 28, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(m_jlblAvailSeats, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 28, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(m_jLblTotalEuros1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 20, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(m_jPanTotalsLayout.createSequentialGroup()
                        .add(10, 10, 10)
                        .add(m_jTxtDiscount, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(m_jPanTotalsLayout.createSequentialGroup()
                        .add(40, 40, 40)
                        .add(m_jTotalEuros, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(m_jPanRefundTotals, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(25, 25, 25))
        );

        jPanel4.add(m_jPanTotals, java.awt.BorderLayout.LINE_END);

        m_jPanelCentral.add(jPanel4, java.awt.BorderLayout.SOUTH);

        m_jPanTicket.add(m_jPanelCentral, java.awt.BorderLayout.CENTER);

        m_jPanContainer.add(m_jPanTicket, java.awt.BorderLayout.CENTER);

        m_jContEntries.setLayout(new java.awt.BorderLayout());

        m_jPanEntries.setLayout(new javax.swing.BoxLayout(m_jPanEntries, javax.swing.BoxLayout.Y_AXIS));

        m_jNumberKeys.addJNumberEventListener(new com.openbravo.beans.JNumberEventListener() {
            public void keyPerformed(com.openbravo.beans.JNumberEvent evt) {
                m_jNumberKeysKeyPerformed(evt);
            }
        });
        m_jPanEntries.add(m_jNumberKeys);

        jPanel9.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        jPanel9.setLayout(new java.awt.GridBagLayout());

        m_jPrice.setBackground(java.awt.Color.white);
        m_jPrice.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        m_jPrice.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(javax.swing.UIManager.getDefaults().getColor("Button.darkShadow")), javax.swing.BorderFactory.createEmptyBorder(1, 4, 1, 4)));
        m_jPrice.setOpaque(true);
        m_jPrice.setPreferredSize(new java.awt.Dimension(100, 22));
        m_jPrice.setRequestFocusEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel9.add(m_jPrice, gridBagConstraints);

        m_jPor.setBackground(java.awt.Color.white);
        m_jPor.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        m_jPor.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(javax.swing.UIManager.getDefaults().getColor("Button.darkShadow")), javax.swing.BorderFactory.createEmptyBorder(1, 4, 1, 4)));
        m_jPor.setOpaque(true);
        m_jPor.setPreferredSize(new java.awt.Dimension(22, 22));
        m_jPor.setRequestFocusEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel9.add(m_jPor, gridBagConstraints);

        m_jEnter.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/barcode.png"))); // NOI18N
        m_jEnter.setFocusPainted(false);
        m_jEnter.setFocusable(false);
        m_jEnter.setRequestFocusEnabled(false);
        m_jEnter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jEnterActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel9.add(m_jEnter, gridBagConstraints);

        m_jTax.setFocusable(false);
        m_jTax.setRequestFocusEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        jPanel9.add(m_jTax, gridBagConstraints);

        m_jaddtax.setText("+");
        m_jaddtax.setFocusPainted(false);
        m_jaddtax.setFocusable(false);
        m_jaddtax.setRequestFocusEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        jPanel9.add(m_jaddtax, gridBagConstraints);

        m_jPanEntries.add(jPanel9);

        m_jKeyFactory.setBackground(javax.swing.UIManager.getDefaults().getColor("Panel.background"));
        m_jKeyFactory.setForeground(javax.swing.UIManager.getDefaults().getColor("Panel.background"));
        m_jKeyFactory.setBorder(null);
        m_jKeyFactory.setCaretColor(javax.swing.UIManager.getDefaults().getColor("Panel.background"));
        m_jKeyFactory.setPreferredSize(new java.awt.Dimension(1, 1));
        m_jKeyFactory.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                m_jKeyFactoryKeyTyped(evt);
            }
        });
        m_jPanEntries.add(m_jKeyFactory);

        m_jContEntries.add(m_jPanEntries, java.awt.BorderLayout.NORTH);

        m_jPanContainer.add(m_jContEntries, java.awt.BorderLayout.LINE_END);

        catcontainer.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        catcontainer.setLayout(new java.awt.BorderLayout());
        m_jPanContainer.add(catcontainer, java.awt.BorderLayout.SOUTH);

        add(m_jPanContainer, "ticket");
    }// </editor-fold>//GEN-END:initComponents

    private void m_jEditLineActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jEditLineActionPerformed
//        String advanceDate = m_jTxtDate.getText();
        // String showName = m_jCboShowlist.getSelectedItem().toString();

        int i = m_ticketlines.getSelectedIndex();
        if (i < 0) {
            Toolkit.getDefaultToolkit().beep(); // no line selected
        } else {
            try {
                double startLinesCount=m_oTicket.getLine(i).getMultiply();
                TicketLineInfo newline = JProductLineEdit.showMessage(this, m_App, m_oTicket.getLine(i));
                if (newline != null) {
                 noOfSeats=   noOfSeats-(startLinesCount-m_oTicket.getLine(i).getMultiply());
                    // line has been modified
                    paintTicketLine(i, newline);
                    //     advanceFunction(advanceDate,showName);
                }
            } catch (BasicException e) {
                new MessageInf(e).show(this);
            }
        }


    }//GEN-LAST:event_m_jEditLineActionPerformed

    private void m_jEnterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jEnterActionPerformed

        stateTransition('\n');

    }//GEN-LAST:event_m_jEnterActionPerformed

    private void m_jNumberKeysKeyPerformed(com.openbravo.beans.JNumberEvent evt) {//GEN-FIRST:event_m_jNumberKeysKeyPerformed

        stateTransition(evt.getKey());

    }//GEN-LAST:event_m_jNumberKeysKeyPerformed

    private void m_jKeyFactoryKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_m_jKeyFactoryKeyTyped

        m_jKeyFactory.setText(null);
        stateTransition(evt.getKeyChar());

    }//GEN-LAST:event_m_jKeyFactoryKeyTyped

    private void m_jDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jDeleteActionPerformed

        int i = m_ticketlines.getSelectedIndex();
        if (i < 0) {
            Toolkit.getDefaultToolkit().beep(); // No hay ninguna seleccionada
        } else {
            removeTicketLine(i);
            // elimino la linea

        }


    }//GEN-LAST:event_m_jDeleteActionPerformed

    private void m_jUpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jUpActionPerformed

        m_ticketlines.selectionUp();

    }//GEN-LAST:event_m_jUpActionPerformed

    private void m_jDownActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jDownActionPerformed

        m_ticketlines.selectionDown();

    }//GEN-LAST:event_m_jDownActionPerformed

    private void m_jListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jListActionPerformed
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        String Currentdate = format.format(new Date()).toString();
        int count = 0;
        try {
            count = dlSales.getDateCount(Currentdate);
        } catch (BasicException ex) {
            Logger.getLogger(JPanelTicket.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (count == 0) {
            ProductInfoExt prod = JProductFinder.showMessage(JPanelTicket.this, dlSales);
            if (prod != null) {
                buttonTransition(prod);
            }
        } else {
            JOptionPane.showMessageDialog(JPanelTicket.this, "No shows Available - Holiday");
        }

    }//GEN-LAST:event_m_jListActionPerformed

    private void m_jbtndateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jbtndateActionPerformed

        Date date = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat dt = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat asdf = new SimpleDateFormat("yyyy-MM-dd 00:00:00");

        date = JCalendarDialog.showCalendarTime(this, date);
        Date sysdate = new Date();
        String currentDate = sdf.format(sysdate);
        String selectDate = sdf.format(date);
        String advSelectDate = dt.format(date);
        int count = 0;
        try {
            count = dlSales.getDateCount(advSelectDate);
        } catch (BasicException ex) {
            Logger.getLogger(JCatalog.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (date != null) {
            if (count != 0) {
                showMessage(this, "No shows Available - Holiday");
            } else if (date.compareTo(sysdate) >= 0 || currentDate.equals(selectDate)) {
                //   if(count!=0){
                m_jTxtDate.setText(dt.format(date).toString());
                String advanceDate = asdf.format(date).toString();

                populateShow(advanceDate);
                m_jCboShowlist.setEnabled(true);
                m_jCboShowlist.setSelectedIndex(-1);
                m_jAvailSeats.setText("");


            } else {
                showMessage(this, "Please select the valid date");
                m_jCboShowlist.setEnabled(false);
                m_jTxtDate.setText("");

            }

        }


}//GEN-LAST:event_m_jbtndateActionPerformed
    private void showMessage(JPanelTicket aThis, String msg) {
        JOptionPane.showMessageDialog(aThis, msg);
    }
    private void m_jCboAdvbookActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jCboAdvbookActionPerformed
        if (m_jCboAdvbook.isSelected() == true) {
            m_jTxtDate.setVisible(true);
            m_jbtndate.setVisible(true);
            m_jLblDate.setVisible(true);
            m_jTxtDate.setText("");
            m_jCloseShow.setEnabled(false);
            m_jCloseDay.setEnabled(false);

        } else {

            m_jTxtDate.setVisible(false);
            m_jbtndate.setVisible(false);
            m_jLblDate.setVisible(false);
            m_jCboShowlist.setEnabled(false);
            m_jTxtDate.setText("");
            m_jCloseShow.setEnabled(true);
            m_jCloseDay.setEnabled(true);
            advanceShowName = "";
            refreshTicket();
            try {
                m_jCboShowlist.setSelectedItem(sysShowName);
                jPanelTicketSales.activate(sysShowName);
            } catch (BasicException ex) {
                Logger.getLogger(JPanelTicket.class.getName()).log(Level.SEVERE, null, ex);
            }

        }// TODO add your handling code here:
    }//GEN-LAST:event_m_jCboAdvbookActionPerformed

    private void m_jCboAdvbookFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_m_jCboAdvbookFocusLost
        if (m_jCboAdvbook.isSelected() == false) {
            m_jTxtDate.setVisible(false);
            m_jbtndate.setVisible(false);
            m_jLblDate.setVisible(false);
            m_jTxtDate.setText("");

        }// TODO add your handling code here:
    }//GEN-LAST:event_m_jCboAdvbookFocusLost

    private void m_jCboShowlistActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jCboShowlistActionPerformed
        //  advanceShowName = "";
        if (m_jCboAdvbook.isSelected() == true) {
            String ShowName = (String) m_jCboShowlist.getSelectedItem();
            int availableSeatCount = 0;
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat dsf = new SimpleDateFormat("dd-MM-yyyy");
            String sysDate = null;
            Date createdDate = new Date();
            Date convertToDate = null;
            String advDate = m_jTxtDate.getText();
            advanceShowDate = advDate;
            Date advshowDate = null;
            advanceShowName = ShowName;
            try {
                advshowDate = dsf.parse(advDate);
            } catch (ParseException ex) {
                Logger.getLogger(JPanelTicket.class.getName()).log(Level.SEVERE, null, ex);
            }

            advDate = df.format(advshowDate);

            if (!advDate.equals("")) {
                sysDate = advDate;

            } else {
                sysDate = df.format(createdDate);
            }
            try {
                convertToDate = df.parse(sysDate);
            } catch (ParseException ex) {
                Logger.getLogger(JPanelTicket.class.getName()).log(Level.SEVERE, null, ex);
            }

            if (m_jCboShowlist.getSelectedIndex() != -1) {


                try {
                    jPanelTicketSales.activate(ShowName);
                } catch (BasicException ex) {
                    Logger.getLogger(JPanelTicket.class.getName()).log(Level.SEVERE, null, ex);
                }
                try {
                    advshowDate = df.parse(advDate);
                } catch (ParseException ex) {
                    Logger.getLogger(JPanelTicket.class.getName()).log(Level.SEVERE, null, ex);
                }

                advDate = dsf.format(advshowDate);
                m_jTxtDate.setText(advDate);


                currentShow = dlSales.getShowDetails(ShowName);

                try {
                    availableSeatCount = dlSales.getAvailableCount(ShowName, convertToDate);
                } catch (BasicException ex) {
                    Logger.getLogger(JPanelTicket.class.getName()).log(Level.SEVERE, null, ex);
                }
                int seats = currentShow.get(0).getnoofseats();
                if (availableSeatCount == 0) {
                    try {
                        dlSales.insertAvailableSeats(ShowName, seats, convertToDate);
                    } catch (BasicException ex) {
                        Logger.getLogger(JPanelTicket.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                try {
                    availableSeats = dlSales.getAvailableSeats(ShowName, convertToDate);
                } catch (BasicException ex) {
                    Logger.getLogger(JPanelTicket.class.getName()).log(Level.SEVERE, null, ex);
                }
                populateShow(sysDate);
                m_jAvailSeats.setText(Integer.toString(availableSeats));
                m_jCboShowlist.setSelectedItem(ShowName);
                m_jCboShowlist.setEnabled(true);
                m_jCboAdvbook.setSelected(true);
                m_jLblDate.setVisible(true);
                m_jbtndate.setVisible(true);
                m_jTxtDate.setVisible(true);
            }
        }

    }//GEN-LAST:event_m_jCboShowlistActionPerformed
    public String getshowName() {
        return advShow;
    }

    public void setShowname(String advShow) {
        this.advShow = advShow;
    }
    private void m_jCloseShowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jCloseShowActionPerformed

        String showName = (String) m_jCboShowlist.getSelectedItem();
        java.util.List<showInfo> nextShow = null;
        String currentStartDateTime = null;
        String dateStart = null;
        int showCount = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String CurrentTime = sdf.format(new Date()).toString();
        Date createdDate = new Date();
        SimpleDateFormat dfst = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
        SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat dateTime = new SimpleDateFormat("yyyy-MM-dd");
        String currentDate =  dateTime.format(createdDate);
        String currentDateTime =  dt.format(createdDate);
        String openShowName =null;
        Date convertToDate = null;
        Date dNow = new Date();
        currentStartDateTime = dfst.format(createdDate);
        String specialShowDate = dfst.format(new Date()).toString();

       try {
            showCount = dlSales.getClosecount(currentDateTime);
        } catch (BasicException ex) {
            Logger.getLogger(JPanelTicket.class.getName()).log(Level.SEVERE, null, ex);
        }
       if(showCount!=0){
            try {
                openShowName = dlSales.getShowName(currentDateTime);
            } catch (BasicException ex) {
                Logger.getLogger(JPanelTicket.class.getName()).log(Level.SEVERE, null, ex);
            }
       }
        loggedUserId = m_App.getAppUserView().getUser().getUserInfo().getId();
        try {

            dlSystem.peoplelogInsert(UUID.randomUUID().toString(), loggedUserId, getDateTime(), "Close Show", 0.0);

            // SentenceExec sent = m_dlSystem.peoplelogInsert();
        } catch (BasicException ex) {
            Logger.getLogger(JTicketsBagShared.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            m_PaymentsToClose = PaymentsShowModel.loadInstance(m_App, showName);
        } catch (BasicException ex) {
            Logger.getLogger(JTicketsBagShared.class.getName()).log(Level.SEVERE, null, ex);
        }
        if(showName.equals(openShowName) || showName.equals("")){
        int res = JOptionPane.showConfirmDialog(this, AppLocal.getIntString("message.wannacloseshow"), AppLocal.getIntString("message.showtitle"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (res == JOptionPane.YES_OPTION) {

            
            int receiptCount = 0;
              int advreceiptCount = 0;
            int showNo = 0;

            closedshowDetails = dlSales.getClosedShow(m_App.getActiveShowIndex());
            String closeShowEndTime = closedshowDetails.get(0).getendTime();
            String closedShowStartTime = null;
            try {
                closedShowStartTime = dlSales.getDateStart(m_App.getActiveShowIndex());
            } catch (BasicException ex) {
                Logger.getLogger(JPanelTicket.class.getName()).log(Level.SEVERE, null, ex);
            }
            Date closedShowStart = null;
             if(closedShowStartTime!=null){
            try {
               
                    closedShowStart = dt.parse(closedShowStartTime);
               
            } catch (ParseException ex) {
                Logger.getLogger(JPanelTicket.class.getName()).log(Level.SEVERE, null, ex);
            }

            dateStart = dfst.format(closedShowStart);

            if (!dateStart.equals(specialShowDate)) {
                nextShow = dlSales.getCurrentShow(specialShowDate);
            } else {

                nextShow = dlSales.getNextShow(closeShowEndTime, currentStartDateTime);
            }
            try {
                receiptCount = dlSales.getNorReceiptCount(m_App.getActiveShowIndex(),currentDate,showName );
            } catch (BasicException ex) {
                Logger.getLogger(JPanelTicket.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                advreceiptCount = dlSales.getAdvanceReceiptCount(currentDate,showName);
            } catch (BasicException ex) {
                Logger.getLogger(JPanelTicket.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (receiptCount == 0 &&  advreceiptCount == 0) {
                //  if(advreceiptCount == 0){
                try {
                    // Cerramos la caja si esta pendiente de cerrar.

                if (m_App.getActiveShowDateEnd() == null) {
                    new StaticSentence(m_App.getSession(), "UPDATE CLOSEDSHOW SET DATEEND = ?, HOSTSEQUENCE = ? WHERE HOST = ? AND MONEY = ?", new SerializerWriteBasic(new Datas[]{Datas.TIMESTAMP, Datas.INT, Datas.STRING, Datas.STRING})).exec(new Object[]{dNow, 0, m_App.getProperties().getHost(), m_App.getActiveShowIndex()});
                }
                } catch (BasicException e) {
                    MessageInf msg = new MessageInf(MessageInf.SGN_NOTICE, AppLocal.getIntString("message.cannotclosecash"), e);
                    msg.show(this);
                }
               // }
            } else {
                try {
                    // Cerramos la caja si esta pendiente de cerrar.

                    if (m_App.getActiveShowDateEnd() == null) {
                        new StaticSentence(m_App.getSession(), "UPDATE CLOSEDSHOW SET DATEEND = ? WHERE HOST = ? AND MONEY = ?", new SerializerWriteBasic(new Datas[]{Datas.TIMESTAMP, Datas.STRING, Datas.STRING})).exec(new Object[]{dNow, m_App.getProperties().getHost(), m_App.getActiveShowIndex()});
                    }
                } catch (BasicException e) {
                    MessageInf msg = new MessageInf(MessageInf.SGN_NOTICE, AppLocal.getIntString("message.cannotclosecash"), e);
                    msg.show(this);
                }
              
                //  printPayments("Printer.CloseShow");
                // Mostramos el mensaje
                try {
                    csCreateReport(m_PaymentsToClose, showName);
                    csCreateAnotherReport(m_PaymentsToClose, showName);
                } catch (IOException ex) {
                    Logger.getLogger(JPanelTicket.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
         
            if (nextShow.size() != 0) {

                nextShowName = nextShow.get(0).getShowName();

                try {
                    jPanelTicketSales.activate(nextShowName);
                } catch (BasicException ex) {
                    Logger.getLogger(JPanelTicket.class.getName()).log(Level.SEVERE, null, ex);
                }
                populateShow(currentStartDateTime);
                try {
                    showNo = dlSystem.getSequenceShow(m_App.getProperties().getHost()) + 1;
                } catch (BasicException ex) {
                    Logger.getLogger(JPanelTicket.class.getName()).log(Level.SEVERE, null, ex);
                }
                m_jCboShowlist.setSelectedItem(nextShowName);
                
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                DateFormat dst = new SimpleDateFormat("dd-MM-yyyy");
                int availableSeatCount = 0;
                String sysDate = null;
                sysDate = df.format(createdDate);
                try {
                    convertToDate = df.parse(sysDate);
                } catch (ParseException ex) {
                    Logger.getLogger(JPanelTicket.class.getName()).log(Level.SEVERE, null, ex);
                }
                try {
                    availableSeatCount = dlSales.getAvailableCount(nextShowName, convertToDate);
                } catch (BasicException ex) {
                    Logger.getLogger(JPanelTicket.class.getName()).log(Level.SEVERE, null, ex);
                }
                //  int seats = currentShow.get(0).getnoofseats();
                if (availableSeatCount == 0) {
                    try {
                        dlSales.insertAvailableSeats(nextShowName, nextShow.get(0).getnoofseats(), convertToDate);
                    } catch (BasicException ex) {
                        Logger.getLogger(JPanelTicket.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    availableSeats = nextShow.get(0).getnoofseats();
                } else {
                    try {
                        availableSeats = dlSales.getAvailableSeats(nextShowName, convertToDate);
                    } catch (BasicException ex) {
                        Logger.getLogger(JPanelTicket.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

                m_jAvailSeats.setText(Integer.toString(availableSeats));

                m_App.setActiveShow(UUID.randomUUID().toString(), showNo, dNow, null);
                try {
                    // creamos la caja activa
                    dlSystem.execInsertShow(new Object[]{m_App.getActiveShowIndex(), m_App.getProperties().getHost(), m_App.getActiveShowSequence(), m_App.getActiveShowDateStart(), m_App.getActiveShowDateEnd(), nextShowName, "Y"});
                } catch (BasicException ex) {
                    Logger.getLogger(JTicketsBagShared.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                m_jCboShowlist.setSelectedIndex(-1);
                m_jAvailSeats.setText("");
            }
            }
          
               JOptionPane.showMessageDialog(this, AppLocal.getIntString("message.closeshowok"), AppLocal.getIntString("message.showtitle"), JOptionPane.INFORMATION_MESSAGE);
            
                m_PaymentsToClose.setDateEnd(dNow);

        }
        }else{
             DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
             String showId = null;
             int closeShowNo = 0;
              if(showCount!=0){
            try {
                closeShowNo = dlSales.getShowNo(currentDateTime);
            } catch (BasicException ex) {
                Logger.getLogger(JPanelTicket.class.getName()).log(Level.SEVERE, null, ex);
            }
                try {
                    showId = dlSales.getShowId(currentDateTime);
                } catch (BasicException ex) {
                    Logger.getLogger(JPanelTicket.class.getName()).log(Level.SEVERE, null, ex);
                }
              }
                String sysDate = null;
                sysDate = df.format(createdDate);
                try {
                    convertToDate = df.parse(sysDate);
                } catch (ParseException ex) {
                    Logger.getLogger(JPanelTicket.class.getName()).log(Level.SEVERE, null, ex);
                }
               int res = JOptionPane.showConfirmDialog(this, AppLocal.getIntString("message.wannaOpenshow"), AppLocal.getIntString("message.showtitle"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
               if (res == JOptionPane.YES_OPTION) {
               try {
                    jPanelTicketSales.activate(openShowName);
                } catch (BasicException ex) {
                    Logger.getLogger(JPanelTicket.class.getName()).log(Level.SEVERE, null, ex);
                }
                populateShow(currentStartDateTime);
                m_jCboShowlist.setSelectedItem(openShowName);
                try {
                    availableSeats = dlSales.getAvailableSeats(openShowName, convertToDate);
                } catch (BasicException ex) {
                    Logger.getLogger(JPanelTicket.class.getName()).log(Level.SEVERE, null, ex);
                }

                m_jAvailSeats.setText(Integer.toString(availableSeats));
                m_App.setActiveShow(showId, closeShowNo, dNow, null);
        }
        }

    }//GEN-LAST:event_m_jCloseShowActionPerformed
    private void csCreateReport(PaymentsShowModel payments, String showName) throws IOException {

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String strDate = sdf.format(new Date());
        String reportsDir = m_App.getProperties().getProperty("jnp.reports");
        File pdffolder = new File(reportsDir);
        String filename = reportsDir + strDate + "-"+showName + ".pdf";
        createFile(pdffolder);
        CreateReport cr_cs = new CreateReport(payments, filename);
        cr_cs.generate();
    }

    private void csCreateAnotherReport(PaymentsShowModel payments, String showName) throws IOException {

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String strDate = sdf.format(new Date());
        String reportsDir = m_App.getProperties().getProperty("jnp.reports1");
        File pdffolder = new File(reportsDir);
        String filename = reportsDir + strDate + "-"+showName + ".pdf";
        createFile(pdffolder);
        CreateReport cr_cs = new CreateReport(payments, filename);
        cr_cs.generate();
    }

    private void createFile(File pdffolder) throws IOException {
        if (!pdffolder.exists()) {
            pdffolder.mkdir();
        }
    }

    private void printPayments(String report) {

        String sresource = dlSystem.getResourceAsXML(report);

        if (sresource == null) {
            MessageInf msg = new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.cannotprintticket"));
            msg.show(this);
        } else {
            try {
                ScriptEngine script = ScriptFactory.getScriptEngine(ScriptFactory.VELOCITY);
                script.put("payments", m_PaymentsToClose);

                m_TTP.printTicket(script.eval(sresource).toString());
            } catch (ScriptException e) {
                MessageInf msg = new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.cannotprintticket"), e);
                msg.show(this);
            } catch (TicketPrinterException e) {
                MessageInf msg = new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.cannotprintticket"), e);
                msg.show(this);
            }
        }
    }

    private void printDayPayments(String report) {

        String sresource = dlSystem.getResourceAsXML(report);


        if (sresource == null) {
            MessageInf msg = new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.cannotprintticket"));
            msg.show(this);
        } else {
            try {
                ScriptEngine script = ScriptFactory.getScriptEngine(ScriptFactory.VELOCITY);
                script.put("payments", m_PaymentsToCloseCash);

                m_TTP.printTicket(script.eval(sresource).toString());
            } catch (ScriptException e) {
                MessageInf msg = new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.cannotprintticket"), e);
                msg.show(this);
            } catch (TicketPrinterException e) {
                MessageInf msg = new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.cannotprintticket"), e);
                msg.show(this);
            }
        }
    }

    public static String getDateTime() {

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
        return sdf.format(cal.getTime());

    }
    private void m_jCloseDayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jCloseDayActionPerformed


        try {
            m_PaymentsToCloseCash = PaymentsModel.loadInstance(m_App);
        } catch (BasicException ex) {
            Logger.getLogger(JTicketsBagShared.class.getName()).log(Level.SEVERE, null, ex);
        }
        loggedUserId = m_App.getAppUserView().getUser().getUserInfo().getId();
        try {
            dlSystem.peoplelogInsert(UUID.randomUUID().toString(), loggedUserId, getDateTime(), "Close Day", 0.0);

        } catch (BasicException ex) {
            Logger.getLogger(JTicketsBagShared.class.getName()).log(Level.SEVERE, null, ex);
        }

        int res = JOptionPane.showConfirmDialog(this, AppLocal.getIntString("message.wannacloseday"), AppLocal.getIntString("message.daytitle"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (res == JOptionPane.YES_OPTION) {

            Date dNow = new Date();

            try {
                // Cerramos la caja si esta pendiente de cerrar.
                if (m_App.getActiveCashDateEnd() == null) {
                    new StaticSentence(m_App.getSession(), "UPDATE CLOSEDCASH SET DATEEND = ? WHERE HOST = ? AND MONEY = ?", new SerializerWriteBasic(new Datas[]{Datas.TIMESTAMP, Datas.STRING, Datas.STRING})).exec(new Object[]{dNow, m_App.getProperties().getHost(), m_App.getActiveCashIndex()});
                }
            } catch (BasicException e) {
                MessageInf msg = new MessageInf(MessageInf.SGN_NOTICE, AppLocal.getIntString("message.cannotcloseday"), e);
                msg.show(this);
            }


            // Creamos una nueva caja
            m_App.setActiveCash(UUID.randomUUID().toString(), m_App.getActiveCashSequence() + 1, dNow, null);
            try {
                // creamos la caja activa
                dlSystem.execInsertCash(new Object[]{m_App.getActiveCashIndex(), m_App.getProperties().getHost(), m_App.getActiveCashSequence(), m_App.getActiveCashDateStart(), m_App.getActiveCashDateEnd()});
            } catch (BasicException ex) {
                Logger.getLogger(JTicketsBagShared.class.getName()).log(Level.SEVERE, null, ex);
            }

            // ponemos la fecha de fin
            m_PaymentsToCloseCash.setDateEnd(dNow);
            try {
                // print report
                //printDayPayments("Printer.CloseCash");
                ccCreateReport(m_PaymentsToCloseCash);
                ccCreateAnotherReport(m_PaymentsToCloseCash);
            } catch (IOException ex) {
                Logger.getLogger(JPanelTicket.class.getName()).log(Level.SEVERE, null, ex);
            }

            // Mostramos el mensaje
            JOptionPane.showMessageDialog(this, AppLocal.getIntString("message.closedayok"), AppLocal.getIntString("message.daytitle"), JOptionPane.INFORMATION_MESSAGE);


        }// TODO add your handling code here:        // TODO add your handling code here:
}//GEN-LAST:event_m_jCloseDayActionPerformed

    private void m_jResetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jResetActionPerformed
       int closeShowCount = 0;
       int openShowCount = 0;
       SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
       Date createdDate = new Date();
       String dateStart =  dt.format(createdDate);
       SimpleDateFormat dfst = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
       String currentStartDateTime = dfst.format(createdDate);
       DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
       String sysDate = null;
       Date convertToDate = null;
       String showId = null;
       int closeShowNo =0;
        java.util.List<CloseshowInfo> showList = null;
       sysDate = df.format(createdDate);
        try {
            convertToDate = df.parse(sysDate);
        } catch (ParseException ex) {
            Logger.getLogger(JPanelTicket.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            closeShowCount = dlSales.getDateEnd();
        } catch (BasicException ex) {
            Logger.getLogger(JPanelTicket.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            openShowCount = dlSales.getShowOpenCount();
        } catch (BasicException ex) {
            Logger.getLogger(JPanelTicket.class.getName()).log(Level.SEVERE, null, ex);
        }
      

            showList =  dlSales.getOpenShowId();
            int receiptCount =0;


        if(closeShowCount==0){
            try {
                new StaticSentence(m_App.getSession(), "UPDATE CLOSEDSHOW SET DATEEND = ? ORDER BY DATEEND DESC LIMIT 1", new SerializerWriteBasic(new Datas[]{Datas.TIMESTAMP})).exec(new Object[]{null});
            } catch (BasicException ex) {
                Logger.getLogger(JPanelTicket.class.getName()).log(Level.SEVERE, null, ex);
            }
            String showname = null;
            try {
                showname = dlSales.getShowName(dateStart);
            } catch (BasicException ex) {
                Logger.getLogger(JPanelTicket.class.getName()).log(Level.SEVERE, null, ex);
            }
              try {
                closeShowNo = dlSales.getShowNo(dateStart);
            } catch (BasicException ex) {
                Logger.getLogger(JPanelTicket.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                showId = dlSales.getShowId(dateStart);
            } catch (BasicException ex) {
                Logger.getLogger(JPanelTicket.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                jPanelTicketSales.activate(showname);
            } catch (BasicException ex) {
                Logger.getLogger(JPanelTicket.class.getName()).log(Level.SEVERE, null, ex);
            }
            populateShow(currentStartDateTime);
            m_jCboShowlist.setSelectedItem(showname);
            try {
                availableSeats = dlSales.getAvailableSeats(showname, convertToDate);
            } catch (BasicException ex) {
                Logger.getLogger(JPanelTicket.class.getName()).log(Level.SEVERE, null, ex);
            }

            m_jAvailSeats.setText(Integer.toString(availableSeats));
            m_App.setActiveShow(showId, closeShowNo, createdDate, null);

       }else if(closeShowCount>1){
           int sequenceNo = 0;
            try {
                sequenceNo = dlSales.getSequenceNo();
            } catch (BasicException ex) {
                Logger.getLogger(JPanelTicket.class.getName()).log(Level.SEVERE, null, ex);
            }
            String closeShowId = null;
            if(openShowCount>1){
                for(int i=0;i<showList.size();i++){
                    try {
                        receiptCount = dlSales.getReceiptCount(showList.get(i).getId());
                    } catch (BasicException ex) {
                        Logger.getLogger(JPanelTicket.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    if(receiptCount!=0){

                        try {
                            closeShowId = dlSales.getCloseShowId(showList.get(i).getshowName(),currentStartDateTime,dateStart);
                        } catch (BasicException ex) {
                            Logger.getLogger(JPanelTicket.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        try {
                            new StaticSentence(m_App.getSession(), "UPDATE RECEIPTS SET MONEYSHOW = ? WHERE MONEYSHOW = '"+showList.get(i).getId()+"' ", new SerializerWriteBasic(new Datas[]{Datas.STRING})).exec(new Object[]{closeShowId});
                        } catch (BasicException ex) {
                            Logger.getLogger(JPanelTicket.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        dlSales.deleteOpenShow(showList.get(i).getId());
                    }else{
                        dlSales.deleteOpenShow(showList.get(i).getId());
                    }

                    }
            }
           sequenceNo = sequenceNo-1;
             try {
                new StaticSentence(m_App.getSession(), "UPDATE CLOSEDSHOW SET DATEEND = ?,HOSTSEQUENCE = ? ORDER BY DATEEND DESC LIMIT 1", new SerializerWriteBasic(new Datas[]{Datas.TIMESTAMP, Datas.INT})).exec(new Object[]{null, sequenceNo});
            } catch (BasicException ex) {
                Logger.getLogger(JPanelTicket.class.getName()).log(Level.SEVERE, null, ex);
            }
                String showname = null;
            try {
                showname = dlSales.getShowName(dateStart);
            } catch (BasicException ex) {
                Logger.getLogger(JPanelTicket.class.getName()).log(Level.SEVERE, null, ex);
            }
              try {
                closeShowNo = dlSales.getShowNo(dateStart);
            } catch (BasicException ex) {
                Logger.getLogger(JPanelTicket.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                showId = dlSales.getShowId(dateStart);
            } catch (BasicException ex) {
                Logger.getLogger(JPanelTicket.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                jPanelTicketSales.activate(showname);
            } catch (BasicException ex) {
                Logger.getLogger(JPanelTicket.class.getName()).log(Level.SEVERE, null, ex);
            }
            populateShow(currentStartDateTime);
            m_jCboShowlist.setSelectedItem(showname);
            try {
                availableSeats = dlSales.getAvailableSeats(showname, convertToDate);
            } catch (BasicException ex) {
                Logger.getLogger(JPanelTicket.class.getName()).log(Level.SEVERE, null, ex);
            }

            m_jAvailSeats.setText(Integer.toString(availableSeats));
            m_App.setActiveShow(showId, closeShowNo, createdDate, null);
       }


    }//GEN-LAST:event_m_jResetActionPerformed

    private void m_jMoneyHandoverActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jMoneyHandoverActionPerformed
       String showName = (String) m_jCboShowlist.getSelectedItem();
        try {
            m_PaymentsToMoneyTransfer = PaymentsHandOverModel.loadInstance(m_App, showName);
        } catch (BasicException ex) {
            Logger.getLogger(JTicketsBagShared.class.getName()).log(Level.SEVERE, null, ex);
        }
        loggedUserId = m_App.getAppUserView().getUser().getUserInfo().getId();
        try {
            dlSystem.peoplelogInsert(UUID.randomUUID().toString(), loggedUserId, getDateTime(), "Money HandOver", 0.0);

        } catch (BasicException ex) {
            Logger.getLogger(JTicketsBagShared.class.getName()).log(Level.SEVERE, null, ex);
        }

        int res = JOptionPane.showConfirmDialog(this, AppLocal.getIntString("message.wannacloseday"), AppLocal.getIntString("message.daytitle"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (res == JOptionPane.YES_OPTION) {

            Date dNow = new Date();
            try {
                hoCreateReport(m_PaymentsToMoneyTransfer, showName);
                hoCreateAnotherReport(m_PaymentsToMoneyTransfer,showName);
                try {
                    new StaticSentence(m_App.getSession(), "UPDATE RECEIPTS SET ISHANDOVER = 'Y' WHERE MONEYSHOW = ?", new SerializerWriteBasic(new Datas[]{Datas.STRING})).exec(new Object[]{m_App.getActiveShowIndex()});
                } catch (BasicException ex) {
                    Logger.getLogger(JPanelTicket.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (IOException ex) {
                Logger.getLogger(JPanelTicket.class.getName()).log(Level.SEVERE, null, ex);
            }
      
            JOptionPane.showMessageDialog(this, AppLocal.getIntString("message.closedayok"), AppLocal.getIntString("message.daytitle"), JOptionPane.INFORMATION_MESSAGE);

        }// TODO add your handlin        // TODO add your handling code here:
    }//GEN-LAST:event_m_jMoneyHandoverActionPerformed

    private void ccCreateReport(PaymentsModel payments) throws IOException {

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyyk-m-s");
        String strDate = sdf.format(new Date());
        String reportsDir = m_App.getProperties().getProperty("jnp.reports");

        File pdffolder = new File(reportsDir);
        String filename = reportsDir + "HO" + strDate + ".pdf";
        createFile(pdffolder);
        CreateReportCloseCash cr_cs = new CreateReportCloseCash(payments, filename);
        cr_cs.generate();
    }

    private void ccCreateAnotherReport(PaymentsModel payments) throws IOException {

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyyk-m-s");
        String strDate = sdf.format(new Date());
        String reportsDir = m_App.getProperties().getProperty("jnp.reports1");

        File pdffolder = new File(reportsDir);
        String filename = reportsDir + "HO" + strDate + ".pdf";
        createFile(pdffolder);
        CreateReportCloseCash cr_cs = new CreateReportCloseCash(payments, filename);
        cr_cs.generate();
    }
     private void hoCreateReport(PaymentsHandOverModel payments, String showName) throws IOException {

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyyk-m-s");
        String strDate = sdf.format(new Date());
        String reportsDir = m_App.getProperties().getProperty("jnp.reports");

        File pdffolder = new File(reportsDir);
        String filename = reportsDir + "CashHO"+"-"+showName+"-" + strDate + ".pdf";
        createFile(pdffolder);
        CreateHoReport cr_cs = new CreateHoReport(payments, filename);
        cr_cs.generate();
    }

    private void hoCreateAnotherReport(PaymentsHandOverModel payments, String showName) throws IOException {

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyyk-m-s");
        String strDate = sdf.format(new Date());
        String reportsDir = m_App.getProperties().getProperty("jnp.reports1");

        File pdffolder = new File(reportsDir);
        String filename = reportsDir + "CashHO" + "-"+showName+"-" + strDate + ".pdf";
        createFile(pdffolder);
        CreateHoReport cr_cs = new CreateHoReport(payments, filename);
        cr_cs.generate();
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel catcontainer;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JLabel m_jAvailSeats;
    private javax.swing.JPanel m_jButtonsExt;
    private javax.swing.JCheckBox m_jCboAdvbook;
    private javax.swing.JComboBox m_jCboShowlist;
    private javax.swing.JButton m_jCloseDay;
    private javax.swing.JButton m_jCloseShow;
    private javax.swing.JPanel m_jContEntries;
    private javax.swing.JButton m_jDelete;
    private javax.swing.JButton m_jDown;
    private javax.swing.JButton m_jEditLine;
    private javax.swing.JButton m_jEnter;
    private javax.swing.JTextField m_jKeyFactory;
    private javax.swing.JLabel m_jLblDate;
    private javax.swing.JLabel m_jLblTotalEuros1;
    private javax.swing.JLabel m_jLblTotalRefundEuros;
    private javax.swing.JButton m_jList;
    private javax.swing.JButton m_jMoneyHandover;
    private com.openbravo.beans.JNumberKeys m_jNumberKeys;
    private javax.swing.JPanel m_jOptions;
    private javax.swing.JPanel m_jPanContainer;
    private javax.swing.JPanel m_jPanEntries;
    private javax.swing.JPanel m_jPanRefundTotals;
    private javax.swing.JPanel m_jPanTicket;
    private javax.swing.JPanel m_jPanTotals;
    private javax.swing.JPanel m_jPanelBag;
    private javax.swing.JPanel m_jPanelCentral;
    private javax.swing.JPanel m_jPanelScripts;
    private javax.swing.JLabel m_jPor;
    private javax.swing.JLabel m_jPrice;
    private javax.swing.JButton m_jReset;
    private javax.swing.JComboBox m_jTax;
    private javax.swing.JLabel m_jTotalEuros;
    private javax.swing.JTextField m_jTotalRefundEuros;
    private javax.swing.JLabel m_jTxtCancelFee;
    private javax.swing.JTextField m_jTxtDate;
    private javax.swing.JTextField m_jTxtDiscount;
    private javax.swing.JButton m_jUp;
    private javax.swing.JToggleButton m_jaddtax;
    private javax.swing.JButton m_jbtndate;
    private javax.swing.JLabel m_jlblAvailSeats;
    private javax.swing.JLabel m_jlblCancelFee;
    private javax.swing.JLabel m_jlblDiscount;
    // End of variables declaration//GEN-END:variables
}
