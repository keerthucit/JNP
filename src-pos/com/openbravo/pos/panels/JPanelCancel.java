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
import com.openbravo.pos.forms.cancellationInfo;
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
public class JPanelCancel extends JPanel implements JPanelView,BeanFactoryApp{
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
    public java.util.List<cancellationInfo> cancellation;
    /** Creates new form JPanelCloseMoney */
    public JPanelCancel() {
      
        initComponents();
         
       
    }
    
    public void init(AppView app) throws BeanFactoryException{
        
        m_App = app;        
        m_dlSystem = (DataLogicSystem) m_App.getBean("com.openbravo.pos.forms.DataLogicSystem");
        m_dlSales = (DataLogicSales) m_App.getBean("com.openbravo.pos.forms.DataLogicSales");
        user = m_App.getAppUserView().getUser().getId();
      
     //   m_dlPurchase = (DataLogicSystem) m_App.getBean("com.openbravo.pos.forms.DataLogicSystem");
        m_TTP = new TicketParser(m_App.getDeviceTicket(), m_dlSystem);
      
    }

  



    public Object getBean() {
         return this;
    }

    public JComponent getComponent() {
        return this;
    }

    public String getTitle() {
        return AppLocal.getIntString("Menu.CancelFee");
    }

    public void activate() throws BasicException {
        java.util.List<cancellationInfo> cancellationInfo =  new java.util.ArrayList<cancellationInfo>();
        cancellation = m_dlSales.getCancellationFee();
        cancellationInfo = cancellation;
        if(cancellation.size()!=0){
            double cancellationFee = cancellationInfo.get(0).getcancelFee();
            int cancelFee = (int)cancellationFee;
            int timelimit = cancellationInfo.get(0).getTimeLimit();
            m_jTxtCancelFee.setText(Integer.toString(cancelFee));
            m_jTxtTimeLmt.setText(Integer.toString(timelimit));
        }
    }

    public boolean deactivate() {
        // se me debe permitir cancelar el deactivate
        return true;
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
        m_jTxtTimeLmt = new javax.swing.JTextField();
        m_jBtnSave = new javax.swing.JButton();
        m_jTxtCancelFee = new javax.swing.JTextField();

        m_jlblShowName.setText("Cancellation Fee");

        jLabel2.setText("Time Limit(In mins)");

        m_jTxtTimeLmt.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(javax.swing.UIManager.getDefaults().getColor("Button.darkShadow")), javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1)));

        m_jBtnSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/filesave.png"))); // NOI18N
        m_jBtnSave.setText("Save");
        m_jBtnSave.setPreferredSize(null);
        m_jBtnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jBtnSaveActionPerformed(evt);
            }
        });

        m_jTxtCancelFee.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(javax.swing.UIManager.getDefaults().getColor("Button.darkShadow")), javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1)));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(43, 43, 43)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(m_jlblShowName, javax.swing.GroupLayout.DEFAULT_SIZE, 124, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(m_jBtnSave, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(m_jTxtCancelFee, javax.swing.GroupLayout.PREFERRED_SIZE, 207, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(m_jTxtTimeLmt, javax.swing.GroupLayout.PREFERRED_SIZE, 207, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(435, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(42, 42, 42)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(m_jTxtCancelFee, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(m_jlblShowName, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(m_jTxtTimeLmt, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(33, 33, 33)
                .addComponent(m_jBtnSave, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(358, Short.MAX_VALUE))
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
       String cancelFee = m_jTxtCancelFee.getText();
       String timeLimit = m_jTxtTimeLmt.getText();
       Pattern pNum = Pattern.compile(".*[0-9].*");

           if(!cancelFee.equals("") && (!pNum.matcher(cancelFee).matches() || Integer.parseInt(cancelFee)<0)){
              showMessage(this, "Please enter the valid cancellation fee");
              m_jTxtCancelFee.setText("");
       }else{

               if(!timeLimit.equals("") && (!pNum.matcher(timeLimit).matches() || Integer.parseInt(timeLimit)<0)){
                showMessage(this, "Please enter the valid time limit");
                m_jTxtTimeLmt.setText("");
           }else{
                int period=0;
                 double fee =0;
                if(cancelFee.equals("") && !timeLimit.equals("")){
                    fee=0;
                    period = Integer.parseInt(timeLimit);
                }else if(timeLimit.equals("") && !cancelFee.equals("")){
                    period=0;
                    fee = Double.parseDouble(cancelFee);
                }else if(cancelFee.equals("") && timeLimit.equals("")){
                      fee=0;
                      period=0;
                }
                else{
                     period = Integer.parseInt(timeLimit);
                     fee = Double.parseDouble(cancelFee);
                }
                 String createdby = user;
                 Date currentDate = null;
                 SimpleDateFormat datetime=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                 String sysDateTime = datetime.format(new Date()).toString();
                 String active = "Y";
                 try {
                    currentDate = datetime.parse(sysDateTime);
                 } catch (ParseException ex) {
                    Logger.getLogger(JPanelCancel.class.getName()).log(Level.SEVERE, null, ex);
                 }
  
                try {
                     m_dlSales.updateCancellationFee();
                     m_dlSystem.cancelFeeInsert(UUID.randomUUID().toString(), fee, period, currentDate, user, active);
                     showMessage(this, "Cancellation setup updated successfully");
                     m_jTxtCancelFee.setText("");
                     m_jTxtTimeLmt.setText("");
                     activate();

                    try {

                        m_dlSystem.peoplelogInsert(UUID.randomUUID().toString(), user, sysDateTime, "Cancellation Setup", 0.0);

                    } catch (BasicException ex) {
                        Logger.getLogger(JPanelCancel.class.getName()).log(Level.SEVERE, null, ex);
                    }

                } catch (BasicException ex) {
                    Logger.getLogger(JPanelCancel.class.getName()).log(Level.SEVERE, null, ex);
                }
                 
           }
       }
    }//GEN-LAST:event_m_jBtnSaveActionPerformed

    private void showMessage(JPanelCancel aThis, String msg) {
        JOptionPane.showMessageDialog(aThis, msg);
    }
   
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel2;
    private javax.swing.JButton m_jBtnSave;
    private javax.swing.JTextField m_jTxtCancelFee;
    private javax.swing.JTextField m_jTxtTimeLmt;
    private javax.swing.JLabel m_jlblShowName;
    // End of variables declaration//GEN-END:variables
    
}