package com.ricedotwho.zero.ws.msg.sb;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.ricedotwho.zero.ws.SocketClient;
import com.ricedotwho.zero.ws.msg.Message;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class StatsMessage extends Message {
    private final int playerCount;
    private final List<String> servers;

    public StatsMessage(SocketClient.Stats stats) {
        this.playerCount = stats.online;
        this.servers = stats.servers;
    }

    @Override
    public String getType() {
        return "stats";
    }

    @Override
    public JsonObject build() {
        JsonObject obj = super.build();
        obj.addProperty("player_count", this.playerCount);

        JsonArray array = new JsonArray();
        this.servers.forEach(array::add);
        obj.add("servers", array);
        return obj;
    }
}
