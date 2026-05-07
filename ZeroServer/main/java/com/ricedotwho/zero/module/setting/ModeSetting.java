package com.ricedotwho.zero.module.setting;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.Getter;

import java.util.List;

@Getter
public class ModeSetting extends Setting<String> {
    private final List<String> options;

    public ModeSetting(String name, String value, List<String> options) {
        super(name, value);
        this.options = options;
    }

    public boolean is(String... others) {
        for (String other : others) {
            if (this.value.equalsIgnoreCase(other)) return true;
        }
        return false;
    }

    public int getIndex() {
        return this.getOptions().indexOf(this.value);
    }

    public void setByIndex(int index) {
        if(this.getOptions().size() < index || index < 0) return;
        this.value = this.getOptions().get(index);
    }

    @Override
    public void loadFromJson(JsonObject obj) {
        this.setValue(obj.get("value").getAsString());
    }

    @Override
    public JsonObject getAsJson() {
        JsonObject obj = new JsonObject();
        obj.addProperty("type", "MODE");
        obj.addProperty("name", this.getName());
        obj.addProperty("description", "");
        obj.addProperty("value", this.value);

        JsonArray array = new JsonArray();
        for (String option : this.options) {
            array.add(option);
        }

        obj.add("options", array);

        return obj;
    }
}
