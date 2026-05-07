package com.ricedotwho.zero.module.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ricedotwho.mcprotocol.protocol.net.client.MinecraftClient;
import com.ricedotwho.mcprotocol.protocol.net.registry.PacketDirection;
import com.ricedotwho.mcprotocol.protocol.packet.common.clientbound.ClientboundCustomPayloadPacket;
import com.ricedotwho.mcprotocol.protocol.packet.common.severbound.ServerboundCustomPayloadPacket;
import com.ricedotwho.mcprotocol.utils.ByteBufUtils;
import com.ricedotwho.zero.event.packet.PacketContext;
import com.ricedotwho.zero.event.packet.PacketEvent;
import com.ricedotwho.zero.module.Module;
import net.kyori.adventure.key.Key;

import java.util.Optional;

public class Config extends Module {
    public Config(MinecraftClient proxy) {
        super("Config", proxy);
        this.enabled = true;
        this.canDisable = false;
    }

    @PacketEvent(direction = PacketDirection.SERVERBOUND, async = true)
    public void onCustomPayload(PacketContext<ServerboundCustomPayloadPacket> ctx) {
        ServerboundCustomPayloadPacket packet = ctx.getPacket();
        packet.lazyDecode();
        if (!packet.getChannel().namespace().equals("zero")) return;
        String value = packet.getChannel().value();

        switch (value) {
            case "config/provide":
                String jsonString = ByteBufUtils.readString(packet.getData());
                JsonArray array = JsonParser.parseString(jsonString).getAsJsonArray();

                for (JsonElement element : array) {
                    JsonObject obj = element.getAsJsonObject();
                    String name = obj.get("name").getAsString();
                    Optional<Module> optional = ctx.getProxy().getMODULES().values().stream().filter(m -> m.getName().equals(name)).findFirst();
                    optional.ifPresent(module -> {
                        boolean enabled = module.isEnabled();
                        module.loadConfig(obj);
                        if (module.isEnabled() != enabled && module.isCanDisable()) {
                            ctx.getProxy().getEVENT_BUS().easyRegister(module);
                        }
                    });
                }
                break;
            case "config/request":
                JsonArray config = this.getProxy().getConfig();
                byte[] bytes = ByteBufUtils.writeString(config.toString());
                this.getProxy().getSession().send(new ClientboundCustomPayloadPacket(Key.key("zero", "config/update"), bytes));
                break;
        }
    }
}
