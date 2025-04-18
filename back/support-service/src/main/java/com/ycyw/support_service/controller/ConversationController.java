package com.ycyw.support_service.controller;

import com.ycyw.support_service.model.Conversation;
import com.ycyw.support_service.model.Message;
import com.ycyw.support_service.repository.ConversationRepository;
import java.util.Optional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/conversations")
public class ConversationController {

    private final ConversationRepository repo;
    public ConversationController(ConversationRepository repo) {
        this.repo = repo;
    }

    @PostMapping
    public Conversation createConversation() {
        Conversation conv = new Conversation();
        return repo.save(conv);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Conversation> getConversation(@PathVariable String id) {
        Optional<Conversation> c = repo.findById(id);
        return c.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/messages")
    public ResponseEntity<Conversation> addMessage(
            @PathVariable String id,
            @RequestBody Message message) {

        return repo.findById(id)
            .map(conv -> {
                conv.getMessages().add(message);
                repo.save(conv);
                return ResponseEntity.ok(conv);
            })
            .orElse(ResponseEntity.notFound().build());
    }
}

