package org.evabirds.evabirds.dto;

import org.evabirds.evabirds.entity.Message;

import java.time.LocalDateTime;

public class MessageResponse {

    private Long id;
    private Long senderId;
    private String senderName;
    private String senderEmail;
    private Long receiverId;
    private String receiverName;
    private String receiverEmail;
    private Long farmId;
    private String farmName;
    private String content;
    private boolean isRead;
    private LocalDateTime createdAt;

    public MessageResponse() {
    }

    public static MessageResponse fromEntity(Message message) {
        if (message == null) {
            return null;
        }

        MessageResponse response = new MessageResponse();
        response.setId(message.getId());

        if (message.getSender() != null) {
            response.setSenderId(message.getSender().getId());
            response.setSenderEmail(message.getSender().getEmail());
            if (message.getSender().getProfile() != null) {
                response.setSenderName(message.getSender().getProfile().getFullName());
            }
        }

        if (message.getReceiver() != null) {
            response.setReceiverId(message.getReceiver().getId());
            response.setReceiverEmail(message.getReceiver().getEmail());
            if (message.getReceiver().getProfile() != null) {
                response.setReceiverName(message.getReceiver().getProfile().getFullName());
            }
        }

        if (message.getFarm() != null) {
            response.setFarmId(message.getFarm().getId());
            response.setFarmName(message.getFarm().getFarmName());
        }

        response.setContent(message.getContent());
        response.setRead(message.isRead());
        response.setCreatedAt(message.getCreatedAt());

        return response;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderEmail() {
        return senderEmail;
    }

    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
    }

    public Long getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getReceiverEmail() {
        return receiverEmail;
    }

    public void setReceiverEmail(String receiverEmail) {
        this.receiverEmail = receiverEmail;
    }

    public Long getFarmId() {
        return farmId;
    }

    public void setFarmId(Long farmId) {
        this.farmId = farmId;
    }

    public String getFarmName() {
        return farmName;
    }

    public void setFarmName(String farmName) {
        this.farmName = farmName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
