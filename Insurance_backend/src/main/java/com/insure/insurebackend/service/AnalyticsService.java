package com.insure.insurebackend.service;

import com.insure.insurebackend.dto.*;
import com.insure.insurebackend.model.AppointmentStatus;
import com.insure.insurebackend.model.PaymentStatus;
import com.insure.insurebackend.repository.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class AnalyticsService {

    private final PaymentRepository paymentRepository;
    private final CustomerPolicyRepository customerPolicyRepository;
    private final AppointmentRepository appointmentRepository;
    private final PolicyRepository policyRepository;
    private final AuditLogRepository auditLogRepository;

    public AnalyticsService(PaymentRepository paymentRepository,
                            CustomerPolicyRepository customerPolicyRepository,
                            AppointmentRepository appointmentRepository,
                            PolicyRepository policyRepository,
                            AuditLogRepository auditLogRepository) {
        this.paymentRepository = paymentRepository;
        this.customerPolicyRepository = customerPolicyRepository;
        this.appointmentRepository = appointmentRepository;
        this.policyRepository = policyRepository;
        this.auditLogRepository = auditLogRepository;
    }

    public DashboardKpiResponse dashboardKpis() {
        LocalDate start = LocalDate.now().withDayOfMonth(1);
        LocalDate end = start.plusMonths(1);
        BigDecimal revenueThisMonth = paymentRepository.sumByStatusAndDate(
                PaymentStatus.SUCCESS,
                start.atStartOfDay(),
                end.atStartOfDay()
        );

        long policyViews = auditLogRepository.countByAction("POLICY_VIEW");
        long purchasedPolicies = customerPolicyRepository.count();
        double conversionRate = policyViews == 0 ? 0.0 : (purchasedPolicies * 100.0) / policyViews;

        long totalAppointments = appointmentRepository.count();
        long cancelledAppointments = appointmentRepository.countByStatus(AppointmentStatus.CANCELLED);
        double cancellationRate = totalAppointments == 0 ? 0.0 : (cancelledAppointments * 100.0) / totalAppointments;

        long completedAppointments = appointmentRepository.countByStatus(AppointmentStatus.COMPLETED);
        double agentProductivity = totalAppointments == 0 ? 0.0 : (completedAppointments * 100.0) / totalAppointments;

        return new DashboardKpiResponse(
                revenueThisMonth.doubleValue(),
                round(conversionRate),
                round(cancellationRate),
                round(agentProductivity)
        );
    }

    public List<RevenueReportResponse> revenueReports(int monthsBack) {
        List<RevenueReportResponse> reports = new ArrayList<>();
        LocalDate current = LocalDate.now().withDayOfMonth(1);

        for (int i = 0; i < monthsBack; i++) {
            LocalDate start = current.minusMonths(i);
            LocalDate end = start.plusMonths(1);
            BigDecimal amount = paymentRepository.sumByStatusAndDate(
                    PaymentStatus.SUCCESS,
                    start.atStartOfDay(),
                    end.atStartOfDay()
            );
            reports.add(new RevenueReportResponse(start.getMonth().name() + " " + start.getYear(), amount.doubleValue()));
        }
        return reports;
    }

    public List<PolicyDistributionResponse> policyDistribution() {
        List<PolicyDistributionResponse> response = new ArrayList<>();
        policyRepository.findByStatus("ACTIVE").forEach(policy -> {
            String category = policy.getMainCategory() == null ? "UNSPECIFIED" : policy.getMainCategory();
            PolicyDistributionResponse existing = response.stream()
                    .filter(item -> item.getType().equals(category))
                    .findFirst()
                    .orElse(null);
            if (existing == null) {
                response.add(new PolicyDistributionResponse(category, 1L));
            } else {
                existing.setCount(existing.getCount() + 1);
            }
        });
        return response;
    }

    public AppointmentAnalyticsResponse appointmentAnalytics() {
        long total = appointmentRepository.count();
        long completed = appointmentRepository.countByStatus(AppointmentStatus.COMPLETED);
        long cancelled = appointmentRepository.countByStatus(AppointmentStatus.CANCELLED);
        long scheduled = appointmentRepository.countByStatus(AppointmentStatus.SCHEDULED);
        return new AppointmentAnalyticsResponse(total, completed, cancelled, scheduled);
    }

    public PaymentSummaryResponse paymentSummary() {
        long success = paymentRepository.countByStatus(PaymentStatus.SUCCESS);
        long failed = paymentRepository.countByStatus(PaymentStatus.FAILED);
        long pending = paymentRepository.countByStatus(PaymentStatus.PENDING);
        BigDecimal totalSuccess = paymentRepository.sumByStatus(PaymentStatus.SUCCESS);
        return new PaymentSummaryResponse(success, failed, pending, totalSuccess.doubleValue());
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
