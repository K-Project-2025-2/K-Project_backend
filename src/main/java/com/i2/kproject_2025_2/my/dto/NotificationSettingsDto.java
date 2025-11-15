package com.i2.kproject_2025_2.my.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationSettingsDto {
    @JsonProperty("push_notifications")
    private Boolean pushNotifications;

    @JsonProperty("shuttle_alert")
    private Boolean shuttleAlert;

    @JsonProperty("taxi_alert")
    private Boolean taxiAlert;
}
