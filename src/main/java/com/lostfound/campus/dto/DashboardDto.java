package com.lostfound.campus.dto;

public class DashboardDto {
    private long totalUsers;
    private long totalStudents;
    private long totalStaff;
    private long totalAdmins;
    private long totalLostReports;
    private long totalFoundItems;
    private long availableItems;
    private long claimedItems;
    private long returnedItems;
    private long totalMatches;

    // Getters and Setters
    public long getTotalUsers() { return totalUsers; }
    public void setTotalUsers(long totalUsers) { this.totalUsers = totalUsers; }
    public long getTotalStudents() { return totalStudents; }
    public void setTotalStudents(long totalStudents) { this.totalStudents = totalStudents; }
    public long getTotalStaff() { return totalStaff; }
    public void setTotalStaff(long totalStaff) { this.totalStaff = totalStaff; }
    public long getTotalAdmins() { return totalAdmins; }
    public void setTotalAdmins(long totalAdmins) { this.totalAdmins = totalAdmins; }
    public long getTotalLostReports() { return totalLostReports; }
    public void setTotalLostReports(long totalLostReports) { this.totalLostReports = totalLostReports; }
    public long getTotalFoundItems() { return totalFoundItems; }
    public void setTotalFoundItems(long totalFoundItems) { this.totalFoundItems = totalFoundItems; }
    public long getAvailableItems() { return availableItems; }
    public void setAvailableItems(long availableItems) { this.availableItems = availableItems; }
    public long getClaimedItems() { return claimedItems; }
    public void setClaimedItems(long claimedItems) { this.claimedItems = claimedItems; }
    public long getReturnedItems() { return returnedItems; }
    public void setReturnedItems(long returnedItems) { this.returnedItems = returnedItems; }
    public long getTotalMatches() { return totalMatches; }
    public void setTotalMatches(long totalMatches) { this.totalMatches = totalMatches; }
}
