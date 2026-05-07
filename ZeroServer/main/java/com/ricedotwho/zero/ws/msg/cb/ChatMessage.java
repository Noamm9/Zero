package com.ricedotwho.zero.ws.msg.cb;

import com.google.gson.JsonObject;
import com.ricedotwho.zero.Zero;
import com.ricedotwho.zero.ws.msg.Message;

import java.net.http.WebSocket;

public class ChatMessage extends Message {
    private final String content;
    private final String recipient;

    public ChatMessage(JsonObject data) {
        this.recipient = data.has("recipient") ? data.get("recipient").getAsString() : null;
        this.content = data.get("content").getAsString();
    }

    @Override
    public String getType() {
        return "chat";
    }

    @Override
    public void handle(WebSocket ws) {
        if (this.recipient == null) {
            Zero.broadcast(this.content);
        } else {
            Zero.message(this.recipient, this.content);
        }
    }
}
