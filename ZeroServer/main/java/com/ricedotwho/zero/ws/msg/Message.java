package com.ricedotwho.zero.ws.msg;

import com.google.gson.JsonObject;

import java.net.http.WebSocket;

public abstract class Message {
    public abstract String getType();

    public void handle(WebSocket ws) {

    }

    public JsonObject build() {
        JsonObject obj = new JsonObject();
        obj.addProperty("type", this.getType());
        return obj;
    }

    public String payload() {
        JsonObject obj = this.build();
        return obj == null ? null : obj.toString();
    }
}
