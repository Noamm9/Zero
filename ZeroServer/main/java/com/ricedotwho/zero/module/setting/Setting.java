package com.ricedotwho.zero.module.setting;

import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Setting<T> {
    private final String name;
    protected T value;

    public Setting(String name, T value) {
        this.name = name;
        this.value = value;
    }
    public void loadFromJson(JsonObject obj) {

    }
    public JsonObject getAsJson() {
        return null;
    }
}
