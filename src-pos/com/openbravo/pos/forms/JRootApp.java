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

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.event.*;
import java.lang.reflect.Constructor;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import javax.swing.*;

import com.openbravo.pos.printer.*;

import com.openbravo.beans.*;

import com.openbravo.basic.BasicException;
import com.openbravo.data.gui.MessageInf;
import com.openbravo.data.gui.JMessageDialog;
import com.openbravo.data.loader.BatchSentence;
import com.openbravo.data.loader.BatchSentenceResource;
import com.openbravo.data.loader.Datas;
import com.openbravo.data.loader.SerializerWriteBasic;
import com.openbravo.data.loader.Session;
import com.openbravo.data.loader.StaticSentence;
import com.openbravo.pos.panels.showInfo;
import com.openbravo.pos.scale.DeviceScale;
import com.openbravo.pos.scanpal2.DeviceScanner;
import com.openbravo.pos.scanpal2.DeviceScannerFactory;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;

/**
 *
 * @author adrianromero
 */
public class JRootApp extends JPanel implements AppView {
 
    private AppProperties m_props;
    private Session session;     
    private DataLogicSystem m_dlSystem;
    private DataLogicSales dlSales;
    private Properties m_propsdb = null;
    private String m_sActiveCashIndex;
    private int m_iActiveCashSequence;
    private Date m_dActiveCashDateStart;
    private Date m_dActiveCashDateEnd;
    private String m_sActiveShowIndex;
    private String m_sActiveShowName;
    private int m_iActiveShowSequence = 0;
    private Date m_dActiveShowDateStart;
    private Date m_dActiveShowDateEnd;
    public static final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";
    private String m_sInventoryLocation;
    
    private StringBuffer inputtext;
   
    private DeviceScale m_Scale;
    private DeviceScanner m_Scanner;
    private DeviceTicket m_TP;   
    private TicketParser m_TTP;
    
    private Map<String, BeanFactory> m_aBeanFactories;
    
    private JPrincipalApp m_principalapp = null;
    
    private static HashMap<String, String> m_oldclasses; // This is for backwards compatibility purposes
    
    static {        
        initOldClasses();
    }
    
    /** Creates new form JRootApp */
    public JRootApp() {    

        m_aBeanFactories = new HashMap<String, BeanFactory>();
        
        // Inicializo los componentes visuales
        initComponents ();            
//        jScrollPane1.getVerticalScrollBar().setPreferredSize(new Dimension(35, 35));
    }
    
    public boolean initApp(AppProperties props) {
        
        m_props = props;
        //setPreferredSize(new java.awt.Dimension(800, 600));

        // support for different component orientation languages.
        applyComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
        
        // Database start
        try {
            session = AppViewConnection.createSession(m_props);
        } catch (BasicException e) {
            JMessageDialog.showMessage(this, new MessageInf(MessageInf.SGN_DANGER, e.getMessage(), e));
            return false;
        }

        m_dlSystem = (DataLogicSystem) getBean("com.openbravo.pos.forms.DataLogicSystem");
        dlSales = (DataLogicSales) getBean("com.openbravo.pos.forms.DataLogicSales");
        
        // Create or upgrade the database if database version is not the expected
        String sDBVersion = readDataBaseVersion();        
        if (!AppLocal.APP_VERSION.equals(sDBVersion)) {
            
            // Create or upgrade database
            
            String sScript = sDBVersion == null 
                    ? m_dlSystem.getInitScript() + "-create.sql"
                    : m_dlSystem.getInitScript() + "-upgrade-" + sDBVersion + ".sql";

            if (JRootApp.class.getResource(sScript) == null) {
                JMessageDialog.showMessage(this, new MessageInf(MessageInf.SGN_DANGER, sDBVersion == null
                            ? AppLocal.getIntString("message.databasenotsupported", session.DB.getName()) // Create script does not exists. Database not supported
                            : AppLocal.getIntString("message.noupdatescript"))); // Upgrade script does not exist.
                session.close();
                return false;
            } else {
                // Create or upgrade script exists.
                if (JOptionPane.showConfirmDialog(this
                        , AppLocal.getIntString(sDBVersion == null ? "message.createdatabase" : "message.updatedatabase")
                        , AppLocal.getIntString("message.title")
                        , JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {  

                    try {
                        BatchSentence bsentence = new BatchSentenceResource(session, sScript);
                        bsentence.putParameter("APP_ID", Matcher.quoteReplacement(AppLocal.APP_ID));
                        bsentence.putParameter("APP_NAME", Matcher.quoteReplacement(AppLocal.APP_NAME));
                        bsentence.putParameter("APP_VERSION", Matcher.quoteReplacement(AppLocal.APP_VERSION));

                        java.util.List l = bsentence.list();
                        if (l.size() > 0) {
                            JMessageDialog.showMessage(this, new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("Database.ScriptWarning"), l.toArray(new Throwable[l.size()])));
                        }
                   } catch (BasicException e) {
                        JMessageDialog.showMessage(this, new MessageInf(MessageInf.SGN_DANGER, AppLocal.getIntString("Database.ScriptError"), e));
                        session.close();
                        return false;
                    }     
                } else {
                    session.close();
                    return false;
                }
            }   
        }
        
        // Cargamos las propiedades de base de datos
        m_propsdb = m_dlSystem.getResourceAsProperties(m_props.getHost() + "/properties");

         try {
             m_sActiveShowIndex = m_propsdb.getProperty("activeshow");
            
            Object[] valcash = m_sActiveShowIndex == null
                    ? null
                    : m_dlSystem.findActiveShow(m_sActiveShowIndex);
            SimpleDateFormat cdf=new SimpleDateFormat("yyyy-MM-dd");
            String closeEndDate = null;
            String SysDate = null;
            
            java.util.List<showInfo> currentShow = null;
            SimpleDateFormat sdf=new SimpleDateFormat("HH:mm");
            String CurrentTime = sdf.format(new Date()).toString();
            int closeShowCount = 0;
            SimpleDateFormat datetime=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat dateValue=new SimpleDateFormat("yyyy-MM-dd 00:00:00");
            SimpleDateFormat monthYear=new SimpleDateFormat("yyyy-MM");
            SimpleDateFormat month=new SimpleDateFormat("MM");
            SimpleDateFormat year=new SimpleDateFormat("yyyy");

            String sysDateTime = datetime.format(new Date()).toString();
            String startDate = dateValue.format(new Date()).toString();
            String currentMonthyear = monthYear.format(new Date()).toString();
            String CurrentMonth = month.format(new Date()).toString();
            String currentYear =  year.format(new Date()).toString();
            Date createdDate = new Date();
            SysDate = cdf.format(createdDate);
            boolean verified = false;
            String currentShowName = null;
            SimpleDateFormat dt=new SimpleDateFormat("yyyy-MM-dd 00:00:00");
            String specialShowDate = dt.format(new Date()).toString();
            currentShow = dlSales.getCurrentShow(specialShowDate);

            int dateCount = dlSales.getDateEnd();
            if (valcash == null || !m_props.getHost().equals(valcash[0]) || dateCount==0) {
                 String active = "N";
               if(CurrentMonth.equals("04")){
                    if(currentShow.size() !=0){
                        currentShowName = currentShow.get(0).getShowName();
                    }
                        int resetCount = dlSales.getResetCount(currentYear+"-04-01");

                        if(resetCount == 0) {
                            new StaticSentence(getSession()
                                        , "UPDATE CLOSEDSHOW SET DATEEND = ? WHERE DATEEND IS NULL"
                                        , new SerializerWriteBasic(new Datas[] {Datas.TIMESTAMP}))
                                        .exec(new Object[] {new Date()});
                            setActiveShow(UUID.randomUUID().toString(), 1, new Date(),null);
                            new StaticSentence(getSession()
                                        , "UPDATE CLOSEDSHOW SET ACTIVE = ?"
                                        , new SerializerWriteBasic(new Datas[] {Datas.STRING}))
                                        .exec(new Object[] {active});
                             }else {
                                setActiveShow(UUID.randomUUID().toString(), m_dlSystem.getSequenceShow(m_props.getHost()) + 1, new Date(),null);
                                
                            }
                                 m_dlSystem.execInsertShow(
                                        new Object[] {getActiveShowIndex(), m_props.getHost(), getActiveShowSequence(), getActiveShowDateStart(), getActiveShowDateEnd(), currentShowName,"Y"});
                              
                            }else {
                                if(currentShow.size() !=0){
                                currentShowName = currentShow.get(0).getShowName();
                                int showCount = 0;
                                showCount = dlSales.getClosedShowCount(currentShowName, startDate, sysDateTime);
                                if(showCount==0){
                                    String endTime = currentShow.get(0).getendTime();

                                    setActiveShow(UUID.randomUUID().toString(), m_dlSystem.getSequenceShow(m_props.getHost()) + 1, new Date(),null);

                                    m_dlSystem.execInsertShow(
                                        new Object[] {getActiveShowIndex(), m_props.getHost(), getActiveShowSequence(), getActiveShowDateStart(), getActiveShowDateEnd(), currentShowName,"Y"});
                                }
                            }
//                                setActiveShow(m_sActiveShowIndex, (Integer) valcash[1], (Date) valcash[2], (Date) valcash[3]);
                           // setActiveShow(UUID.randomUUID().toString(), m_dlSystem.getSequenceShow(m_props.getHost()) + 1, new Date(),null);
                            }
               
                // verified = true;
            }else{

                setActiveShow(m_sActiveShowIndex, (Integer) valcash[1], (Date) valcash[2], (Date) valcash[3]);

            }
           /* if(verified == false){
                 String active = "N";
                 if(currentShow.size() !=0){
                    currentShowName = currentShow.get(0).getShowName();
                 }               
                 
            }*/
        } catch (BasicException e) {
            // Casco. Sin caja no hay pos
            MessageInf msg = new MessageInf(MessageInf.SGN_NOTICE, AppLocal.getIntString("message.cannotclosecash"), e);
            msg.show(this);
            session.close();
            return false;
        }

        // creamos la caja activa si esta no existe      
        try {
            String sActiveCashIndex = m_propsdb.getProperty("activecash");
            Object[] valcash = sActiveCashIndex == null
                    ? null
                    : m_dlSystem.findActiveCash(sActiveCashIndex);
            if (valcash == null || !m_props.getHost().equals(valcash[0])) {
                // no la encuentro o no es de mi host por tanto creo una...
                setActiveCash(UUID.randomUUID().toString(), m_dlSystem.getSequenceCash(m_props.getHost()) + 1, new Date(), null);
            int closeCashCount =0;
            closeCashCount = dlSales.getClosedCashCount();
            if(closeCashCount==0){
                            // creamos la caja activa
                m_dlSystem.execInsertCash(
                        new Object[] {getActiveCashIndex(), m_props.getHost(), getActiveCashSequence(), getActiveCashDateStart(), getActiveCashDateEnd()});

}
            } else {
                setActiveCash(sActiveCashIndex, (Integer) valcash[1], (Date) valcash[2], (Date) valcash[3]);
            }
        } catch (BasicException e) {
            // Casco. Sin caja no hay pos
            MessageInf msg = new MessageInf(MessageInf.SGN_NOTICE, AppLocal.getIntString("message.cannotclosecash"), e);
            msg.show(this);
            session.close();
            return false;
        }  
        
        // Leo la localizacion de la caja (Almacen).
        m_sInventoryLocation = m_propsdb.getProperty("location");
        if (m_sInventoryLocation == null) {
            m_sInventoryLocation = "0";
            m_propsdb.setProperty("location", m_sInventoryLocation);
            m_dlSystem.setResourceAsProperties(m_props.getHost() + "/properties", m_propsdb);
        }
        
        // Inicializo la impresora...
        m_TP = new DeviceTicket(this, m_props);
        
        // Inicializamos 
        m_TTP = new TicketParser(getDeviceTicket(), m_dlSystem);
        printerStart();
        
        // Inicializamos la bascula
        m_Scale = new DeviceScale(this, m_props);
        
        // Inicializamos la scanpal
        m_Scanner = DeviceScannerFactory.createInstance(m_props);
            
        // Leemos los recursos basicos
        BufferedImage imgicon = m_dlSystem.getResourceAsImage("Window.Logo");
      // m_jLblTitle.setIcon(imgicon == null ? null : new ImageIcon(imgicon));
//        m_jLblTitle.setText(m_dlSystem.getResourceAsText("Window.Title"));
        
        String sWareHouse;
        try {
            sWareHouse = m_dlSystem.findLocationName(m_sInventoryLocation);
        } catch (BasicException e) {
            sWareHouse = null; // no he encontrado el almacen principal
        }        
        
        // Show Hostname, Warehouse and URL in taskbar
        String url;
        try {
            url = session.getURL();
        } catch (SQLException e) {
            url = "";
        }        
       // m_jHost.setText("<html>" + m_props.getHost() + " - " + sWareHouse + "<br>" + url);
        
        showLogin();

        return true;
    }
    public String getActiveShowIndex() {
        return m_sActiveShowIndex;
    }
    public int getActiveShowSequence() {
        return m_iActiveShowSequence;
    }
    public Date getActiveShowDateStart() {
        return m_dActiveShowDateStart;
    }
    public Date getActiveShowDateEnd(){
        return m_dActiveShowDateEnd;
    }
    public int getCloseShowsequence()
        {

            String sActiveShowIndex = m_propsdb.getProperty("activeshow");
                Object[] valcash = null;
            try {
                valcash = sActiveShowIndex == null ? null : m_dlSystem.findActiveShow(sActiveShowIndex);
            } catch (BasicException ex) {
                Logger.getLogger(JRootApp.class.getName()).log(Level.SEVERE, null, ex);
            }
               if(valcash!=null)
               {
               int acthostsequence=((Integer)valcash[1]).intValue();
               Date actualdate=(Date) valcash[2];
               int day = actualdate.getDay();
               int now = (new Date()).getDay();

               if(day==now)
               return acthostsequence+1;
               else
               return (new Integer(1)).intValue();
               }
               else

               {
                    return (new Integer(1)).intValue();
               }
      }

     public void setActiveShow(String sIndex, int iSeq, Date dStart, Date dEnd) {
         
        m_sActiveShowIndex = sIndex;
        m_iActiveShowSequence = iSeq;
        m_dActiveShowDateStart = dStart;
        m_dActiveShowDateEnd = dEnd;

        m_propsdb.setProperty("activeshow", m_sActiveShowIndex);
        m_dlSystem.setResourceAsProperties(m_props.getHost() + "/properties", m_propsdb);
    }

    private String readDataBaseVersion() {
        try {
            return m_dlSystem.findVersion();
        } catch (BasicException ed) {
            return null;
        }
    }
    
    public void tryToClose() {   
        
        if (closeAppView()) {

            // success. continue with the shut down

            // apago el visor
            m_TP.getDeviceDisplay().clearVisor();
            // me desconecto de la base de datos.
            session.close();

            // Download Root form
            SwingUtilities.getWindowAncestor(this).dispose();
        }
    }
    
    // Interfaz de aplicacion
    public DeviceTicket getDeviceTicket(){
        return m_TP;
    }
    
    public DeviceScale getDeviceScale() {
        return m_Scale;
    }
    public DeviceScanner getDeviceScanner() {
        return m_Scanner;
    }
    
    public Session getSession() {
        return session;
    } 

    public String getInventoryLocation() {
        return m_sInventoryLocation;
    }   
    public String getActiveCashIndex() {
        return m_sActiveCashIndex;
    }
    public int getActiveCashSequence() {
        return m_iActiveCashSequence;
    }
    public Date getActiveCashDateStart() {
        return m_dActiveCashDateStart;
    }
    public Date getActiveCashDateEnd(){
        return m_dActiveCashDateEnd;
    }
    public void setActiveCash(String sIndex, int iSeq, Date dStart, Date dEnd) {
        m_sActiveCashIndex = sIndex;
        m_iActiveCashSequence = iSeq;
        m_dActiveCashDateStart = dStart;
        m_dActiveCashDateEnd = dEnd;
        
        m_propsdb.setProperty("activecash", m_sActiveCashIndex);
        m_dlSystem.setResourceAsProperties(m_props.getHost() + "/properties", m_propsdb);
    }   
       
    public AppProperties getProperties() {
        return m_props;
    }
    
    public Object getBean(String beanfactory) throws BeanFactoryException {
        
        // For backwards compatibility
        beanfactory = mapNewClass(beanfactory);
        
        
        BeanFactory bf = m_aBeanFactories.get(beanfactory);
        if (bf == null) {   
            
            // testing sripts
            if (beanfactory.startsWith("/")) {
                bf = new BeanFactoryScript(beanfactory);               
            } else {
                // Class BeanFactory
                try {
                    Class bfclass = Class.forName(beanfactory);

                    if (BeanFactory.class.isAssignableFrom(bfclass)) {
                        bf = (BeanFactory) bfclass.newInstance();             
                    } else {
                        // the old construction for beans...
                        Constructor constMyView = bfclass.getConstructor(new Class[] {AppView.class});
                        Object bean = constMyView.newInstance(new Object[] {this});

                        bf = new BeanFactoryObj(bean);
                    }

                } catch (Exception e) {
                    // ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException
                    throw new BeanFactoryException(e);
                }
            }
            
            // cache the factory
            m_aBeanFactories.put(beanfactory, bf);         
            
            // Initialize if it is a BeanFactoryApp
            if (bf instanceof BeanFactoryApp) {
                ((BeanFactoryApp) bf).init(this);
            }
        }
        return bf.getBean();
    }
    
    private static String mapNewClass(String classname) {
        String newclass = m_oldclasses.get(classname);
        return newclass == null 
                ? classname 
                : newclass;
    }
    
    private static void initOldClasses() {
        m_oldclasses = new HashMap<String, String>();
        
        // update bean names from 2.00 to 2.20    
        m_oldclasses.put("com.openbravo.pos.reports.JReportCustomers", "/com/openbravo/reports/customers.bs");
        m_oldclasses.put("com.openbravo.pos.reports.JReportCustomersB", "/com/openbravo/reports/customersb.bs");
        m_oldclasses.put("com.openbravo.pos.reports.JReportClosedPos", "/com/openbravo/reports/closedpos.bs");
        m_oldclasses.put("com.openbravo.pos.reports.JReportClosedProducts", "/com/openbravo/reports/closedproducts.bs");
        m_oldclasses.put("com.openbravo.pos.reports.JChartSales", "/com/openbravo/reports/chartsales.bs");
        m_oldclasses.put("com.openbravo.pos.reports.JReportInventory", "/com/openbravo/reports/inventory.bs");
        m_oldclasses.put("com.openbravo.pos.reports.JReportInventory2", "/com/openbravo/reports/inventoryb.bs");
        m_oldclasses.put("com.openbravo.pos.reports.JReportInventoryBroken", "/com/openbravo/reports/inventorybroken.bs");
        m_oldclasses.put("com.openbravo.pos.reports.JReportInventoryDiff", "/com/openbravo/reports/inventorydiff.bs");
        m_oldclasses.put("com.openbravo.pos.reports.JReportPeople", "/com/openbravo/reports/people.bs");
        m_oldclasses.put("com.openbravo.pos.reports.JReportTaxes", "/com/openbravo/reports/taxes.bs");
        m_oldclasses.put("com.openbravo.pos.reports.JReportUserSales", "/com/openbravo/reports/usersales.bs");
        m_oldclasses.put("com.openbravo.pos.reports.JReportProducts", "/com/openbravo/reports/products.bs");
        m_oldclasses.put("com.openbravo.pos.reports.JReportCatalog", "/com/openbravo/reports/productscatalog.bs");
        
        // update bean names from 2.10 to 2.20
        m_oldclasses.put("com.openbravo.pos.panels.JPanelTax", "com.openbravo.pos.inventory.TaxPanel");
       
    }
    
    public void waitCursorBegin() {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    }
    
    public void waitCursorEnd(){
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }
    
    public AppUserView getAppUserView() {
        return m_principalapp;
    }

    
    private void printerStart() {
        
        String sresource = m_dlSystem.getResourceAsXML("Printer.Start");
        if (sresource == null) {
            m_TP.getDeviceDisplay().writeVisor(AppLocal.APP_NAME, AppLocal.APP_VERSION);
        } else {
            try {
                m_TTP.printTicket(sresource);
            } catch (TicketPrinterException eTP) {
                m_TP.getDeviceDisplay().writeVisor(AppLocal.APP_NAME, AppLocal.APP_VERSION);
            }
        }        
    }
    
    private void listPeople() {
        
        try {
           
//            jScrollPane1.getViewport().setView(null);

            JFlowPanel jPeople = new JFlowPanel();
            jPeople.applyComponentOrientation(getComponentOrientation());
           
            java.util.List people = m_dlSystem.listPeopleVisible();
                     
            for (int i = 0; i < people.size(); i++) {
                 
                AppUser user = (AppUser) people.get(i);

                JButton btn = new JButton(new AppUserAction(user));
                btn.applyComponentOrientation(getComponentOrientation());
                btn.setFocusPainted(false);
                btn.setFocusable(false);
                btn.setRequestFocusEnabled(false);
                btn.setHorizontalAlignment(SwingConstants.LEADING);
                btn.setMaximumSize(new Dimension(150, 50));
                btn.setPreferredSize(new Dimension(150, 50));
                btn.setMinimumSize(new Dimension(150, 50));
        
                jPeople.add(btn);                    
            }
//            jScrollPane1.getViewport().setView(jPeople);
            
        } catch (BasicException ee) {
            ee.printStackTrace();
        }
    }





    // La accion del selector
    private class AppUserAction extends AbstractAction {
        
        private AppUser m_actionuser;
        
        public AppUserAction(AppUser user) {
            m_actionuser = user;
            putValue(Action.SMALL_ICON, m_actionuser.getIcon());
            putValue(Action.NAME, m_actionuser.getName());
        }
        
        public AppUser getUser() {
            return m_actionuser;
        }
        
        public void actionPerformed(ActionEvent evt) {
            // String sPassword = m_actionuser.getPassword();
            if (m_actionuser.authenticate()) {
                // p'adentro directo, no tiene password        
                openAppView(m_actionuser);         
            } else {
                // comprobemos la clave antes de entrar...
                String sPassword = JPasswordDialog.showEditPassword(JRootApp.this, 
                        AppLocal.getIntString("Label.Password"),
                        m_actionuser.getName(),
                        m_actionuser.getIcon());
                if (sPassword != null) {
                    if (m_actionuser.authenticate(sPassword)) {
                        openAppView(m_actionuser);                
                    } else {
                        MessageInf msg = new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.BadPassword"));
                        msg.show(JRootApp.this);                        
                    }
                }   
            }
        }
    }
    
    private void showView(String view) {
        CardLayout cl = (CardLayout)(m_jPanelContainer.getLayout());
        cl.show(m_jPanelContainer, view);  
    }
    
    private void openAppView(AppUser user) {
        
        if (closeAppView()) {

            m_principalapp = new JPrincipalApp(this, user);
            try {
                 m_dlSystem.peoplelogInsert(UUID.randomUUID().toString(), m_principalapp.getUser().getId(),now(), "Login",0.0);

            } catch (BasicException ex) {

                 Logger.getLogger(JRootApp.class.getName()).log(Level.SEVERE, null, ex);
            }
            // The user status notificator
//            jPanel3.add(m_principalapp.getNotificator());
           // jPanel3.revalidate();
            
            // The main panel
            m_jPanelContainer.add(m_principalapp, "_" + m_principalapp.getUser().getId());
            showView("_" + m_principalapp.getUser().getId());

            m_principalapp.activate();
        }
    }
        public static String now() {

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
        return sdf.format(cal.getTime());

  }
    public boolean closeAppView() {
        
        if (m_principalapp == null) {
            return true;
        } else if (!m_principalapp.deactivate()) {
            return false;
        } else {
            // the status label
//            jPanel3.remove(m_principalapp.getNotificator());
     //       jPanel3.revalidate();
   //         jPanel3.repaint();

            // remove the card
            m_jPanelContainer.remove(m_principalapp);
            m_principalapp = null;

            showLogin();
            
            return true;
        }
    }
    
    private void showLogin() {
        
        // Show Login
        listPeople();
        showView("login");     

        // show welcome message
        printerStart();
 
        // keyboard listener activation
        inputtext = new StringBuffer();
//        m_txtKeys.setText(null);
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
              //  m_txtKeys.requestFocus();
            }
        });  
    }
    
    private void processKey(char c) {
        
        if (c == '\n') {
            
            AppUser user = null;
            try {
                user = m_dlSystem.findPeopleByCard(inputtext.toString());
            } catch (BasicException e) {
                e.printStackTrace();
            }
            
            if (user == null)  {
                // user not found
                MessageInf msg = new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.nocard"));
                msg.show(this);                
            } else {
                openAppView(user);   
            }

            inputtext = new StringBuffer();
        } else {
            inputtext.append(c);
        }
    }

        
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the FormEditor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        m_jPanelTitle = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        m_jPanelContainer = new javax.swing.JPanel();
        m_jPanelLogin = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        m_jLblUserName = new javax.swing.JLabel();
        m_jLblPassword = new javax.swing.JLabel();
        poweredby1 = new javax.swing.JLabel();
        m_jTxtUserName = new javax.swing.JTextField();
        m_jBtnLogin = new javax.swing.JButton();
        m_jBtnCancel = new javax.swing.JButton();
        jLblInvalidNamePwd = new javax.swing.JLabel();
        m_jTxtPassword = new javax.swing.JPasswordField();
        m_jPanelDown = new javax.swing.JPanel();
        panelTask = new javax.swing.JPanel();
        poweredby = new javax.swing.JLabel();

        setPreferredSize(new java.awt.Dimension(1024, 768));
        setLayout(new java.awt.BorderLayout());

        m_jPanelTitle.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        m_jPanelTitle.setPreferredSize(new java.awt.Dimension(82, 15));
        m_jPanelTitle.setLayout(new java.awt.BorderLayout());

        jLabel2.setPreferredSize(new java.awt.Dimension(80, 34));
        m_jPanelTitle.add(jLabel2, java.awt.BorderLayout.LINE_START);

        add(m_jPanelTitle, java.awt.BorderLayout.NORTH);

        m_jPanelContainer.setLayout(new java.awt.CardLayout());

        m_jLblUserName.setText("User Name");

        m_jLblPassword.setText("Password");

        poweredby1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/splash.png"))); // NOI18N
        poweredby1.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 5, 0, 5));

        m_jBtnLogin.setText("Login");
        m_jBtnLogin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jBtnLoginActionPerformed(evt);
            }
        });
        m_jBtnLogin.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                m_jBtnLoginKeyPressed(evt);
            }
        });

        m_jBtnCancel.setText("Cancel");
        m_jBtnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jBtnCancelActionPerformed(evt);
            }
        });

        jLblInvalidNamePwd.setForeground(new java.awt.Color(255, 0, 51));
        jLblInvalidNamePwd.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap(313, Short.MAX_VALUE)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel2Layout.createSequentialGroup()
                        .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel2Layout.createSequentialGroup()
                                .add(15, 15, 15)
                                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(m_jLblUserName)
                                    .add(m_jLblPassword))
                                .add(41, 41, 41)
                                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                                    .add(m_jTxtPassword)
                                    .add(m_jTxtUserName, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 171, Short.MAX_VALUE)))
                            .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel2Layout.createSequentialGroup()
                                .add(75, 75, 75)
                                .add(m_jBtnLogin)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(m_jBtnCancel)))
                        .add(113, 113, 113))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel2Layout.createSequentialGroup()
                        .add(poweredby1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 392, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(30, 30, 30))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel2Layout.createSequentialGroup()
                        .add(10, 10, 10)
                        .add(jLblInvalidNamePwd, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(57, 57, 57))))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .add(poweredby1)
                .add(1, 1, 1)
                .add(jLblInvalidNamePwd, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 27, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(m_jTxtUserName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 28, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(m_jLblUserName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 21, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(19, 19, 19)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(m_jTxtPassword, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 30, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(m_jLblPassword, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .add(18, 18, 18)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(m_jBtnCancel)
                    .add(m_jBtnLogin))
                .add(87, 87, 87))
        );

        org.jdesktop.layout.GroupLayout m_jPanelLoginLayout = new org.jdesktop.layout.GroupLayout(m_jPanelLogin);
        m_jPanelLogin.setLayout(m_jPanelLoginLayout);
        m_jPanelLoginLayout.setHorizontalGroup(
            m_jPanelLoginLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, m_jPanelLoginLayout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 110, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        m_jPanelLoginLayout.setVerticalGroup(
            m_jPanelLoginLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(m_jPanelLoginLayout.createSequentialGroup()
                .add(41, 41, 41)
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(569, 569, 569))
            .add(org.jdesktop.layout.GroupLayout.TRAILING, m_jPanelLoginLayout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(113, 113, 113))
        );

        m_jPanelContainer.add(m_jPanelLogin, "login");

        add(m_jPanelContainer, java.awt.BorderLayout.CENTER);

        m_jPanelDown.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 0, 0, 0, javax.swing.UIManager.getDefaults().getColor("Button.darkShadow")));
        m_jPanelDown.setLayout(new java.awt.BorderLayout());

        panelTask.setPreferredSize(new java.awt.Dimension(465, 34));
        m_jPanelDown.add(panelTask, java.awt.BorderLayout.LINE_START);

        poweredby.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/poweredby.png"))); // NOI18N
        poweredby.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 5, 0, 5));
        m_jPanelDown.add(poweredby, java.awt.BorderLayout.CENTER);

        add(m_jPanelDown, java.awt.BorderLayout.SOUTH);
    }// </editor-fold>//GEN-END:initComponents

    private void m_jBtnLoginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jBtnLoginActionPerformed
         validateFunction();// TODO add your handling code here:
    }//GEN-LAST:event_m_jBtnLoginActionPerformed
private void validateFunction(){
        String expression = "";
        String TxtUserName = m_jTxtUserName.getText();
        String TxtPassword = m_jTxtPassword.getText();

        AppUser user = null;
         String dbUserName = null;
         String dbPasssword;

        if(TxtUserName.equals("") && TxtPassword.equals(""))
        {
          // jLblUserNameIsNull.setText("");
           jLblInvalidNamePwd.setText("");
           jLblInvalidNamePwd.setText("Please Enter User Name and Password");

        }
        else if(!TxtUserName.equals("") && TxtPassword.equals(""))
        {

           jLblInvalidNamePwd.setText("");
           jLblInvalidNamePwd.setText("Please Enter Password");
        }
        else if(TxtUserName.equals("") && !TxtPassword.equals(""))
        {
          //  jLblPasswordIsNull.setText("");
            jLblInvalidNamePwd.setText("");
            jLblInvalidNamePwd.setText("Please Enter User Name");
            m_jTxtPassword.setText("");

        }

        else
        {

            try {
                user = (AppUser) m_dlSystem.findPeopleByName(TxtUserName);
                if(user!= null){

                    dbUserName = user.getUserName();
                    dbPasssword = user.getPassword();
                }
                else
                {
                    dbUserName = null;
                    dbPasssword = null;
                }

                if(TxtUserName.trim().length()!=TxtUserName.length())
                {
                    jLblInvalidNamePwd.setText("Please Enter User Name Without Space");
                    m_jTxtUserName.setFocusable(true);
                    m_jTxtUserName.setText("");
                    m_jTxtPassword.setText("");

                }
                else
                {
                    if(user == null)
                    {
                        jLblInvalidNamePwd.setText("Invalid User Name/Password");
                        m_jTxtUserName.setFocusable(true);
                        m_jTxtUserName.setText("");
                        m_jTxtPassword.setText("");

                    }
                    else
                    {

                         if(TxtUserName.equals(dbUserName) &&  user.authenticate(TxtPassword))
                         {
                             openAppView(user);
                             jLblInvalidNamePwd.setText("");
                             m_jTxtUserName.setFocusable(true);
                             m_jTxtUserName.setText("");
                             m_jTxtPassword.setText("");


                        }
                        else if(TxtUserName.equals(dbUserName) &&  !user.authenticate(TxtPassword))
                        {

                             jLblInvalidNamePwd.setText("Invalid Password");
                             m_jTxtPassword.setFocusable(true);
                             m_jTxtPassword.setText("");
                        }

                         else
                         {
                                jLblInvalidNamePwd.setText("Invalid User Name/Password");
                                m_jTxtUserName.setFocusable(true);
                                m_jTxtUserName.setText("");
                                m_jTxtPassword.setText("");

                         }
                     }
                }

            }catch(Exception ex) {

                    jLblInvalidNamePwd.setText("Invalid User Name/Password");
                    m_jTxtUserName.setText("");
                    m_jTxtPassword.setText("");
                    ex.printStackTrace();
                    Logger.getLogger(JRootApp.class.getName()).log(Level.SEVERE, null, ex);
            }

    }
}
    private void m_jBtnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jBtnCancelActionPerformed
         tryToClose();// TODO add your handling code here:
    }//GEN-LAST:event_m_jBtnCancelActionPerformed

    private void m_jBtnLoginKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_m_jBtnLoginKeyPressed

        validateFunction();

    }//GEN-LAST:event_m_jBtnLoginKeyPressed



    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLblInvalidNamePwd;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JButton m_jBtnCancel;
    private javax.swing.JButton m_jBtnLogin;
    private javax.swing.JLabel m_jLblPassword;
    private javax.swing.JLabel m_jLblUserName;
    private javax.swing.JPanel m_jPanelContainer;
    private javax.swing.JPanel m_jPanelDown;
    private javax.swing.JPanel m_jPanelLogin;
    private javax.swing.JPanel m_jPanelTitle;
    private javax.swing.JPasswordField m_jTxtPassword;
    private javax.swing.JTextField m_jTxtUserName;
    private javax.swing.JPanel panelTask;
    private javax.swing.JLabel poweredby;
    private javax.swing.JLabel poweredby1;
    // End of variables declaration//GEN-END:variables
}
