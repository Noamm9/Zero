package com.ricedotwho.zero.ws.msg.cb;

import com.google.gson.JsonObject;
import com.ricedotwho.zero.Zero;
import com.ricedotwho.zero.ws.msg.Message;

import java.net.http.WebSocket;

public class RestartMessage extends Message {

    public RestartMessage(JsonObject data) {
    }

    @Override
    public String getType() {
        return "restart";
    }

    @Override
    public void handle(WebSocket ws) {
        Zero.stopProxy();
    }
}
