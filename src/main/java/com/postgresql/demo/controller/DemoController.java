package com.postgresql.demo.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import com.postgresql.demo.model.Demo;
import com.postgresql.demo.services.DemoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/persons")
public class DemoController {

    @Autowired
    private DemoService service;

    @PostMapping
    public ResponseEntity<Demo> addPerson(@RequestBody Demo person) {
        return ResponseEntity.ok(service.addPerson(person));
    }

    @GetMapping
    public ResponseEntity<List<Demo>> getAllPersons() {
        return ResponseEntity.ok(service.getAllPersons());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Demo> getPersonById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getPersonById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Demo> updatePerson(@PathVariable Long id, @RequestBody Demo personDetails) {
        return ResponseEntity.ok(service.updatePerson(id, personDetails));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePerson(@PathVariable Long id) {
        service.deletePerson(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/download")
    public ResponseEntity<byte[]> downloadExcel() throws IOException {
        byte[] excelData = service.generateExcel(); // Use 'service' instead of 'demoService'
        String filename = service.generateFilename();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(excelData);
    }
    @GetMapping("/download/pdf")
    public ResponseEntity<byte[]> downloadPdf() throws IOException {
        byte[] pdfData = service.generatePdf();
        String filename = service.generatePdfFilename();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfData);
    }
    
    @GetMapping("/preview-pdf")
    public ResponseEntity<byte[]> previewPdf() throws IOException {
        byte[] pdfData = service.generatePdf();

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=preview.pdf") // Inline to preview
                .body(pdfData);
    }

    }