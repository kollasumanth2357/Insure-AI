package com.insure.insurebackend.dto;

public class AdminDashboardStats {

    private long totalCustomers;
    private long totalAgents;
    private long activePolicies;
    private long totalAppointments;
    private long completedAppointments;
    private long pendingAppointments;

    public long getTotalCustomers() { return totalCustomers; }
    public void setTotalCustomers(long totalCustomers) { this.totalCustomers = totalCustomers; }

    public long getTotalAgents() { return totalAgents; }
    public void setTotalAgents(long totalAgents) { this.totalAgents = totalAgents; }

    public long getActivePolicies() { return activePolicies; }
    public void setActivePolicies(long activePolicies) { this.activePolicies = activePolicies; }

    public long getTotalAppointments() { return totalAppointments; }
    public void setTotalAppointments(long totalAppointments) { this.totalAppointments = totalAppointments; }

    public long getCompletedAppointments() { return completedAppointments; }
    public void setCompletedAppointments(long completedAppointments) { this.completedAppointments = completedAppointments; }

    public long getPendingAppointments() { return pendingAppointments; }
    public void setPendingAppointments(long pendingAppointments) { this.pendingAppointments = pendingAppointments; }
}
