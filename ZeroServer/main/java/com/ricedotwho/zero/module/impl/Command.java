package com.ricedotwho.zero.module.impl;

import com.ricedotwho.mcprotocol.protocol.net.client.MinecraftClient;
import com.ricedotwho.mcprotocol.protocol.net.registry.PacketDirection;
import com.ricedotwho.mcprotocol.protocol.packet.common.severbound.ServerboundCustomPayloadPacket;
import com.ricedotwho.mcprotocol.protocol.packet.ingame.clientbound.ClientboundSystemChatPacket;
import com.ricedotwho.mcprotocol.utils.ByteBufUtils;
import com.ricedotwho.zero.Zero;
import com.ricedotwho.zero.event.packet.PacketContext;
import com.ricedotwho.zero.event.packet.PacketEvent;
import com.ricedotwho.zero.module.Module;
import com.ricedotwho.zero.util.ChatUtil;
import com.ricedotwho.zero.util.command.CommandBase;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class Command extends Module {
    private static final Logger logger = LoggerFactory.getLogger(Command.class);
    private final Map<String, CommandBase> commands = new HashMap<>();
    private static final Key COMMAND_KEY = Key.key("zero", "command");

    public Command(MinecraftClient proxy) {
        super("Command", proxy);
        this.enabled = true;
        this.canDisable = false;

        this.register("help", new CommandBase("Shows this", "", args -> {
            StringBuilder sb = new StringBuilder();
            commands.forEach((k, v) -> sb.append("\n").append("§f").append(k).append("§7 - ").append(v.description()));
            ChatUtil.prefix(this.getProxy(), "Commands:" + sb);
        }));

        this.register("c", new CommandBase("A command to chat with the console", "", args -> {
            StringBuilder sb = new StringBuilder();
            sb.append(this.getProxy().getProfile().getName()).append(": ");
            for (String s : args) {
                sb.append(s).append(" ");
            }
            String message = sb.toString().trim();
            logger.info(message);
            ChatUtil.prefix(this.getProxy(), message);
        }));
    }

    public void register(String name, CommandBase handler) {
        commands.put(name.toLowerCase(), handler);
    }

    @PacketEvent(direction = PacketDirection.SERVERBOUND, async = true)
    public void onCustomPayload(PacketContext<ServerboundCustomPayloadPacket> ctx) {
        ServerboundCustomPayloadPacket packet = ctx.getPacket();
        packet.lazyDecode();
        if (!packet.getChannel().equals(COMMAND_KEY)) return;

        String[] args = ByteBufUtils.readString(packet.getData()).split("\\s+");
        String name = args[0].toLowerCase();
        CommandBase commandBase = commands.get(name);

        if (commandBase == null) {
            ChatUtil.prefix(this.getProxy(), "§cUnknown Command (" + name + "). Try /zero help for help");
            return;
        }

        String[] remainingArgs = new String[args.length - 1];
        System.arraycopy(args, 1, remainingArgs, 0, remainingArgs.length);

        commandBase.handler().run(remainingArgs);
    }
}
