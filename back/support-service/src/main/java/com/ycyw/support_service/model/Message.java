package com.ycyw.support_service.model;

import java.time.Instant;
import lombok.Data;

@Data
public class Message {
    private String sender;
    private String content;
    private Instant timestamp = Instant.now();
}