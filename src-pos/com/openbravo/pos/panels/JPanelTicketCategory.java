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

import com.openbravo.pos.forms.JPanelView;
import com.openbravo.pos.forms.AppView;
import com.openbravo.pos.forms.AppLocal;
import java.awt.image.BufferedImage;
import java.text.ParseException;
import java.util.List;
import javax.swing.*;
import java.util.Date;
import com.openbravo.format.Formats;
import com.openbravo.basic.BasicException;
import com.openbravo.beans.JCalendarDialog;
import com.openbravo.pos.forms.BeanFactoryApp;
import com.openbravo.pos.forms.BeanFactoryException;
import com.openbravo.pos.forms.DataLogicSales;
import com.openbravo.pos.forms.DataLogicSystem;
import com.openbravo.pos.forms.JRootApp;
import com.openbravo.pos.forms.holidayInfo;
import com.openbravo.pos.printer.TicketParser;
import com.openbravo.pos.ticket.ProductInfoExt;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 *
 * @author adrianromero
 */
public class JPanelTicketCategory extends JPanel implements JPanelView,BeanFactoryApp{
    private AppView m_App;
    private DataLogicSystem m_dlSystem;
    private com.openbravo.pos.forms.holidayInfo m_polines;
    private TicketParser m_TTP;
   
    protected DataLogicSales m_dlSales;
    private String[] holiday;
   
    private int holidaySize = 0;
    private static Pattern p = Pattern.compile("^[a-zA-Z0-9][a-zA-Z0-9 ]*$");
    private static Pattern pNum = Pattern.compile(".*[0-9].*");
    private String user;
    public DefaultListModel model = null;
    public java.util.List<ticketCategoryInfo> list = null;
    public boolean updateMode = false;
     private JRootApp m_RootApp;

    /** Creates new form JPanelCloseMoney */
    public JPanelTicketCategory() {
        
        initComponents();
       
        m_jTxtMinAmt.setVisible(false);
        m_jTxtNoofPeople.setVisible(false);
        m_jLblMinAmt.setVisible(false);
       m_jLblNoofPeople.setVisible(false);
    }
    
    public void init(AppView app) throws BeanFactoryException{
        
        m_App = app;        
        m_dlSystem = (DataLogicSystem) m_App.getBean("com.openbravo.pos.forms.DataLogicSystem");
        m_dlSales = (DataLogicSales) m_App.getBean("com.openbravo.pos.forms.DataLogicSales");
        user = m_App.getAppUserView().getUser().getId();
        try {
            populateList(m_dlSales);
        } catch (BasicException ex) {
            Logger.getLogger(JPanelShow.class.getName()).log(Level.SEVERE, null, ex);
        }
     //   m_dlPurchase = (DataLogicSystem) m_App.getBean("com.openbravo.pos.forms.DataLogicSystem");
        m_TTP = new TicketParser(m_App.getDeviceTicket(), m_dlSystem);
  
   // JOptionPane.showMessageDialog(this, "The user has to close all pending bills before doing the close shift", AppLocal.getIntString("message.header"), JOptionPane.INFORMATION_MESSAGE);
       
    }

    public Object getBean() {
         return this;
    }

    public JComponent getComponent() {
        return this;
    }

    public String getTitle() {
        return AppLocal.getIntString("Menu.TicketCategory");
    }

    public void activate() throws BasicException {
        populateList(m_dlSales);
        clearTxtFields();
        updateMode = false;
    }

    public boolean deactivate() {
        // se me debe permitir cancelar el deactivate
        return true;
    }
    private void clearFieldsReloadJList() {
    try {
        clearTxtFields();
    } catch (BasicException ex) {
        Logger.getLogger(JPanelTicketCategory.class.getName()).log(Level.SEVERE, null, ex);
    }
    try {
        setJListContents();
    } catch (BasicException ex) {
        Logger.getLogger(JPanelTicketCategory.class.getName()).log(Level.SEVERE, null, ex);
    }
    }

public void clearTxtFields() throws BasicException {
         m_jCode.setText("");
         m_jName.setText("");
         m_jPriceSell.setText("");
         m_jTxtMinAmt.setText("");
         m_jTxtNoofPeople.setText("");
         m_jCboGroup.setSelected(false);
         m_jTxtMinAmt.setVisible(false);
         m_jCboDiscount.setSelected(false);
         m_jTxtNoofPeople.setVisible(false);
         m_jLblMinAmt.setVisible(false);
         m_jLblNoofPeople.setVisible(false);
         m_jImage.setImage(null);
       
    }
   

    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        m_jBtnSave = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        m_jCategoryList = new javax.swing.JList();
        m_jbtnNew = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        m_jName = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        m_jCode = new javax.swing.JTextField();
        m_jPriceSell = new javax.swing.JTextField();
        m_jTxtMinAmt = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        m_jLblMinAmt = new javax.swing.JLabel();
        m_jTxtNoofPeople = new javax.swing.JTextField();
        m_jCboGroup = new javax.swing.JCheckBox();
        m_jLblNoofPeople = new javax.swing.JLabel();
        m_jBtnDelete = new javax.swing.JButton();
        m_jImage = new com.openbravo.data.gui.JImageEditor();
        m_lblImage = new javax.swing.JLabel();
        m_jLblGroup = new javax.swing.JLabel();
        m_jLblDiscount = new javax.swing.JLabel();
        m_jCboDiscount = new javax.swing.JCheckBox();

        m_jBtnSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/filesave.png"))); // NOI18N
        m_jBtnSave.setPreferredSize(new java.awt.Dimension(32, 32));
        m_jBtnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jBtnSaveActionPerformed(evt);
            }
        });

        m_jCategoryList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                m_jCategoryListValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(m_jCategoryList);

        m_jbtnNew.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/editnew.png"))); // NOI18N
        m_jbtnNew.setPreferredSize(new java.awt.Dimension(32, 32));
        m_jbtnNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jbtnNewActionPerformed(evt);
            }
        });

        jLabel2.setText(AppLocal.getIntString("label.prodname")); // NOI18N

        m_jName.setBorder(javax.swing.BorderFactory.createLineBorder(javax.swing.UIManager.getDefaults().getColor("Button.darkShadow")));
        m_jName.setPreferredSize(new java.awt.Dimension(207, 27));

        jLabel6.setText(AppLocal.getIntString("label.prodticket")); // NOI18N

        m_jCode.setBorder(javax.swing.BorderFactory.createLineBorder(javax.swing.UIManager.getDefaults().getColor("Button.darkShadow")));
        m_jCode.setPreferredSize(new java.awt.Dimension(207, 27));

        m_jPriceSell.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        m_jPriceSell.setBorder(javax.swing.BorderFactory.createLineBorder(javax.swing.UIManager.getDefaults().getColor("Button.darkShadow")));
        m_jPriceSell.setPreferredSize(new java.awt.Dimension(207, 27));

        m_jTxtMinAmt.setBorder(javax.swing.BorderFactory.createLineBorder(javax.swing.UIManager.getDefaults().getColor("Button.darkShadow")));
        m_jTxtMinAmt.setPreferredSize(new java.awt.Dimension(207, 27));

        jLabel5.setText(AppLocal.getIntString("label.prodprice")); // NOI18N

        m_jLblMinAmt.setText(AppLocal.getIntString("label.prodminamt")); // NOI18N

        m_jTxtNoofPeople.setBorder(javax.swing.BorderFactory.createLineBorder(javax.swing.UIManager.getDefaults().getColor("Button.darkShadow")));
        m_jTxtNoofPeople.setPreferredSize(new java.awt.Dimension(207, 27));

        m_jCboGroup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jCboGroupActionPerformed(evt);
            }
        });

        m_jLblNoofPeople.setText(AppLocal.getIntString("label.noofpeople")); // NOI18N

        m_jBtnDelete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/editdelete.png"))); // NOI18N
        m_jBtnDelete.setPreferredSize(new java.awt.Dimension(32, 32));
        m_jBtnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jBtnDeleteActionPerformed(evt);
            }
        });

        m_lblImage.setText("Image");

        m_jLblGroup.setText("Group");

        m_jLblDiscount.setText("Discount Applicable");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(m_lblImage, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(m_jLblDiscount, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(m_jLblGroup, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(m_jLblMinAmt, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(m_jLblNoofPeople, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(134, 134, 134)
                        .addComponent(m_jbtnNew, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(6, 6, 6)
                        .addComponent(m_jBtnSave, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(6, 6, 6)
                        .addComponent(m_jBtnDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(m_jCode, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(m_jName, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(m_jPriceSell, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(m_jImage, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(m_jCboDiscount)
                    .addComponent(m_jCboGroup, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(m_jTxtMinAmt, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(m_jTxtNoofPeople, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 500, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(layout.createSequentialGroup()
                .addGap(52, 52, 52)
                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(19, 19, 19)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(19, 19, 19)
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20)
                .addComponent(m_lblImage, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(110, 110, 110)
                .addComponent(m_jLblDiscount)
                .addGap(26, 26, 26)
                .addComponent(m_jLblGroup)
                .addGap(26, 26, 26)
                .addComponent(m_jLblMinAmt, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20)
                .addComponent(m_jLblNoofPeople, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(m_jbtnNew, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(m_jBtnSave, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(m_jBtnDelete, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(8, 8, 8)
                .addComponent(m_jCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(13, 13, 13)
                .addComponent(m_jName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(13, 13, 13)
                .addComponent(m_jPriceSell, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(13, 13, 13)
                .addComponent(m_jImage, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(m_jCboDiscount)
                .addGap(19, 19, 19)
                .addComponent(m_jCboGroup)
                .addGap(19, 19, 19)
                .addComponent(m_jTxtMinAmt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(13, 13, 13)
                .addComponent(m_jTxtNoofPeople, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
    }// </editor-fold>//GEN-END:initComponents
 public void populateList(DataLogicSales m_dlSales) throws BasicException {

        model = new DefaultListModel();
        m_jCategoryList.setModel(model);
        list =  m_dlSales.getticketcategoryList();
        //String[] dListId = null;
        
        for (int i = 0; i < list.size(); i++) {
     
            String listid = list.get(i).getproductname();
            model.add(i, listid);
        }
    } private String getFormattedDate(String strDate) {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date d = new Date();
        try {
            d = (Date) format.parse(strDate);

        } catch (ParseException ex) {
        }
        return format.format(d);
    }
       private boolean isNegativeChecked(double price, double minAmount, int people) {
        boolean retVal = false;
        boolean isGroupChk = m_jCboGroup.isSelected();
        String priceMsg = "Please enter valid price ";
        String minAmtMsg = "Please enter valid amount";
        String peoplesMsg = "Please enter valid no. of peoples";
        if (isValidPrice(price + "")) {
            if (isValidPrice(minAmount + "") || (!isGroupChk)) {
                if (isValidPrice(people + "") || (!isGroupChk)) {
                    retVal = true;
                } else {
                    showMessage(this,peoplesMsg);
                    resetField(m_jTxtNoofPeople);
                }
            } else {
                showMessage(this,minAmtMsg);
                resetField(m_jTxtMinAmt);
            }
        } else {
            showMessage(this,priceMsg);
            resetField(m_jPriceSell);
        }

        return retVal;
    }
          private boolean isValidPrice(String sellPrice) {
        if (ObParseCurrency(sellPrice) < 0.0) {
            return false;
        } else {
            return true;
        }

    }
     private double ObParseCurrency(String minAmt) {
        double retVal = 0.0;
        try {
            retVal = (Double) Formats.DOUBLE.parseValue(minAmt);
        } catch (BasicException ex) {
            Logger.getLogger(JPanelTicketCategory.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NullPointerException ex) {
            Logger.getLogger(JPanelTicketCategory.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(JPanelTicketCategory.class.getName()).log(Level.SEVERE, null, ex);
        }
        return retVal;
    }
 private boolean isCodeAlreadyExists(String code) {
        boolean retVal = false;
        int codeCount = 0;
        try {
            codeCount = m_dlSales.getTicketCodeCount(code);
        } catch (BasicException ex) {
            Logger.getLogger(JPanelTicketCategory.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (codeCount != 0) {
            showMessage(this,"Ticket Code already exists");
            resetField(m_jCode);
        } else {
            retVal = true;
        }
        return retVal;
    }
  public void resetField(JTextField tField) {
        tField.setText("");
    }
    private void m_jCategoryListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_m_jCategoryListValueChanged
        // TODO add your handling code here:
        if (evt.getValueIsAdjusting()) {
          
            updateMode = true;
            String productname = null;
            try {
                productname = m_jCategoryList.getSelectedValue().toString();
            } catch (Exception ex) {
                System.out.println("unknown exception");
            }
             if (m_jCategoryList.getSelectedIndex() > -1) {

                int index = m_jCategoryList.getSelectedIndex();
                m_jCode.setText(list.get(index).getticketcode());
                m_jName.setText(list.get(index).getproductname());
                m_jPriceSell.setText(Formats.DOUBLE.formatValue(list.get(index).getPrice()));
                m_jImage.setImage((BufferedImage)list.get(index).getImage());
                if(list.get(index).getDiscount().equals("Y")){
                    m_jCboDiscount.setSelected(true);
                }else{
                    m_jCboDiscount.setSelected(false);
                }
                if(list.get(index).getgroup().equals("Y")){
                    m_jCboGroup.setSelected(true);
                    m_jLblMinAmt.setVisible(true);
                    m_jTxtMinAmt.setVisible(true);
                    m_jLblNoofPeople.setVisible(true);
                    m_jTxtNoofPeople.setVisible(true);
                    m_jTxtMinAmt.setText(Double.toString(list.get(index).getminAmount()));
                    m_jTxtNoofPeople.setText(Integer.toString(list.get(index).getnoofpeople()));
                   
                }else{
                    m_jCboGroup.setSelected(false);
                    m_jLblMinAmt.setVisible(false);
                    m_jTxtMinAmt.setVisible(false);
                    m_jLblNoofPeople.setVisible(false);
                    m_jTxtNoofPeople.setVisible(false);
                    
                }
            
            }

        }
    }//GEN-LAST:event_m_jCategoryListValueChanged
 private void setJListContents() throws BasicException {

        list = m_dlSales.getticketcategoryList();
        AbstractListModel model = new AbstractListModel() {

            public int getSize() {
                return list.size();
            }

            public Object getElementAt(int i) {
                return list.get(i).getproductname();
            }
        };
        m_jCategoryList.setModel(model);
    }
    private void m_jBtnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jBtnSaveActionPerformed

        String ticketCode = m_jCode.getText();
        String productname = m_jName.getText();
        String sellPrice = m_jPriceSell.getText();
        String minAmt = m_jTxtMinAmt.getText();
        String noofPeople = m_jTxtNoofPeople.getText();
        String group = "N";
        BufferedImage image  = m_jImage.getImage();
        String discount = "N";
        if(m_jCboDiscount.isSelected() == false){
            discount = "N";
        }
         else {
             discount = "Y";
         }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String Currentdate = sdf.format(new Date()).toString();
        double minAmount = 0.0;
        int people = 0;

        Date createdDate = null;
        Date specialDate = null;
        try {
            createdDate = (Date) sdf.parse(Currentdate);
        } catch (ParseException ex) {
            Logger.getLogger(JPanelShow.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (ticketCode.equals("")) {
            showMessage(this, "Please enter the category code");
            // m_jCode.setText("");
        } else if (!p.matcher(ticketCode).matches()) {
            showMessage(this, "Please enter the valid show name");
            m_jCode.setText("");
        } else {
            if (productname.equals("")) {
                showMessage(this, "Please enter the ticket category name");
                resetField(m_jName);
            } else if (!p.matcher(productname).matches()) {
                showMessage(this, "Please enter the valid ticket category name");
                m_jName.setText("");
            } else {
                if (sellPrice.equals("")) {
                    showMessage(this, "Please enter the price");
                    resetField(m_jPriceSell);
               // } else if (!pNum.matcher(sellPrice).matches()) {
                    //mateen
                } else if (!isNumberPatternMatches(sellPrice)) {
                    showMessage(this, "Please enter the valid price");
                    m_jPriceSell.setText("");
                } else {
                    double price = ObParseCurrency(sellPrice);
                    if (isValidPrice(sellPrice)) {
                        if (m_jCboGroup.isSelected() == false) {

                            group = "N";
                            minAmount = 0.0;
                            people = 0;
                            saveAction(ticketCode, productname, price, group, minAmount, people, image,discount);
                        } else {
                            group = "Y";

                            if ((!isNumberPatternMatches(minAmt)) || (!isValidPrice(minAmt)) ) {
                                showMessage(this, "Please enter the minimum amount");
                                resetField(m_jTxtMinAmt);
                            }
                            //else if (!pNum.matcher(minAmt).matches()) {
                            else if (!isNumberPatternMatches(minAmt)) {
                                showMessage(this, "Please enter the valid minimum amount");
                                m_jTxtMinAmt.setText("");
                            } else {
                                
                                if (noofPeople.equals("")) {
                                    showMessage(this, "Please enter the no of people");
                                    resetField(m_jTxtNoofPeople);
                                    //mateen
                                } else if (!isNumberPatternMatches(noofPeople) || Integer.parseInt(noofPeople) <= 0) {
                                    showMessage(this, "Please enter the valid no of people");
                                    m_jTxtNoofPeople.setText("");
                                } else {
                                    people = Integer.parseInt(noofPeople);
                                    minAmount = Double.parseDouble(minAmt);
                                    saveAction(ticketCode, productname, price, group, minAmount, people,image,discount);
                                }
                            }

                        }
                    } else {
                        //showMessage(this, "Please enter valid mimimum amount");
                        showMessage(this, "Please enter valid price");
                        resetField(m_jPriceSell);
                    }
                   
                }
            }
        }
        //}
    }//GEN-LAST:event_m_jBtnSaveActionPerformed

      private boolean isNumberPatternMatches(String num){
        String regx = "^[+]?\\d+$";
        Pattern pattern = Pattern.compile(regx);
        Matcher matcher = pattern.matcher(num);
        return matcher.matches();
    }
    public void saveAction(String ticketCode, String productname, double price, String group, double minAmount, int people, BufferedImage image, String discount){
              int productCount = 0;
        int showCount = 0;

        try {
            productCount = m_dlSales.getCategoryNameCount(productname);

        } catch (BasicException ex) {
            Logger.getLogger(JPanelShow.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (updateMode == false) {
            if (productCount != 0) {
                showMessage(this, "Entered ticket category is already exist");
                //mateen
                resetField(m_jName);
            } else {
                //mateen
                if (isCodeAlreadyExists(ticketCode) && isNegativeChecked(price, minAmount, people)) {
                    try {
                        m_dlSystem.ticketCategoryInsert(UUID.randomUUID().toString(), ticketCode, productname, price, "000", "000", group, minAmount, people, image, discount);
              clearFieldsReloadJList();
                    } catch (BasicException ex) {
                        Logger.getLogger(JPanelShow.class.getName()).log(Level.SEVERE, null, ex);
                    }// TODO add your handling code


                    try {
                        m_dlSystem.peoplelogInsert(UUID.randomUUID().toString(), user, JRootApp.now(), "Ticket category", 0.0);
                        // TODO add your handling code here:;
                    } catch (BasicException ex) {
                        Logger.getLogger(JPanelShow.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
         } else {
            // mateen
            if (isNegativeChecked(price, minAmount, people)) {
                String id = list.get(m_jCategoryList.getSelectedIndex()).getId();
                try {
                    m_dlSales.updateTicketCategory(id, ticketCode, productname, price, group, minAmount, people, image, discount);
                    clearFieldsReloadJList();
                } catch (BasicException ex) {
                    Logger.getLogger(JPanelShow.class.getName()).log(Level.SEVERE, null, ex);
                }
                updateMode = false;
            }
        }
         try {
            setJListContents();
        } catch (BasicException ex) {
            Logger.getLogger(JPanelShow.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private void m_jbtnNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jbtnNewActionPerformed
         m_jCode.setText("");
         m_jName.setText("");
         m_jPriceSell.setText("");
         m_jTxtMinAmt.setText("");
         m_jTxtNoofPeople.setText("");
         m_jCboGroup.setSelected(false);
         m_jTxtMinAmt.setVisible(false);
         m_jTxtNoofPeople.setVisible(false);
         m_jLblMinAmt.setVisible(false);
         m_jImage.setImage(null);
         m_jLblNoofPeople.setVisible(false);
         m_jCategoryList.clearSelection();
         updateMode = false;
    }//GEN-LAST:event_m_jbtnNewActionPerformed

    private void m_jCboGroupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jCboGroupActionPerformed
    if(m_jCboGroup.isSelected()==true){
            m_jTxtNoofPeople.setVisible(true);
            m_jTxtMinAmt.setVisible(true);
            m_jLblMinAmt.setVisible(true);
            m_jLblNoofPeople.setVisible(true);
       }else{
            m_jTxtNoofPeople.setVisible(false);
            m_jTxtMinAmt.setVisible(false);
            m_jLblMinAmt.setVisible(false);
            m_jLblNoofPeople.setVisible(false);
       }        // TODO add your handling code here:
    }//GEN-LAST:event_m_jCboGroupActionPerformed

    private void m_jBtnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jBtnDeleteActionPerformed
        String id = list.get(m_jCategoryList.getSelectedIndex()).getId();
        try{
              m_dlSales.updateProducts(id);
              clearFieldsReloadJList();
        }catch(Exception ex){
        }
        updateMode = false;
        // TODO add your handling code here:
    }//GEN-LAST:event_m_jBtnDeleteActionPerformed

    private void showMessage(JPanelTicketCategory aThis, String msg) {
        JOptionPane.showMessageDialog(aThis, msg);
    }
   
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton m_jBtnDelete;
    private javax.swing.JButton m_jBtnSave;
    private javax.swing.JList m_jCategoryList;
    private javax.swing.JCheckBox m_jCboDiscount;
    private javax.swing.JCheckBox m_jCboGroup;
    private javax.swing.JTextField m_jCode;
    private com.openbravo.data.gui.JImageEditor m_jImage;
    private javax.swing.JLabel m_jLblDiscount;
    private javax.swing.JLabel m_jLblGroup;
    private javax.swing.JLabel m_jLblMinAmt;
    private javax.swing.JLabel m_jLblNoofPeople;
    private javax.swing.JTextField m_jName;
    private javax.swing.JTextField m_jPriceSell;
    private javax.swing.JTextField m_jTxtMinAmt;
    private javax.swing.JTextField m_jTxtNoofPeople;
    private javax.swing.JButton m_jbtnNew;
    private javax.swing.JLabel m_lblImage;
    // End of variables declaration//GEN-END:variables
    
}
