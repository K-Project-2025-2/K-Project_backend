package com.i2.kproject_2025_2.my.controller;

import com.i2.kproject_2025_2.my.dto.*;
import com.i2.kproject_2025_2.shuttle.dto.MessageResponse;
import com.i2.kproject_2025_2.my.service.MyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/me")
@RequiredArgsConstructor
public class MyController {

    private final MyService myService;

    @GetMapping
    public ResponseEntity<MyProfileResponse> getMyProfile(@AuthenticationPrincipal String email) {
        MyProfileResponse response = myService.getMyProfile(email);
        return ResponseEntity.ok(response);
    }

    @PutMapping
    public ResponseEntity<MessageResponse> updateMyProfile(
            @AuthenticationPrincipal String email,
            @RequestBody UpdateProfileRequest request
    ) {
        myService.updateMyProfile(email, request);
        return ResponseEntity.ok(new MessageResponse("Profile updated successfully"));
    }

    @GetMapping("/notifications")
    public ResponseEntity<NotificationSettingsResponse> getNotificationSettings(@AuthenticationPrincipal String email) {
        NotificationSettingsResponse response = myService.getNotificationSettings(email);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/notifications")
    public ResponseEntity<MessageResponse> updateNotificationSettings(
            @AuthenticationPrincipal String email,
            @RequestBody UpdateNotificationSettingsRequest request
    ) {
        myService.updateNotificationSettings(email, request);
        return ResponseEntity.ok(new MessageResponse("Notification settings updated"));
    }

    @GetMapping("/deposit")
    public ResponseEntity<DepositStatusResponse> getDepositStatus(@AuthenticationPrincipal String email) {
        DepositStatusResponse response = myService.getDepositStatus(email);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/deposit")
    public ResponseEntity<DepositPaymentResponse> payDeposit(
            @AuthenticationPrincipal String email,
            @RequestBody DepositPaymentRequest request
    ) {
        int newDepositAmount = myService.payDeposit(email, request);
        DepositPaymentResponse response = new DepositPaymentResponse(
                "Deposit paid successfully",
                newDepositAmount
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/report")
    public ResponseEntity<UserReportResponse> reportUser(@AuthenticationPrincipal String email, @RequestBody UserReportRequest request) {
        UserReportResponse response = myService.reportUser(email, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
