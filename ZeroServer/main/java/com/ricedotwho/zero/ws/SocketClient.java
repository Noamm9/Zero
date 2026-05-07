package com.ricedotwho.zero.ws;

import com.google.gson.JsonObject;
import com.ricedotwho.zero.Zero;
import com.ricedotwho.zero.util.DataLoader;
import com.ricedotwho.zero.util.FileUtils;
import com.ricedotwho.zero.ws.msg.Message;
import com.ricedotwho.zero.ws.msg.sb.StatsMessage;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class SocketClient {
    private WebSocket webSocket;
    private final AtomicBoolean running = new AtomicBoolean(true);
    private final AtomicBoolean scheduled = new AtomicBoolean(false);
    private static final ScheduledExecutorService exec = Executors.newScheduledThreadPool(2);
    private final Stats stats = new Stats();

    public void start() {
        new Thread(this::runLoop).start();
        exec.scheduleAtFixedRate(() -> {
            if (webSocket != null) {
                try {
                    webSocket.sendPing(ByteBuffer.wrap(new byte[]{1}));
                } catch (Exception e) {
                    forceReconnect();
                }
            }  else {
                forceReconnect();
            }
        }, 30, 30, TimeUnit.SECONDS);
    }

    private void forceReconnect() {
        Zero.getLogger().info("Attempting reconnect");
        try {
            if (webSocket != null) {
                webSocket.abort();
            }
            this.notify();
        } catch (Exception ignored) {

        }
    }

    public void restart() {
        webSocket.sendClose(WebSocket.NORMAL_CLOSURE, "Restarting")
                .thenRun(() -> {
                    Zero.getLogger().info("Restarting WS");
                    SocketClient.this.notify();
                });
    }

    private void runLoop() {
        HttpClient client = HttpClient.newHttpClient();

        while (running.get()) {
            try {
                String ts = String.valueOf(System.currentTimeMillis());
                String nonce = UUID.randomUUID().toString().replace("-", "");
                String body = getFirstMessage().toString();
                String msgStr = ts + nonce + body;

                String signature = hmac(msgStr.getBytes(StandardCharsets.UTF_8));

                if (signature == null) {
                    Zero.getLogger().error("Secret is not set!");
                    return;
                }

                webSocket = client.newWebSocketBuilder()
                        .header("X-Request-Nonce", nonce)
                        .header("X-Timestamp", ts)
                        .header("X-Signature", signature)
                        .buildAsync(URI.create(DataLoader.getData().getWebSocketAddress()), new Listener())
                        .join();

                synchronized (this) {
                    this.wait();
                }

            } catch (Exception e) {
                // Zero.getLogger().warn("WS Disconnected or connect failed: {}", e.getMessage());
                try {
                    Thread.sleep(30000);
                } catch (InterruptedException ignored) {

                }
            }
        }
    }

    private String hmac(byte[] msg) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            String secretString = DataLoader.getData().getSecret();
            if (secretString == null) {
                return null;
            }
            byte[] secret = Base64.getDecoder().decode(secretString);
            mac.init(new SecretKeySpec(secret, "HmacSHA256"));

            byte[] raw = mac.doFinal(msg);

            StringBuilder hex = new StringBuilder();
            for (byte b : raw) {
                hex.append(String.format("%02x", b));
            }

            return hex.toString();
        } catch (InvalidKeyException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private JsonObject getFirstMessage() {
        JsonObject obj = new JsonObject();
        obj.addProperty("user_id", DataLoader.getData().getUserId());
        return obj;
    }

    public void updateCount(int count) {
        stats.online = count;
        update();
    }

    public void addServer(String server) {
        stats.servers.add(server);
        update();
    }

    public void removeServer(String server) {
        stats.servers.remove(server);
        update();
    }

    private void update() {
        if (webSocket != null) {
            webSocket.sendText(new StatsMessage(stats).payload(), true);
        }
    }

    private void delayedNotify() {
        if (!scheduled.compareAndSet(false, true)) return;
        exec.schedule(() -> {
            synchronized (SocketClient.this) {
                scheduled.set(false);
                SocketClient.this.notify();
            }
        }, 30, TimeUnit.SECONDS);
    }

    public class Listener implements WebSocket.Listener {

        @Override
        public void onOpen(WebSocket webSocket) {
            Zero.getLogger().info("WS Connected!");
            webSocket.sendText(getFirstMessage().toString(), true);
            WebSocket.Listener.super.onOpen(webSocket);
        }

        @Override
        public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
            Zero.getLogger().warn("WS connection lost!");
            SocketClient.this.delayedNotify();
            return WebSocket.Listener.super.onClose(webSocket, statusCode, reason);
        }

        @Override
        public void onError(WebSocket webSocket, Throwable e) {
            Zero.getLogger().warn("WS Error: {}", e.getMessage());
            SocketClient.this.delayedNotify();
        }

        @Override
        public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
            try {
                JsonObject json = FileUtils.gson.fromJson(data.toString(), JsonObject.class);
                Message msg = MessageRegistry.getMessage(json);
                if (msg != null) msg.handle(webSocket);
            } catch (Exception e) {
                Zero.getLogger().warn("onText Exception: {}", e.getMessage());
            }
            return WebSocket.Listener.super.onText(webSocket, data, last);
        }
    }

    public static class Stats {
        public int online = 0;
        public List<String> servers = new ArrayList<>();
    }
}