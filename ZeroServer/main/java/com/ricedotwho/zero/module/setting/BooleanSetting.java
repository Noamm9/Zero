package com.ricedotwho.zero.module.setting;

import com.google.gson.JsonObject;

public class BooleanSetting extends Setting<Boolean> {

    public BooleanSetting(String name, boolean value) {
        super(name, value);
    }
    public void toggle() {
        this.value = !this.value;
    }

    @Override
    public void loadFromJson(JsonObject obj) {
        this.setValue(obj.get("value").getAsBoolean());
    }
    @Override
    public JsonObject getAsJson() {
        JsonObject obj = new JsonObject();
        obj.addProperty("type", "SWITCH");
        obj.addProperty("name", this.getName());
        obj.addProperty("description", "");
        obj.addProperty("value", this.value);
        return obj;
    }
}
