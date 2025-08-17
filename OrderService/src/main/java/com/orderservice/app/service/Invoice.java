package com.orderservice.app.service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.orderservice.app.entity.Address;
import com.orderservice.app.entity.Items;
import com.orderservice.app.entity.Order;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

@Component
public class Invoice {

    public byte[] generateInvoicePdf(List<Items> cart, Order order) throws DocumentException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document();
        PdfWriter.getInstance(document, out);
        document.open();

        // Company Header
        Font boldFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
        Paragraph header = new Paragraph("EShopping Zone Pvt. Ltd.", boldFont);
        header.setAlignment(Element.ALIGN_CENTER);
        document.add(header);

        document.add(new Paragraph("INVOICE", new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD)));
        document.add(new Paragraph("Date: " + LocalDate.now()));
        document.add(new Paragraph(" "));

        // User Details Section
        Font labelFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
        document.add(new Paragraph("Name: " + order.getCustomerName(), labelFont));
        document.add(new Paragraph("Email: " + order.getCustomerEmail(), labelFont));
        document.add(new Paragraph("Payment Mode: " + order.getModeOfPayment(), labelFont));
        document.add(new Paragraph("Address: " + makeAddress(order.getAddress().get(0)), labelFont));
        document.add(new Paragraph(" "));

        // Table Header
        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(10f);

        Stream.of("Item", "Quantity", "Unit Price", "Total Price").forEach(col -> {
            PdfPCell cell = new PdfPCell(new Phrase(col, new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD)));
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
        });

        // Used BigDecimal for storing only two float digit after point
        BigDecimal total = BigDecimal.ZERO;

        for (Items item : cart) {
            table.addCell(item.getItemName());
            table.addCell(String.valueOf(item.getQuantity()));
            table.addCell("₹" + item.getPrice());

            BigDecimal lineTotal = BigDecimal.valueOf(item.getPrice())
                    .multiply(BigDecimal.valueOf(item.getQuantity()));
            table.addCell("₹" + lineTotal.setScale(2, RoundingMode.HALF_UP));
            total = total.add(lineTotal);
        }

        document.add(table);

// Total Section
//        BigDecimal gst = total.multiply(BigDecimal.valueOf(0.18)).setScale(2, RoundingMode.HALF_UP);
        BigDecimal discount = total.multiply(BigDecimal.valueOf(0.08)).setScale(2, RoundingMode.HALF_UP);
        BigDecimal grandTotal = total.subtract(discount).setScale(2, RoundingMode.HALF_UP);

        document.add(new Paragraph("Subtotal: ₹" + total.setScale(2, RoundingMode.HALF_UP)));
        document.add(new Paragraph("Discount (8%): ₹" + discount));
        document.add(new Paragraph("Grand Total: ₹" + grandTotal, new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD)));
        document.add(new Paragraph(" "));
        document.add(new Paragraph("Thank you for your purchase!", new Font(Font.FontFamily.HELVETICA, 12, Font.ITALIC)));

        document.close();
        return out.toByteArray();
    }

    String makeAddress(Address address) {
        return address.getStreet() + " " + address.getCity() + " " + address.getState() + " " + address.getCountry() + " " + address.getZipCode();
    }
}