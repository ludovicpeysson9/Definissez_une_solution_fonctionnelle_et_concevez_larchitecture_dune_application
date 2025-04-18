package com.ycyw.support_service.model;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;

@Data
@Document(collection = "conversations")
public class Conversation {
    @Id
    private String id;
    private List<Message> messages = new ArrayList<>();
}
