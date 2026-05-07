package com.ricedotwho.zero.ws;

import com.google.gson.JsonObject;
import com.ricedotwho.zero.ws.msg.*;
import com.ricedotwho.zero.ws.msg.cb.*;
import lombok.experimental.UtilityClass;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@UtilityClass
public class MessageRegistry {
    private final Map<String, Function<JsonObject, Message>> messages = new HashMap<>();

    public void register() {
        messages.put("chat", ChatMessage::new);
        messages.put("restart", RestartMessage::new);
        messages.put("config", ConfigMessage::new);
        messages.put("change_user", ChangeUserMessage::new);
        messages.put("whitelist", WhitelistMessage::new);
        messages.put("irc", IRCMessage::new);
        messages.put("update", UpdateMessage::new);
    }

    public Message getMessage(JsonObject object) {
        Function<JsonObject, Message> factory = messages.get(object.get("type").getAsString());
        if (factory == null) return null;
        return factory.apply(object);
    }
}
