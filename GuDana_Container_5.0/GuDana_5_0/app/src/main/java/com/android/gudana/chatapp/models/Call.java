package com.android.gudana.chatapp.models;



public class Call
{
    private String message;
    private int typing;
    private long timestamp, seen;
    private String Call_Type;
    private String Call_duration;
    private String CallAtribut ; // incomming  Call , Outgoing Call  , missed Call

    public Call()
    {


    }

    public Call(String message, int typing, long timestamp, long seen)
    {
        this.message = message;
        this.typing = typing;
        this.timestamp = timestamp;
        this.seen = seen;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    public int getTyping()
    {
        return typing;
    }

    public void setTyping(int typing)
    {
        this.typing = typing;
    }

    public long getTimestamp()
    {
        return timestamp;
    }

    public void setTimestamp(long timestamp)
    {
        this.timestamp = timestamp;
    }

    public long getSeen()
    {
        return seen;
    }

    public void setSeen(long seen)
    {
        this.seen = seen;
    }
}
