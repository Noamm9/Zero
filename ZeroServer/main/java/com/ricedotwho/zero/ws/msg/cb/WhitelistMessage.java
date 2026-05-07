package com.ricedotwho.zero.ws.msg.cb;

import com.google.gson.JsonObject;
import com.ricedotwho.zero.Zero;
import com.ricedotwho.zero.util.DataLoader;
import com.ricedotwho.zero.ws.msg.Message;

import java.net.http.WebSocket;
import java.util.UUID;

public class WhitelistMessage extends Message {
    private final boolean add;
    private final String uuid;

    public WhitelistMessage(JsonObject data) {
        this.add = data.get("add").getAsBoolean();
        this.uuid = data.get("uuid").getAsString();
    }

    @Override
    public String getType() {
        return "whitelist";
    }

    @Override
    public void handle(WebSocket ws) {
        if (this.add) {
            DataLoader.addWhitelist(this.uuid);
        } else {
            DataLoader.removeWhitelist(this.uuid);
        }
    }
}
