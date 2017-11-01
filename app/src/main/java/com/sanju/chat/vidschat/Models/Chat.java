package com.sanju.chat.vidschat.Models;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * @author greg
 * @since 6/21/13
 */
public class Chat {

    public String message;
    public String author;
    public String timeStamp;
    public boolean isMine;
    public String photoUrl = "";
    public String name = "";

    // Required default constructor for Firebase object mapping
    @SuppressWarnings("unused")
    public Chat() {
    }

    public Chat(String message, String author, String timeStamp, boolean isMine) {
        this.message = message;
        this.author = author;
        this.timeStamp = timeStamp;
        this.isMine = isMine;
    }

    public String getMessage() {
        return message;
    }

    public String getAuthor() {
        return author;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("message", message);
        result.put("author", author);
        result.put("timeStamp", timeStamp);
        result.put("isMine", isMine);

        return result;
    }
}
