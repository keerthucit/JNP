package com.openbravo.pos.ticket;
    
    import com.openbravo.pos.util.StringUtils;
    import java.io.Serializable;
    import java.util.Properties;

public class TicketProductInfo implements Serializable {
    private String id;
    private String name;
    private boolean com;
    
    private TaxInfo tax;
    private Properties attributes;
    
    public TicketProductInfo(String id, String name, boolean com, TaxInfo tax, Properties attributes) {
       this.id = id;
       this.name = name;
        this.com = com;
        this.tax = tax; 
        this.attributes = attributes;
         }
         
         public TicketProductInfo(String name, TaxInfo tax) {
             this(null, name, false, tax, new Properties());
         }
         
         public TicketProductInfo() {
             this(null, null, false, null, new Properties());
         }
         
         public TicketProductInfo(ProductInfoExt product) {
             this(product.getID(), product.getName(), product.isCom(), null, product.getProperties());
         }
         
         public TicketProductInfo cloneTicketProduct() {
             TicketProductInfo p = new TicketProductInfo();
             p.id = id;
             p.name = name;
             p.com = com;
             p.tax = tax;   
             p.attributes = attributes;
             return p;        
         }
         
         public String getId() {
             return id;
         }    
         
         public String getName() {
             return name;
         }     
         
         public void setName(String value) {
             if (id == null) {
                 name = value;
             }
         }
      
         public boolean isCom() {
             return com;
         }
         
         public void setCom(boolean value) {
             if (id == null) {
                 com = value;
             }
         }    
         
         public TaxInfo getTax() {
             return tax;
         }    
         
         public void setTax(TaxInfo value) {
            tax = value;
        }
        
        public String getProperty(String key) {
            return attributes.getProperty(key);
        }
        
        public String getProperty(String key, String defaultvalue) {
            return attributes.getProperty(key, defaultvalue);
        }
        
        public void setProperty(String key, String value) {
            attributes.setProperty(key, value);
        }
        
        public Properties getProperties() {
            return attributes;
        }
        
        public String printName() {
             return name == null ? "" : StringUtils.encodeXML(name);
        }
}