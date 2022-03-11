package com.google.codelabs.mdc.java.shrine.network;

public class Message {

    public String message;
    public int isYourMessage;
    public String time;


    public Message(String message, int isYourMessage, String time){
        this.isYourMessage = isYourMessage;
        this.message = message;
        this.time = time;
    }

    @Override
    public boolean equals(Object object)
    {
        if (object != null && object instanceof Message) {
            return ((this.message.equals(((Message) object).getMessage())) && (this.time.equals(((Message) object).getTime())));
        }
        return false;
    }
    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getIsYourMessage() {
        return isYourMessage;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setIsYourMessage(int isYourMessage) {
        this.isYourMessage = isYourMessage;
    }

}
