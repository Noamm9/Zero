package com.ricedotwho.zero.module.setting;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ricedotwho.zero.Zero;

import java.util.*;

public class MultiSetting extends Setting<Map<String, Boolean>> {
    public MultiSetting(String name, List<String> options) {
        super(name, new HashMap<>());
        Map<String, Boolean> values = new LinkedHashMap<>();
        for (String option : options) {
            values.put(option, false);
        }
        this.setValue(values);
    }

    public MultiSetting(String name, List<String> options, List<String> on) {
        super(name, new HashMap<>());
        Map<String, Boolean> values = new LinkedHashMap<>();
        for (String option : options) {
            values.put(option, on.contains(option));
        }
        this.setValue(values);
    }

    public boolean get(String key) {
        return this.getValue().getOrDefault(key, false);
    }

    public void set(String key, boolean value) {
        if (this.getValue().containsKey(key)) {
            this.getValue().put(key, value);
        }
    }

    public void toggle(String key) {
        if (this.getValue().containsKey(key)) {
            this.getValue().put(key, !this.getValue().get(key));
        }
    }

    public List<String> getValues() {
        List<String> enabled = new ArrayList<>();
        this.value.forEach((value, on) -> {
            if(on) enabled.add(value);
        });
        return enabled;
    }

    @Override
    public void loadFromJson(JsonObject obj) {
        JsonArray boolArray = obj.getAsJsonArray("options");
        for (JsonElement boolElement : boolArray) {
            JsonObject boolObj = boolElement.getAsJsonObject();
            String key = boolObj.get("name").getAsString();
            boolean value = boolObj.get("value").getAsBoolean();
            this.set(key, value);
        }
    }
    @Override
    public JsonObject getAsJson() {
        JsonObject obj = new JsonObject();
        obj.addProperty("type", "MULTI");
        obj.addProperty("name", this.getName());
        obj.addProperty("description", "");

        JsonArray array = new JsonArray();

        this.getValue().forEach((k, v) -> {
            JsonObject element = new JsonObject();
            element.addProperty("name", k);
            element.addProperty("value", v);
            array.add(element);
        });

        obj.add("options", array);

        return obj;
    }
}
