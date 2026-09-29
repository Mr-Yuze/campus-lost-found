package com.lostfound.campus.dto;

public class MatchDto {
    private Long id;
    private Long lostReportId;
    private Long foundItemId;
    private Integer matchScore;
    private Boolean matched;
    
    // Additional details for display
    private String lostItemName;
    private String foundItemName;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getLostReportId() { return lostReportId; }
    public void setLostReportId(Long lostReportId) { this.lostReportId = lostReportId; }
    public Long getFoundItemId() { return foundItemId; }
    public void setFoundItemId(Long foundItemId) { this.foundItemId = foundItemId; }
    public Integer getMatchScore() { return matchScore; }
    public void setMatchScore(Integer matchScore) { this.matchScore = matchScore; }
    public Boolean getMatched() { return matched; }
    public void setMatched(Boolean matched) { this.matched = matched; }
    public String getLostItemName() { return lostItemName; }
    public void setLostItemName(String lostItemName) { this.lostItemName = lostItemName; }
    public String getFoundItemName() { return foundItemName; }
    public void setFoundItemName(String foundItemName) { this.foundItemName = foundItemName; }
}
