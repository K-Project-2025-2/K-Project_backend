package com.i2.kproject_2025_2.my.controller;

import com.i2.kproject_2025_2.my.dto.*;
import com.i2.kproject_2025_2.shuttle.dto.MessageResponse;
import com.i2.kproject_2025_2.my.service.MyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/me")
@RequiredArgsConstructor
public class MyController {

    private final MyService myService;

    // ... (existing methods) ...

    @PostMapping("/report")
    public ResponseEntity<UserReportResponse> reportUser(@AuthenticationPrincipal UserDetails userDetails, @RequestBody UserReportRequest request) {
        UserReportResponse response = myService.reportUser(userDetails.getUsername(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
