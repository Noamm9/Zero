package com.ricedotwho.zero.util;

import com.ricedotwho.mcprotocol.protocol.net.client.MinecraftClient;
import com.ricedotwho.mcprotocol.protocol.packet.ingame.clientbound.ClientboundSystemChatPacket;
import com.ricedotwho.mcprotocol.protocol.packet.ingame.clientbound.title.ClientboundSetSubtitleTextPacket;
import com.ricedotwho.mcprotocol.protocol.packet.ingame.clientbound.title.ClientboundSetTitleTextPacket;
import com.ricedotwho.mcprotocol.protocol.packet.ingame.clientbound.title.ClientboundSetTitlesAnimationPacket;
import com.ricedotwho.zero.Zero;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;

@UtilityClass
public class ChatUtil {
    public void prefix(MinecraftClient client, String message) {
        prefix(client, Component.text(message));
    }

    public void prefix(MinecraftClient client, Component message) {
        prefix(client, message, false);
    }

    public void prefix(MinecraftClient client, Component message, boolean overlay) {
        client.getSession().send(new ClientboundSystemChatPacket(Zero.getPrefix().append(message), overlay));
    }

    public void chat(MinecraftClient client, String message) {
        chat(client, Component.text(message));
    }

    public void chat(MinecraftClient client, Component message) {
        chat(client, message, false);
    }

    public void chat(MinecraftClient client, Component message, boolean overlay) {
        client.getSession().send(new ClientboundSystemChatPacket(message, overlay));
    }

    public String stripFormatting(String input) {
        return input.replaceAll("§.", "");
    }

    public String getContent(Component component) {
        StringBuilder result = new StringBuilder();

        if (component instanceof TextComponent text) {
            result.append(text.content());
        }
        if (component != null) {
            for (Component child : component.children()) {
                result.append(getContent(child));
            }
        }

        return result.toString();
    }

    public void sendTitle(MinecraftClient client, Component title, Component subtitle, int in, int stay, int out) {
        ClientboundSetTitlesAnimationPacket timesPacket = new ClientboundSetTitlesAnimationPacket(in, stay, out);
        ClientboundSetTitleTextPacket titlePacket = new ClientboundSetTitleTextPacket(title);
        ClientboundSetSubtitleTextPacket subtitlePacket = new ClientboundSetSubtitleTextPacket(subtitle);

        client.getSession().send(timesPacket);
        client.getSession().send(titlePacket);
        client.getSession().send(subtitlePacket);
    }
}
