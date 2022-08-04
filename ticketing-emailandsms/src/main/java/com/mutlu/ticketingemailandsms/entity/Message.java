package com.mutlu.ticketingemailandsms.entity;

public abstract class Message {
    private Long messageId;
    private String message;
    private String receiver;

    public Long getMessageId() {
        return messageId;
    }

    public Message setMessageId(Long messageId) {
        this.messageId = messageId;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public Message setMessage(String message) {
        this.message = message;
        return this;
    }

    public String getReceiver() {
        return receiver;
    }

    public Message setReceiver(String receiver) {
        this.receiver = receiver;
        return this;
    }
}
