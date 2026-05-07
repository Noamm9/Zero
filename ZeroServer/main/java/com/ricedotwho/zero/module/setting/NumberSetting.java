package com.ricedotwho.zero.module.setting;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NumberSetting extends Setting<Double> {
    private double min;
    private double max;
    private double increment;

    public NumberSetting(String name, double value) {
        super(name, value);
        this.min = 0;
        this.max = 1;
        this.increment = 0.1;
    }
    public NumberSetting(String name, double value, double min, double max, double increment) {
        super(name, value);
        this.min = min;
        this.max = max;
        this.increment = increment;
    }
    public void setValue(Double value) {
        double rounded = Math.round((value / increment)) * increment;
        this.value = Math.max(min, Math.min(max, rounded));
    }
    @Override
    public void loadFromJson(JsonObject obj) {
        this.setMin(obj.get("min").getAsDouble());
        this.setMax(obj.get("max").getAsDouble());
        this.setIncrement(obj.get("increment").getAsDouble());
        this.setValue(obj.get("value").getAsDouble());
    }
    @Override
    public JsonObject getAsJson() {
        JsonObject obj = new JsonObject();
        obj.addProperty("type", "NUMBER");
        obj.addProperty("name", this.getName());
        obj.addProperty("description", "");
        obj.addProperty("max", this.getMax());
        obj.addProperty("min", this.getMin());
        obj.addProperty("increment", this.getIncrement());
        obj.addProperty("value", this.getValue());
        return obj;
    }
}
