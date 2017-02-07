/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openbravo.pos.sales;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPTable;

/**
 *
 * @author root
 */
class CustomPdfPTable extends PdfPTable{
    
    Font font;
    
    public CustomPdfPTable(int cols, Font font) throws DocumentException {
        super(cols);
        this.font = font;
        float[] relativeWidths = {0.30F, 0.25F, 0.25F};
        getDefaultCell().setBorder(Rectangle.NO_BORDER);
        setWidthPercentage(40F);
        setHorizontalAlignment(Element.ALIGN_LEFT);
        setTotalWidth(relativeWidths);
    }
    
    public void addCells(String content){
        addCell(new Paragraph(content, font));
    }
    
}
