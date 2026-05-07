package com.ricedotwho.zero;

import com.ricedotwho.mcprotocol.protocol.MinecraftProtocol;
import com.ricedotwho.mcprotocol.protocol.net.client.MinecraftClient;
import com.ricedotwho.mcprotocol.protocol.net.ping.ServerPinger;
import com.ricedotwho.mcprotocol.protocol.packet.ingame.clientbound.ClientboundSystemChatPacket;
import com.ricedotwho.zero.module.Module;
import com.ricedotwho.zero.module.impl.*;
import com.ricedotwho.zero.module.impl.sequence.SequenceManager;
import com.ricedotwho.zero.module.impl.task.TickTask;
import com.ricedotwho.zero.util.DataLoader;
import com.ricedotwho.zero.ws.MessageRegistry;
import com.ricedotwho.zero.ws.SocketClient;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.geysermc.mcprotocollib.auth.GameProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class Zero {
    @Getter
    private static final Logger logger = LoggerFactory.getLogger(Zero.class);
    private static final List<Class<? extends Module>> MODULES = List.of(
            Config.class,
            Command.class,
            SequenceManager.class,
            Location.class,
            TickTask.class,
            PingCommand.class,
            ZeroPingTerms.class,
            BloodClipHelper.class,
            AutoTerms.class,
            ReAutoTerms.class,
            Auto4.class,
            AutoSS.class,
            CrystalAura.class,
            AutoTotem.class
    );

    @Getter
    private static final List<String> ignoredConfig = Arrays.asList("Config", "Command", "Location", "SequenceManager", "PingCommand", "TickTask");
    private static final MinecraftProtocol mc = new MinecraftProtocol();
    @Getter
    private static final SocketClient socketClient = new SocketClient();
    @Getter
    private static boolean needsUpdate = false;

    public static void onUpdated() {
        needsUpdate = true;

        if (MinecraftProtocol.getPlayerCount() == 0) {
            stopProxy();
        } else {
            Zero.getLogger().info("Update queued!");
        }
    }

    public static void main(String[] args) {
        DataLoader.loadData();
        MessageRegistry.register();
        startConsoleReader();
        // socketClient.start();
        try {
            mc.start(DataLoader.getData().getHostPort());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static void getModules(MinecraftClient proxy) {
        List<String> disabled = DataLoader.getData().getDisabled();
        for (Class<? extends Module> clazz : MODULES) {
            if (disabled.contains(clazz.getSimpleName())) continue;
            logger.info(clazz.getSimpleName());
            try {
                Module module = clazz.getDeclaredConstructor(MinecraftClient.class)
                        .newInstance(proxy);
                proxy.getMODULES().put(clazz, module);

            } catch (InstantiationException | IllegalAccessException |
                     InvocationTargetException | NoSuchMethodException e) {
                logger.error("Failed to instantiate {}!", clazz.getSimpleName(), e);
            }
        }

        proxy.getMODULES().values().forEach(o -> {
            if (!o.isCanDisable()) proxy.getEVENT_BUS().easyRegister(o);
        });
    }

    public static Component getPrefix() {
        return Component.empty()
                .append(Component.text("[").color(TextColor.color(0, 0, 0))
                        .append(Component.text("Z").color(TextColor.fromHexString("#FFAA00")))
                        .append(Component.text("e").color(TextColor.fromHexString("#FFB10F")))
                        .append(Component.text("r").color(TextColor.fromHexString("#FFB924")))
                        .append(Component.text("o").color(TextColor.fromHexString("#FFBF40")))
                        .append(Component.text("] ").color(TextColor.color(0, 0, 0))))
                        .append(Component.empty().color(NamedTextColor.WHITE));
    }

    private static void startConsoleReader() {
        Thread console = new Thread(() -> {
            Scanner scanner = new Scanner(System.in);

            while (true) {
                String[] line = scanner.nextLine().trim().toLowerCase().split(" ");

                switch (line[0]) {
                    case "help":
                        logger.info("Commands: help, stop, ping, online, broadcast, say");
                        break;
                    case "stop":
                        stopProxy();
                        break;
                    case "ping":
                        if (line.length != 2) {
                            logger.info("Incorrect usage: use like 'ping mc.hypixel.net'");
                            return;
                        }
                        logger.info("Pinging {}", line[1]);
                        ServerPinger pinger = getServerPinger(line);
                        pinger.ping();
                        pinger.disconnect();
                        break;
                    case "online", "playing": {
                        StringBuilder sb = new StringBuilder();
                        Map<GameProfile, MinecraftClient> online = MinecraftProtocol.getOnlinePlayers();
                        sb.append("Online: ").append(online.size());
                        for (Map.Entry<GameProfile, MinecraftClient> e : online.entrySet()) {
                            String ip = e.getValue().getInfo().getHost();
                            sb.append("\n").append(e.getKey().getName()).append(" (").append(e.getKey().getId()).append(") ").append("Server: ").append(ip);
                            if (ip.contains("hypixel")) {
                                sb.append(" Location: ").append(e.getValue().getArea().getName());
                            }
                        }
                        logger.info(sb.toString());
                    }
                        break;
                    case "broadcast", "b": {
                        if (line.length == 1) {
                            logger.warn("No message to broadcast!");
                            break;
                        }
                        StringBuilder sb = new StringBuilder();
                        for (int i = 1; i < line.length; i++) {
                            sb.append(line[i]).append(" ");
                        }
                        String message = sb.toString().trim();
                        broadcast(message);
                    }
                        break;
                    case "say", "s": {
                        if (line.length < 2) {
                            logger.warn("Missing recipient or message!");
                            break;
                        }
                        String recipient = line[1];
                        StringBuilder sb = new StringBuilder();
                        for (int i = 2; i < line.length; i++) {
                            sb.append(line[i]).append(" ");
                        }
                        String message = sb.toString().trim();
                        message(recipient, message);
                    }
                        break;
                    default:
                        logger.info("Unknown command {}", line[0]);
                }
            }
        });

        console.setDaemon(true);
        console.start();
    }

    private static ServerPinger getServerPinger(String[] line) {
        String[] split = line[1].split(":");
        String host = split[0];
        int port;
        if (split.length == 2) {
            try {
                port = Integer.parseInt(split[1]);
            } catch (NumberFormatException e) {
                port = 25565;
            }
        } else {
            port = 25565;
        }

        return new ServerPinger(host, port);
    }

    public static void broadcast(String message) {
        Component component = getPrefix().append(Component.text("Broadcast: " + message));
        Map<GameProfile, MinecraftClient> online = MinecraftProtocol.getOnlinePlayers();
        for (Map.Entry<GameProfile, MinecraftClient> e : online.entrySet()) {
            e.getValue().getSession().send(new ClientboundSystemChatPacket(component, false));
        }
        logger.info("Broadcast: {}", message);
    }

    public static void broadcastNoPrefix(String message) {
        Component component = getPrefix().append(Component.text(message));
        Map<GameProfile, MinecraftClient> online = MinecraftProtocol.getOnlinePlayers();
        for (Map.Entry<GameProfile, MinecraftClient> e : online.entrySet()) {
            e.getValue().getSession().send(new ClientboundSystemChatPacket(component, false));
        }
        logger.info("{}", message);
    }

    public static void message(String recipient, String message) {
        Map<GameProfile, MinecraftClient> online = MinecraftProtocol.getOnlinePlayers();
        Optional<GameProfile> profile = online.keySet().stream().filter(gp -> gp.getName().equalsIgnoreCase(recipient) || gp.getId().toString().equals(recipient)).findFirst();
        if (profile.isEmpty()) {
            logger.warn("No player found for: {}", recipient);
            return;
        }
        Component component = getPrefix().append(Component.text("From Console: " + message));
        online.get(profile.get()).getSession().send(new ClientboundSystemChatPacket(component, false));
        logger.info("To {}: {}", profile.get().getName(), message);
    }

    public static void stopProxy() {
        needsUpdate = false;
        logger.info("Stopping Zero");
        new Thread(() -> {
            mc.stop();
            System.exit(0);
        }, "shutdown-thread").start();
    }
}