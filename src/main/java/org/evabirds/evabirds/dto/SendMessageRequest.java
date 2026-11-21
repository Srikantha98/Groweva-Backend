package org.evabirds.evabirds.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class SendMessageRequest {

    @NotNull(message = "Receiver ID is required")
    private Long receiverId;

    private Long farmId;

    @NotBlank(message = "Message content cannot be empty")
    @Size(max = 2000, message = "Message content must not exceed 2000 characters")
    private String content;

    public SendMessageRequest() {
    }

    public SendMessageRequest(Long receiverId, Long farmId, String content) {
        this.receiverId = receiverId;
        this.farmId = farmId;
        this.content = content;
    }

    public Long getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }

    public Long getFarmId() {
        return farmId;
    }

    public void setFarmId(Long farmId) {
        this.farmId = farmId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
