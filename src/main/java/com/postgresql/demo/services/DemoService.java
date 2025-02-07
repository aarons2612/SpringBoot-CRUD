package com.postgresql.demo.services;

import com.postgresql.demo.exceptions.PersonNotFoundException;
import com.postgresql.demo.model.Demo;
import com.postgresql.demo.repo.DemoRepo;

import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Date;

@Service
public class DemoService {

    @Autowired
    private DemoRepo repo;

    public Demo addPerson(Demo person) {
        return repo.save(person);
    }

    public List<Demo> getAllPersons() {
        return repo.findAll();
    }

        public Demo getPersonById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new PersonNotFoundException("Person not found with ID: " + id));
    }

    public Demo updatePerson(Long id, Demo personDetails) {
        return repo.findById(id).map(person -> {
            person.setName(personDetails.getName());
            person.setEmail(personDetails.getEmail());
            return repo.save(person);
        }).orElseThrow(() -> new PersonNotFoundException("Person not found with ID: " + id));
    }

    public void deletePerson(Long id) {
        Demo person = repo.findById(id)
                .orElseThrow(() -> new PersonNotFoundException("Person not found with ID: " + id));
        repo.delete(person);
    }
    public byte[] generateExcel() throws IOException {
        List<Demo> persons = repo.findAll();
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Persons");

        // Create header style (Bold + Background Color)
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 12);
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderTop(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);

        // Create normal cell style with border
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);

        // Create Header Row
        Row headerRow = sheet.createRow(0);
        String[] columns = {"ID", "Name", "Email"};
        for (int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(headerStyle);
            sheet.autoSizeColumn(i);
        }

        // Populate Data Rows
        int rowNum = 1;
        for (Demo person : persons) {
            Row row = sheet.createRow(rowNum++);
            Cell cell1 = row.createCell(0);
            cell1.setCellValue(person.getId());
            cell1.setCellStyle(cellStyle);

            Cell cell2 = row.createCell(1);
            cell2.setCellValue(person.getName());
            cell2.setCellStyle(cellStyle);

            Cell cell3 = row.createCell(2);
            cell3.setCellValue(person.getEmail());
            cell3.setCellStyle(cellStyle);
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();
        return outputStream.toByteArray();
    }

    public String generateFilename() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        return "persons_" + dateFormat.format(new Date()) + ".xlsx";
    }
    public byte[] generatePdf() throws IOException {
        List<Demo> persons = repo.findAll();
        
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                // Add Logo
                PDImageXObject logo = PDImageXObject.createFromFile("src/main/resources/static/logo.png", document);
                contentStream.drawImage(logo, 450, 750, 100, 50);

                // Title
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 18);
                contentStream.beginText();
                contentStream.newLineAtOffset(50, 750);
                contentStream.showText("Persons Report");
                contentStream.endText();

                // Draw Table
                drawTable(contentStream, persons);
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            document.save(outputStream);
            return outputStream.toByteArray();
        }
    }

    private void drawTable(PDPageContentStream contentStream, List<Demo> persons) throws IOException {
        float margin = 50;
        float yStart = 700;
        float rowHeight = 20;
        float cellMargin = 5;

        // Column widths
        float[] colWidths = {50, 200, 250}; // ID | Name | Email

        // Draw Header
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
        float xStart = margin;
        float yPosition = yStart;
        contentStream.setLineWidth(1);

        String[] headers = {"ID", "Name", "Email"};
        for (int i = 0; i < headers.length; i++) {
            contentStream.addRect(xStart, yPosition, colWidths[i], -rowHeight);
            contentStream.beginText();
            contentStream.newLineAtOffset(xStart + cellMargin, yPosition - 15);
            contentStream.showText(headers[i]);
            contentStream.endText();
            xStart += colWidths[i];
        }
        contentStream.stroke();

        // Draw Data Rows
        contentStream.setFont(PDType1Font.HELVETICA, 12);
        for (Demo person : persons) {
            yPosition -= rowHeight;
            xStart = margin;
            String[] data = {String.valueOf(person.getId()), person.getName(), person.getEmail()};
            for (int i = 0; i < data.length; i++) {
                contentStream.addRect(xStart, yPosition, colWidths[i], -rowHeight);
                contentStream.beginText();
                contentStream.newLineAtOffset(xStart + cellMargin, yPosition - 15);
                contentStream.showText(data[i]);
                contentStream.endText();
                xStart += colWidths[i];
            }
            contentStream.stroke();
        }
    }

    public String generatePdfFilename() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        return "persons_" + dateFormat.format(new Date()) + ".pdf";
    }
}
