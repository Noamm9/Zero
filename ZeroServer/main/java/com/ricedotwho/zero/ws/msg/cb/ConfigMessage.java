package com.ricedotwho.zero.ws.msg.cb;

import com.google.gson.JsonObject;
import com.ricedotwho.zero.util.DataLoader;
import com.ricedotwho.zero.ws.msg.Message;

import java.net.http.WebSocket;

public class ConfigMessage extends Message {
    private final Integer hostPort;
    private final Integer maxPlayers;
    private final String motd;
    private final String iconPng;

    public ConfigMessage(JsonObject data) {
        this.hostPort = data.has("port") ? Integer.parseInt(data.get("port").getAsString()) : null;
        this.maxPlayers = data.has("max_players") ? Integer.parseInt(data.get("max_players").getAsString()) : null;
        this.motd = data.has("motd") ? data.get("motd").getAsString() : null;
        this.iconPng = data.has("iconPng") ? data.get("iconPng").getAsString() : null;
    }

    @Override
    public String getType() {
        return "config";
    }

    @Override
    public void handle(WebSocket ws) {
        DataLoader.Data d = DataLoader.getData();
        if (this.hostPort != null)
            d.setHostPort(this.hostPort);
        if (this.maxPlayers != null)
            d.setMaxPlayers(this.maxPlayers);
        if (this.motd != null)
            d.setMotd(this.motd);
        if (this.iconPng != null)
            d.setIconPng(this.iconPng);
        DataLoader.save();
    }
}
