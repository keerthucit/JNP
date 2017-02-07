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

package com.openbravo.pos.inventory;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import java.awt.image.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import com.openbravo.pos.forms.AppLocal;
import com.openbravo.format.Formats;
import com.openbravo.basic.BasicException;
import com.openbravo.data.gui.ComboBoxValModel;
import com.openbravo.data.loader.SentenceList;
import com.openbravo.data.user.EditorRecord;
import com.openbravo.data.user.DirtyManager;
import com.openbravo.pos.forms.DataLogicSales;
import com.openbravo.pos.sales.TaxesLogic;
import java.util.UUID; 

/**
 * 
 * @author adrianromero
 */
public class ProductsEditor extends JPanel implements EditorRecord {
       
    private SentenceList m_sentcat;
    private ComboBoxValModel m_CategoryModel;

    private SentenceList taxcatsent;
    private ComboBoxValModel taxcatmodel;  

    private SentenceList attsent;
    private ComboBoxValModel attmodel;
    
    private SentenceList taxsent;
    private TaxesLogic taxeslogic;
    
    private ComboBoxValModel m_CodetypeModel;
    
    private Object m_id;
    private Object pricesell;
    private boolean priceselllock = false;
    
    private boolean reportlock = false;
    
    /** Creates new form JEditProduct */
    public ProductsEditor(DataLogicSales dlSales, DirtyManager dirty) {
        initComponents();
        
        // The taxes sentence
       // taxsent = dlSales.getTaxList();
             
        // The categories model
        m_sentcat = dlSales.getCategoriesList();
        m_CategoryModel = new ComboBoxValModel();
        
        // The taxes model
        taxcatsent = dlSales.getTaxCategoriesList();
        taxcatmodel = new ComboBoxValModel();

        // The attributes model
        attsent = dlSales.getAttributeSetList();
        attmodel = new ComboBoxValModel();
        
        m_CodetypeModel = new ComboBoxValModel();
        m_CodetypeModel.add(null);
        m_CodetypeModel.add(CodeType.EAN13);
        m_CodetypeModel.add(CodeType.CODE128);
     //   m_jCodetype.setModel(m_CodetypeModel);
      //  m_jCodetype.setVisible(false);
               
      //  m_jRef.getDocument().addDocumentListener(dirty);
        m_jCode.getDocument().addDocumentListener(dirty);
        m_jName.getDocument().addDocumentListener(dirty);
       // m_jComment.addActionListener(dirty);
        //m_jScale.addActionListener(dirty);
     //   m_jCategory.addActionListener(dirty);
     //   m_jTax.addActionListener(dirty);
       // m_jAtt.addActionListener(dirty);
      //  m_jPriceBuy.getDocument().addDocumentListener(dirty);
        m_jPriceSell.getDocument().addDocumentListener(dirty);
       // m_jImage.addPropertyChangeListener("image", dirty);
        //m_jstockcost.getDocument().addDocumentListener(dirty);
     //   m_jstockvolume.getDocument().addDocumentListener(dirty);
     //   m_jInCatalog.addActionListener(dirty);
       // m_jCatalogOrder.getDocument().addDocumentListener(dirty);
      //  txtAttributes.getDocument().addDocumentListener(dirty);

        FieldsManager fm = new FieldsManager();
      //  m_jPriceBuy.getDocument().addDocumentListener(fm);
        m_jPriceSell.getDocument().addDocumentListener(new PriceSellManager());
     //   m_jTax.addActionListener(fm);
        
      //  m_jPriceSellTax.getDocument().addDocumentListener(new PriceTaxManager());
      //  m_jmargin.getDocument().addDocumentListener(new MarginManager());
        
        writeValueEOF();
    }
    
    public void activate() throws BasicException {
        
        // Load the taxes logic
//        taxeslogic = new TaxesLogic(taxsent.list());
        
        //m_CategoryModel = new ComboBoxValModel(m_sentcat.list());
        //m_jCategory.setModel(m_CategoryModel);

        //taxcatmodel = new ComboBoxValModel(taxcatsent.list());
        //m_jTax.setModel(taxcatmodel);

        attmodel = new ComboBoxValModel(attsent.list());
        attmodel.add(0, null);
        //m_jAtt.setModel(attmodel);
    }
    
    public void refresh() {
    }    
    
    public void writeValueEOF() {
        
        reportlock = true;
        // Los valores
//        m_jTitle.setText(AppLocal.getIntString("label.recordeof"));
        m_id = null;
      //  m_jRef.setText(null);
        m_jCode.setText(null);
        m_jName.setText(null);
       // m_jComment.setSelected(false);
       // m_jScale.setSelected(false);
        m_CategoryModel.setSelectedKey(null);
        taxcatmodel.setSelectedKey(null);
        attmodel.setSelectedKey(null);
       // m_jPriceBuy.setText(null);
        setPriceSell(null);         
      //  m_jImage.setImage(null);
       // m_jstockcost.setText(null);
       // m_jstockvolume.setText(null);
       // m_jInCatalog.setSelected(false);
      //  m_jCatalogOrder.setText(null);
      //  txtAttributes.setText(null);
        reportlock = false;
        
        // Los habilitados
       // m_jRef.setEnabled(false);
        m_jCode.setEnabled(false);
        m_jName.setEnabled(false);
       // m_jComment.setEnabled(false);
        //m_jScale.setEnabled(false);
       // m_jCategory.setEnabled(false);
      //  m_jTax.setEnabled(false);
      //  m_jAtt.setEnabled(false);
      //  m_jPriceBuy.setEnabled(false);
        m_jPriceSell.setEnabled(false);
     //   m_jPriceSellTax.setEnabled(false);
      //  m_jmargin.setEnabled(false);
    //    m_jImage.setEnabled(false);
       // m_jstockcost.setEnabled(false);
       // m_jstockvolume.setEnabled(false);
       // m_jInCatalog.setEnabled(false);
       // m_jCatalogOrder.setEnabled(false);
       /// txtAttributes.setEnabled(false);
        
        calculateMargin();
        calculatePriceSellTax();
    }
    public void writeValueInsert() {
       
        reportlock = true;
        // Los valores
//        m_jTitle.setText(AppLocal.getIntString("label.recordnew"));
        m_id = UUID.randomUUID().toString();
       // m_jRef.setText(null);
        m_jCode.setText(null);
        m_jName.setText(null);
       // m_jComment.setSelected(false);
      //  m_jScale.setSelected(false);
        m_CategoryModel.setSelectedKey(null);
        taxcatmodel.setSelectedKey(null);
        attmodel.setSelectedKey(null);
       // m_jPriceBuy.setText(null);
        setPriceSell(null);                     
       // m_jImage.setImage(null);
        //m_jstockcost.setText(null);
       // m_jstockvolume.setText(null);
       // m_jInCatalog.setSelected(true);
      //  m_jCatalogOrder.setText(null);
       // txtAttributes.setText(null);
        reportlock = false;
        
        // Los habilitados
       // m_jRef.setEnabled(true);
        m_jCode.setEnabled(true);
        m_jName.setEnabled(true);
      //  m_jComment.setEnabled(true);
       // m_jScale.setEnabled(true);
       // m_jCategory.setEnabled(true);
      //  m_jTax.setEnabled(true);
       // m_jAtt.setEnabled(true);
      //  m_jPriceBuy.setEnabled(true);
        m_jPriceSell.setEnabled(true); 
      //  m_jPriceSellTax.setEnabled(true);
     //   m_jmargin.setEnabled(true);
     //   m_jImage.setEnabled(true);
       // m_jstockcost.setEnabled(true);
       // m_jstockvolume.setEnabled(true);
      //  m_jInCatalog.setEnabled(true);
        //m_jCatalogOrder.setEnabled(false);
     //   txtAttributes.setEnabled(true);
        
        calculateMargin();
        calculatePriceSellTax();
   }
    public void writeValueDelete(Object value) {
        
        reportlock = true;       
        Object[] myprod = (Object[]) value;
//        m_jTitle.setText(Formats.STRING.formatValue(myprod[1]) + " - " + Formats.STRING.formatValue(myprod[3]) + " " + AppLocal.getIntString("label.recorddeleted"));
        m_id = myprod[0];
      //  m_jRef.setText(Formats.STRING.formatValue(myprod[1]));
        m_jCode.setText(Formats.STRING.formatValue(myprod[2]));
        m_jName.setText(Formats.STRING.formatValue(myprod[3]));
       // m_jComment.setSelected(((Boolean)myprod[4]).booleanValue());
       // m_jScale.setSelected(((Boolean)myprod[5]).booleanValue());
      //  m_jPriceBuy.setText(Formats.CURRENCY.formatValue(myprod[6]));
        setPriceSell(myprod[7]);                    
        m_CategoryModel.setSelectedKey(myprod[8]);
        taxcatmodel.setSelectedKey(myprod[9]);
        attmodel.setSelectedKey(myprod[10]);
       // m_jImage.setImage((BufferedImage) myprod[11]);
       // m_jstockcost.setText(Formats.CURRENCY.formatValue(myprod[12]));
      // m_jstockvolume.setText(Formats.DOUBLE.formatValue(myprod[13]));
      //  m_jInCatalog.setSelected(((Boolean)myprod[14]).booleanValue());
       // m_jCatalogOrder.setText(Formats.INT.formatValue(myprod[15]));
        //txtAttributes.setText(Formats.BYTEA.formatValue(myprod[16]));
       // txtAttributes.setCaretPosition(0);
        reportlock = false;
        
        // Los habilitados
     //   m_jRef.setEnabled(false);
        m_jCode.setEnabled(false);
        m_jName.setEnabled(false);
       // m_jComment.setEnabled(false);
       // m_jScale.setEnabled(false);
       // m_jCategory.setEnabled(false);
       // m_jTax.setEnabled(false);
       // m_jAtt.setEnabled(false);
      // m_jPriceBuy.setEnabled(false);
        m_jPriceSell.setEnabled(false);
       // m_jPriceSellTax.setEnabled(false);
      //  m_jmargin.setEnabled(false);
     //   m_jImage.setEnabled(false);
       // m_jstockcost.setEnabled(false);
       // m_jstockvolume.setEnabled(false);
      //  m_jInCatalog.setEnabled(false);
     //   m_jCatalogOrder.setEnabled(false);
  //      txtAttributes.setEnabled(false);
        
        calculateMargin();
        calculatePriceSellTax();
    }    
    
    public void writeValueEdit(Object value) {
        
        reportlock = true;
        Object[] myprod = (Object[]) value;
//        m_jTitle.setText(Formats.STRING.formatValue(myprod[1]) + " - " + Formats.STRING.formatValue(myprod[3]));
        m_id = myprod[0];
       // m_jRef.setText(Formats.STRING.formatValue(myprod[1]));
        m_jCode.setText(Formats.STRING.formatValue(myprod[2]));
        m_jName.setText(Formats.STRING.formatValue(myprod[3]));
      //  m_jComment.setSelected(((Boolean)myprod[4]).booleanValue());
        //m_jScale.setSelected(((Boolean)myprod[5]).booleanValue());
       // m_jPriceBuy.setText(Formats.CURRENCY.formatValue(myprod[6]));
        setPriceSell(myprod[7]);                               
        m_CategoryModel.setSelectedKey(myprod[8]);
        taxcatmodel.setSelectedKey(myprod[9]);
       // attmodel.setSelectedKey(myprod[10]);
       // m_jImage.setImage((BufferedImage) myprod[11]);
       // m_jstockcost.setText(Formats.CURRENCY.formatValue(myprod[12]));
       // m_jstockvolume.setText(Formats.DOUBLE.formatValue(myprod[13]));
      //  m_jInCatalog.setSelected(((Boolean)myprod[14]).booleanValue());
       // m_jCatalogOrder.setText(Formats.INT.formatValue(myprod[15]));
      //  txtAttributes.setText(Formats.BYTEA.formatValue(myprod[16]));
       // txtAttributes.setCaretPosition(0);
        reportlock = false;
        
        // Los habilitados
     //   m_jRef.setEnabled(true);
        m_jCode.setEnabled(true);
        m_jName.setEnabled(true);
       // m_jComment.setEnabled(true);
      //  m_jScale.setEnabled(true);
      //  m_jCategory.setEnabled(true);
      //  m_jTax.setEnabled(true);
        //m_jAtt.setEnabled(true);
       // m_jPriceBuy.setEnabled(true);
        m_jPriceSell.setEnabled(true); 
      //  m_jPriceSellTax.setEnabled(true);
      //  m_jmargin.setEnabled(true);
      //  m_jImage.setEnabled(true);
     //   m_jstockcost.setEnabled(true);
      //  m_jstockvolume.setEnabled(true);
      //  m_jInCatalog.setEnabled(true);
      //  m_jCatalogOrder.setEnabled(m_jInCatalog.isSelected());
     //   txtAttributes.setEnabled(true);
        
        calculateMargin();
        calculatePriceSellTax();
    }

    public Object createValue() throws BasicException {
         System.out.println("enrtr---"+taxcatmodel.getSelectedKey());
        Object[] myprod = new Object[17];
        myprod[0] = m_id;
        //myprod[1] = m_jRef.getText();
        myprod[2] = m_jCode.getText();
        myprod[3] = m_jName.getText();
     //   myprod[4] = Boolean.valueOf(m_jComment.isSelected());
     //   myprod[5] = Boolean.valueOf(m_jScale.isSelected());
     //   myprod[6] = Formats.CURRENCY.parseValue(m_jPriceBuy.getText());
        myprod[7] = pricesell;
        myprod[8] = Formats.STRING.parseValue("000");

        myprod[9] = Formats.STRING.parseValue("000");
       // myprod[10] = attmodel.getSelectedKey();
      //  myprod[11] = m_jImage.getImage();
    //    myprod[12] = Formats.CURRENCY.parseValue(m_jstockcost.getText());
    //    myprod[13] = Formats.DOUBLE.parseValue(m_jstockvolume.getText());
    //    myprod[14] = Boolean.valueOf(m_jInCatalog.isSelected());
      //  myprod[15] = Formats.INT.parseValue(m_jCatalogOrder.getText());
       // myprod[16] = Formats.BYTEA.parseValue(txtAttributes.getText());
        
        return myprod;
    }    
    
    public Component getComponent() {
        return this;
    }
    
    private void calculateMargin() {
        
        if (!reportlock) {
            reportlock = true;
            
           /* Double dPriceBuy = readCurrency(m_jPriceBuy.getText());
            Double dPriceSell = (Double) pricesell;

            if (dPriceBuy == null || dPriceSell == null) {
                m_jmargin.setText(null);
            } else {
                m_jmargin.setText(Formats.PERCENT.formatValue(new Double(dPriceSell.doubleValue() / dPriceBuy.doubleValue() - 1.0)));
            }    */
            reportlock = false;
        }
    }
    
    private void calculatePriceSellTax() {
        
        if (!reportlock) {
            reportlock = true;
            
            Double dPriceSell = (Double) pricesell;
            
            if (dPriceSell == null) {
              //  m_jPriceSellTax.setText(null);
            } else {               
//                double dTaxRate = taxeslogic.getTaxRate((TaxCategoryInfo) taxcatmodel.getSelectedItem());
              //  m_jPriceSellTax.setText(Formats.CURRENCY.formatValue(new Double(dPriceSell.doubleValue() * (1.0 + dTaxRate))));
            }            
            reportlock = false;
        }
    }
    
    private void calculatePriceSellfromMargin() {
        
        if (!reportlock) {
            reportlock = true;
            
            //Double dPriceBuy = readCurrency(m_jPriceBuy.getText());
            //Double dMargin = readPercent(m_jmargin.getText());
            
/*            if (dMargin == null || dPriceBuy == null) {
                setPriceSell(null);
            } else {
                setPriceSell(new Double(dPriceBuy.doubleValue() * (1.0 + dMargin.doubleValue())));
            }       */
            
            reportlock = false;
        }
      
    }
    
    private void calculatePriceSellfromPST() {
        
        if (!reportlock) {
            reportlock = true;
                    
           // Double dPriceSellTax = readCurrency(m_jPriceSellTax.getText());

           /* if (dPriceSellTax == null) {
                setPriceSell(null);
            } else {
                double dTaxRate = taxeslogic.getTaxRate((TaxCategoryInfo) taxcatmodel.getSelectedItem()); 
                setPriceSell(new Double(dPriceSellTax.doubleValue() / (1.0 + dTaxRate)));
            }   */
                        
            reportlock = false;
        }    
    }
    
    private void setPriceSell(Object value) {
        
        if (!priceselllock) {
            priceselllock = true;
            pricesell = value;
            m_jPriceSell.setText(Formats.CURRENCY.formatValue(pricesell));  
            priceselllock = false;
        }
    }
    
    private class PriceSellManager implements DocumentListener {
        public void changedUpdate(DocumentEvent e) {
            if (!priceselllock) {
                priceselllock = true;
                pricesell = readCurrency(m_jPriceSell.getText());
                priceselllock = false;
            }
            calculateMargin();
            calculatePriceSellTax();
        }
        public void insertUpdate(DocumentEvent e) {
            if (!priceselllock) {
                priceselllock = true;
                pricesell = readCurrency(m_jPriceSell.getText());
                priceselllock = false;
            }
            calculateMargin();
            calculatePriceSellTax();
        }    
        public void removeUpdate(DocumentEvent e) {
            if (!priceselllock) {
                priceselllock = true;
                pricesell = readCurrency(m_jPriceSell.getText());
                priceselllock = false;
            }
            calculateMargin();
            calculatePriceSellTax();
        }  
    }
    
    private class FieldsManager implements DocumentListener, ActionListener {
        public void changedUpdate(DocumentEvent e) {
            calculateMargin();
            calculatePriceSellTax();
        }
        public void insertUpdate(DocumentEvent e) {
            calculateMargin();
            calculatePriceSellTax();
        }    
        public void removeUpdate(DocumentEvent e) {
            calculateMargin();
            calculatePriceSellTax();
        }         
        public void actionPerformed(ActionEvent e) {
            calculateMargin();
            calculatePriceSellTax();
        }
    }

    private class PriceTaxManager implements DocumentListener {
        public void changedUpdate(DocumentEvent e) {
            calculatePriceSellfromPST();
            calculateMargin();
        }
        public void insertUpdate(DocumentEvent e) {
            calculatePriceSellfromPST();
            calculateMargin();
        }    
        public void removeUpdate(DocumentEvent e) {
            calculatePriceSellfromPST();
            calculateMargin();
        }         
    }
    
    private class MarginManager implements DocumentListener  {
        public void changedUpdate(DocumentEvent e) {
            calculatePriceSellfromMargin();
            calculatePriceSellTax();
        }
        public void insertUpdate(DocumentEvent e) {
            calculatePriceSellfromMargin();
            calculatePriceSellTax();
        }    
        public void removeUpdate(DocumentEvent e) {
            calculatePriceSellfromMargin();
            calculatePriceSellTax();
        }         
    }
    
    private final static Double readCurrency(String sValue) {
        try {
            return (Double) Formats.CURRENCY.parseValue(sValue);
        } catch (BasicException e) {
            return null;
        }
    }
        
    private final static Double readPercent(String sValue) {
        try {
            return (Double) Formats.PERCENT.parseValue(sValue);
        } catch (BasicException e) {
            return null;
        }
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel2 = new javax.swing.JLabel();
        m_jName = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        m_jCode = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        m_jPriceSell = new javax.swing.JTextField();

        setLayout(null);

        jLabel2.setText(AppLocal.getIntString("label.prodname")); // NOI18N
        add(jLabel2);
        jLabel2.setBounds(10, 100, 70, 18);
        add(m_jName);
        m_jName.setBounds(160, 100, 220, 28);

        jLabel6.setText(AppLocal.getIntString("label.prodticket")); // NOI18N
        add(jLabel6);
        jLabel6.setBounds(10, 60, 150, 18);
        add(m_jCode);
        m_jCode.setBounds(160, 60, 220, 28);

        jLabel4.setText(AppLocal.getIntString("label.prodprice")); // NOI18N
        add(jLabel4);
        jLabel4.setBounds(10, 140, 150, 18);

        m_jPriceSell.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        add(m_jPriceSell);
        m_jPriceSell.setBounds(160, 140, 220, 28);
    }// </editor-fold>//GEN-END:initComponents

    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JTextField m_jCode;
    private javax.swing.JTextField m_jName;
    private javax.swing.JTextField m_jPriceSell;
    // End of variables declaration//GEN-END:variables
    
}
