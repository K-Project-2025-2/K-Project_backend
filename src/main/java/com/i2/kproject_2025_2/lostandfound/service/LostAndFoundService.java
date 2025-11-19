package com.i2.kproject_2025_2.lostandfound.service;

import com.i2.kproject_2025_2.entity.User;
import com.i2.kproject_2025_2.lostandfound.dto.LostItemReportRequest;
import com.i2.kproject_2025_2.lostandfound.dto.LostItemReportResponse;
import com.i2.kproject_2025_2.lostandfound.entity.LostItemReport;
import com.i2.kproject_2025_2.lostandfound.repository.LostItemReportRepository;
import com.i2.kproject_2025_2.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LostAndFoundService {

    private final LostItemReportRepository lostItemReportRepository;
    private final UserRepository userRepository;

    @Transactional
    public LostItemReportResponse reportLostItem(String email, LostItemReportRequest request) {
        User reporter = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        LostItemReport report = new LostItemReport();
        report.setReporter(reporter);
        report.setLocation(request.getLocation());
        report.setDescription(request.getDescription());

        LostItemReport savedReport = lostItemReportRepository.save(report);

        return LostItemReportResponse.builder()
                .message("Lost item reported")
                .reportId(savedReport.getId())
                .build();
    }
}
