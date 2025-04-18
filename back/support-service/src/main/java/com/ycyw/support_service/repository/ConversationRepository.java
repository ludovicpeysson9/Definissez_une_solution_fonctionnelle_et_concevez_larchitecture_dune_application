package com.ycyw.support_service.repository;

import com.ycyw.support_service.model.Conversation;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ConversationRepository extends MongoRepository<Conversation, String> {
}
