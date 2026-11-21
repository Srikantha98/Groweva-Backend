package org.evabirds.evabirds.service;

import org.evabirds.evabirds.dto.ConversationSummaryResponse;
import org.evabirds.evabirds.dto.MessageResponse;
import org.evabirds.evabirds.dto.SendMessageRequest;
import org.evabirds.evabirds.entity.Farm;
import org.evabirds.evabirds.entity.Message;
import org.evabirds.evabirds.entity.User;
import org.evabirds.evabirds.exception.ResourceNotFoundException;
import org.evabirds.evabirds.repository.FarmRepository;
import org.evabirds.evabirds.repository.MessageRepository;
import org.evabirds.evabirds.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final FarmRepository farmRepository;
    private final NotificationService notificationService;

    public MessageService(
            MessageRepository messageRepository,
            UserRepository userRepository,
            FarmRepository farmRepository,
            NotificationService notificationService
    ) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.farmRepository = farmRepository;
        this.notificationService = notificationService;
    }

    @Transactional
    public MessageResponse sendMessage(String senderEmail, SendMessageRequest request) {
        User sender = userRepository.findByEmailIgnoreCase(senderEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Sender not found with email: " + senderEmail));

        User receiver = userRepository.findById(request.getReceiverId())
                .orElseThrow(() -> new ResourceNotFoundException("Receiver not found with id: " + request.getReceiverId()));

        if (sender.getId().equals(receiver.getId())) {
            throw new IllegalArgumentException("You cannot send a message to yourself");
        }

        Farm farm = null;
        if (request.getFarmId() != null) {
            farm = farmRepository.findById(request.getFarmId())
                    .orElseThrow(() -> new ResourceNotFoundException("Farm not found with id: " + request.getFarmId()));
            if (farm.getOwner() != null && farm.getOwner().getId().equals(sender.getId())) {
                throw new IllegalArgumentException("You are the owner of this farm listing and cannot send messages to yourself");
            }
        }

        Message message = new Message(sender, receiver, farm, request.getContent());
        Message saved = messageRepository.save(message);

        // Generate real notification strictly for receiver (the target recipient)
        try {
            boolean isInquiry = request.getContent() != null && request.getContent().startsWith("[RENT INQUIRY");
            String title = isInquiry ? "New Rental Inquiry" : "New Message";
            String type = isInquiry ? "inquiry" : "alert";
            String senderName = (sender.getProfile() != null && sender.getProfile().getFullName() != null && !sender.getProfile().getFullName().isBlank())
                    ? sender.getProfile().getFullName()
                    : sender.getEmail().split("@")[0];
            String farmName = farm != null ? farm.getFarmName() : "Poultry Shed";

            String notifMsg = isInquiry
                    ? senderName + " submitted a rental inquiry for " + farmName
                    : senderName + ": " + (request.getContent().length() > 65 ? request.getContent().substring(0, 62) + "..." : request.getContent());

            String receiverLink = "/messages?otherUserId=" + sender.getId() + (farm != null ? "&farmId=" + farm.getId() : "");
            notificationService.createNotification(receiver, title, notifMsg, type, receiverLink);
        } catch (Exception e) {
            // Best-effort notification delivery
            System.err.println("Warning: Failed to create in-app notification: " + e.getMessage());
        }

        return MessageResponse.fromEntity(saved);
    }

    @Transactional
    public List<MessageResponse> getConversation(String userEmail, Long otherUserId) {
        User currentUser = userRepository.findByEmailIgnoreCase(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + userEmail));

        if (!userRepository.existsById(otherUserId)) {
            throw new ResourceNotFoundException("User not found with id: " + otherUserId);
        }

        // Mark all unread incoming messages from otherUser as read
        messageRepository.markConversationAsRead(currentUser.getId(), otherUserId);

        List<Message> conversation = messageRepository.findConversation(currentUser.getId(), otherUserId);

        return conversation.stream()
                .map(MessageResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ConversationSummaryResponse> getInbox(String userEmail) {
        User currentUser = userRepository.findByEmailIgnoreCase(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + userEmail));

        List<Message> allMessages = messageRepository.findAllUserMessages(currentUser.getId());
        Map<Long, ConversationSummaryResponse> summaryMap = new LinkedHashMap<>();

        for (Message m : allMessages) {
            boolean isSender = m.getSender().getId().equals(currentUser.getId());
            User otherUser = isSender ? m.getReceiver() : m.getSender();
            Long otherUserId = otherUser.getId();

            if (!summaryMap.containsKey(otherUserId)) {
                ConversationSummaryResponse summary = new ConversationSummaryResponse();
                summary.setOtherUserId(otherUserId);
                summary.setOtherUserEmail(otherUser.getEmail());
                if (otherUser.getProfile() != null) {
                    summary.setOtherUserName(otherUser.getProfile().getFullName());
                    summary.setOtherUserPhone(otherUser.getProfile().getPhone());
                }
                summary.setLastMessage(m.getContent());
                summary.setLastMessageTime(m.getCreatedAt());

                if (m.getFarm() != null) {
                    summary.setFarmId(m.getFarm().getId());
                    summary.setFarmName(m.getFarm().getFarmName());
                }

                summaryMap.put(otherUserId, summary);
            }

            // Count unread incoming messages
            if (!isSender && !m.isRead()) {
                ConversationSummaryResponse summary = summaryMap.get(otherUserId);
                summary.setUnreadCount(summary.getUnreadCount() + 1);
            }
        }

        return new ArrayList<>(summaryMap.values());
    }

    @Transactional
    public void markAsRead(Long messageId, String userEmail) {
        User currentUser = userRepository.findByEmailIgnoreCase(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + userEmail));

        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found with id: " + messageId));

        if (message.getReceiver().getId().equals(currentUser.getId())) {
            message.setRead(true);
            messageRepository.save(message);
        }
    }

    @Transactional(readOnly = true)
    public long getUnreadCount(String userEmail) {
        User currentUser = userRepository.findByEmailIgnoreCase(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + userEmail));

        return messageRepository.countUnreadMessages(currentUser.getId());
    }
}
