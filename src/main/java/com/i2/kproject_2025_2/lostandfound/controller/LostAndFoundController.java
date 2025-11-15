package com.i2.kproject_2025_2.lostandfound.controller;

import com.i2.kproject_2025_2.lostandfound.dto.LostItemReportRequest;
import com.i2.kproject_2025_2.lostandfound.dto.LostItemReportResponse;
import com.i2.kproject_2025_2.lostandfound.service.LostAndFoundService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/lost-and-found")
@RequiredArgsConstructor
public class LostAndFoundController {

    private final LostAndFoundService lostAndFoundService;

    @PostMapping
    public ResponseEntity<LostItemReportResponse> reportLostItem(@AuthenticationPrincipal UserDetails userDetails, @RequestBody LostItemReportRequest request) {
        LostItemReportResponse response = lostAndFoundService.reportLostItem(userDetails.getUsername(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
