package com.kalianey.oxapp.models;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by kalianey on 13/08/2015.
 */
public class ModelMessage implements Serializable {

    private static final long sId = 1L;

    /* Required */
    String senderId;
    String senderDisplayName;
    Long timeStamp;
    Boolean isMediaMessage = false;
    Integer messageHash;
    String text;
    /**/

    String id;
    String conversationId;
    String imageUrl;
    String recipientId;
    //Boolean recipientRead;
    Integer recipientRead;

    String attachmentId;
    String downloadUrl;
    String fileExt;
    String fileName;
    String fileSize;
    Object attachment; // JSQMediaItem?

    public Object getAttachment() {
        return attachment;
    }

    public void setAttachment(Object attachment) {
        this.attachment = attachment;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getSenderDisplayName() {
        return senderDisplayName;
    }

    public void setSenderDisplayName(String senderDisplayName) {
        this.senderDisplayName = senderDisplayName;
    }

    public Long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public Boolean getIsMediaMessage() {
        return isMediaMessage;
    }

    public void setIsMediaMessage(Boolean isMediaMessage) {
        this.isMediaMessage = isMediaMessage;
    }

    public Integer getMessageHash() {
        return messageHash;
    }

    public void setMessageHash(Integer messageHash) {
        this.messageHash = messageHash;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(String recipientId) {
        this.recipientId = recipientId;
    }

//    public Boolean getRecipientRead() {
//        return recipientRead;
//    }

    public Integer getRecipientRead() {
        return recipientRead;
    }

//    public void setRecipientRead(Boolean recipientRead) {
//        this.recipientRead = recipientRead;
//    }

    public void setRecipientRead(Integer recipientRead) {
        this.recipientRead = recipientRead;
    }

    public String getAttachmentId() {
        return attachmentId;
    }

    public void setAttachmentId(String attachmentId) {
        this.attachmentId = attachmentId;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getFileExt() {
        return fileExt;
    }

    public void setFileExt(String fileExt) {
        this.fileExt = fileExt;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }


    //CHATT APP INTEGRATION

    /** The Constant STATUS_SENDING. */
    public static final int STATUS_SENDING = 0;

    /** The Constant STATUS_SENT. */
    public static final int STATUS_SENT = 1;

    /** The Constant STATUS_FAILED. */
    public static final int STATUS_FAILED = 2;
    /** The msg. */
    private String msg;

    /** The status. */
    private int status = STATUS_SENT;

    /** The date. */
    private Date date;

    /** The sender. */
    private String sender;

    /**
     * Instantiates a new conversation.
     *
     * @param msg
     *            the msg
     * @param date
     *            the date
     * @param sender
     *            the sender
     */
    public ModelMessage(String msg, Date date, String sender)
    {
        this.msg = msg;
        this.date = date;
        this.sender = sender;
    }

    /**
     * Instantiates a new conversation.
     */
    public ModelMessage()
    {
    }

    /**
     * Gets the msg.
     *
     * @return the msg
     */
    public String getMsg()
    {
        return msg;
    }

    /**
     * Sets the msg.
     *
     * @param msg
     *            the new msg
     */
    public void setMsg(String msg)
    {
        this.msg = msg;
    }

    /**
     * Checks if is sent.
     *
     * @return true, if is sent
     */
    public boolean isSent()
    {
        return false; //TODO
    }

    /**
     * Gets the date.
     *
     * @return the date
     */
    public Date getDate()
    {
        return date;
    }

    /**
     * Sets the date.
     *
     * @param date
     *            the new date
     */
    public void setDate(Date date)
    {
        this.date = date;
    }

    /**
     * Gets the sender.
     *
     * @return the sender
     */
    public String getSender()
    {
        return sender;
    }

    /**
     * Sets the sender.
     *
     * @param sender
     *            the new sender
     */
    public void setSender(String sender)
    {
        this.sender = sender;
    }

    /**
     * Gets the status.
     *
     * @return the status
     */
    public int getStatus()
    {
        return status;
    }

    /**
     * Sets the status.
     *
     * @param status
     *            the new status
     */
    public void setStatus(int status)
    {
        this.status = status;
    }


}
