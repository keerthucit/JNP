/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.openbravo.pos.panels;

import com.openbravo.basic.BasicException;
import com.openbravo.data.loader.DataRead;
import com.openbravo.data.loader.DataWrite;
import com.openbravo.data.loader.ImageUtils;
import com.openbravo.data.loader.SerializableRead;
import com.openbravo.data.loader.SerializableWrite;
import java.awt.image.BufferedImage;
import java.util.Date;

/**
 *
 * @author preethi
 */
public class ticketCategoryInfo implements SerializableRead, SerializableWrite {

    private static final long serialVersionUID = 7640633837719L;
    private String id;
    private String productname;
    private String ticketcode;
    private double price;
    private int noofpeople;
    private String group;
    private double minAmount;
    private BufferedImage image;
     private String discount;
    /** Creates a new instance of DiscountRateinfo */
    public ticketCategoryInfo() {
    }

    public void readValues(DataRead dr) throws BasicException {
        id = dr.getString(1);
        ticketcode = dr.getString(2);
        productname = dr.getString(3);
        price = dr.getDouble(4);
        group = dr.getString(5);
        minAmount =dr.getDouble(6);
        noofpeople = dr.getInt(7);
        image = ImageUtils.readImage(dr.getBytes(8));
        discount = dr.getString(9);
    }
    public void writeValues(DataWrite dp) throws BasicException {
        dp.setString(1, id);
        dp.setString(2, ticketcode);
        dp.setString(3, productname);
        dp.setDouble(4, price);
        dp.setString(5, group);
        dp.setDouble(6, minAmount);
        dp.setInt(7, noofpeople);
        dp.setImage(8, image);
        dp.setString(9, discount);
        
        
    }

    public String getId() {
        return id;
    }

    public String getproductname() {
        return productname;
    }
    public void setproductname(String productname) {
        this.productname = productname;
    }

    public String getticketcode() {
        return ticketcode;
    }
    public void setticketcode(String ticketcode) {
        this.ticketcode = ticketcode;
    }
    public String getgroup() {
        return group;
    }

    public void setgroup(String group) {
        this.group = group;
    }
     public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }
     public int getnoofpeople() {
        return noofpeople;
    }
    public void setnoofpeople(int noofpeople) {
        this.noofpeople = noofpeople;
    }
    public double getminAmount() {
        return minAmount;
    }

    public void setminAmount(double minAmount) {
        this.minAmount = minAmount;
    }
     public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price= price;

    }

      public BufferedImage getImage() {
        return image;
    }

    public void setImage(BufferedImage image) {
        this.image= image;

    }
}
