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
import java.text.ParseException;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.UUID;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;


/**
 *
 * @author adrianromero
 */
public class JPanelSeats extends JPanel implements JPanelView,BeanFactoryApp{
    private AppView m_App;
    private DataLogicSystem m_dlSystem;
    private com.openbravo.pos.forms.holidayInfo m_polines;
    private TicketParser m_TTP;
   
    protected DataLogicSales m_dlSales;
    private static Pattern p = Pattern.compile("^[A-Za-z0-9]+$");
    private static Pattern pNum = Pattern.compile(".*[0-9].*");
    private String user;
    public DefaultListModel model = null;
    public java.util.List<showInfo> list = null;
    public boolean updateMode = false;
     public java.util.List<showInfo> showDetails = null;
      private JRootApp m_RootApp;

    /** Creates new form JPanelCloseMoney */
    public JPanelSeats() {
        
        initComponents();
         
       
    }
    
    public void init(AppView app) throws BeanFactoryException{
        
        m_App = app;        
        m_dlSystem = (DataLogicSystem) m_App.getBean("com.openbravo.pos.forms.DataLogicSystem");
        m_dlSales = (DataLogicSales) m_App.getBean("com.openbravo.pos.forms.DataLogicSales");
        user = m_App.getAppUserView().getUser().getId();
      
     //   m_dlPurchase = (DataLogicSystem) m_App.getBean("com.openbravo.pos.forms.DataLogicSystem");
        m_TTP = new TicketParser(m_App.getDeviceTicket(), m_dlSystem);
  
   // JOptionPane.showMessageDialog(this, "The user has to close all pending bills before doing the close shift", AppLocal.getIntString("message.header"), JOptionPane.INFORMATION_MESSAGE);
         populateShow();

    }

  



    public Object getBean() {
         return this;
    }

    public JComponent getComponent() {
        return this;
    }

    public String getTitle() {
        return AppLocal.getIntString("Menu.Extended");
    }

    public void activate() throws BasicException {
       populateShow();
    }

    public boolean deactivate() {
        // se me debe permitir cancelar el deactivate
        return true;
    }
    private void populateShow() {
        DateFormat dt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date sysDate = new Date();
        String createDate = dt.format(sysDate);
        String showName = null;
        try {
            showName = m_dlSales.getShowName(createDate);
        } catch (BasicException ex) {
            Logger.getLogger(JPanelSeats.class.getName()).log(Level.SEVERE, null, ex);
        }
        m_jShowname.setText(showName);
    //    Vector<String> shows = getShowNames();
//        m_jCboShowlist.setModel(new DefaultComboBoxModel(shows));
    }
      private Vector<String> getShowNames() {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
        Date sysDate = new Date();
        String createDate = format.format(sysDate);
        Vector<String> showList = new Vector<String>();
        try {
            showDetails = m_dlSales.getextShowList(createDate);
        } catch (BasicException ex) {
            Logger.getLogger(JPanelSeats.class.getName()).log(Level.SEVERE, null, ex);
        }
        showList.add(0, "");
        for (int i = 0; i < showDetails.size(); i++) {
            showList.add(i + 1, showDetails.get(i).getShowName());
        }
        return showList;
    }
public void clearTxtFields() throws BasicException {
       
         m_jTxtNoofSeats.setText("");

      
    }
   

    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        m_jlblShowName = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        m_jTxtNoofSeats = new javax.swing.JTextField();
        m_jBtnSave = new javax.swing.JButton();
        m_jShowname = new javax.swing.JTextField();

        m_jlblShowName.setText("Show Name");

        jLabel2.setText("No of Seats");

        m_jTxtNoofSeats.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(javax.swing.UIManager.getDefaults().getColor("Button.darkShadow")), javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1)));

        m_jBtnSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/filesave.png"))); // NOI18N
        m_jBtnSave.setText("Save");
        m_jBtnSave.setPreferredSize(null);
        m_jBtnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jBtnSaveActionPerformed(evt);
            }
        });

        m_jShowname.setBackground(new java.awt.Color(255, 255, 255));
        m_jShowname.setEditable(false);
        m_jShowname.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(javax.swing.UIManager.getDefaults().getColor("Button.darkShadow")), javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1)));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(43, 43, 43)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(m_jlblShowName, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(4, 4, 4)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(m_jShowname, javax.swing.GroupLayout.DEFAULT_SIZE, 207, Short.MAX_VALUE)
                    .addComponent(m_jBtnSave, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(m_jTxtNoofSeats, javax.swing.GroupLayout.DEFAULT_SIZE, 207, Short.MAX_VALUE))
                .addGap(435, 435, 435))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(44, 44, 44)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(m_jlblShowName, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(m_jShowname, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(m_jTxtNoofSeats, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(34, 34, 34)
                .addComponent(m_jBtnSave, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(358, 358, 358))
        );
    }// </editor-fold>//GEN-END:initComponents
 
 private Date getFormattedDate(String strDate) {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date d = new Date();
        try {
            d = (Date) format.parse(strDate);

        } catch (ParseException ex) {
        }
        return d;
    } 
    private void m_jBtnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jBtnSaveActionPerformed
      // String showName = m_jCboShowlist.getSelectedItem().toString();
       String showName = m_jShowname.getText();
       String noofSeats = m_jTxtNoofSeats.getText();
      
       String special = "N";
       DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
       DateFormat dfst = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
       DateFormat dfet = new SimpleDateFormat("yyyy-MM-dd 23:59:59");
       DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
       Date createdDate = new Date();
       String currentDate = format.format(createdDate);
       String dateStart = dfst.format(createdDate);
       Date convertToDate = null;
       String sysDate = df.format(createdDate);
       try {
            convertToDate = df.parse(sysDate);

       } catch (ParseException ex) {
            Logger.getLogger(JPanelSeats.class.getName()).log(Level.SEVERE, null, ex);
       }
       if(showName.equals(null)){
             showMessage(this, "Please select the show name");
           
       }else{
              if(noofSeats.equals("")){
                        showMessage(this, "Please enter the no of seats");
                        m_jTxtNoofSeats.setText("");
                    }else if(!pNum.matcher(noofSeats).matches() || Integer.parseInt(noofSeats)<=0){
                          showMessage(this, "Please enter the valid seats");
                          m_jTxtNoofSeats.setText("");
                    }
                    else{
                          int seats = Integer.parseInt(noofSeats);

                          int showNameCount = 0;
                          int showCount = 0;
                          int extSeatscount = 0;
                          int extSeats = 0;
                          int availableSeats  = 0;

                          String currentStartDateTime = dfst.format(createdDate);
                          String currentEndDateTime = dfet.format(createdDate);
                          try {
                             
                               showNameCount = m_dlSales.getShowextCount(showName, currentDate, dateStart);
                          } catch (BasicException ex) {
                             Logger.getLogger(JPanelShow.class.getName()).log(Level.SEVERE, null, ex);
                          }
                          if(showNameCount!=0){
                             showMessage(this, "Seat is already extended for a particular show");
                            try {
                                clearTxtFields();
                            } catch (BasicException ex) {
                                Logger.getLogger(JPanelSeats.class.getName()).log(Level.SEVERE, null, ex);
                            }
                          }else{
                              try {
                                    m_dlSystem.showExtInsert(UUID.randomUUID().toString(), showName, createdDate, seats, user);
                                    showMessage(this, "Seats extended successfully");
                              } catch (BasicException ex) {
                                    Logger.getLogger(JPanelSeats.class.getName()).log(Level.SEVERE, null, ex);
                              }
                              try {
                                    extSeatscount = m_dlSales.getExtSeatscount(showName, currentStartDateTime, currentEndDateTime);
                              } catch (BasicException ex) {
                                    Logger.getLogger(JPanelSeats.class.getName()).log(Level.SEVERE, null, ex);
                              }
                              if(extSeatscount!=0){
                                  try {
                                    extSeats = m_dlSales.getExtSeats(showName, currentStartDateTime, currentEndDateTime);
                                  } catch (BasicException ex) {
                                    Logger.getLogger(JPanelSeats.class.getName()).log(Level.SEVERE, null, ex);
                                  }
                              }
                              try {
                                    availableSeats = m_dlSales.getAvailableSeats(showName, convertToDate);
                              } catch (BasicException ex) {
                                    Logger.getLogger(JPanelSeats.class.getName()).log(Level.SEVERE, null, ex);
                              }
                              availableSeats = availableSeats+extSeats;
                               try {
                                    m_dlSales.updateAvailableSeats(convertToDate,showName,availableSeats);
                              } catch (BasicException ex) {
                                    Logger.getLogger(JPanelSeats.class.getName()).log(Level.SEVERE, null, ex);
                              }
                               try {
                                    m_dlSystem.peoplelogInsert(UUID.randomUUID().toString(), user, m_RootApp.now(), "Extended Seats", 0.0);
                                } catch (BasicException ex) {
                                    Logger.getLogger(JPanelSeats.class.getName()).log(Level.SEVERE, null, ex);
                                }

                               try {
                                    clearTxtFields();
                                } catch (BasicException ex) {
                                    Logger.getLogger(JPanelShow.class.getName()).log(Level.SEVERE, null, ex);
                                }

                          }
                     }
               }
    }//GEN-LAST:event_m_jBtnSaveActionPerformed



    private void showMessage(JPanelSeats aThis, String msg) {
        JOptionPane.showMessageDialog(aThis, msg);
    }
   
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel2;
    private javax.swing.JButton m_jBtnSave;
    private javax.swing.JTextField m_jShowname;
    private javax.swing.JTextField m_jTxtNoofSeats;
    private javax.swing.JLabel m_jlblShowName;
    // End of variables declaration//GEN-END:variables
    
}
