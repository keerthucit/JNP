/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openbravo.pos.sales;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfCell;
import com.lowagie.text.pdf.PdfWriter;
import com.openbravo.pos.panels.PaymentsHandOverModel;
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
public class CreateHoReport extends Document {

    PaymentsHandOverModel payments;
    String filename;
    private Font small;
    private Font bSmall;
    private Font big;
    private Font italic;
    private Font bBig;
      private Font spaceSmall;

    public CreateHoReport(PaymentsHandOverModel payments, String filename) {
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

        Paragraph paragraph = new Paragraph("Money Hand Over Report", bBig);
        //paragraph.setAlignment(Element.ALIGN_CENTER);
        add(paragraph);
        //addDottedLine();
        drawDottedLine();
        CustomPdfPTable tableShowInfo = new CustomPdfPTable(3, small);
        tableShowInfo.addCells("Hand Over Date: ");
        tableShowInfo.addCells(payments.printSysDate());
        tableShowInfo.addCells("");
     //   tableShowInfo.addCells("");
      //  tableShowInfo.addCells("");
        tableShowInfo.addCells("Hand Over Time: ");
        tableShowInfo.addCells(payments.printSysTime());
        tableShowInfo.addCells("");
       // tableShowInfo.addCells("");
        //tableShowInfo.addCells("");
        tableShowInfo.addCells("Show Name: ");
        tableShowInfo.addCells(payments.printShowName());
        tableShowInfo.addCells("");
        add(tableShowInfo);
        drawDottedLine();

        CustomPdfPTable table = new CustomPdfPTable(3, small);
        table.addCells("Hand Over Total: ");
        table.addCells(payments.printPaymentsTotal());
        table.addCells("");
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
