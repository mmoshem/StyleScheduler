package com.example.stylescheduler.Models;

public class NotificationData {
    private String title;
    private String body;
    private String recipientEmail;

    public NotificationData() {
        // Required empty constructor for Firebase
    }

    public NotificationData(String title, String body, String recipientEmail) {
        this.title = title;
        this.body = body;
        this.recipientEmail = recipientEmail;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public String getRecipientEmail() {
        return recipientEmail;
    }
}
