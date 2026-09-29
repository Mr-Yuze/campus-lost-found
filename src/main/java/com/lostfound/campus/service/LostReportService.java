package com.lostfound.campus.service;

import com.lostfound.campus.dto.LostReportDto;
import com.lostfound.campus.entity.LostReport;
import com.lostfound.campus.entity.LostStatus;
import com.lostfound.campus.entity.User;
import com.lostfound.campus.exception.ResourceNotFoundException;
import com.lostfound.campus.repository.LostReportRepository;
import com.lostfound.campus.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LostReportService {

    private final LostReportRepository lostReportRepository;
    private final UserRepository userRepository;
    private final MatchService matchService;

    public LostReportService(LostReportRepository lostReportRepository, UserRepository userRepository, MatchService matchService) {
        this.lostReportRepository = lostReportRepository;
        this.userRepository = userRepository;
        this.matchService = matchService;
    }

    @Transactional
    public LostReportDto createLostReport(LostReportDto dto, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        LostReport report = new LostReport();
        report.setItemName(dto.getItemName());
        report.setCategory(dto.getCategory());
        report.setDescription(dto.getDescription());
        report.setLocationLost(dto.getLocationLost());
        report.setDateLost(dto.getDateLost());
        report.setReportedBy(user);
        
        LostReport savedReport = lostReportRepository.save(report);
        
        // Trigger matching asynchronously or synchronously
        matchService.findMatchesForLostReport(savedReport);
        
        return mapToDto(savedReport);
    }

    public LostReportDto getLostReportById(Long id) {
        LostReport report = lostReportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lost report not found with id: " + id));
        return mapToDto(report);
    }

    public List<LostReportDto> getAllLostReports(String category, LostStatus status, String location, String keyword) {
        List<LostReport> reports = lostReportRepository.searchLostReports(category, status, location, keyword);
        return reports.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Transactional
    public LostReportDto updateLostReport(Long id, LostReportDto dto) {
        LostReport report = lostReportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lost report not found with id: " + id));
        
        report.setItemName(dto.getItemName());
        report.setCategory(dto.getCategory());
        report.setDescription(dto.getDescription());
        report.setLocationLost(dto.getLocationLost());
        report.setDateLost(dto.getDateLost());
        
        if (dto.getStatus() != null) {
            report.setStatus(dto.getStatus());
        }
        
        LostReport updatedReport = lostReportRepository.save(report);
        return mapToDto(updatedReport);
    }

    public void deleteLostReport(Long id) {
        if (!lostReportRepository.existsById(id)) {
            throw new ResourceNotFoundException("Lost report not found with id: " + id);
        }
        lostReportRepository.deleteById(id);
    }

    public LostReportDto mapToDto(LostReport report) {
        LostReportDto dto = new LostReportDto();
        dto.setId(report.getId());
        dto.setItemName(report.getItemName());
        dto.setCategory(report.getCategory());
        dto.setDescription(report.getDescription());
        dto.setLocationLost(report.getLocationLost());
        dto.setDateLost(report.getDateLost());
        dto.setStatus(report.getStatus());
        dto.setReportedById(report.getReportedBy().getId());
        dto.setReportedByEmail(report.getReportedBy().getEmail());
        dto.setCreatedAt(report.getCreatedAt());
        return dto;
    }
}
