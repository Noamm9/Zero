package com.ricedotwho.zero.ws.msg.cb;

import com.google.gson.JsonObject;
import com.ricedotwho.zero.Zero;
import com.ricedotwho.zero.util.DataLoader;
import com.ricedotwho.zero.ws.msg.Message;

import java.net.http.WebSocket;

public class ChangeUserMessage extends Message {
    private final long userId;

    public ChangeUserMessage(JsonObject data) {
        this.userId = data.get("user_id").getAsLong();
    }

    @Override
    public String getType() {
        return "change_user";
    }

    @Override
    public void handle(WebSocket ws) {
        DataLoader.getData().setUserId(this.userId);
        DataLoader.save();
        Zero.getSocketClient().restart();
    }
}
