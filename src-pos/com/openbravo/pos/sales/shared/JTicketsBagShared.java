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

package com.openbravo.pos.sales.shared;

import com.openbravo.pos.ticket.TicketInfo;
import java.awt.Component;
import java.util.*;
import javax.swing.*;

import com.openbravo.basic.BasicException;
import com.openbravo.data.gui.MessageInf;
import com.openbravo.data.loader.Datas;
import com.openbravo.data.loader.SerializerWriteBasic;
import com.openbravo.data.loader.StaticSentence;
import com.openbravo.pos.customers.CustomerInfoExt;
import com.openbravo.pos.sales.*;
import com.openbravo.pos.forms.*; 
import com.openbravo.pos.panels.PaymentsModel;
import com.openbravo.pos.panels.PaymentsShowModel;
import com.openbravo.pos.panels.showInfo;
import com.openbravo.pos.printer.TicketParser;
import com.openbravo.pos.printer.TicketPrinterException;
import com.openbravo.pos.scripting.ScriptEngine;
import com.openbravo.pos.scripting.ScriptException;
import com.openbravo.pos.scripting.ScriptFactory;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JTicketsBagShared extends JTicketsBag { 
    
    private String m_sCurrentTicket = null;
    private DataLogicReceipts dlReceipts = null;
    private String loggedUserId = null;
    private PaymentsShowModel m_PaymentsToClose;
    private PaymentsModel m_PaymentsToCloseCash;
    protected DataLogicSystem dlSystem;
    private final TicketParser m_TTP;
 //   private PaymentDayModel m_PaymentsToCloseDay;
    public static final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";
    protected DataLogicSales dlSales;
    String nextShowName = null;
    /** Creates new form JTicketsBagShared */
    public JTicketsBagShared(AppView app, TicketsEditor panelticket) {
        
        super(app, panelticket);
         dlSystem = (DataLogicSystem) app.getBean("com.openbravo.pos.forms.DataLogicSystem");
        dlReceipts = (DataLogicReceipts) app.getBean("com.openbravo.pos.sales.DataLogicReceipts");
        dlSales = (DataLogicSales) m_App.getBean("com.openbravo.pos.forms.DataLogicSales");
         m_TTP = new TicketParser(m_App.getDeviceTicket(), dlSystem);
        initComponents();
    }

    public void activate() {
        
        // precondicion es que no tenemos ticket activado ni ticket en el panel
        
        m_sCurrentTicket = null;
        selectValidTicket();     
        
        // Authorization
        m_jDelTicket.setEnabled(m_App.getAppUserView().getUser().hasPermission("com.openbravo.pos.sales.JPanelTicketEdits"));
       
        // postcondicion es que tenemos ticket activado aqui y ticket en el panel
    }
    
    public boolean deactivate() {
        
        // precondicion es que tenemos ticket activado aqui y ticket en el panel 
   
        saveCurrentTicket();
        
        m_sCurrentTicket = null;
        m_panelticket.setActiveTicket(null, null);       
        
        return true;
        
        // postcondicion es que no tenemos ticket activado ni ticket en el panel
    }
        
    public void deleteTicket() {          
        m_sCurrentTicket = null;
        selectValidTicket();      
    }
    
    protected JComponent getBagComponent() {
        return this;
    }
    
    protected JComponent getNullComponent() {
        return new JPanel();
    }
   
    private void saveCurrentTicket() {
        
        // save current ticket, if exists,
        if (m_sCurrentTicket != null) {
            try {
                dlReceipts.insertSharedTicket(m_sCurrentTicket, m_panelticket.getActiveTicket());
            } catch (BasicException e) {
                new MessageInf(e).show(this);
            }  
        }    
    }
    
    private void setActiveTicket(String id) throws BasicException{
          
        // BEGIN TRANSACTION
        TicketInfo ticket = dlReceipts.getSharedTicket(id);
        if (ticket == null)  {
            // Does not exists ???
            throw new BasicException(AppLocal.getIntString("message.noticket"));
        } else {
            dlReceipts.deleteSharedTicket(id);
            m_sCurrentTicket = id;
            m_panelticket.setActiveTicket(ticket, null);
            ticket.setShowName(nextShowName);
        } 
        // END TRANSACTION                 
    }
    
    private void selectValidTicket() {
        
        try {
            List<SeatInfo> l = dlReceipts.getSeatList("");
            if (l.size() == 0) {
                newTicket();
            } else {
                setActiveTicket(l.get(0).getId());
            }
        } catch (BasicException e) {
            new MessageInf(e).show(this);
            newTicket();
        }    
    }    
    
    private void newTicket() {      
        
        saveCurrentTicket();

        TicketInfo ticket = new TicketInfo();    
        m_sCurrentTicket = UUID.randomUUID().toString(); // m_fmtid.format(ticket.getId());
        m_panelticket.setActiveTicket(ticket, null);      
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        m_jNewTicket = new javax.swing.JButton();
        m_jDelTicket = new javax.swing.JButton();

        setMinimumSize(new java.awt.Dimension(167, 46));
        setPreferredSize(new java.awt.Dimension(167, 46));
        setLayout(new java.awt.BorderLayout());

        jPanel1.setMinimumSize(new java.awt.Dimension(275, 46));
        jPanel1.setPreferredSize(new java.awt.Dimension(150, 46));

        m_jNewTicket.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/editnew.png"))); // NOI18N
        m_jNewTicket.setToolTipText("New Book");
        m_jNewTicket.setFocusPainted(false);
        m_jNewTicket.setFocusable(false);
        m_jNewTicket.setMargin(new java.awt.Insets(8, 14, 8, 14));
        m_jNewTicket.setRequestFocusEnabled(false);
        m_jNewTicket.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jNewTicketActionPerformed(evt);
            }
        });

        m_jDelTicket.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/editdelete.png"))); // NOI18N
        m_jDelTicket.setToolTipText("Cancel");
        m_jDelTicket.setFocusPainted(false);
        m_jDelTicket.setFocusable(false);
        m_jDelTicket.setMargin(new java.awt.Insets(8, 14, 8, 14));
        m_jDelTicket.setRequestFocusEnabled(false);
        m_jDelTicket.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jDelTicketActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(58, 58, 58)
                .add(m_jNewTicket)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(m_jDelTicket)
                .add(264, 264, 264))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(m_jDelTicket)
                    .add(m_jNewTicket))
                .addContainerGap(15, Short.MAX_VALUE))
        );

        add(jPanel1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void m_jDelTicketActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jDelTicketActionPerformed
        
        int res = JOptionPane.showConfirmDialog(this, AppLocal.getIntString("message.wannadelete"), AppLocal.getIntString("title.editor"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (res == JOptionPane.YES_OPTION) {
            deleteTicket();
        }
        
    }//GEN-LAST:event_m_jDelTicketActionPerformed

    private void m_jNewTicketActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jNewTicketActionPerformed

        newTicket();
        
    }//GEN-LAST:event_m_jNewTicketActionPerformed
    public String getShowName() {
        return nextShowName;
    }

    public void setShowName(String nextShowName) {
        this.nextShowName = nextShowName;
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
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JButton m_jDelTicket;
    private javax.swing.JButton m_jNewTicket;
    // End of variables declaration//GEN-END:variables
    
}
