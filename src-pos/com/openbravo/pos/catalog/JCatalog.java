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

package com.openbravo.pos.catalog;

import com.openbravo.pos.ticket.CategoryInfo;
import com.openbravo.pos.ticket.ProductInfoExt;
import com.openbravo.pos.util.ThumbNailBuilder;

import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;
import java.awt.*;
import com.openbravo.pos.forms.AppLocal; 
import com.openbravo.basic.BasicException;
import com.openbravo.data.gui.JMessageDialog;
import com.openbravo.data.gui.MessageInf;
import com.openbravo.pos.forms.DataLogicSales;
import com.openbravo.pos.panels.showInfo;
import com.openbravo.pos.sales.TaxesLogic;
import com.openbravo.pos.ticket.TaxInfo;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author adrianromero
 */
public class JCatalog extends JPanel implements ListSelectionListener, CatalogSelector {
    
    protected EventListenerList listeners = new EventListenerList();
    private DataLogicSales m_dlSales;   
    private TaxesLogic taxeslogic;
    
    private boolean pricevisible;
    private boolean taxesincluded;
    
    // Set of Products panels
    private Map<String, ProductInfoExt> m_productsset = new HashMap<String, ProductInfoExt>();
    
    // Set of Categoriespanels
     private Set<String> m_categoriesset = new HashSet<String>();
        
    private ThumbNailBuilder tnbbutton;
    private ThumbNailBuilder tnbcat;
    
    private CategoryInfo showingcategory = null;
      public java.util.List<showInfo> currentShow = null;
      String sysShowName=null;
    /** Creates new form JCatalog */
    public JCatalog(DataLogicSales dlSales) {
        this(dlSales, false, false, 200, 200);
    }
    
    public JCatalog(DataLogicSales dlSales, boolean pricevisible, boolean taxesincluded, int width, int height) {
        
        m_dlSales = dlSales;
        this.pricevisible = pricevisible;
        this.taxesincluded = taxesincluded;
        
        initComponents();
        
       // m_jListCategories.addListSelectionListener(this);
//        m_jscrollcat.getVerticalScrollBar().setPreferredSize(new Dimension(35, 35));
        
        tnbcat = new ThumbNailBuilder(32, 32, "com/openbravo/images/folder_yellow.png");
        tnbbutton = new ThumbNailBuilder(150, 72, "com/openbravo/images/package.png");
    }

    public JCatalog() {

    }
    
    public Component getComponent() {
        return this;
    }
    
    public void showCatalogPanel(String id) {
           
        if (id == null) {
            showRootCategoriesPanel();
        } else {            
            showProductPanel(id);
        }
    }
    public void resetCatalog(String Showname) throws BasicException {
         sysShowName = Showname;
         selectCategoryPanel("000");
    }
    
    public void loadCatalog() throws BasicException {
       
        // delete all categories panel
        m_jProducts.removeAll();
        
        m_productsset.clear();        
        m_categoriesset.clear();
        
        showingcategory = null;
                
        // Load the taxes logic
        taxeslogic = new TaxesLogic(m_dlSales.getTaxList().list());

        // Load all categories.
        java.util.List<CategoryInfo> categories = m_dlSales.getRootCategories(); 
        
        // Select the first category
 
        // Display catalog panel
        showRootCategoriesPanel();
    }
    
    public void setComponentEnabled(boolean value) {
    
        m_jProducts.setEnabled(value); 
        synchronized (m_jProducts.getTreeLock()) {
            int compCount = m_jProducts.getComponentCount();
            for (int i = 0 ; i < compCount ; i++) {
                m_jProducts.getComponent(i).setEnabled(value);
            }
        }
     
        this.setEnabled(value);
    }
    
    public void addActionListener(ActionListener l) {
        listeners.add(ActionListener.class, l);
    }
    public void removeActionListener(ActionListener l) {
        listeners.remove(ActionListener.class, l);
    }

    public void valueChanged(ListSelectionEvent evt) {
        
  
    }  
    
    protected void fireSelectedProduct(ProductInfoExt prod) {
       SimpleDateFormat format=new SimpleDateFormat("dd-MM-yyyy");
       String Currentdate = format.format(new Date()).toString();
       int count = 0;
        try {
           count = m_dlSales.getDateCount(Currentdate);
        } catch (BasicException ex) {
            Logger.getLogger(JCatalog.class.getName()).log(Level.SEVERE, null, ex);
        }
        if(count==0){
        EventListener[] l = listeners.getListeners(ActionListener.class);
        ActionEvent e = null;
        for (int i = 0; i < l.length; i++) {
            if (e == null) {
                e = new ActionEvent(prod, ActionEvent.ACTION_PERFORMED, prod.getID());
            }
            ((ActionListener) l[i]).actionPerformed(e);	       
        }
        }else{
           JOptionPane.showMessageDialog(JCatalog.this, "No shows Available - Holiday");

        }
    }   
    
    public void selectCategoryPanel(String catid) {
        
        try {
            // Load categories panel if not exists
            if (!m_categoriesset.contains(catid)) {
                
                JCatalogTab jcurrTab = new JCatalogTab();     
                jcurrTab.applyComponentOrientation(getComponentOrientation());
                m_jProducts.add(jcurrTab, catid);
                m_categoriesset.add(catid);
               
                // Add subcategories
                java.util.List<CategoryInfo> categories = m_dlSales.getSubcategories(catid);
                for (CategoryInfo cat : categories) {

                    jcurrTab.addButton(new ImageIcon(tnbbutton.getThumbNailText(cat.getImage(), cat.getName())), new SelectedCategory(cat));
                }

               
               SimpleDateFormat sdf=new SimpleDateFormat("HH:mm");
               String CurrentTime = sdf.format(new Date()).toString();
               SimpleDateFormat dt=new SimpleDateFormat("yyyy-MM-dd 00:00:00");
               String specialShowDate = dt.format(new Date()).toString();
               String currentShowName = null;
               String open = null;
               String showType = null;
               String ticketGroup = null;
               java.util.List<ProductInfoExt> products = null;
               SimpleDateFormat datetime=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String sysDateTime = datetime.format(new Date()).toString();
                int showCount =0;

                    showCount = m_dlSales.getClosecount(sysDateTime);

               if(showCount == 1){

                    currentShow = m_dlSales.getClosedShowDetails();

                }else{
                    currentShow = m_dlSales.getCurrentShow(specialShowDate);
                }

               if(currentShow.size() !=0){
                  
                    currentShowName = currentShow.get(0).getShowName();                                    
                    open  = currentShow.get(0).getGroupOpen();
                    showType = currentShow.get(0).getspecial();
                    
                    if(showType.equals("Group") && open.equals("N")){
                         ticketGroup = "Y";
                         products = m_dlSales.getProductGroupCatalog(catid,ticketGroup);
                    }else if(showType.equals("Group") && open.equals("Y")){

                         products = m_dlSales.getProductCatalog(catid);
                    }
                    else{
                         if((showType.equals("Regular") || (showType.equals("Special"))) && open.equals("Y")){
                            products = m_dlSales.getProductCatalog(catid);
                        }else{
                             ticketGroup = "N";
                             products = m_dlSales.getProductGroupCatalog(catid,ticketGroup);
                        }
                        //ticketGroup = "N";

                    // Add products
                        //products = m_dlSales.getProductGroupCatalog(catid,ticketGroup);

                    }
                    }else{
                   products = m_dlSales.getProductCatalog(catid);
                    }
                   
               //  products = m_dlSales.getProductCatalog(catid);
                 for (ProductInfoExt prod : products) {
                  
                    jcurrTab.addButton(new ImageIcon(tnbbutton.getThumbNailText(prod.getImage(), getProductLabel(prod))), new SelectedAction(prod));
                }
            }
            
            // Show categories panel
            CardLayout cl = (CardLayout)(m_jProducts.getLayout());
            cl.show(m_jProducts, catid);  
        } catch (BasicException e) {
            JMessageDialog.showMessage(this, new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.notactive"), e));            
        }
    }
    
    private String getProductLabel(ProductInfoExt product) {

        if (pricevisible) {
            if (taxesincluded) {
                TaxInfo tax = taxeslogic.getTaxInfo(product.getTaxCategoryID());
                return "<html><center>" + product.getName() + "<br>" + product.printPriceSellTax(tax);
            } else {
                return "<html><center>" + product.getName() + "<br>" + product.printPriceSell();
            }
        } else {
            return product.getName();
        }
    }
    
    
   
    private void showRootCategoriesPanel() {
        
        
        // Show selected root category
        selectCategoryPanel("000");

        showingcategory = null;
    }
    
    private void showSubcategoryPanel(CategoryInfo category) {
       
        selectCategoryPanel("000");
        showingcategory = category;
    }
   
    private void showProductPanel(String id) {
       
        ProductInfoExt product = m_productsset.get(id);

        if (product == null) {
            if (m_productsset.containsKey(id)) {
                // It is an empty panel
                if (showingcategory == null) {
                    showRootCategoriesPanel();                         
                } else {
                    showSubcategoryPanel(showingcategory);
                }
            } else {
                try {
                    // Create  products panel
                    java.util.List<ProductInfoExt> products = m_dlSales.getProductComments(id);

                    if (products.size() == 0) {
                        // no hay productos por tanto lo anado a la de vacios y muestro el panel principal.
                        m_productsset.put(id, null);
                        if (showingcategory == null) {
                            showRootCategoriesPanel();                         
                        } else {
                            showSubcategoryPanel(showingcategory);
                        }
                    } else {

                        // Load product panel
                        product = m_dlSales.getProductInfo(id);
                        m_productsset.put(id, product);

                        JCatalogTab jcurrTab = new JCatalogTab();      
                        jcurrTab.applyComponentOrientation(getComponentOrientation());

                        m_jProducts.add(jcurrTab, "PRODUCT." + id);                        

                        // Add products
                        for (ProductInfoExt prod : products) {
                            jcurrTab.addButton(new ImageIcon(tnbbutton.getThumbNailText(prod.getImage(), getProductLabel(prod))), new SelectedAction(prod));
                        }                       

//                    selectIndicatorPanel(new ImageIcon(tnbbutton.getThumbNail(product.getImage())), product.getName());

                        CardLayout cl = (CardLayout)(m_jProducts.getLayout());
                        cl.show(m_jProducts, "PRODUCT." + id); 
                    }
                } catch (BasicException eb) {
                    eb.printStackTrace();
                    m_productsset.put(id, null);
                    if (showingcategory == null) {
                        showRootCategoriesPanel();                         
                    } else {
                        showSubcategoryPanel(showingcategory);
                    }
                }
            }
        } else {
            // already exists
           // selectIndicatorPanel(new ImageIcon(tnbbutton.getThumbNail(product.getImage())), product.getName());

            CardLayout cl = (CardLayout)(m_jProducts.getLayout());
            cl.show(m_jProducts, "PRODUCT." + id); 
        }
    }

   public void loadCatalog(String showName) throws BasicException {

        // delete all categories panel
        m_jProducts.removeAll();

        m_productsset.clear();
        m_categoriesset.clear();

        showingcategory = null;

        // Load the taxes logic
        taxeslogic = new TaxesLogic(m_dlSales.getTaxList().list());

        // Load all categories.
        java.util.List<CategoryInfo> categories = m_dlSales.getRootCategories();

        // Select the first category

        // Display catalog panel
        showRootCategoriesPanel(showName);
    }
        public void selectCategoryPanel(String catid, String showName) {
      
        try {
            // Load categories panel if not exists
            if (!m_categoriesset.contains(catid)) {

                JCatalogTab jcurrTab = new JCatalogTab();
                jcurrTab.applyComponentOrientation(getComponentOrientation());
                m_jProducts.add(jcurrTab, catid);
                m_categoriesset.add(catid);

                // Add subcategories
                java.util.List<CategoryInfo> categories = m_dlSales.getSubcategories(catid);
                for (CategoryInfo cat : categories) {

                    jcurrTab.addButton(new ImageIcon(tnbbutton.getThumbNailText(cat.getImage(), cat.getName())), new SelectedCategory(cat));
                }


               SimpleDateFormat sdf=new SimpleDateFormat("HH:mm");
               String CurrentTime = sdf.format(new Date()).toString();
               SimpleDateFormat dt=new SimpleDateFormat("yyyy-MM-dd 00:00:00");
               String specialShowDate = dt.format(new Date()).toString();
               String currentShowName = null;
               String open = null;
               String showType = null;
               String ticketGroup = null;
               java.util.List<ProductInfoExt> products = null;
             //  if(sysShowName ==null){

                    //currentShow = m_dlSales.getCurrentShow(CurrentTime, specialShowDate);
              //  }else{
                    currentShow = m_dlSales.getShowDetails(showName);
              //  }

               if(currentShow.size() !=0){

                    currentShowName = currentShow.get(0).getShowName();
                 
                    open  = currentShow.get(0).getGroupOpen();
                    showType = currentShow.get(0).getspecial();
                    }
                    if(showType.equals("Group") && open.equals("N")){
                        
                        ticketGroup = "Y";
                         products = m_dlSales.getProductGroupCatalog(catid,ticketGroup);
                    }else if(showType.equals("Group") && open.equals("Y")){
                        
                         products = m_dlSales.getProductCatalog(catid);
                    }
                    else{
                        if((showType.equals("Regular") || (showType.equals("Special")))&& open.equals("Y")){
                            products = m_dlSales.getProductCatalog(catid);
                        }else{
                             ticketGroup = "N";
                             products = m_dlSales.getProductGroupCatalog(catid,ticketGroup);
                        }
                    }

                for (ProductInfoExt prod : products) {

                    jcurrTab.addButton(new ImageIcon(tnbbutton.getThumbNailText(prod.getImage(), getProductLabel(prod))), new SelectedAction(prod));
                }
            }

            // Show categories panel
            CardLayout cl = (CardLayout)(m_jProducts.getLayout());
            cl.show(m_jProducts, catid);
        } catch (BasicException e) {
            JMessageDialog.showMessage(this, new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.notactive"), e));
        }
    }
    private void showRootCategoriesPanel(String showName) {


        // Show selected root category
        selectCategoryPanel("000",showName);

        showingcategory = null;
    }

    
    private class SelectedAction implements ActionListener {
        private ProductInfoExt prod;
        public SelectedAction(ProductInfoExt prod) {
            this.prod = prod;
        }
        public void actionPerformed(ActionEvent e) {
            fireSelectedProduct(prod);
        }
    }
    
    private class SelectedCategory implements ActionListener {
        private CategoryInfo category;
        public SelectedCategory(CategoryInfo category) {
            this.category = category;
        }
        public void actionPerformed(ActionEvent e) {
            showSubcategoryPanel(category);
        }
    }
    
    private class CategoriesListModel extends AbstractListModel {
        private java.util.List m_aCategories;
        public CategoriesListModel(java.util.List aCategories) {
            m_aCategories = aCategories;
        }
        public int getSize() { 
            return m_aCategories.size(); 
        }
        public Object getElementAt(int i) {
            return m_aCategories.get(i);
        }    
    }
    
    private class SmallCategoryRenderer extends DefaultListCellRenderer {

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, null, index, isSelected, cellHasFocus);
            CategoryInfo cat = (CategoryInfo) value;
            setText(cat.getName());
            setIcon(new ImageIcon(tnbcat.getThumbNail(cat.getImage())));
            return this;
        }      
    }            
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        m_jProducts = new javax.swing.JPanel();

        setLayout(new java.awt.BorderLayout());

        m_jProducts.setLayout(new java.awt.CardLayout());
        add(m_jProducts, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel m_jProducts;
    // End of variables declaration//GEN-END:variables
    
}
