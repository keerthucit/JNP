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

package com.openbravo.pos.admin;

import java.awt.Component;
import javax.swing.*;
import com.openbravo.pos.forms.AppLocal;
import com.openbravo.pos.util.Hashcypher;
import java.awt.image.BufferedImage;
import java.util.UUID;
import com.openbravo.basic.BasicException;
import com.openbravo.data.gui.ComboBoxValModel;
import com.openbravo.data.loader.SentenceList;
import com.openbravo.data.user.*;
import com.openbravo.format.Formats;
import com.openbravo.pos.util.StringUtils; 

/**
 *
 * @author adrianromero
 */
public class PeopleView extends JPanel implements EditorRecord {

    private Object m_oId;
    private String m_sPassword;
    
    private DirtyManager m_Dirty;
    
    private SentenceList m_sentrole;
    private ComboBoxValModel m_RoleModel;  
    
    /** Creates new form PeopleEditor */
    public PeopleView(DataLogicAdmin dlAdmin, DirtyManager dirty) {
        initComponents();
                
        // El modelo de roles
        m_sentrole = dlAdmin.getRolesList();
        m_RoleModel = new ComboBoxValModel();
        
        m_Dirty = dirty;
        m_jName.getDocument().addDocumentListener(dirty);
        m_jUserName.getDocument().addDocumentListener(dirty);
        m_jRole.addActionListener(dirty);
        m_jVisible.addActionListener(dirty);
//        m_jImage.addPropertyChangeListener("image", dirty);

        
        writeValueEOF();
    }

    public void writeValueEOF() {
        m_oId = null;
        m_jUserName.setText(null);
        m_sPassword = null;
        m_RoleModel.setSelectedKey(null);
        m_jVisible.setSelected(false);
       // jcard.setText(null);
//        m_jImage.setImage(null);
        m_jName.setEnabled(false);
        m_jUserName.setEnabled(false);
        m_jRole.setEnabled(false);
        m_jVisible.setEnabled(false);
      //  jcard.setEnabled(false);
//        m_jImage.setEnabled(false);
        jButton1.setEnabled(false);
      //  jButton2.setEnabled(false);
      //  jButton3.setEnabled(false);
    }
    
    public void writeValueInsert() {
        m_oId = null;
        m_jName.setText(null);
        m_jUserName.setText(null);
        m_sPassword = null;
        m_RoleModel.setSelectedKey(null);
        m_jVisible.setSelected(true);
       // jcard.setText(null);
//        m_jImage.setImage(null);
        m_jName.setEnabled(true);
        m_jUserName.setEnabled(true);
        m_jRole.setEnabled(true);
        m_jVisible.setEnabled(true);
      //  jcard.setEnabled(true);
    //    m_jImage.setEnabled(true);
        jButton1.setEnabled(true);
      //  jButton2.setEnabled(true);
       // jButton3.setEnabled(true);
    }
    
    public void writeValueDelete(Object value) {
        Object[] people = (Object[]) value;
        m_oId = people[0];
        m_jName.setText(Formats.STRING.formatValue(people[1]));
        m_jUserName.setText(Formats.STRING.formatValue(people[2]));
        m_sPassword = Formats.STRING.formatValue(people[3]);
        m_RoleModel.setSelectedKey(people[4]);
        m_jVisible.setSelected(((Boolean) people[5]).booleanValue());
        //jcard.setText(Formats.STRING.formatValue(people[5]));
//        m_jImage.setImage((BufferedImage) people[6]);
        m_jName.setEnabled(false);
        m_jUserName.setEnabled(false);
        m_jRole.setEnabled(false);
        m_jVisible.setEnabled(false);
      //  jcard.setEnabled(false);
     //   m_jImage.setEnabled(false);
        jButton1.setEnabled(false);
      //  jButton2.setEnabled(false);
        //jButton3.setEnabled(false);
    }    
    
    public void writeValueEdit(Object value) {
        Object[] people = (Object[]) value;
        m_oId = people[0];
        m_jName.setText(Formats.STRING.formatValue(people[1]));
        m_jUserName.setText(Formats.STRING.formatValue(people[2]));
        m_sPassword = Formats.STRING.formatValue(people[3]);
        m_RoleModel.setSelectedKey(people[4]);
        m_jVisible.setSelected(((Boolean) people[5]).booleanValue());
       // jcard.setText(Formats.STRING.formatValue(people[5]));
//        m_jImage.setImage((BufferedImage) people[6]);
        m_jName.setEnabled(true);
        m_jUserName.setEnabled(true);
        m_jRole.setEnabled(true);
        m_jVisible.setEnabled(true);
      //  jcard.setEnabled(true);
       // m_jImage.setEnabled(true);
        jButton1.setEnabled(true);
      //  jButton2.setEnabled(true);
      //  jButton3.setEnabled(true);
    }
    
    public Object createValue() throws BasicException {
        Object[] people = new Object[6];
        people[0] = m_oId == null ? UUID.randomUUID().toString() : m_oId;
        people[1] = Formats.STRING.parseValue(m_jName.getText());
        people[2] = Formats.STRING.parseValue(m_jUserName.getText());
        people[3] = Formats.STRING.parseValue(m_sPassword);
        people[4] = m_RoleModel.getSelectedKey();
        people[5] = Boolean.valueOf(m_jVisible.isSelected());
      //  people[5] = Formats.STRING.parseValue("");
     //   people[6] = "";
        return people;
    }    
    
    public Component getComponent() {
        return this;
    }    
    
    public void activate() throws BasicException {
        
        m_RoleModel = new ComboBoxValModel(m_sentrole.list());
        m_jRole.setModel(m_RoleModel);
    }
    
    public void refresh() {
    }
     
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        m_jUserName = new javax.swing.JTextField();
        m_jVisible = new javax.swing.JCheckBox();
        jLabel3 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        m_jRole = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        m_jName = new javax.swing.JTextField();

        jLabel1.setText(AppLocal.getIntString("label.userName")); // NOI18N

        jLabel3.setText(AppLocal.getIntString("label.peoplevisible")); // NOI18N

        jButton1.setText(AppLocal.getIntString("button.peoplepassword")); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel2.setText(AppLocal.getIntString("label.role")); // NOI18N

        jLabel4.setText(AppLocal.getIntString("label.peoplename")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(m_jUserName, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(m_jRole, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(m_jVisible))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(m_jName, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(129, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(m_jName, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(m_jUserName, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(m_jRole, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(m_jVisible))
                .addContainerGap(399, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed

        String sNewPassword = Hashcypher.changePassword(this);
        if (sNewPassword != null) {
            m_sPassword = sNewPassword;
            m_Dirty.setDirty(true);
        }

    }//GEN-LAST:event_jButton1ActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JTextField m_jName;
    private javax.swing.JComboBox m_jRole;
    private javax.swing.JTextField m_jUserName;
    private javax.swing.JCheckBox m_jVisible;
    // End of variables declaration//GEN-END:variables
    
}
