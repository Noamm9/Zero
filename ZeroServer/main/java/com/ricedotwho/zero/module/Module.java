package com.ricedotwho.zero.module;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ricedotwho.mcprotocol.protocol.net.client.MinecraftClient;
import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import com.ricedotwho.zero.module.setting.Setting;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Module {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Getter
    protected boolean enabled;
    @Getter
    protected boolean canDisable = true;
    @Getter
    private final List<Setting<?>> settings = new ArrayList<>();
    @Getter
    protected final String name;
    @Getter
    private final MinecraftClient proxy;

    public Module(String name, MinecraftClient proxy) {
        this.name = name;
        this.proxy = proxy;
    }

    public void onEnable() {

    }

    public void onDisable() {

    }

    public void loadConfig(JsonObject object) {
        if (!object.get("name").getAsString().equals(this.getName())) return;
        this.enabled = object.get("enabled").getAsBoolean();

        JsonArray settingsArray = object.getAsJsonArray("settings");

        for (JsonElement element : settingsArray) {
            JsonObject settingJson = element.getAsJsonObject();
            String settingName = settingJson.get("name").getAsString();

            Setting<?> existing = getSettingByName(settingName);
            if (existing == null) {
                logger.warn("Unknown setting in config: {}, JSON: {}", settingName, settingJson);
                continue;
            }

            existing.loadFromJson(settingJson);
        }
    }

    public void register(Setting<?>... setting) {
        this.settings.addAll(Arrays.asList(setting));
    }
    public JsonObject getAsJson() {
        JsonObject obj = new JsonObject();
        obj.addProperty("name", this.getName());
        obj.addProperty("enabled", false);
        JsonArray array = new JsonArray();
        for (Setting<?> setting : this.settings) {
            JsonObject s = setting.getAsJson();
            if (s == null) continue;
            array.add(s);
        }
        obj.add("settings", array);
        return obj;
    }
    private Setting<?> getSettingByName(String name) {
        for (Setting<?> s : this.settings) {
            if (s.getName().equals(name)) {
                return s;
            }
        }
        return null;
    }
    protected void sendServer(Packet packet) {
        this.proxy.getRemoteSession().send(packet);
    }
    protected void sendClient(Packet packet) {
        this.proxy.getSession().send(packet);
    }
}
