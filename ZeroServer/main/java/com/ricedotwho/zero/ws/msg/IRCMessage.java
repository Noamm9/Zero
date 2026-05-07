package com.ricedotwho.zero.ws.msg;

import com.google.gson.JsonObject;
import com.ricedotwho.zero.Zero;
import lombok.AllArgsConstructor;

import java.net.http.WebSocket;

public class IRCMessage extends Message {
    private final String content;
    private final String sender;

    public IRCMessage(JsonObject data) {
        this.content = data.get("content").getAsString();
        this.sender = data.get("sender").getAsString();
    }

    @Override
    public String getType() {
        return "irc";
    }

    @Override
    public void handle(WebSocket ws) {
        Zero.broadcastNoPrefix(this.sender + ": " + this.content);
    }

    @Override
    public JsonObject build() {
        JsonObject obj = super.build();
        obj.addProperty("content", content);
        obj.addProperty("sender", sender);
        return obj;
    }
}
