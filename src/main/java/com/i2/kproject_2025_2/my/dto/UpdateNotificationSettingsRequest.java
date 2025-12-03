package com.i2.kproject_2025_2.my.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UpdateNotificationSettingsRequest(
    @JsonProperty("push_notifications")
    Boolean pushNotifications,

    @JsonProperty("shuttle_alert")
    Boolean shuttleAlert,

    @JsonProperty("taxi_alert")
    Boolean taxiAlert
) {}
