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

package com.openbravo.pos.sales.restaurant;

import com.openbravo.pos.ticket.TicketInfo;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import com.openbravo.pos.sales.*;
import com.openbravo.pos.forms.*; 
import com.openbravo.data.loader.StaticSentence;
import com.openbravo.data.loader.SerializerReadClass;
import com.openbravo.basic.BasicException;
import com.openbravo.data.gui.MessageInf;
import com.openbravo.data.loader.SentenceList;
import com.openbravo.pos.customers.CustomerInfo;
import com.openbravo.pos.ticket.TicketLineInfo;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Timer;

public class JTicketsBagRestaurantMap extends JTicketsBag {

//    private static final Icon ICO_OCU = new ImageIcon(JTicketsBag.class.getResource("/com/openbravo/images/edit_group.png"));
//    private static final Icon ICO_FRE = new NullIcon(22, 22);
        
    private java.util.List<Place> m_aplaces;
    private java.util.List<Floor> m_afloors;
    
    private JTicketsBagRestaurant m_restaurantmap;  
    private JTicketsBagRestaurantRes m_jreservations;   
    
    private Place m_PlaceCurrent;
    
    // State vars
    private Place m_PlaceClipboard;  
    private CustomerInfo customer;

    private DataLogicReceipts dlReceipts = null;
    private DataLogicSales dlSales = null;
    private int noOfSeats=0;
    private RefreshSeats autoRefreshSeat = new RefreshSeats();
    private Timer seatTimer= new Timer(120000, autoRefreshSeat);
    private RefreshShow autoRefreshShow = new RefreshShow();
    private Timer showTimer = new Timer(1200, autoRefreshShow);
    private java.util.List<CurrentSeatInfo> currentSeatList = null;
    private String currentShowId;
    private String currentShowEndTime;
    
    /** Creates new form JTicketsBagRestaurant */
    public JTicketsBagRestaurantMap(AppView app, TicketsEditor panelticket) {
        
        super(app, panelticket);
        
        dlReceipts = (DataLogicReceipts) app.getBean("com.openbravo.pos.sales.DataLogicReceipts");
        dlSales = (DataLogicSales) m_App.getBean("com.openbravo.pos.forms.DataLogicSales");
        
        m_restaurantmap = new JTicketsBagRestaurant(app, this);
        m_PlaceCurrent = null;
        m_PlaceClipboard = null;
        customer = null;
        try {
            currentSeatList=dlSales.getCurrentShowId();
            System.out.println("currentSeatList----"+currentSeatList);
            if(currentSeatList.size()!=0){
            currentShowId=currentSeatList.get(0).getId();
            currentShowEndTime=currentSeatList.get(0).getEndTime();
            }
        } catch (BasicException ex) {
            Logger.getLogger(JTicketsBagRestaurantMap.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            SentenceList sent = new StaticSentence(
                    app.getSession(), 
                    "SELECT ID, NAME, IMAGE FROM FLOORS ORDER BY NAME", 
                    null, 
                    new SerializerReadClass(Floor.class));
            m_afloors = sent.list();
               
                
            
        } catch (BasicException eD) {
            m_afloors = new ArrayList<Floor>();
        }
        try {
            SentenceList sent = new StaticSentence(
                    app.getSession(), 
                    "SELECT ID, NAME, X, Y, FLOOR FROM PLACES ORDER BY FLOOR", 
                    null, 
                    new SerializerReadClass(Place.class));
            m_aplaces = sent.list();
        } catch (BasicException eD) {
            m_aplaces = new ArrayList<Place>();
        } 
        
        initComponents(); 
          
        // add the Floors containers
        if (m_afloors.size() > 1) {
            // A tab container for 2 or more floors
            JTabbedPane jTabFloors = new JTabbedPane();
            jTabFloors.applyComponentOrientation(getComponentOrientation());
            jTabFloors.setBorder(new javax.swing.border.EmptyBorder(new Insets(5, 5, 5, 5)));
            jTabFloors.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
            jTabFloors.setFocusable(false);
            jTabFloors.setRequestFocusEnabled(false);
            m_jPanelMap.add(jTabFloors, BorderLayout.CENTER);
            
            for (Floor f : m_afloors) {
                f.getContainer().applyComponentOrientation(getComponentOrientation());
                
                JScrollPane jScrCont = new JScrollPane();
                jScrCont.applyComponentOrientation(getComponentOrientation());
                JPanel jPanCont = new JPanel();  
                jPanCont.applyComponentOrientation(getComponentOrientation());
                
                jTabFloors.addTab(f.getName(), f.getIcon(), jScrCont);     
                jScrCont.setViewportView(jPanCont);
                jPanCont.add(f.getContainer());
            }
        } else if (m_afloors.size() == 1) {
            // Just a frame for 1 floor
            Floor f = m_afloors.get(0);
            f.getContainer().applyComponentOrientation(getComponentOrientation());
           // this.setPreferredSize(this.getParent().getPreferredSize());
                //  f.setMaximumSize(new Dimension(999999,400));
            
            JPanel jPlaces = new JPanel();
            jPlaces.applyComponentOrientation(getComponentOrientation());
            jPlaces.setLayout(new BorderLayout());
            jPlaces.setBorder(new javax.swing.border.CompoundBorder(
                   // new javax.swing.border.EmptyBorder(new Insets(5, 5, 5, 5)),
                     new javax.swing.border.EmptyBorder(new Insets(2, 2, 2, 2)),
                    new javax.swing.border.TitledBorder(f.getName())));
            
            JScrollPane jScrCont = new JScrollPane();
            jScrCont.applyComponentOrientation(getComponentOrientation());
            JPanel jPanCont = new JPanel();
           
            jPanCont.applyComponentOrientation(getComponentOrientation());
            
            // jPlaces.setLayout(new FlowLayout());           
            m_jPanelMap.add(jPlaces, BorderLayout.CENTER);
            jPlaces.add(jScrCont, BorderLayout.CENTER);
            jScrCont.setViewportView(jPanCont);            
            jPanCont.add(f.getContainer());
             
        }      
        
        // Add all the Table buttons.
        Floor currfloor = null;
        
        
        for (Place pl : m_aplaces) {
            int iFloor = 0;
            
            if (currfloor == null || !currfloor.getID().equals(pl.getFloor())) {
                // Look for a new floor
                do {
                    currfloor = m_afloors.get(iFloor++);
                } while (!currfloor.getID().equals(pl.getFloor()));
            }

            currfloor.getContainer().add(pl.getButton());
            pl.setButtonBounds();
            pl.getButton().addActionListener(new MyActionListener(pl));
         
        }
        //adding static labels
       /* Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
double width = screenSize.getWidth();
double height = screenSize.getHeight();
                    
                    System.out.println(" screen size is"+ width + height);
                     JLabel labl = new JLabel("width");
                  currfloor.getContainer().add(labl);
                   labl.setFont(new Font(labl.getName(), Font.BOLD, 25));
               //labl.setBounds(235 - d.width / 2, 483 - d.height / 2, 28, 28);            
                             labl.setBounds( 990 ,580, 28, 28);     */     
                               
          
        
       
               JLabel labl=new JLabel("A");
               Dimension d = labl.getPreferredSize();         
                          
                 currfloor.getContainer().add(labl);
               labl.setFont(new Font(labl.getName(), Font.BOLD, 25));
               //labl.setBounds(235 - d.width / 2, 483 - d.height / 2, 28, 28);            
                labl.setBounds( 500, 580 , 28, 28);
          
              currfloor.getContainer().add(labl);
               labl=new JLabel("B");
               labl.setFont(new Font(labl.getName(), Font.BOLD, 25));
               //labl.setBounds(210 - d.width / 2, 440 - d.height / 2, 28, 28);
                    labl.setBounds(830,518, 28, 28);
               currfloor.getContainer().add(labl);
               labl=new JLabel("C");
               labl.setFont(new Font(labl.getName(), Font.BOLD, 25));
               //labl.setBounds(87 - d.width / 2, 398 - d.height / 2, 28, 28);
               labl.setBounds(960,460,28, 28);
              currfloor.getContainer().add(labl);
               labl=new JLabel("D");
               labl.setFont(new Font(labl.getName(), Font.BOLD, 25));
               //labl.setBounds(55 - d.width / 2, 355 - d.height / 2, 28, 28);
               labl.setBounds(999,410, 28, 28);
              currfloor.getContainer().add(labl);
               labl=new JLabel("E");
               labl.setFont(new Font(labl.getName(), Font.BOLD, 25));
               //labl.setBounds(45 - d.width / 2, 315 - d.height / 2, 28, 28);
               //labl.setBounds(6,319 ,28, 28);
               labl.setBounds(999,352 ,28, 28);
              currfloor.getContainer().add(labl);
       
              labl=new JLabel(" --> ENTRY");
               labl.setFont(new Font(labl.getName(), Font.BOLD, 15));
                                                                //12
               //labl.setBounds(30 - d.width / 2, 280 - d.height / 2, 60, 40);
                  labl.setBounds(20,290 ,100 , 80);
              currfloor.getContainer().add(labl);
             
       
               labl=new JLabel("F");
               labl.setFont(new Font(labl.getName(), Font.BOLD, 25));
               //labl.setBounds(30 - d.width / 2, 250 - d.height / 2, 28, 28);
               //labl.setBounds(6,245, 28, 28);
                      labl.setBounds(0,284, 28, 28);
              currfloor.getContainer().add(labl);
               labl=new JLabel("G");
               labl.setFont(new Font(labl.getName(), Font.BOLD, 25));
               //labl.setBounds(45 - d.width / 2, 210 - d.height / 2, 28, 28);
               labl.setBounds(0,224, 28, 28);
              currfloor.getContainer().add(labl);
               labl=new JLabel("H");
               labl.setFont(new Font(labl.getName(), Font.BOLD, 25));
              // labl.setBounds(60 - d.width / 2, 170 - d.height / 2, 28, 28);
               labl.setBounds(15,170, 28, 28);
              currfloor.getContainer().add(labl);
               labl=new JLabel("I");
               labl.setFont(new Font(labl.getName(), Font.BOLD, 25));
                 //labl.setBounds(95 - d.width / 2, 130 - d.height / 2, 28, 28);
               labl.setBounds(36,122, 28, 28);
              currfloor.getContainer().add(labl);
              labl=new JLabel("J");
               labl.setFont(new Font(labl.getName(), Font.BOLD, 25));
               //labl.setBounds(125 - d.width / 2, 90 - d.height / 2, 28, 28);
               labl.setBounds( 65,78, 28, 28);
              currfloor.getContainer().add(labl);
               labl=new JLabel("K");
               labl.setFont(new Font(labl.getName(), Font.BOLD, 25));
               //labl.setBounds(175 - d.width / 2, 53 - d.height / 2, 28, 28);
               labl.setBounds(150,39,28, 28);
              currfloor.getContainer().add(labl);
             
              labl=new JLabel("L");
               labl.setFont(new Font(labl.getName(), Font.BOLD, 25));
               labl.setBounds(230,6, 28, 28);
                 //labl.setBounds(235 - d.width / 2, 15 - d.height / 2, 28, 28);
              currfloor.getContainer().add(labl);
             
             
             
               labl=new JLabel("A");
               labl.setFont(new Font(labl.getName(), Font.BOLD, 25));
               //labl.setBounds(453 - d.width / 2, 486 - d.height / 2, 28, 28);
               labl.setBounds(230,570, 28, 28);
               
               
               currfloor.getContainer().add(labl);
         
               labl=new JLabel("B");
               labl.setFont(new Font(labl.getName(), Font.BOLD, 25));
              // labl.setBounds(730 - d.width / 2, 440 - d.height / 2, 28, 28);
               labl.setBounds(197,518, 28, 28);
               currfloor.getContainer().add(labl);
               
               labl=new JLabel("C");
               labl.setFont(new Font(labl.getName(), Font.BOLD, 25));
               //labl.setBounds(815 - d.width / 2, 398 - d.height / 2, 28, 28);
               labl.setBounds( 35,460, 28, 28);
               currfloor.getContainer().add(labl);
               labl=new JLabel("D");
               labl.setFont(new Font(labl.getName(), Font.BOLD, 25));
                //labl.setBounds(1,386,28, 28);
                labl.setBounds(0,410,28, 28);
               //labl.setBounds(850 - d.width / 2, 355 - d.height / 2, 28, 28);
                 
              currfloor.getContainer().add(labl);
               labl=new JLabel("E");
               labl.setFont(new Font(labl.getName(), Font.BOLD, 25));
               //labl.setBounds(860 - d.width / 2, 315 - d.height / 2, 28, 28);
                 labl.setBounds(0,352,28, 28);
              currfloor.getContainer().add(labl);
       
              labl=new JLabel("EXIT -->");
               labl.setFont(new Font(labl.getName(), Font.BOLD, 15));
               //labl.setBounds(860 - d.width / 2, 280 - d.height / 2, 60, 40);
               labl.setBounds(950,290 , 100, 80);
              currfloor.getContainer().add(labl);
             
       
               labl=new JLabel("F");
               labl.setFont(new Font(labl.getName(), Font.BOLD, 25));
              // labl.setBounds(875 - d.width / 2, 250 - d.height / 2, 28, 28);
               //labl.setBounds(982,245, 28, 28);
                labl.setBounds(999,284, 28, 28);
              currfloor.getContainer().add(labl);
             
               labl=new JLabel("G");
               labl.setFont(new Font(labl.getName(), Font.BOLD, 25));
               //labl.setBounds(860 - d.width / 2, 210 - d.height / 2, 28, 28);
              //labl.setBounds(970,216, 28, 28);
               labl.setBounds(999,224, 28, 28);
               currfloor.getContainer().add(labl);
               labl=new JLabel("H");
               labl.setFont(new Font(labl.getName(), Font.BOLD, 25));
               //labl.setBounds(848 - d.width / 2, 170 - d.height / 2, 28, 28);
               //labl.setBounds(970,172, 28, 28);
                 labl.setBounds(985,170, 28, 28);
              currfloor.getContainer().add(labl);
               labl=new JLabel("I");
               labl.setFont(new Font(labl.getName(), Font.BOLD, 25));
                //labl.setBounds(820 - d.width / 2, 130 - d.height / 2, 28, 28);
               labl.setBounds(970,122, 28, 28);
              currfloor.getContainer().add(labl);
              labl=new JLabel("J");
               labl.setFont(new Font(labl.getName(), Font.BOLD, 25));
               //labl.setBounds(790 - d.width / 2, 90 - d.height / 2, 28, 28);
                 labl.setBounds(930,78, 28, 28);
              currfloor.getContainer().add(labl);
               labl=new JLabel("K");
               labl.setFont(new Font(labl.getName(), Font.BOLD, 25));
               labl.setBounds(842,39, 28, 28);
                 //labl.setBounds(730 - d.width / 2, 53 - d.height / 2, 28, 28);
              currfloor.getContainer().add(labl);
              labl=new JLabel("L");
               labl.setFont(new Font(labl.getName(), Font.BOLD, 25));
               labl.setBounds(768,6,28, 28);
                //labl.setBounds(670 - d.width / 2, 15 - d.height / 2, 28, 28);
              currfloor.getContainer().add(labl);
             
   
                 
              labl=new JLabel("PROJECTOR");
              labl.setFont(new Font(labl.getName(), Font.BOLD, 15));
              //labl.setBounds(420 - d.width / 2, 220 - d.height / 2, 100, 40);
              //labl.setBounds(440,245, 100, 80);
               labl.setBounds(460 ,232, 100, 80);
              currfloor.getContainer().add(labl);
             
              labl=new JLabel("CONSOLE");
              labl.setFont(new Font(labl.getName(), Font.BOLD, 20));
              //labl.setBounds(500 - d.width / 2, 480 - d.height / 2, 100, 40);
              labl.setBounds(545,580,100, 40);
              currfloor.getContainer().add(labl);
        // Add the reservations panel
        m_jreservations = new JTicketsBagRestaurantRes(app, this);
        add(m_jreservations, "res");
    }
    
     private class RefreshSeats implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent ae) {
            loadSeats();
        }
    }
      private class RefreshShow implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent ae) {
            loadShow();
        }
    }
     
    public void activate() {
        
        // precondicion es que no tenemos ticket activado ni ticket en el panel

        m_PlaceClipboard = null;
        customer = null;
        loadSeats();        
        printState(); 
        
        m_panelticket.setActiveTicket(null, null); 
        m_restaurantmap.activate();
       
        showView("map"); // arrancamos en la vista de las mesas.
        
        // postcondicion es que tenemos ticket activado aqui y ticket en el panel
    }
    
    public boolean deactivate() {
        
        // precondicion es que tenemos ticket activado aqui y ticket en el panel
        
        if (viewTables()) {
        
            // borramos el clipboard
            m_PlaceClipboard = null;
            customer = null;

            // guardamos el ticket
            if (m_PlaceCurrent != null) {
                            
                try {
                    dlReceipts.updateSharedTicket(m_PlaceCurrent.getId(), m_panelticket.getActiveTicket());
                } catch (BasicException e) {
                    new MessageInf(e).show(this);
                }                                  
 
                m_PlaceCurrent = null;
            }

            // desactivamos cositas.
            printState();     
            m_panelticket.setActiveTicket(null, null); 

            return true;
        } else {
            return false;
        }
        
        // postcondicion es que no tenemos ticket activado
    }

        
    protected JComponent getBagComponent() {
        return m_restaurantmap;
    }
    protected JComponent getNullComponent() {
        return this;
    }
   
    public void moveTicket() {
        
        // guardamos el ticket
        if (m_PlaceCurrent != null) {
                          
            try {
                dlReceipts.updateSharedTicket(m_PlaceCurrent.getId(), m_panelticket.getActiveTicket());
            } catch (BasicException e) {
                new MessageInf(e).show(this);
            }      
            
            // me guardo el ticket que quiero copiar.
            m_PlaceClipboard = m_PlaceCurrent;    
            customer = null;
            m_PlaceCurrent = null;
        }
        
        printState();
        m_panelticket.setActiveTicket(null, null);
    }
    
    public boolean viewTables(CustomerInfo c) {
        // deberiamos comprobar si estamos en reservations o en tables...
        if (m_jreservations.deactivate()) {
            showView("map"); // arrancamos en la vista de las mesas.
            
            m_PlaceClipboard = null;    
            customer = c;     
            printState();
            
            return true;
        } else {
            return false;
        }        
    }
    
    public boolean viewTables() {
        return viewTables(null);
    }
        
    public void newTicket() {
        
        // guardamos el ticket
        if (m_PlaceCurrent != null) {
                         
            try {
                dlReceipts.updateSharedTicket(m_PlaceCurrent.getId(), m_panelticket.getActiveTicket());                
            } catch (BasicException e) {
                new MessageInf(e).show(this); // maybe other guy deleted it
            }              

            m_PlaceCurrent = null;
            noOfSeats=0;
        }
        
        printState();     
        m_panelticket.setActiveTicket(null, null);     
    }
    
    public void deleteTicket() {
        
        if (m_PlaceCurrent != null) {
            
            String id = m_PlaceCurrent.getId();
            try {
                dlReceipts.deleteSharedTicket(id);
            } catch (BasicException e) {
                new MessageInf(e).show(this);
            }       
            
            m_PlaceCurrent.setPeople(false);
            
            m_PlaceCurrent = null;
        }        
        
        printState();     
        m_panelticket.setActiveTicket(null, null); 
    }
        private void showMessage(JTicketsBagRestaurantMap aThis, String msg) {
        JOptionPane.showMessageDialog(aThis, getLabelPanel(msg), "Message",
                JOptionPane.INFORMATION_MESSAGE);

    }

    private JPanel getLabelPanel(String msg) {
        JPanel panel = new JPanel();
        Font font = new Font("Verdana", Font.BOLD, 12);
        panel.setFont(font);
        panel.setOpaque(true);
        // panel.setBackground(Color.BLUE);
        JLabel label = new JLabel(msg, JLabel.LEFT);
        label.setForeground(Color.RED);
        label.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        panel.add(label);

        return panel;
    }
    
    public void loadShow() {
        
          try {
        Date sysdate = new Date();
        SimpleDateFormat time = new SimpleDateFormat("HH:mm");
        String currentTime = time.format(sysdate);
        if(currentShowEndTime.equals(currentTime)){
            String previousShowId=currentSeatList.get(0).getId();
            currentSeatList=dlSales.getCurrentShowId();
             if(currentSeatList.size()!=0){
            currentShowId=currentSeatList.get(0).getId();
            currentShowEndTime=currentSeatList.get(0).getEndTime();
            }
            //delete from showlines with previous showid-previousShowId
        }
        } catch (BasicException ex) {
            Logger.getLogger(JTicketsBagRestaurantMap.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
   
    
    public void loadSeats() {

           // list for all sharedtickets database table entries
        java.util.List<SeatInfo> runningSeatsList = null;
        // Map for Partial detail of SharedTicketInfo Object i.e <ID as key and ISPRINTED as value> 
        Map<String, String> runningSeatsMap = new HashMap<String, String>();
        String key = null;
        String value = null;
        try {
            runningSeatsList = dlReceipts.getSeatList(currentShowId);//retrieve sharedticketes data from db
        } catch (BasicException ex) {
            Logger.getLogger(JTicketsBagRestaurantMap.class.getName()).log(Level.SEVERE, null, ex);
        }
            //Iterate over all sharedtickets and create HashMap for <ID,ISPRINTED> details
            for (SeatInfo sharedTicket : runningSeatsList) {
                //atickets.add(ticket.getId());
                key = sharedTicket.getId();
               value=sharedTicket.getStatus();
               
                runningSeatsMap.put(key, value); //add partial detail in Map.
            }
      
        //Iterate of all the tables present in this floor.
        for (Place seat : m_aplaces) {
            //table.setPeople(atickets.contains(table.getId()));
            if (runningSeatsMap.containsKey(seat.getId())) {//if current table is present in my map as key
                //retrieve the value of same key entry from map of shared tickets
                String busyTableColor = runningSeatsMap.get(seat.getId());
                //If current table is already being printed ,display yellow button ,if not then red button.
              if("BLOCKED".equals(busyTableColor))  {
             // seat.getButton().setBackground(Color.LIGHT_GRAY);
              seat.getButton().setBackground(new Color(3, 59, 90));
              }else{
                    seat.getButton().setBackground(Color.GREEN);
              }
            } else {
                //if table is not busy or occupied , then display button as white color.
                 seat.getButton().setBackground(Color.WHITE); 
                 
              
            }
        }
    }
    
    private void printState() {
        
        if (m_PlaceClipboard == null) {
            if (customer == null) {
                // Select a table
                m_jText.setText(null);
                // Enable all tables
                for (Place place : m_aplaces) {
                    place.getButton().setEnabled(true);
                }
                m_jbtnReservations.setEnabled(true);
            } else {
                // receive a customer
                m_jText.setText(AppLocal.getIntString("label.restaurantcustomer", new Object[] {customer.getName()}));
                // Enable all tables
                for (Place place : m_aplaces) {
                    place.getButton().setEnabled(!place.hasPeople());
                }                
                m_jbtnReservations.setEnabled(false);
            }
        } else {
            // Moving or merging the receipt to another table
            m_jText.setText(AppLocal.getIntString("label.restaurantmove", new Object[] {m_PlaceClipboard.getName()}));
            // Enable all empty tables and origin table.
            for (Place place : m_aplaces) {
                place.getButton().setEnabled(true);
            }  
            m_jbtnReservations.setEnabled(false);
        }
    }   
    
    private TicketInfo getTicketInfo(Place place) {

        try {
            return dlReceipts.getSharedTicket(place.getId());
        } catch (BasicException e) {
            new MessageInf(e).show(JTicketsBagRestaurantMap.this);
            return null;
        }
    }
    
    private void setActivePlace(Place place, TicketInfo ticket) {
        m_PlaceCurrent = place;
        m_panelticket.setActiveTicket(ticket, "");
    }

    private void showView(String view) {
        CardLayout cl = (CardLayout)(getLayout());
        cl.show(this, view);  
    }
    
    private class MyActionListener implements ActionListener {
        
        private Place m_place;
        
        public MyActionListener(Place place) {
            m_place = place;
        }
        
        public void actionPerformed(ActionEvent evt) {
        //   loadSeats();
            if(m_place.getButton().getBackground().equals(Color.WHITE)){
                noOfSeats=noOfSeats+1;
                m_place.getButton().setBackground(Color.PINK);
                 //m_place.getButton().setBackground(new Color(3, 59, 90));
                //set the m_place variable to blocked
                //Here insert into showlines with blocked status
            }else if(m_place.getButton().getBackground().equals(Color.LIGHT_GRAY)){
                //check here whether it is blocked by same system then make it white else no
               m_place.getButton().setBackground(Color.WHITE); 
              noOfSeats=noOfSeats-1;
              // if status s changed to white then delete that line from shwline
            }
          
        }
    }  
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        m_jPanelMap = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        m_jbtnReservations = new javax.swing.JButton();
        m_jbtnRefresh = new javax.swing.JButton();
        m_jText = new javax.swing.JLabel();
        jButtonBook = new javax.swing.JButton();

        setLayout(new java.awt.CardLayout());

        m_jPanelMap.setLayout(new java.awt.BorderLayout());

        jPanel1.setLayout(new java.awt.BorderLayout());

        jPanel2.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        m_jbtnReservations.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/date.png"))); // NOI18N
        m_jbtnReservations.setText(AppLocal.getIntString("button.reservations")); // NOI18N
        m_jbtnReservations.setFocusPainted(false);
        m_jbtnReservations.setFocusable(false);
        m_jbtnReservations.setMargin(new java.awt.Insets(8, 14, 8, 14));
        m_jbtnReservations.setRequestFocusEnabled(false);
        m_jbtnReservations.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jbtnReservationsActionPerformed(evt);
            }
        });
        jPanel2.add(m_jbtnReservations);

        m_jbtnRefresh.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/reload.png"))); // NOI18N
        m_jbtnRefresh.setText(AppLocal.getIntString("button.reloadticket")); // NOI18N
        m_jbtnRefresh.setFocusPainted(false);
        m_jbtnRefresh.setFocusable(false);
        m_jbtnRefresh.setMargin(new java.awt.Insets(8, 14, 8, 14));
        m_jbtnRefresh.setRequestFocusEnabled(false);
        m_jbtnRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jbtnRefreshActionPerformed(evt);
            }
        });
        jPanel2.add(m_jbtnRefresh);
        jPanel2.add(m_jText);

        jButtonBook.setText("Book");
        jButtonBook.setFocusable(false);
        jButtonBook.setMargin(new java.awt.Insets(8, 14, 8, 14));
        jButtonBook.setMaximumSize(new java.awt.Dimension(107, 46));
        jButtonBook.setMinimumSize(new java.awt.Dimension(107, 46));
        jButtonBook.setPreferredSize(new java.awt.Dimension(107, 39));
        jButtonBook.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonBookActionPerformed(evt);
            }
        });
        jPanel2.add(jButtonBook);

        jPanel1.add(jPanel2, java.awt.BorderLayout.LINE_START);

        m_jPanelMap.add(jPanel1, java.awt.BorderLayout.NORTH);

        add(m_jPanelMap, "map");
    }// </editor-fold>//GEN-END:initComponents

    private void m_jbtnRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jbtnRefreshActionPerformed

        m_PlaceClipboard = null;
        customer = null;
        loadSeats();     
        printState();   
        
    }//GEN-LAST:event_m_jbtnRefreshActionPerformed

    private void m_jbtnReservationsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jbtnReservationsActionPerformed

        showView("res");
        m_jreservations.activate();
        
    }//GEN-LAST:event_m_jbtnReservationsActionPerformed

    private void jButtonBookActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonBookActionPerformed
       if(noOfSeats==0){
             showMessage(this, "Please select the seats");
       }else{
          TicketInfo   ticket = new TicketInfo();
          ticket.setSeats(noOfSeats);
          setActivePlace(m_PlaceClipboard, ticket);
            
       }
    }//GEN-LAST:event_jButtonBookActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonBook;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel m_jPanelMap;
    private javax.swing.JLabel m_jText;
    private javax.swing.JButton m_jbtnRefresh;
    private javax.swing.JButton m_jbtnReservations;
    // End of variables declaration//GEN-END:variables
    
}
