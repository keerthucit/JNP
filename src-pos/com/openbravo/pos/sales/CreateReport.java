/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openbravo.pos.sales;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.openbravo.pos.panels.PaymentsShowModel;
import com.openbravo.pos.panels.PaymentsShowModel.AdvSalesLine;
import com.openbravo.pos.panels.PaymentsShowModel.DiscountSalesLine;
import com.openbravo.pos.panels.PaymentsShowModel.GroupAdvSalesLine;
import com.openbravo.pos.panels.PaymentsShowModel.GroupSalesLine;
import com.openbravo.pos.panels.PaymentsShowModel.PaymentsLine;
import com.openbravo.pos.panels.PaymentsShowModel.RefSalesLine;
import com.openbravo.pos.panels.PaymentsShowModel.SalesLine;
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
public class CreateReport extends Document {

    PaymentsShowModel payments;
    String filename;
    private Font small;
    private Font spaceSmall;
    private Font bSmall;
    private Font big;
    private Font bBig;
    private Font italic;

    public CreateReport(PaymentsShowModel payments, String filename) {
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

    private void addDottedLine() throws DocumentException {
        add(new Paragraph("-------------------------------------------------------------------------", small));
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

        List<SalesLine> lineSales = payments.getSaleLines();
        List<AdvSalesLine> advLines = payments.getAdvSaleLines();
        List<GroupSalesLine> GrouplineSales = payments.getGroupSaleLines();
        List<GroupAdvSalesLine> GroupadvLines = payments.getGroupAdvSaleLines();
        List<RefSalesLine> refLines = payments.getRefSaleLines();
        List<DiscountSalesLine> DisLines = payments.getDisSaleLines();
        
        Paragraph paragraph = new Paragraph("Close Show Report", bBig);
        paragraph.setAlignment(Element.ALIGN_LEFT);
        add(paragraph);
        addDottedLine();
        addSpaceDottedLine();
        CustomPdfPTable tableShowInfo = new CustomPdfPTable(3, small);
        tableShowInfo.addCells("Issue Close Date: ");
        tableShowInfo.addCells(payments.printSysDate());
        tableShowInfo.addCells("Time: " + payments.printSysTime());
        tableShowInfo.addCells("");
        tableShowInfo.addCells("");
        tableShowInfo.addCells("");
        tableShowInfo.addCells("Show No: ");
        tableShowInfo.addCells("");
        tableShowInfo.addCells(payments.printSequence());
        tableShowInfo.addCells("");
        tableShowInfo.addCells("");
        tableShowInfo.addCells("");
        tableShowInfo.addCells("Show Name: ");
        tableShowInfo.addCells("");

        tableShowInfo.addCells(payments.printShowName());
        tableShowInfo.addCells("");
        tableShowInfo.addCells("");
        tableShowInfo.addCells("");
        tableShowInfo.addCells("Show Time: ");
        tableShowInfo.addCells("");
        tableShowInfo.addCells(payments.printShowStarttime() + "-" + payments.printShowEndTime());
        add(tableShowInfo);
        addDottedLine();
        add(new Paragraph("Normal booking", bSmall));
        addSpaceDottedLine();
        CustomPdfPTable tableHeader = new CustomPdfPTable(3, small);
        tableHeader.addCells("Ticket");
        tableHeader.addCells("Qty");
        tableHeader.addCells("Amount");
        add(tableHeader);
        addDottedLine();

        for (int i = 0; i < lineSales.size(); i++) {
            SalesLine line = lineSales.get(i);
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
        if (advLines.size() > 0) {

            add(new Paragraph("Advance booking", bSmall));
            addDottedLine();
            for (int i = 0; i < advLines.size(); i++) {
                AdvSalesLine line = advLines.get(i);
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
           
        }

        //refunds
        if (refLines.size() > 0) {

            add(new Paragraph("Refund", bSmall));
            addDottedLine();
            for (int i = 0; i < refLines.size(); i++) {
                RefSalesLine line = refLines.get(i);
                CustomPdfPTable table = new CustomPdfPTable(3, small);
                table.addCells(line.printRefProductName());
                table.addCells(line.printRefProductCount());
                table.addCells(line.printRefProduct());
                add(table);

            }
            
        }
       addDottedLine();
        if(payments.getCancelFee()!=0){
            CustomPdfPTable table0 = new CustomPdfPTable(3, small);
            table0.addCells("Cancellation Fee ");
            table0.addCells("");
            table0.addCells(payments.printCancelFee());
            add(table0);
        }

        CustomPdfPTable table = new CustomPdfPTable(3, bSmall);

        table.addCells("Total: ");
        table.addCells(payments.printTotalUnits());

        table.addCells(payments.printPaymentsTotal());
        add(table);

      
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
