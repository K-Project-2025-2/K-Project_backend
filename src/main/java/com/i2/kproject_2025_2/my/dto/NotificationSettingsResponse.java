package com.i2.kproject_2025_2.my.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record NotificationSettingsResponse(
    @JsonProperty("push_notifications")
    boolean pushNotifications,

    @JsonProperty("shuttle_alert")
    boolean shuttleAlert,

    @JsonProperty("taxi_alert")
    boolean taxiAlert
) {}
