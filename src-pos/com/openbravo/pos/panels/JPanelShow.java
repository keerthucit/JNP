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
import com.openbravo.data.loader.Datas;
import com.openbravo.data.loader.SerializerWriteBasic;
import com.openbravo.data.loader.StaticSentence;
import com.openbravo.pos.forms.AppProperties;
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
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 *
 * @author adrianromero
 */
public class JPanelShow extends JPanel implements JPanelView,BeanFactoryApp{
    private AppView m_App;
    private AppProperties m_props;
    private DataLogicSystem m_dlSystem;
    private com.openbravo.pos.forms.holidayInfo m_polines;
    private TicketParser m_TTP;
   
    protected DataLogicSales m_dlSales;
    private String[] holiday;
    private java.util.List<holidayInfo> holidayList = null;
    private int holidaySize = 0;
    private static Pattern p = Pattern.compile("^[a-zA-Z0-9][a-zA-Z0-9 ]*$");
    private static Pattern pNum = Pattern.compile(".*[0-9].*");
    private String user;
    public DefaultListModel model = null;
    public java.util.List<showInfo> list = null;
    public boolean updateMode = false;
     private JRootApp m_RootApp;
      public java.util.List<showInfo> currentShow = null;
     String starttime;

    /** Creates new form JPanelCloseMoney */
    public JPanelShow() {
        
        initComponents();
         m_jTxtSpecialShowDate.setVisible(false);
        m_jbtnDate.setVisible(false);
        m_jLblDate.setVisible(false);
        m_jCboShowtype.setSelectedItem("Regular");
       
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
   starttime = m_jTxtStartTime.getText();
        try {
            clearTxtFields();
            // JOptionPane.showMessageDialog(this, "The user has to close all pending bills before doing the close shift", AppLocal.getIntString("message.header"), JOptionPane.INFORMATION_MESSAGE);
        } catch (BasicException ex) {
            Logger.getLogger(JPanelShow.class.getName()).log(Level.SEVERE, null, ex);
        }
       
    }

    public Object getBean() {
         return this;
    }

    public JComponent getComponent() {
        return this;
    }

    public String getTitle() {
        return AppLocal.getIntString("Menu.Show");
    }

    public void activate() throws BasicException {
        clearTxtFields();
    }

    public boolean deactivate() {
        // se me debe permitir cancelar el deactivate
        return true;
    }

public void clearTxtFields() throws BasicException {
         m_jtxtShowname.setText("");
         m_jTxtNoofSeats.setText("");
         m_jTxtEndTime.setText("");
         m_jTxtStartTime.setText("");
         m_jTxtSpecialShowDate.setText("");
         m_jTxtSpecialShowDate.setVisible(false);
         m_jbtnDate.setVisible(false);
         m_jLblDate.setVisible(false);
         m_jCboOpen.setVisible(true);
         m_jLblOpen.setVisible(true);
         m_jCboOpen.setSelected(true);
         m_jCboShowtype.setSelectedItem("Regular");
         m_jTxtSpecialShowDate.setText("");

        try {
            setJListContents();
        } catch (BasicException ex) {
            Logger.getLogger(JPanelShow.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
   
public void saveAction(String showName, String starttime, String endTime, int seats, String special, Date specialDate, String groupOpen, Date createdDate, String specialShowDate) throws BasicException {


        int showNameCount = 0;
        int showCount = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
        SimpleDateFormat dtformat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat dt = new SimpleDateFormat("HH:mm");
        String sysDate = null;
        String curSysDate = null;
        Date currentSysDate = null;
        curSysDate = format.format(new Date()).toString();
        try {
              currentSysDate = format.parse(curSysDate);
        } catch (ParseException ex) {
              Logger.getLogger(JPanelShow.class.getName()).log(Level.SEVERE, null, ex);
        }
        String currentDate = null;
        String sysTime = dt.format(new Date()).toString();
        showNameCount = m_dlSales.getShowNameCount(showName);

        if (m_jCboShowtype.getSelectedItem() == "Regular") {
            special = "Regular";
           
            sysDate = format.format(new Date()).toString();
            
            currentDate = format.format(new Date()).toString();
           
            try {
                showCount = m_dlSales.getRgularShowCount(starttime, endTime);
            } catch (BasicException ex) {
                Logger.getLogger(JPanelShow.class.getName()).log(Level.SEVERE, null, ex);
            }

        } else if (m_jCboShowtype.getSelectedItem() == "Special") {
            special = "Special";
          
            sysDate = dtformat.format(new Date()).toString();
            if(sysDate.equals(specialShowDate)){
                 sysDate = specialShowDate;
                try {
                showCount = m_dlSales.getShowCount(starttime, endTime, sysDate);
            } catch (BasicException ex) {
                Logger.getLogger(JPanelShow.class.getName()).log(Level.SEVERE, null, ex);
            }
            }else{
                 sysDate = specialShowDate;
                try {
                showCount = m_dlSales.getShowSpecialCount(starttime, endTime, sysDate);
            } catch (BasicException ex) {
                Logger.getLogger(JPanelShow.class.getName()).log(Level.SEVERE, null, ex);
            }
            }

        } else {
            special = "Group";
             sysDate = dtformat.format(new Date()).toString();
            if(sysDate.equals(specialShowDate)){
                 sysDate = specialShowDate;
                try {
                showCount = m_dlSales.getShowCount(starttime, endTime, sysDate);
            } catch (BasicException ex) {
                Logger.getLogger(JPanelShow.class.getName()).log(Level.SEVERE, null, ex);
            }
            }else{
                 sysDate = specialShowDate;
                try {
                showCount = m_dlSales.getShowSpecialCount(starttime, endTime, sysDate);
            } catch (BasicException ex) {
                Logger.getLogger(JPanelShow.class.getName()).log(Level.SEVERE, null, ex);
            }
            }
           
        }

        if (updateMode == false) {
            if (showNameCount != 0) {
                showMessage(this, "Entered show name is already exist");
                m_jtxtShowname.setText("");
            } else {
               if (showCount != 0) {
                    showMessage(this, "Show is already set for the particular time");
                    m_jTxtEndTime.setText("");
                    m_jTxtStartTime.setText("");
                } else {
                    try {
                        m_dlSystem.showInsert(UUID.randomUUID().toString(), showName, starttime, endTime, seats, special, specialDate, groupOpen, createdDate, user);

                    } catch (BasicException ex) {
                        Logger.getLogger(JPanelShow.class.getName()).log(Level.SEVERE, null, ex);
                    }// TODO add your handling code

                      SimpleDateFormat datetime=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                      Date currentShowTime = null;
                      Date newShowTime = null;
                      int receiptCount =0;

                    try {
                        currentShowTime = (Date) dt.parse(starttime);
                    } catch (ParseException ex) {
                        Logger.getLogger(JPanelShow.class.getName()).log(Level.SEVERE, null, ex);
                    }
                       String sysDateTime = datetime.format(new Date()).toString();
                       int showcloseCount =0;
                    try {
                        showcloseCount = m_dlSales.getClosecount(sysDateTime);
                    } catch (BasicException ex) {
                        Logger.getLogger(JPanelShow.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    if(showcloseCount == 1){
                        currentShow = m_dlSales.getClosedShowDetails();
                
                     String currentShowStartime = currentShow.get(0).getstartTime();
                    
                     if(special.equals("Regular")){
                    if(currentShowStartime.compareTo(starttime)>= 0 ){
                        try {
                            receiptCount = m_dlSales.getReceiptCount(m_App.getActiveShowIndex());
                        } catch (BasicException ex) {
                            Logger.getLogger(JPanelShow.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        if(receiptCount==0){
                            new StaticSentence(m_App.getSession()
                                    , "UPDATE CLOSEDSHOW SET SHOWNAME = ? WHERE HOST = ? AND MONEY = ?"
                                    , new SerializerWriteBasic(new Datas[] {Datas.STRING, Datas.STRING, Datas.STRING}))
                                    .exec(new Object[] {showName, m_App.getProperties().getHost(), m_App.getActiveShowIndex()});
                        }
                    }
                     }else{
                         Date newSpDate = null;
                         String spShowDt = format.format(specialDate);
                       
                         if(currentShowStartime.compareTo(starttime)>= 0 &&  spShowDt.equals(curSysDate)){
                        try {
                            receiptCount = m_dlSales.getReceiptCount(m_App.getActiveShowIndex());
                        } catch (BasicException ex) {
                            Logger.getLogger(JPanelShow.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        if(receiptCount==0){
                            new StaticSentence(m_App.getSession()
                                    , "UPDATE CLOSEDSHOW SET SHOWNAME = ? WHERE HOST = ? AND MONEY = ?"
                                    , new SerializerWriteBasic(new Datas[] {Datas.STRING, Datas.STRING, Datas.STRING}))
                                    .exec(new Object[] {showName, m_App.getProperties().getHost(), m_App.getActiveShowIndex()});
                        }
                        }
                       }
                    }else{
                            currentShow = m_dlSales.getLastClosedDetails();
                            if(currentShow.size()!=0){
                            String lastShowStartime = currentShow.get(0).getstartTime();
                            if(special.equals("Regular")){
                                if(starttime.compareTo(lastShowStartime)>= 0 ){
                                     m_App.setActiveShow(UUID.randomUUID().toString(), m_dlSystem.getSequenceShow(m_App.getProperties().getHost()) + 1, new Date(),null);
                                     m_dlSystem.execInsertShow(
                                     new Object[] {m_App.getActiveShowIndex(), m_App.getProperties().getHost(), m_App.getActiveShowSequence(), m_App.getActiveShowDateStart(), m_App.getActiveShowDateEnd(), showName,"Y"});
                                 }
                            }else{
                                    String spShowDt = format.format(specialDate);
                                    if(starttime.compareTo(lastShowStartime)>= 0 && spShowDt.equals(curSysDate)){
                                     m_App.setActiveShow(UUID.randomUUID().toString(), m_dlSystem.getSequenceShow(m_App.getProperties().getHost()) + 1, new Date(),null);
                                     m_dlSystem.execInsertShow(
                                     new Object[] {m_App.getActiveShowIndex(), m_App.getProperties().getHost(), m_App.getActiveShowSequence(), m_App.getActiveShowDateStart(), m_App.getActiveShowDateEnd(), showName,"Y"});
                                }
                            }
                            }
                    }
                    try {
                        m_dlSystem.peoplelogInsert(UUID.randomUUID().toString(), user, JRootApp.now(), "Show", 0.0);
                        // TODO add your handling code here:;
                    } catch (BasicException ex) {
                        Logger.getLogger(JPanelShow.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    try {
                        clearTxtFields();
                    } catch (BasicException ex) {
                        Logger.getLogger(JPanelShow.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }
            }
        } else {
            String id = list.get(m_jShowList.getSelectedIndex()).getId();

            try {
                m_dlSales.updateShows(id, showName, starttime, endTime, seats, special, specialDate, groupOpen, createdDate, user);
            } catch (BasicException ex) {
                Logger.getLogger(JPanelShow.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                clearTxtFields();
            } catch (BasicException ex) {
                Logger.getLogger(JPanelShow.class.getName()).log(Level.SEVERE, null, ex);
            }
            updateMode = false;


        }

      
}
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        m_jTxtSpecialShowDate = new javax.swing.JTextField();
        m_jLblDate = new javax.swing.JLabel();
        m_jbtnEndTime = new javax.swing.JButton();
        m_jtxtShowname = new javax.swing.JTextField();
        m_jlblShowName = new javax.swing.JLabel();
        m_jLblStartTime = new javax.swing.JLabel();
        m_jTxtStartTime = new javax.swing.JTextField();
        m_jLblEndTime = new javax.swing.JLabel();
        m_jTxtEndTime = new javax.swing.JTextField();
        m_jBtnStartTime = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        m_jTxtNoofSeats = new javax.swing.JTextField();
        m_jbtnDate = new javax.swing.JButton();
        m_jBtnSave = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        m_jShowList = new javax.swing.JList();
        m_jbtnNew = new javax.swing.JButton();
        m_jCboShowtype = new javax.swing.JComboBox();
        m_jLblShowType = new javax.swing.JLabel();
        m_jLblOpen = new javax.swing.JLabel();
        m_jCboOpen = new javax.swing.JCheckBox();
        m_jBtnDelete = new javax.swing.JButton();

        setPreferredSize(new java.awt.Dimension(766, 530));

        m_jTxtSpecialShowDate.setEditable(false);
        m_jTxtSpecialShowDate.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(javax.swing.UIManager.getDefaults().getColor("Button.darkShadow")), javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1)));

        m_jLblDate.setText("Date");

        m_jbtnEndTime.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/date.png"))); // NOI18N
        m_jbtnEndTime.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jbtnEndTimeActionPerformed(evt);
            }
        });

        m_jtxtShowname.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(javax.swing.UIManager.getDefaults().getColor("Button.darkShadow")), javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1)));

        m_jlblShowName.setText("Show Name");

        m_jLblStartTime.setText("Start Time");

        m_jTxtStartTime.setEditable(false);
        m_jTxtStartTime.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(javax.swing.UIManager.getDefaults().getColor("Button.darkShadow")), javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1)));

        m_jLblEndTime.setText("End Time");

        m_jTxtEndTime.setEditable(false);
        m_jTxtEndTime.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(javax.swing.UIManager.getDefaults().getColor("Button.darkShadow")), javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1)));

        m_jBtnStartTime.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/date.png"))); // NOI18N
        m_jBtnStartTime.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jBtnStartTimeActionPerformed(evt);
            }
        });

        jLabel2.setText("No of Seats");

        m_jTxtNoofSeats.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(javax.swing.UIManager.getDefaults().getColor("Button.darkShadow")), javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1)));

        m_jbtnDate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/date.png"))); // NOI18N
        m_jbtnDate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jbtnDateActionPerformed(evt);
            }
        });

        m_jBtnSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/filesave.png"))); // NOI18N
        m_jBtnSave.setPreferredSize(new java.awt.Dimension(32, 32));
        m_jBtnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jBtnSaveActionPerformed(evt);
            }
        });

        m_jShowList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                m_jShowListValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(m_jShowList);

        m_jbtnNew.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/editnew.png"))); // NOI18N
        m_jbtnNew.setPreferredSize(new java.awt.Dimension(32, 32));
        m_jbtnNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jbtnNewActionPerformed(evt);
            }
        });

        m_jCboShowtype.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Regular", "Special", "Group" }));
        m_jCboShowtype.setBorder(null);
        m_jCboShowtype.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jCboShowtypeActionPerformed(evt);
            }
        });

        m_jLblShowType.setText("Show Type");

        m_jLblOpen.setText("Open");

        m_jCboOpen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jCboOpenActionPerformed(evt);
            }
        });
        m_jCboOpen.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                m_jCboOpenFocusLost(evt);
            }
        });

        m_jBtnDelete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/editdelete.png"))); // NOI18N
        m_jBtnDelete.setMinimumSize(new java.awt.Dimension(22, 22));
        m_jBtnDelete.setPreferredSize(new java.awt.Dimension(32, 32));
        m_jBtnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jBtnDeleteActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(m_jlblShowName, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addComponent(m_jLblStartTime, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addComponent(m_jLblEndTime, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addComponent(m_jLblShowType, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(m_jLblDate, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(m_jLblOpen, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                            .addGap(134, 134, 134)
                            .addComponent(m_jbtnNew, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(m_jBtnSave, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(m_jtxtShowname, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(m_jTxtStartTime, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(m_jTxtEndTime, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(m_jTxtNoofSeats, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(m_jCboShowtype, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(m_jCboOpen, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(m_jTxtSpecialShowDate, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(m_jBtnStartTime, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(m_jbtnEndTime, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(m_jbtnDate, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(m_jBtnDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(96, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 500, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(layout.createSequentialGroup()
                .addGap(53, 53, 53)
                .addComponent(m_jlblShowName, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(17, 17, 17)
                .addComponent(m_jLblStartTime, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20)
                .addComponent(m_jLblEndTime, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20)
                .addComponent(m_jLblShowType, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(m_jLblOpen)
                .addGap(17, 17, 17)
                .addComponent(m_jLblDate, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(m_jBtnSave, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(m_jbtnNew, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(8, 8, 8)
                        .addComponent(m_jtxtShowname, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(13, 13, 13)
                        .addComponent(m_jTxtStartTime, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(13, 13, 13)
                        .addComponent(m_jTxtEndTime, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(13, 13, 13)
                        .addComponent(m_jTxtNoofSeats, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(13, 13, 13)
                        .addComponent(m_jCboShowtype, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(m_jCboOpen))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(m_jBtnDelete, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(48, 48, 48)
                        .addComponent(m_jBtnStartTime)
                        .addGap(15, 15, 15)
                        .addComponent(m_jbtnEndTime)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(m_jbtnDate)
                    .addComponent(m_jTxtSpecialShowDate, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void m_jbtnEndTimeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jbtnEndTimeActionPerformed

        Date endTime = null;
        SimpleDateFormat sdf=new SimpleDateFormat("HH:mm");

        endTime = JCalendarDialog.showCalendarTime(this, endTime);
        if (endTime != null) {
            m_jTxtEndTime.setText(sdf.format(endTime).toString());
        }
}//GEN-LAST:event_m_jbtnEndTimeActionPerformed

    private void m_jBtnStartTimeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jBtnStartTimeActionPerformed
        Date Starttime = null;
        SimpleDateFormat sdf=new SimpleDateFormat("HH:mm");

        Starttime = JCalendarDialog.showCalendarTime(this, Starttime);
        if (Starttime != null) {
            m_jTxtStartTime.setText(sdf.format(Starttime).toString());
        }
    }//GEN-LAST:event_m_jBtnStartTimeActionPerformed
 public void populateList(DataLogicSales m_dlSales) throws BasicException {

        model = new DefaultListModel();
        m_jShowList.setModel(model);
        list = m_dlSales.getShowList();
        String[] dListId = null;
        
        for (int i = 0; i < list.size(); i++) {
           
            String listid = list.get(i).getShowName();
            model.add(i, listid);
        }
    }
    private void m_jbtnDateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jbtnDateActionPerformed

        Date specialDate = null;
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
          SimpleDateFormat dt=new SimpleDateFormat("dd-MM-yyyy");
    
        specialDate = JCalendarDialog.showCalendarTime(this, specialDate);

        Date sysdate = new Date();
        String currentDate = sdf.format(sysdate);
        String selectDate = sdf.format(specialDate);
        if(specialDate.compareTo(sysdate)>= 0 || currentDate.equals(selectDate)){
            m_jTxtSpecialShowDate.setText(dt.format(specialDate).toString());
         
        }else{
              showMessage(this, "Please select the valid date");
              m_jTxtSpecialShowDate.setText("");
        }
              // TODO add your handling code here:
    }//GEN-LAST:event_m_jbtnDateActionPerformed
    private String getFormattedDate(String strDate) {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date d =null;
        try {
            d = (Date) format.parse(strDate);

        } catch (ParseException ex) {
        }
        return format.format(d);
    }
    private void m_jShowListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_m_jShowListValueChanged
        // TODO add your handling code here:
        
        DateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        DateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
        if (evt.getValueIsAdjusting()) {
            int openCount = 0;
            updateMode = true;
            String showName = null;
            String open = null;
            try {
                showName = m_jShowList.getSelectedValue().toString();
            } catch (Exception ex) {
               
            }
             if (m_jShowList.getSelectedIndex() > -1) {

                int index = m_jShowList.getSelectedIndex();
                m_jtxtShowname.setText(list.get(index).getShowName());
                m_jTxtStartTime.setText(list.get(index).getstartTime());
                m_jTxtEndTime.setText(list.get(index).getendTime());
                m_jTxtNoofSeats.setText(Integer.toString(list.get(index).getnoofseats()));

                open = list.get(index).getGroupOpen();
                if(open.equals("Y")){
                   m_jCboOpen.setSelected(true);
                   m_jLblOpen.setVisible(true);
                     m_jCboOpen.setVisible(true);
                }else{
                    m_jCboOpen.setSelected(false);
                    m_jLblOpen.setVisible(true);
                    m_jCboOpen.setVisible(true);
                }
                try {
                         openCount = m_dlSales.getopenShowCountValue(list.get(index).getShowName());
                    } catch (BasicException ex) {
                        Logger.getLogger(JPanelShow.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    int groupcount =0;
                    String sysDate = format1.format(new Date());
                     try {
                         groupcount = m_dlSales.getGroupShowCountValue(list.get(index).getShowName(),sysDate);
                    } catch (BasicException ex) {
                        Logger.getLogger(JPanelShow.class.getName()).log(Level.SEVERE, null, ex);
                    }
                if(list.get(index).getspecial().equals("Special")){
                   // m_jCboShowtype.setSelectedItem();
                    if(openCount!=0){
                         m_jBtnSave.setEnabled(false);
                         m_jBtnDelete.setEnabled(false);
                    }else if(groupcount!=0){
                           m_jBtnSave.setEnabled(false);
                         m_jBtnDelete.setEnabled(false);
                    }else{
                           m_jBtnSave.setEnabled(true);
                         m_jBtnDelete.setEnabled(true);
                    }

                    m_jCboShowtype.setSelectedItem("Special");
                    m_jTxtSpecialShowDate.setVisible(true);
                    m_jbtnDate.setVisible(true);
                    m_jLblDate.setVisible(true);
                   
                    m_jTxtSpecialShowDate.setText(format.format(list.get(index).getspecialShowDate()).toString());
                }else if(list.get(index).getspecial().equals("Group")){
                    

                    if(openCount!=0){
                         m_jBtnSave.setEnabled(false);
                         m_jBtnDelete.setEnabled(false);
                    }else if(groupcount!=0){
                           m_jBtnSave.setEnabled(false);
                         m_jBtnDelete.setEnabled(false);
                    }else{
                           m_jBtnSave.setEnabled(true);
                         m_jBtnDelete.setEnabled(true);
                    }

                    m_jCboShowtype.setSelectedItem("Group");
                    m_jTxtSpecialShowDate.setVisible(true);
                    m_jbtnDate.setVisible(true);
                    m_jLblDate.setVisible(true);
                   
                    m_jTxtSpecialShowDate.setText(format.format(list.get(index).getspecialShowDate()).toString());
                  
                }
                else{
                    
                      if(openCount!=0){
                         m_jBtnSave.setEnabled(false);
                         m_jBtnDelete.setEnabled(false);
                     }else{
                          m_jBtnSave.setEnabled(true);
                          m_jBtnDelete.setEnabled(true);
                     }
                    m_jCboShowtype.setSelectedItem("Regular");
                    m_jTxtSpecialShowDate.setVisible(false);
                    m_jbtnDate.setVisible(false);
                    m_jLblDate.setVisible(false);
                   
                }
            
            }

        }
    }//GEN-LAST:event_m_jShowListValueChanged
 private void setJListContents() throws BasicException {

        list = m_dlSales.getShowList();
        AbstractListModel model = new AbstractListModel() {

            public int getSize() {
                return list.size();
            }

            public Object getElementAt(int i) {
                return list.get(i).getShowName();
            }
        };
        m_jShowList.setModel(model);
    }
    private void m_jBtnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jBtnSaveActionPerformed
       
       SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
       SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
       SimpleDateFormat dsf=new SimpleDateFormat("dd-MM-yyyy");
       SimpleDateFormat dt=new SimpleDateFormat("HH:mm");
       String showName = m_jtxtShowname.getText();
       String starttime = m_jTxtStartTime.getText();
       String endTime = m_jTxtEndTime.getText();
       String noofSeats = m_jTxtNoofSeats.getText();
       String specialShowDate = m_jTxtSpecialShowDate.getText();
       

       String special = null;
       String groupOpen = null;
       if (m_jCboOpen.isSelected() == true) {
            groupOpen = "Y";
        } else {
            groupOpen = "N";
        }

       String sysDate = format.format(new Date()).toString();
       String Currentdate = sdf.format(new Date()).toString();
       String currentTime = dt.format(new Date()).toString();
       Date showStartime = null;
       Date showEndTime = null;

       Date createdDate = null;
       Date specialDate = null;
        try {
            createdDate = (Date) sdf.parse(Currentdate);
        } catch (ParseException ex) {
            Logger.getLogger(JPanelShow.class.getName()).log(Level.SEVERE, null, ex);
        }
        if(showName.equals("")){
             showMessage(this, "Please enter the show name");
             m_jtxtShowname.setText("");
        }else if(!p.matcher(showName).matches()){
             showMessage(this, "Please enter the valid show name");
             m_jtxtShowname.setText("");
        }else{
           if(starttime.equals("")){
               showMessage(this, "Please select the start time");
           }else{
                try {

                    showStartime = (Date) dt.parse(starttime);
                } catch (ParseException ex) {
                    Logger.getLogger(JPanelShow.class.getName()).log(Level.SEVERE, null, ex);
                }

                if(endTime.equals("")){
                   showMessage(this, "Please select the end time");
                }
                else{
                     //String sysEndTime = dt.format(endTime);
                    try {
                    showEndTime = (Date) dt.parse(endTime);
                    } catch (ParseException ex) {
                        Logger.getLogger(JPanelShow.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    if(showStartime.compareTo(showEndTime)>= 0){
                        showMessage(this, "End Time should be greater than start time");
                         m_jTxtEndTime.setText("");
                    }else{
                    if(noofSeats.equals("")){
                        showMessage(this, "Please enter the no of seats");
                        m_jTxtNoofSeats.setText("");
                    }else if (!isNumberPatternMatches(noofSeats) || Integer.parseInt(noofSeats) <= 0) {
                          showMessage(this, "Please enter the valid seats");
                          m_jTxtNoofSeats.setText("");
                    }else{
                          int seats = Integer.parseInt(noofSeats);
                          if(m_jCboShowtype.getSelectedItem()=="Regular"){
                            special = "Regular";
                            
                                try {
                                    saveAction(showName, starttime, endTime, seats, special, specialDate, groupOpen, createdDate,null);
                                } catch (BasicException ex) {
                                    Logger.getLogger(JPanelShow.class.getName()).log(Level.SEVERE, null, ex);
                                }
                          }else{
                              Date showSpecial = null;
                                try {
                                    showSpecial = dsf.parse(specialShowDate);
                                } catch (ParseException ex) {
                                    Logger.getLogger(JPanelShow.class.getName()).log(Level.SEVERE, null, ex);
                                }
                                specialShowDate = format.format(showSpecial);

                         
                               Date sysTime = null;
                                    try {
                                        sysTime = (Date) dt.parse(currentTime);
                                    } catch (ParseException ex) {
                                        Logger.getLogger(JPanelShow.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                if(specialShowDate.equals("")){
                                     showMessage(this, "Please select the show date");

                                }else if(sysTime.compareTo(showStartime)>= 0 && sysDate.equals(specialShowDate)){
                                    showMessage(this, "Startime should be greater than system time");
                                }
                                else{
                                     try {
                                          specialDate = (Date) format.parse(specialShowDate);
                                      } catch (ParseException ex) {
                                          Logger.getLogger(JPanelShow.class.getName()).log(Level.SEVERE, null, ex);
                                      }
                                       try {
                                        saveAction(showName, starttime, endTime, seats, special, specialDate, groupOpen, createdDate,specialShowDate);
                                } catch (BasicException ex) {
                                    Logger.getLogger(JPanelShow.class.getName()).log(Level.SEVERE, null, ex);
                                }
                                 
                                }
                                
                              }
                        }
                  }
             }
        }
    }//GEN-LAST:event_m_jBtnSaveActionPerformed
    }
 private boolean isNumberPatternMatches(String num){
        String regx = "^[+]?\\d+$";
        Pattern pattern = Pattern.compile(regx);
        Matcher matcher = pattern.matcher(num);
        return matcher.matches();
    }
    private void m_jbtnNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jbtnNewActionPerformed
        m_jtxtShowname.setText("");
        m_jTxtStartTime.setText(null);
        m_jTxtEndTime.setText("");
        m_jTxtNoofSeats.setText("");
//        m_jCboSpecial.setSelected(false);
        m_jTxtSpecialShowDate.setVisible(false);
        m_jbtnDate.setVisible(false);
        m_jLblDate.setVisible(false);
        m_jCboOpen.setVisible(true);
        m_jCboOpen.setSelected(true);
        m_jLblOpen.setVisible(true);
        m_jShowList.clearSelection();
        m_jBtnSave.setEnabled(true);
        m_jBtnDelete.setEnabled(true);
        updateMode = false;      
    }//GEN-LAST:event_m_jbtnNewActionPerformed

    private void m_jCboShowtypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jCboShowtypeActionPerformed
       if(m_jCboShowtype.getSelectedItem() == "Special" ){
           m_jTxtSpecialShowDate.setText("");
            m_jTxtSpecialShowDate.setVisible(true);
            m_jbtnDate.setVisible(true);
            m_jLblDate.setVisible(true);
          
       }else if(m_jCboShowtype.getSelectedItem() == "Group"){
           m_jTxtSpecialShowDate.setText("");
            m_jTxtSpecialShowDate.setVisible(true);
            m_jbtnDate.setVisible(true);
            m_jLblDate.setVisible(true);
         
       }else{
           m_jTxtSpecialShowDate.setVisible(false);
            m_jbtnDate.setVisible(false);
            m_jLblDate.setVisible(false);
  
       } // TODO add your handling code here:
    }//GEN-LAST:event_m_jCboShowtypeActionPerformed

    private void m_jCboOpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jCboOpenActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_m_jCboOpenActionPerformed

    private void m_jCboOpenFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_m_jCboOpenFocusLost
        // TODO add your handling code here:m_jTxtSpecialShowDate
    }//GEN-LAST:event_m_jCboOpenFocusLost

    private void m_jBtnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jBtnDeleteActionPerformed
        String id = list.get(m_jShowList.getSelectedIndex()).getId();
        try{
              m_dlSales.updateActiveShow(id);
               clearTxtFields();
        }catch(Exception ex){
        }
        updateMode = false;
// TODO add your handling code here:
    }//GEN-LAST:event_m_jBtnDeleteActionPerformed



    private void showMessage(JPanelShow aThis, String msg) {
        JOptionPane.showMessageDialog(aThis, msg);
    }
   
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton m_jBtnDelete;
    private javax.swing.JButton m_jBtnSave;
    private javax.swing.JButton m_jBtnStartTime;
    private javax.swing.JCheckBox m_jCboOpen;
    private javax.swing.JComboBox m_jCboShowtype;
    private javax.swing.JLabel m_jLblDate;
    private javax.swing.JLabel m_jLblEndTime;
    private javax.swing.JLabel m_jLblOpen;
    private javax.swing.JLabel m_jLblShowType;
    private javax.swing.JLabel m_jLblStartTime;
    private javax.swing.JList m_jShowList;
    private javax.swing.JTextField m_jTxtEndTime;
    private javax.swing.JTextField m_jTxtNoofSeats;
    private javax.swing.JTextField m_jTxtSpecialShowDate;
    private javax.swing.JTextField m_jTxtStartTime;
    private javax.swing.JButton m_jbtnDate;
    private javax.swing.JButton m_jbtnEndTime;
    private javax.swing.JButton m_jbtnNew;
    private javax.swing.JLabel m_jlblShowName;
    private javax.swing.JTextField m_jtxtShowname;
    // End of variables declaration//GEN-END:variables
    
}
