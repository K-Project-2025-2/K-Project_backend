package com.i2.kproject_2025_2.my.service;

import com.i2.kproject_2025_2.entity.User;
import com.i2.kproject_2025_2.my.dto.*;
import com.i2.kproject_2025_2.my.entity.UserReport;
import com.i2.kproject_2025_2.my.repository.UserReportRepository;
import com.i2.kproject_2025_2.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MyService {

    private final UserRepository userRepository;
    private final UserReportRepository userReportRepository;

    // ... (existing methods) ...

    @Transactional
    public UserReportResponse reportUser(String reporterEmail, UserReportRequest request) {
        User reporter = userRepository.findByEmail(reporterEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Reporter not found with email: " + reporterEmail));

        User reported = userRepository.findById(request.getReportedUserId())
                .orElseThrow(() -> new UsernameNotFoundException("Reported user not found"));

        UserReport report = new UserReport();
        report.setReporter(reporter);
        report.setReported(reported);
        report.setReason(request.getReason());
        report.setDetails(request.getDetails());

        UserReport savedReport = userReportRepository.save(report);

        return UserReportResponse.builder()
                .message("User reported successfully")
                .reportId(savedReport.getId())
                .build();
    }
}
