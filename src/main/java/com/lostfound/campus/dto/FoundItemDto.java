package com.lostfound.campus.dto;

import com.lostfound.campus.entity.FoundStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class FoundItemDto {
    private Long id;
    
    @NotBlank(message = "Item name cannot be blank")
    private String itemName;
    
    @NotBlank(message = "Category cannot be blank")
    private String category;
    
    private String description;
    private String locationFound;
    private LocalDate dateFound;
    private FoundStatus status;
    
    private Long reportedById;
    private String reportedByEmail; // Read-only
    
    private Long linkedLostReportId;
    
    private LocalDateTime createdAt;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getLocationFound() { return locationFound; }
    public void setLocationFound(String locationFound) { this.locationFound = locationFound; }
    public LocalDate getDateFound() { return dateFound; }
    public void setDateFound(LocalDate dateFound) { this.dateFound = dateFound; }
    public FoundStatus getStatus() { return status; }
    public void setStatus(FoundStatus status) { this.status = status; }
    public Long getReportedById() { return reportedById; }
    public void setReportedById(Long reportedById) { this.reportedById = reportedById; }
    public String getReportedByEmail() { return reportedByEmail; }
    public void setReportedByEmail(String reportedByEmail) { this.reportedByEmail = reportedByEmail; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public Long getLinkedLostReportId() { return linkedLostReportId; }
    public void setLinkedLostReportId(Long linkedLostReportId) { this.linkedLostReportId = linkedLostReportId; }
}
