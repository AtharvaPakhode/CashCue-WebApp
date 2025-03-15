package com.codelab.expensetracker.helper;


/**
 * This class represents a message with a content and a type.
 * It is typically used to represent a notification or message
 * that can be passed to the user, including information about the
 * message type (e.g., error, success, info) and the content of the message.
 */
public class CustomDisplayMessage {

    // Content of the message, typically the message text or description
    private String content;

    // Type of the message (e.g., success, error, info)
    private String type;

    /**
     * Constructor to initialize the message with content and type.
     *
     * @param content The message content.
     * @param type    The message type (e.g., success, error, info).
     */
    public CustomDisplayMessage(String content, String type) {
        this.content = content;
        this.type = type;
    }

    /**
     * Gets the content of the message.
     *
     * @return The content of the message.
     */
    public String getContent() {

        return content;
    }

    /**
     * Sets the content of the message.
     *
     * @param content The content to set for the message.
     */
    public void setContent(String content) {

        this.content = content;
    }

    /**
     * Gets the type of the message.
     *
     * @return The type of the message.
     */
    public String getType() {

        return type;
    }

    /**
     * Sets the type of the message.
     *
     * @param type The type of the message (e.g., success, error, info).
     */
    public void setType(String type) {

        this.type = type;
    }
}

