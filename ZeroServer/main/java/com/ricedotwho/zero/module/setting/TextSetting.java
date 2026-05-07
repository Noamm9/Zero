package com.ricedotwho.zero.module.setting;

import com.google.gson.JsonObject;
import lombok.Getter;

@Getter
public class TextSetting extends Setting<String> {
    private boolean allowBlank;
    private boolean secure;
    public TextSetting(String name, String value, boolean allowBlank, boolean secure) {
        super(name, value);
        this.allowBlank = allowBlank;
        this.secure = secure;
    }
    @Override
    public void loadFromJson(JsonObject obj) {
        this.setValue(obj.get("value").getAsString());
        this.allowBlank = obj.get("allowBlank").getAsBoolean();
        this.secure = obj.get("secure").getAsBoolean();
    }
    @Override
    public JsonObject getAsJson() {
        JsonObject obj = new JsonObject();
        obj.addProperty("type", "TEXT");
        obj.addProperty("name", this.getName());
        obj.addProperty("description", "");
        obj.addProperty("allowBlank", this.isAllowBlank());
        obj.addProperty("secure", this.isSecure());
        obj.addProperty("value", this.getValue());
        return obj;
    }
}
