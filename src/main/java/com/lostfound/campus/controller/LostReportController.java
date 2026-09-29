package com.lostfound.campus.controller;

import com.lostfound.campus.dto.LostReportDto;
import com.lostfound.campus.entity.LostStatus;
import com.lostfound.campus.service.LostReportService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lost-reports")
public class LostReportController {

    private final LostReportService lostReportService;

    public LostReportController(LostReportService lostReportService) {
        this.lostReportService = lostReportService;
    }

    @PostMapping
    public ResponseEntity<LostReportDto> createLostReport(@Valid @RequestBody LostReportDto dto, java.security.Principal principal) {
        return new ResponseEntity<>(lostReportService.createLostReport(dto, principal.getName()), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<LostReportDto>> getAllLostReports(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) LostStatus status,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String keyword) {
        return ResponseEntity.ok(lostReportService.getAllLostReports(category, status, location, keyword));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LostReportDto> getLostReportById(@PathVariable Long id) {
        return ResponseEntity.ok(lostReportService.getLostReportById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LostReportDto> updateLostReport(@PathVariable Long id, @Valid @RequestBody LostReportDto dto) {
        return ResponseEntity.ok(lostReportService.updateLostReport(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLostReport(@PathVariable Long id) {
        lostReportService.deleteLostReport(id);
        return ResponseEntity.noContent().build();
    }
}
