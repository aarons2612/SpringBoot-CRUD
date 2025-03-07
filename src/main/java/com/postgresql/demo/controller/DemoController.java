package com.postgresql.demo.controller;

import com.postgresql.demo.model.Demo;
import com.postgresql.demo.services.DemoService;
import com.postgresql.demo.services.TemporalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/persons")
public class DemoController {

    @Autowired
    private DemoService service;

    @Autowired
    private TemporalService temporalService; // Temporal service to start workflow

    /**
     * Add a new person and start the Temporal workflow.
     */
    @PostMapping
    public ResponseEntity<String> addPerson(@RequestBody Demo person) {
        String workflowId = temporalService.startPersonWorkflow(person); // ✅ Start workflow
    
        Long personId = temporalService.getPersonId(workflowId); // ✅ Wait & get stored ID
    
        if (personId == null) {
            return ResponseEntity.status(500).body("Person ID not found after waiting.");
        }
    
        return ResponseEntity.ok("Workflow completed. Person ID: " + personId);
    }
    
    /**
     * Retrieve all persons.
     */
    @GetMapping
    public ResponseEntity<List<Demo>> getAllPersons() {
        return ResponseEntity.ok(service.getAllPersons());
    }

    /**
     * Retrieve a person by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Demo> getPersonById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getPersonById(id));
    }

    /**
     * Update person details.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Demo> updatePerson(@PathVariable Long id, @RequestBody Demo personDetails) {
        return ResponseEntity.ok(service.updatePerson(id, personDetails));
    }

    /**
     * Delete a person.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePerson(@PathVariable Long id) {
        service.deletePerson(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Download all persons data as an Excel file.
     */
    @GetMapping("/download")
    public ResponseEntity<byte[]> downloadExcel() throws IOException {
        byte[] excelData = service.generateExcel();
        String filename = service.generateFilename();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(excelData);
    }

    /**
     * Download all persons data as a PDF file.
     */
    @GetMapping("/download/pdf")
    public ResponseEntity<byte[]> downloadPdf() throws IOException {
        byte[] pdfData = service.generatePdf();
        String filename = service.generatePdfFilename();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfData);
    }

    /**
     * Preview the generated PDF inline.
     */
    @GetMapping("/preview-pdf")
    public ResponseEntity<byte[]> previewPdf() throws IOException {
        byte[] pdfData = service.generatePdf();

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=preview.pdf") // Inline preview
                .body(pdfData);
    }
}
