package com.android.gudana.chatapp.models;

/**
 * This is a part of ChatApp Project (https://github.com/h01d/ChatApp)
 * Licensed under Apache License 2.0
 *
 * @author  Raf (https://github.com/h01d)
 * @version 1.1
 * @since   27/02/2018
 */

public class Message
{
    private String message;
    private String filename;
    private String type;
    private String from;
    private String to;
    private long timestamp;

    public Message()
    {

    }

    public Message(String message, String filename, String type, String from, String to, long timestamp)
    {
        this.message = message;
        this.type = type;
        this.from = from;
        this.to = to;
        this.timestamp = timestamp;
        this.filename = filename;
    }

    public String getMessage()
    {
        return message;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public String getFrom()
    {
        return from;
    }

    public void setFrom(String from)
    {
        this.from = from;
    }

    public String getTo()
    {
        return to;
    }

    public void setTo(String to)
    {
        this.to = to;
    }

    public long getTimestamp()
    {
        return timestamp;
    }

    public void setTimestamp(long timestamp)
    {
        this.timestamp = timestamp;
    }
}
