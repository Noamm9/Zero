package com.ricedotwho.zero.ws.msg.cb;

import com.google.gson.JsonObject;
import com.ricedotwho.zero.Zero;
import com.ricedotwho.zero.ws.msg.Message;

import java.net.http.WebSocket;

public class UpdateMessage extends Message {

    public UpdateMessage(JsonObject data) {
    }

    @Override
    public String getType() {
        return "update";
    }

    @Override
    public void handle(WebSocket ws) {
        Zero.onUpdated();
    }
}
