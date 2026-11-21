package org.evabirds.evabirds.dto;

public class DashboardKPIsResponse {

    private long totalUsers;
    private long totalEmployees;
    private long totalFarms;
    private long activeFarms;
    private long pendingReviewFarms;
    private long rentedLeasedFarms;
    private long totalBirdCapacity;
    private long totalVisits;
    private long monthlyInquiries;
    private String systemHealth;

    public DashboardKPIsResponse() {
    }

    public DashboardKPIsResponse(
            long totalUsers,
            long totalEmployees,
            long totalFarms,
            long activeFarms,
            long pendingReviewFarms,
            long rentedLeasedFarms,
            long totalBirdCapacity,
            long totalVisits,
            long monthlyInquiries,
            String systemHealth
    ) {
        this.totalUsers = totalUsers;
        this.totalEmployees = totalEmployees;
        this.totalFarms = totalFarms;
        this.activeFarms = activeFarms;
        this.pendingReviewFarms = pendingReviewFarms;
        this.rentedLeasedFarms = rentedLeasedFarms;
        this.totalBirdCapacity = totalBirdCapacity;
        this.totalVisits = totalVisits;
        this.monthlyInquiries = monthlyInquiries;
        this.systemHealth = systemHealth;
    }

    public long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(long totalUsers) {
        this.totalUsers = totalUsers;
    }

    public long getTotalEmployees() {
        return totalEmployees;
    }

    public void setTotalEmployees(long totalEmployees) {
        this.totalEmployees = totalEmployees;
    }

    public long getTotalFarms() {
        return totalFarms;
    }

    public void setTotalFarms(long totalFarms) {
        this.totalFarms = totalFarms;
    }

    public long getActiveFarms() {
        return activeFarms;
    }

    public void setActiveFarms(long activeFarms) {
        this.activeFarms = activeFarms;
    }

    public long getPendingReviewFarms() {
        return pendingReviewFarms;
    }

    public void setPendingReviewFarms(long pendingReviewFarms) {
        this.pendingReviewFarms = pendingReviewFarms;
    }

    public long getRentedLeasedFarms() {
        return rentedLeasedFarms;
    }

    public void setRentedLeasedFarms(long rentedLeasedFarms) {
        this.rentedLeasedFarms = rentedLeasedFarms;
    }

    public long getTotalBirdCapacity() {
        return totalBirdCapacity;
    }

    public void setTotalBirdCapacity(long totalBirdCapacity) {
        this.totalBirdCapacity = totalBirdCapacity;
    }

    public long getTotalVisits() {
        return totalVisits;
    }

    public void setTotalVisits(long totalVisits) {
        this.totalVisits = totalVisits;
    }

    public long getMonthlyInquiries() {
        return monthlyInquiries;
    }

    public void setMonthlyInquiries(long monthlyInquiries) {
        this.monthlyInquiries = monthlyInquiries;
    }

    public String getSystemHealth() {
        return systemHealth;
    }

    public void setSystemHealth(String systemHealth) {
        this.systemHealth = systemHealth;
    }
}

