package org.evabirds.evabirds.controller;

import jakarta.validation.Valid;
import org.evabirds.evabirds.dto.ConversationSummaryResponse;
import org.evabirds.evabirds.dto.MessageResponse;
import org.evabirds.evabirds.dto.SendMessageRequest;
import org.evabirds.evabirds.service.MessageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping
    public ResponseEntity<MessageResponse> sendMessage(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody SendMessageRequest request
    ) {
        MessageResponse response = messageService.sendMessage(userDetails.getUsername(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/conversation/{otherUserId}")
    public ResponseEntity<List<MessageResponse>> getConversation(
            @PathVariable Long otherUserId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        List<MessageResponse> conversation = messageService.getConversation(userDetails.getUsername(), otherUserId);
        return ResponseEntity.ok(conversation);
    }

    @GetMapping("/inbox")
    public ResponseEntity<List<ConversationSummaryResponse>> getInbox(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        List<ConversationSummaryResponse> inbox = messageService.getInbox(userDetails.getUsername());
        return ResponseEntity.ok(inbox);
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Long>> getUnreadCount(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        long count = messageService.getUnreadCount(userDetails.getUsername());
        return ResponseEntity.ok(Collections.singletonMap("unreadCount", count));
    }

    @PatchMapping("/{messageId}/read")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void markAsRead(
            @PathVariable Long messageId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        messageService.markAsRead(messageId, userDetails.getUsername());
    }
}
