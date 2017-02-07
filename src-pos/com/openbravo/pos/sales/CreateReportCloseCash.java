/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openbravo.pos.sales;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfCell;
import com.lowagie.text.pdf.PdfWriter;
import com.openbravo.pos.panels.PaymentsModel;
import com.openbravo.pos.panels.PaymentsModel.DiscountSalesLine;
import com.openbravo.pos.panels.PaymentsModel.GroupAdvSalesLine;
import com.openbravo.pos.panels.PaymentsModel.GroupSalesLine;
import com.openbravo.pos.panels.PaymentsModel.PaymentsLine;
import com.openbravo.pos.panels.PaymentsModel.SalesLine;
import com.openbravo.pos.panels.PaymentsShowModel;
import com.openbravo.pos.panels.PaymentsShowModel.AdvSalesLine;
import com.openbravo.pos.panels.PaymentsShowModel.RefSalesLine;
import com.openbravo.pos.printer.printer.DevicePrinterPrinter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author root
 */
public class CreateReportCloseCash extends Document {

    PaymentsModel payments;
    String filename;
    private Font small;
    private Font bSmall;
    private Font big;
    private Font italic;
    private Font bBig;
      private Font spaceSmall;

    public CreateReportCloseCash(PaymentsModel payments, String filename) {
        super(PageSize.A4);
        setMargins(40, 40, 40, 40);
        this.payments = payments;
        this.filename = filename;
    }

    public void generate() {

        try {
            PdfWriter.getInstance(this, new FileOutputStream(filename));
            open();
            newPage();
            setFontStyles();
            addFileDetails();
            addReportData();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(DevicePrinterPrinter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (DocumentException ex) {
            Logger.getLogger(DevicePrinterPrinter.class.getName()).log(Level.SEVERE, null, ex);
        }

        close();
    }

    private void drawDottedLine() throws DocumentException {
        add(new Paragraph("---------------------------------------------------------------------", small));
    }
    private void addSpaceDottedLine() throws DocumentException {
        add(new Paragraph("", spaceSmall));
    }


    private void setFontStyles() {

        small = new com.lowagie.text.Font();
        small.setSize(new Float(9.0));
        small.setStyle(com.lowagie.text.Font.NORMAL);

         spaceSmall = new com.lowagie.text.Font();
        spaceSmall.setSize(new Float(6.0));
        spaceSmall.setStyle(com.lowagie.text.Font.NORMAL);

        bSmall = new com.lowagie.text.Font();
        bSmall.setSize(new Float(10.0));
        bSmall.setStyle(com.lowagie.text.Font.BOLD);


        big = new com.lowagie.text.Font();
        big.setSize(new Float(11.0));
        big.setStyle(com.lowagie.text.Font.NORMAL);

        bBig = new com.lowagie.text.Font();
        bBig.setSize(new Float(11.0));
        bBig.setStyle(com.lowagie.text.Font.BOLD);

        italic = new com.lowagie.text.Font();
        italic.setSize(new Float(9.0));
        italic.setStyle(com.lowagie.text.Font.ITALIC);

    }

    private void addFileDetails() {
        addAuthor("Sysfore Technologies");
        addCreationDate();
        addCreator("Sysfore Technologies");
        addSubject("Sales Report");
        addTitle("Sales Report");
    }

    private void addReportData() throws DocumentException {
        List<DiscountSalesLine> DisLines = payments.getDisSaleLines();
        List<GroupSalesLine> GrouplineSales = payments.getGroupSaleLines();
        List<GroupAdvSalesLine> GroupadvLines = payments.getGroupAdvSaleLines();
        Paragraph paragraph = new Paragraph("Hand Over Report", bBig);
        //paragraph.setAlignment(Element.ALIGN_CENTER);
        add(paragraph);
        //addDottedLine();
        drawDottedLine();
        CustomPdfPTable tableShowInfo = new CustomPdfPTable(3, small);
        tableShowInfo.addCells("Date: ");
        tableShowInfo.addCells(payments.printSysDate());
        tableShowInfo.addCells("");  
        add(tableShowInfo);
        drawDottedLine();
        add(new Paragraph("Normal booking", bSmall));
        addSpaceDottedLine();
        CustomPdfPTable tableHeader = new CustomPdfPTable(3, small);
        tableHeader.addCells("Ticket");
        tableHeader.addCells("Qty");
        tableHeader.addCells("Amount");
        add(tableHeader);
        drawDottedLine();
        
        for (SalesLine line : payments.getSaleLines()) {
            CustomPdfPTable table = new CustomPdfPTable(3, small);
            table.addCells(line.printProductName());
            table.addCells(line.printProductCount());
            table.addCells(line.printProduct());
            add(table);
        }
         if (GrouplineSales.size() > 0) {
            for (int i = 0; i < GrouplineSales.size(); i++) {
            GroupSalesLine line = GrouplineSales.get(i);
            CustomPdfPTable table = new CustomPdfPTable(3, small);
            table.addCells(line.printGroupProductName());
            table.addCells(line.printGroupProductCount());
            table.addCells(line.printGroupProduct());
            add(table);
            }
        }
         if (DisLines.size() > 0) {
            CustomPdfPTable table1 = new CustomPdfPTable(3, italic);
            table1.addCells("Discount");
            table1.addCells(payments.printNormalDisTicket());
            table1.addCells("-"+payments.printNorDiscount());
            add(table1);
        }

        //advance booking
        List<PaymentsModel.AdvSalesLine> advLines = payments.getAdvSaleLines();
        if (advLines.size() > 0) {

            add(new Paragraph("Advance booking", bSmall));
            drawDottedLine();
            for (int i = 0; i < advLines.size(); i++) {
                PaymentsModel.AdvSalesLine line = advLines.get(i);
                CustomPdfPTable table = new CustomPdfPTable(3, small);
                table.addCells(line.printAdvProductName());
                table.addCells(line.printAdvProductCount());
                table.addCells(line.printAdvProduct());
                add(table);
            }
            if (GroupadvLines.size() > 0) {
            for (int i = 0; i < GroupadvLines.size(); i++) {
            GroupAdvSalesLine line = GroupadvLines.get(i);
            CustomPdfPTable table = new CustomPdfPTable(3, small);
            table.addCells(line.printGroupAdvProductName());
            table.addCells(line.printGroupAdvProductCount());
            table.addCells(line.printGroupAdvProduct());
            add(table);
            }
        }

             if (DisLines.size() > 0) {
                CustomPdfPTable tableDis = new CustomPdfPTable(3, italic);
                tableDis.addCells("Discount");
                tableDis.addCells(payments.printAdvDisTicket());
                tableDis.addCells("-"+payments.printAdvDiscount());
                add(tableDis);
            }
            drawDottedLine();
        }

        //refunds
        List<PaymentsModel.RefSalesLine> refLines = payments.getRefSaleLines();
        if (refLines.size() > 0) {

            add(new Paragraph("Refund", bSmall));
            drawDottedLine();
            for (int i = 0; i < refLines.size(); i++) {
                PaymentsModel.RefSalesLine line = refLines.get(i);
                CustomPdfPTable table = new CustomPdfPTable(3, small);
                table.addCells(line.printRefProductName());
                table.addCells(line.printRefProductCount());
                table.addCells(line.printRefProduct());
                add(table);
            }
             drawDottedLine();
        }
       
         if(payments.getCancelFee()!=0){
            CustomPdfPTable table0 = new CustomPdfPTable(3, small);
            table0.addCells("Cancellation Fee ");
            table0.addCells("");
            table0.addCells(payments.printCancelFee());
            add(table0);
        }
        CustomPdfPTable table = new CustomPdfPTable(3, small);
        table.addCells("Total: ");
         table.addCells(payments.printTotalUnits());
        table.addCells(payments.printPaymentsTotal());
        add(table);
        drawDottedLine();

        
        
    }

    private Paragraph newLine(int linenos) throws DocumentException {
        Paragraph p = null;
        for (int i = 0; i < linenos; i++) {
            p = new Paragraph("\n");
            add(p);
        }
        return p;
    }

    private String getTabs(int count) {
        String retStr = "";
        for (int i = 0; i < count; i++) {
            retStr = retStr + "\t";
        }
        return retStr;
    }
}
