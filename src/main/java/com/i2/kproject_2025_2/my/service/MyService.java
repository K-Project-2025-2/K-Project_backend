package com.i2.kproject_2025_2.my.service;

import com.i2.kproject_2025_2.entity.User;
import com.i2.kproject_2025_2.my.dto.*;
import com.i2.kproject_2025_2.my.entity.UserReport;
import com.i2.kproject_2025_2.my.repository.UserReportRepository;
import com.i2.kproject_2025_2.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class MyService {

    private static final int REQUIRED_DEPOSIT = 2500;

    private final UserRepository userRepository;
    private final UserReportRepository userReportRepository;

    @Transactional(readOnly = true)
    public MyProfileResponse getMyProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        return new MyProfileResponse(
                user.getId(),       // user_id
                user.getName(),     // name
                user.getStudentId(),// studentId
                user.getPhone(),    // phone
                user.getEmail()     // email
        );
    }

    @Transactional
    public void updateMyProfile(String email, UpdateProfileRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        // 요청에 name이 포함되어 있으면 업데이트
        if (request.name() != null) {
            user.setName(request.name());
        }

        // 요청에 phone이 포함되어 있으면 업데이트
        if (request.phone() != null) {
            user.setPhone(request.phone());
        }

        // 요청에 studentId가 포함되어 있으면 업데이트
        if (request.studentId() != null) {
            user.setStudentId(request.studentId());
        }

        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public NotificationSettingsResponse getNotificationSettings(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        return new NotificationSettingsResponse(
                user.isPushNotifications(),
                user.isShuttleAlert(),
                user.isTaxiAlert()
        );
    }

    @Transactional
    public void updateNotificationSettings(String email, UpdateNotificationSettingsRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        if (request.pushNotifications() != null) {
            user.setPushNotifications(request.pushNotifications());
        }
        if (request.shuttleAlert() != null) {
            user.setShuttleAlert(request.shuttleAlert());
        }
        if (request.taxiAlert() != null) {
            user.setTaxiAlert(request.taxiAlert());
        }

        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public DepositStatusResponse getDepositStatus(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        return new DepositStatusResponse(
                user.getId(),
                user.getDepositAmount(),
                user.isDepositLocked()
        );
    }

    @Transactional
    public int payDeposit(String email, DepositPaymentRequest request) {
        if (request.amount() != REQUIRED_DEPOSIT) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "보증금은 " + REQUIRED_DEPOSIT + "원이어야 합니다.");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        if (user.getDepositAmount() >= REQUIRED_DEPOSIT) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 보증금을 납부했습니다.");
        }

        user.setDepositAmount(REQUIRED_DEPOSIT);
        user.setDepositLocked(false); // 보증금 사용 가능 상태로 변경
        userRepository.save(user);

        return user.getDepositAmount();
    }

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
