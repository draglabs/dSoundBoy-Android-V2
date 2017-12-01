package com.draglabs.dsoundboy.dsoundboy.Models;

/**
 * <p>Holds an email to send for support</p>
 * <p>Created by davrukin on 8/14/17.</p>
 */

@SuppressWarnings("DefaultFileTemplate")
public class EmailModel {

    private String email;
    private String subject;
    private String message;

    /**
     * Constructor for the EmailModel
     * @param email the email address sending the email
     * @param subject the subject of the email
     * @param message the message of the email
     */
    public EmailModel(String email, String subject, String message) {
        this.email = email;
        this.subject = subject;
        this.message = message;
    }

    /**
     * Gets the email address
     * @return the email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email address
     * @param email the email address
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the subject
     * @return the subject
     */
    public String getSubject() {
        return subject;
    }

    /**
     * Sets the subject
     * @param subject the subject
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }

    /**
     * Gets the message
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the message
     * @param message the message
     */
    public void setMessage(String message) {
        this.message = message;
    }
}
