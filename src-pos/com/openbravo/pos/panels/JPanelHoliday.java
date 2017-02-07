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

import com.lowagie.text.pdf.codec.Base64.InputStream;
import com.openbravo.pos.forms.JPanelView;
import com.openbravo.pos.forms.AppView;
import com.openbravo.pos.forms.AppLocal;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import java.util.Date;
import java.util.UUID;
import com.openbravo.data.loader.StaticSentence;
import com.openbravo.data.loader.SerializerWriteBasic;
import com.openbravo.format.Formats;
import com.openbravo.basic.BasicException;
import com.openbravo.beans.DateUtils;
import com.openbravo.beans.JCalendarDialog;
import com.openbravo.data.loader.Datas;
import com.openbravo.data.gui.MessageInf;
import com.openbravo.pos.forms.BeanFactoryApp;
import com.openbravo.pos.forms.BeanFactoryException;
import com.openbravo.pos.forms.DataLogicSales;
import com.openbravo.pos.scripting.ScriptEngine;
import com.openbravo.pos.scripting.ScriptException;
import com.openbravo.pos.scripting.ScriptFactory;
import com.openbravo.pos.forms.DataLogicSystem;
import com.openbravo.pos.forms.JRootApp;
import com.openbravo.pos.forms.holidayInfo;
import com.openbravo.pos.printer.TicketParser;
import com.openbravo.pos.printer.TicketPrinterException;
import com.openbravo.pos.sales.shared.JTicketsBagShared;
import com.openbravo.pos.util.JRPrinterAWT300;
import com.openbravo.pos.util.ReportUtils;
import java.awt.Dimension;
import java.io.ObjectInputStream;
import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.regex.Pattern;
import javax.print.PrintService;
import javax.swing.table.DefaultTableModel;
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
public class JPanelHoliday extends JPanel implements JPanelView,BeanFactoryApp{
    private AppView m_App;
    private DataLogicSystem m_dlSystem;
    private com.openbravo.pos.forms.holidayInfo m_polines;
     private TicketParser m_TTP;
     protected DataLogicSystem dlSystem;
     protected DataLogicSales m_dlSales;
     private String[] holiday;
     private String[] weekholiday;
     private java.util.List<holidayInfo> holidayList = null;
      private java.util.List<holidayInfo> weekHolidayList = null;
     private int holidaySize = 0;
     private int weekholidaySize = 0;
     private static Pattern pNum = Pattern.compile(".*[0-9].*");
     private JRootApp m_RootApp;
     private String user;

    /** Creates new form JPanelCloseMoney */
    public JPanelHoliday() {
        
        initComponents();
       
    }
    
    public void init(AppView app) throws BeanFactoryException{
        
        m_App = app;        
        m_dlSystem = (DataLogicSystem) m_App.getBean("com.openbravo.pos.forms.DataLogicSystem");
        m_dlSales = (DataLogicSales) m_App.getBean("com.openbravo.pos.forms.DataLogicSales");
        user = m_App.getAppUserView().getUser().getId();
     //   m_dlPurchase = (DataLogicSystem) m_App.getBean("com.openbravo.pos.forms.DataLogicSystem");
        m_TTP = new TicketParser(m_App.getDeviceTicket(), m_dlSystem);
         initTableData();
         initTableWeekData();
         m_jtblHoliday.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
   // JOptionPane.showMessageDialog(this, "The user has to close all pending bills before doing the close shift", AppLocal.getIntString("message.header"), JOptionPane.INFORMATION_MESSAGE);
       
    }
   private void initTableData() {


        holiday = new String[]{
            "SL No.", "Holiday Date", "Description", "Delete"
        };
         holidayList = m_dlSales.getAllHoliday();
         holidaySize = holidayList.size();
         setHolidayTableModelAndHeader(m_jtblHoliday, holidaySize);
         setCellRenderer(m_jtblHoliday);
         setHolidayTableData(m_jtblHoliday);

    }
   private void initTableWeekData() {


        weekholiday = new String[]{
            "SL No.", "Holiday Date", "Delete"
        };
         weekHolidayList = m_dlSales.getAllWeekHoliday();
         weekholidaySize = weekHolidayList.size();
         setWeekHolidayTableModelAndHeader(m_jtblWeekHoliday, weekholidaySize);
         setCellRenderer(m_jtblWeekHoliday);
         setWeekHolidayTableData(m_jtblWeekHoliday);

    }
  private void setCellRenderer(JTable table) {
        table.getColumn("Delete").setCellRenderer(new CustomCheckBoxEditor(table));
    }
    private void setHolidayTableData(JTable table) {

        for (int col = 0; col < holidaySize; col++) {
          
            table.setValueAt(col+1, col, 0);
        }
        for (int col = 0; col < holidaySize; col++) {
            table.setValueAt(holidayList.get(col).getHolidayDate(), col, 1);
        }
        for (int col = 0; col < holidaySize; col++) {
            table.setValueAt(holidayList.get(col).getHolidayDesc(), col, 2);
        }

       
    }
    private void setWeekHolidayTableData(JTable table) {

        for (int col = 0; col < weekholidaySize; col++) {

            table.setValueAt(col+1, col, 0);
        }
        for (int col = 0; col < weekholidaySize; col++) {
            table.setValueAt(weekHolidayList.get(col).getHolidayDate(), col, 1);
        }
      


    }

 private void setHolidayTableModelAndHeader(JTable table, int size) {
        table.getTableHeader().setPreferredSize(new Dimension(30, 25));
        table.setModel(new DefaultTableModel(holiday, size));
    }
private void setWeekHolidayTableModelAndHeader(JTable table, int size) {
        table.getTableHeader().setPreferredSize(new Dimension(30, 25));
        table.setModel(new DefaultTableModel(weekholiday, size));
    }
    public Object getBean() {
         return this;
    }

    public JComponent getComponent() {
        return this;
    }

    public String getTitle() {
        return AppLocal.getIntString("Menu.Holiday");
    }

    public void activate() throws BasicException {
        
    }

    public boolean deactivate() {
        // se me debe permitir cancelar el deactivate
        return true;
    }


   

    private class FormatsPayment extends Formats {
        protected String formatValueInt(Object value) {
            return AppLocal.getIntString("transpayment." + (String) value);
        }   
        protected Object parseValueInt(String value) throws ParseException {
            return value;
        }
        public int getAlignment() {
            return javax.swing.SwingConstants.LEFT;
        }         
    }    
   
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        m_jLblDate = new javax.swing.JLabel();
        m_jTxtDate = new javax.swing.JTextField();
        m_jbtndate = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        m_jHolidayDesc = new javax.swing.JTextField();
        m_JbtnSave = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        m_jtblHoliday = new javax.swing.JTable();
        m_jBtnDelete = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jTxtYear = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        m_jCboDayNos = new javax.swing.JComboBox();
        m_jBtnWeekSave = new javax.swing.JButton();
        m_jCboweekDay = new javax.swing.JComboBox();
        jScrollPane2 = new javax.swing.JScrollPane();
        m_jtblWeekHoliday = new javax.swing.JTable();
        m_jBtnWeekDelete = new javax.swing.JButton();

        jPanel1.setLayout(null);

        m_jLblDate.setText("Holiday Date");
        jPanel1.add(m_jLblDate);
        m_jLblDate.setBounds(60, 30, 120, 20);

        m_jTxtDate.setEditable(false);
        m_jTxtDate.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(javax.swing.UIManager.getDefaults().getColor("Button.darkShadow")), javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1)));
        jPanel1.add(m_jTxtDate);
        m_jTxtDate.setBounds(230, 30, 200, 28);

        m_jbtndate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/date.png"))); // NOI18N
        m_jbtndate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jbtndateActionPerformed(evt);
            }
        });
        jPanel1.add(m_jbtndate);
        m_jbtndate.setBounds(440, 30, 40, 25);

        jLabel1.setText("Holiday Description");
        jPanel1.add(jLabel1);
        jLabel1.setBounds(60, 80, 160, 22);

        m_jHolidayDesc.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(javax.swing.UIManager.getDefaults().getColor("Button.darkShadow")), javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1)));
        jPanel1.add(m_jHolidayDesc);
        m_jHolidayDesc.setBounds(230, 80, 200, 27);

        m_JbtnSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/filesave.png"))); // NOI18N
        m_JbtnSave.setText("Save");
        m_JbtnSave.setPreferredSize(new java.awt.Dimension(110, 25));
        m_JbtnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_JbtnSaveActionPerformed(evt);
            }
        });
        jPanel1.add(m_JbtnSave);
        m_JbtnSave.setBounds(190, 130, 110, 25);

        m_jtblHoliday.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "SL No.", "Holiday Date", "Description", "Delete"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        m_jtblHoliday.setColumnSelectionAllowed(true);
        jScrollPane1.setViewportView(m_jtblHoliday);

        jPanel1.add(jScrollPane1);
        jScrollPane1.setBounds(60, 180, 625, 230);

        m_jBtnDelete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/editdelete.png"))); // NOI18N
        m_jBtnDelete.setText("Delete");
        m_jBtnDelete.setPreferredSize(new java.awt.Dimension(110, 25));
        m_jBtnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jBtnDeleteActionPerformed(evt);
            }
        });
        jPanel1.add(m_jBtnDelete);
        m_jBtnDelete.setBounds(310, 130, 110, 25);

        jTabbedPane1.addTab("General", jPanel1);

        jPanel2.setLayout(null);

        jLabel2.setText("Year");
        jPanel2.add(jLabel2);
        jLabel2.setBounds(60, 30, 100, 20);

        jTxtYear.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(javax.swing.UIManager.getDefaults().getColor("Button.darkShadow")), javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1)));
        jPanel2.add(jTxtYear);
        jTxtYear.setBounds(230, 30, 190, 30);

        jLabel3.setText("Day of Week");
        jPanel2.add(jLabel3);
        jLabel3.setBounds(60, 80, 100, 30);

        jLabel4.setText("Week of Month");
        jPanel2.add(jLabel4);
        jLabel4.setBounds(60, 130, 110, 30);

        m_jCboDayNos.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Select", "1", "2", "3", "4", "All" }));
        jPanel2.add(m_jCboDayNos);
        m_jCboDayNos.setBounds(230, 130, 190, 30);

        m_jBtnWeekSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/filesave.png"))); // NOI18N
        m_jBtnWeekSave.setText("Save");
        m_jBtnWeekSave.setPreferredSize(new java.awt.Dimension(110, 25));
        m_jBtnWeekSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jBtnWeekSaveActionPerformed(evt);
            }
        });
        jPanel2.add(m_jBtnWeekSave);
        m_jBtnWeekSave.setBounds(150, 190, 110, 25);

        m_jCboweekDay.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Select", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday" }));
        jPanel2.add(m_jCboweekDay);
        m_jCboweekDay.setBounds(230, 80, 190, 30);

        m_jtblWeekHoliday.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "SL No.", "Holiday Date", "Delete"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        m_jtblWeekHoliday.setColumnSelectionAllowed(true);
        jScrollPane2.setViewportView(m_jtblWeekHoliday);

        jPanel2.add(jScrollPane2);
        jScrollPane2.setBounds(60, 240, 625, 300);

        m_jBtnWeekDelete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/editdelete.png"))); // NOI18N
        m_jBtnWeekDelete.setText("Delete");
        m_jBtnWeekDelete.setPreferredSize(new java.awt.Dimension(110, 25));
        m_jBtnWeekDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jBtnWeekDeleteActionPerformed(evt);
            }
        });
        jPanel2.add(m_jBtnWeekDelete);
        m_jBtnWeekDelete.setBounds(270, 190, 110, 25);

        jTabbedPane1.addTab("Weekly", jPanel2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(45, 45, 45)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 762, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(88, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(47, 47, 47)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 587, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void m_jbtndateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jbtndateActionPerformed

        Date date = null;
        SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");

        date = JCalendarDialog.showCalendarTime(this, date);
        if (date != null) {
            m_jTxtDate.setText(sdf.format(date).toString());
        }
}//GEN-LAST:event_m_jbtndateActionPerformed

    private void m_JbtnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_JbtnSaveActionPerformed
       String holidayDate = m_jTxtDate.getText();
       String description = m_jHolidayDesc.getText();
       String[] year;
       int holidayYear;

       int count = 0;
       if(holidayDate.equals("")){
             showMessage(this, "Please select the holiday date");

        }else{
           if(description.equals("")){
               showMessage(this, "Please select the holiday description");
           }else{
             year = holidayDate.split("-", 3);
             holidayYear = Integer.parseInt(year[2]);
        try {
            count = m_dlSales.getDateCount(holidayDate);
        } catch (BasicException ex) {
            Logger.getLogger(JPanelHoliday.class.getName()).log(Level.SEVERE, null, ex);
        }
        if(count!=0){
              showMessage(this, "Already set the holiday for selected date");
              m_jTxtDate.setText("");
              m_jHolidayDesc.setText("");
        }else{
             try {
             m_dlSystem.holidayInsert(UUID.randomUUID().toString(), holidayDate,description, "Y",holidayYear,null);

            }catch (BasicException ex) {
             Logger.getLogger(JPanelHoliday.class.getName()).log(Level.SEVERE, null, ex);
            }// TODO add your handling code here:
             try {
                m_dlSystem.peoplelogInsert(UUID.randomUUID().toString(), user, m_RootApp.now(), "Holiday", 0.0);
                // TODO add your handling code here:;
            } catch (BasicException ex) {
                Logger.getLogger(JPanelShow.class.getName()).log(Level.SEVERE, null, ex);
            }
            initTableData();
            showMessage(this, "Holiday updated successfully");
            m_jTxtDate.setText("");
            m_jHolidayDesc.setText("");
        }
           }
        }
        
       
    }//GEN-LAST:event_m_JbtnSaveActionPerformed

    private void m_jBtnWeekSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jBtnWeekSaveActionPerformed
        String year = jTxtYear.getText();
        int weekofmonth=0;
        String holidayDay;
        String holidayWeek;
        String id = UUID.randomUUID().toString();

        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        System.out.println("enrt current"+m_jCboweekDay.getSelectedIndex());

        if(jTxtYear.equals("")){
             showMessage(this, "Please enter the year");
             jTxtYear.setText("");
           }
        else if(!pNum.matcher(year).matches()){
             showMessage(this, "Please enter the valid year");
             jTxtYear.setText("");
        }
        else{
            int yearInt = Integer.parseInt(jTxtYear.getText());
            if((yearInt<currentYear)){
             showMessage(this, "System should not allow to enter the previous year holiday");
             jTxtYear.setText("");
            }
            else{
                if(m_jCboweekDay.getSelectedIndex()==0){
                     showMessage(this, "Please Select the day");
                }
                else{
                    holidayDay = (String) m_jCboweekDay.getSelectedItem();
                    if(m_jCboDayNos.getSelectedIndex()==0){
                         showMessage(this, "Please select the nos");
                    }
                    else{
                         holidayWeek = (String) m_jCboDayNos.getSelectedItem();
                        int weekcount = 0;
                         try {
                            weekcount = m_dlSales.getweekHolidayCount(holidayDay, holidayWeek, yearInt);
                        } catch (BasicException ex) {
                            Logger.getLogger(JPanelHoliday.class.getName()).log(Level.SEVERE, null, ex);
                        }

                        if(weekcount!=0){
                                showMessage(this, "Weekly Holiday is already set for the selected data");
                                jTxtYear.setText("");
                                m_jCboweekDay.setSelectedIndex(0);
                                m_jCboDayNos.setSelectedIndex(0);
                        }else{


                        Calendar cal = new GregorianCalendar();
                        DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
                        int day  = m_jCboweekDay.getSelectedIndex();
                         weekofmonth = m_jCboDayNos.getSelectedIndex();
                        if(m_jCboDayNos.getSelectedItem().toString().equals("All")){
                           
                            cal.set(Calendar.MONTH, Calendar.JANUARY);
                            cal.add(Calendar.DAY_OF_YEAR, -7);
                            cal.set(Calendar.DAY_OF_WEEK, day);
                             cal.set(Calendar.YEAR,yearInt);
                            int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
                            try {
                                m_dlSystem.holidayInterInsert(id, holidayWeek, holidayDay, yearInt);
                            } catch (BasicException ex) {
                                Logger.getLogger(JPanelHoliday.class.getName()).log(Level.SEVERE, null, ex);
                            }

                            for (int i = 0; i < 52; i++) {
                                if (dayOfWeek == day) {
                                    String weeklydate = df.format(cal.getTime());
                                        try {
                                            m_dlSystem.holidayInsert(UUID.randomUUID().toString(), weeklydate, "Weekly Holiday", "N",yearInt,id);
                                        } catch (BasicException ex) {
                                            Logger.getLogger(JPanelHoliday.class.getName()).log(Level.SEVERE, null, ex);
                                        }

                                    System.out.println(df.format(cal.getTime()));
                                    cal.add(Calendar.DAY_OF_YEAR, +7);
                                }
                             }
                            initTableWeekData();
                        }else{
                            int i=0;
                            if(weekofmonth == 1){
                                i=1;
                            }else if(weekofmonth == 2){
                                i=8;
                            }else if(weekofmonth == 3){
                                i=15;
                            }else if(weekofmonth == 4){
                                i=22;
                            }
                          
                             for (int month = 0; month < 12; month++) {
                                  cal.set(Calendar.MONTH, month);
                                  cal.set(Calendar.DAY_OF_WEEK, day);
                                  cal.set(Calendar.WEEK_OF_MONTH,weekofmonth);
                                  cal.set(Calendar.YEAR,yearInt);
                                  int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
                                  if( weekofmonth!= 1){
                                      if(dayOfMonth >= i) {
                                        cal.set(Calendar.WEEK_OF_MONTH,weekofmonth);
                                      }else {
                                         cal.set(Calendar.WEEK_OF_MONTH,(weekofmonth+1));
                                      }
                                  }else{

                                      if(dayOfMonth >7) {
                                            cal.set(Calendar.MONTH, month);
                                            cal.set(Calendar.DAY_OF_WEEK, day);
                                            cal.set(Calendar.WEEK_OF_MONTH,(weekofmonth+1));
                                            cal.set(Calendar.YEAR,yearInt);
                                      }else {
                                            cal.set(Calendar.WEEK_OF_MONTH,(weekofmonth));
                                      }
                                  }
                                  String weeklydate = df.format(cal.getTime());
                                  int count = 0;
                               
                               
                                  try {

                                     m_dlSystem.holidayInsert(UUID.randomUUID().toString(), weeklydate, "Weekly Holiday", "N",yearInt,id);
                                    }catch (BasicException ex) {
                                     Logger.getLogger(JPanelHoliday.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                    jTxtYear.setText("");
                                    m_jCboweekDay.setSelectedIndex(0);
                                    m_jCboDayNos.setSelectedIndex(0);
                                   
                             }

                            try {
                                m_dlSystem.holidayInterInsert(id, holidayWeek, holidayDay, yearInt);
                            } catch (BasicException ex) {
                                Logger.getLogger(JPanelHoliday.class.getName()).log(Level.SEVERE, null, ex);
                            }
                             
                             initTableWeekData();
                         }
                        try {
                        m_dlSystem.peoplelogInsert(UUID.randomUUID().toString(), user, m_RootApp.now(), "Week Holiday", 0.0);
                    // TODO add your handling code here:;
                        } catch (BasicException ex) {
                            Logger.getLogger(JPanelShow.class.getName()).log(Level.SEVERE, null, ex);
                        }
                         showMessage(this, "Weekly Holiday updated successfully");
                         jTxtYear.setText("");
                         m_jCboweekDay.setSelectedIndex(0);
                         m_jCboDayNos.setSelectedIndex(0);
                     }
                }
            }
        }
        
        }
    }//GEN-LAST:event_m_jBtnWeekSaveActionPerformed



    private void showMessage(JPanelHoliday aThis, String msg) {
        JOptionPane.showMessageDialog(aThis, msg);
    }
    private void m_jBtnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jBtnDeleteActionPerformed
       deleteHoliday(); // TODO add your handling code here:
    }//GEN-LAST:event_m_jBtnDeleteActionPerformed

    private void m_jBtnWeekDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jBtnWeekDeleteActionPerformed
        deleteWeekHoliday();// TODO add your handling code here:
    }//GEN-LAST:event_m_jBtnWeekDeleteActionPerformed
 private void printReport(String resourcefile, holidayInfo holiday) {


        holidayList = m_dlSales.getAllHoliday();

        java.util.List<holidayInfo> holidayInfo =  new java.util.ArrayList<holidayInfo>();

        holidayInfo = holidayList;
    
        try {

            JasperReport jr;

            InputStream in = (InputStream) getClass().getResourceAsStream(resourcefile + ".ser");
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
         //    reportparams.put("ARG", params);
            try {
                reportparams.put("REPORT_RESOURCE_BUNDLE", ResourceBundle.getBundle(resourcefile + ".properties"));
            } catch (MissingResourceException e) {
            }
          //  reportparams.put("document", document);

            Map reportfields = new HashMap();
            for(int i=0;i<holidayInfo.size();i++){
            System.out.println("holidayInfo.get(0).getLines().size();"+holidayInfo.get(i).printHolidayDate());
             reportfields.put("holidayData", holidayInfo.get(i));
               reportfields.put("holiday", holiday);
            }
          


            JasperPrint jp = JasperFillManager.fillReport(jr, reportparams, new JRMapArrayDataSource(new Object[] { reportfields } ));

            PrintService service = ReportUtils.getPrintService(m_App.getProperties().getProperty("machine.printername"));

            JRPrinterAWT300.printPages(jp, 0, jp.getPages().size() - 1, service);

        } catch (Exception e) {
            MessageInf msg = new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.cannotloadreport"), e);
            msg.show(this);
        }
    }
    private void deleteWeekHoliday() {
        boolean exe = false;
          DateFormat format = new SimpleDateFormat("dd-MM-yyyy");
          String sysDate = format.format(new Date());
        int result = 0;
        int[] rowsCIn = m_jtblWeekHoliday.getSelectedRows();
        //updateCInAttndListByDate();
        for (int i = 0; i < rowsCIn.length; i++) {
            int row = rowsCIn[i];
            String id = weekHolidayList.get(row).getId();
            String holidayDate = weekHolidayList.get(row).getHolidayDate();

                 m_dlSales.deleteHoliday(id);

        }

       // showMsg("Attendance Approved");
        resetWeekTable();
    }
    private void deleteHoliday() {
        boolean exe = false;
          DateFormat format = new SimpleDateFormat("dd-MM-yyyy");
          String sysDate = format.format(new Date());
          Date currentDate= null;
          Date holidaySetDate = null;
        try {
            currentDate = format.parse(sysDate);
        } catch (ParseException ex) {
            Logger.getLogger(JPanelHoliday.class.getName()).log(Level.SEVERE, null, ex);
        }


        int result = 0;
        int[] rowsCIn = m_jtblHoliday.getSelectedRows();
        //updateCInAttndListByDate();
        for (int i = 0; i < rowsCIn.length; i++) {
            int row = rowsCIn[i];
            String id = holidayList.get(row).getId();
            String holidayDate = holidayList.get(row).getHolidayDate();
            try {
                holidaySetDate = format.parse(holidayDate);
            } catch (ParseException ex) {
                Logger.getLogger(JPanelHoliday.class.getName()).log(Level.SEVERE, null, ex);
            }
          
            if((holidaySetDate.compareTo(currentDate)>=0)){
                 m_dlSales.deleteHoliday(id);              
            }else{
                 showMessage(this, "System should not allow to delete the previous holiday");
                
            }
            
            exe = true;
        }

       // showMsg("Attendance Approved");
        resetTable();
    }
        private void resetTable() {
              holidayList = m_dlSales.getAllHoliday();
              holidaySize = holidayList.size();
              setHolidayTableModelAndHeader(m_jtblHoliday, holidaySize);
              setCellRenderer(m_jtblHoliday);
              setHolidayTableData(m_jtblHoliday);

    }
         private void resetWeekTable() {
             weekHolidayList = m_dlSales.getAllWeekHoliday();
             weekholidaySize = weekHolidayList.size();
             setWeekHolidayTableModelAndHeader(m_jtblWeekHoliday, weekholidaySize);
             setCellRenderer(m_jtblWeekHoliday);
             setWeekHolidayTableData(m_jtblWeekHoliday);

    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextField jTxtYear;
    private javax.swing.JButton m_JbtnSave;
    private javax.swing.JButton m_jBtnDelete;
    private javax.swing.JButton m_jBtnWeekDelete;
    private javax.swing.JButton m_jBtnWeekSave;
    private javax.swing.JComboBox m_jCboDayNos;
    private javax.swing.JComboBox m_jCboweekDay;
    private javax.swing.JTextField m_jHolidayDesc;
    private javax.swing.JLabel m_jLblDate;
    private javax.swing.JTextField m_jTxtDate;
    private javax.swing.JButton m_jbtndate;
    private javax.swing.JTable m_jtblHoliday;
    private javax.swing.JTable m_jtblWeekHoliday;
    // End of variables declaration//GEN-END:variables
    
}
