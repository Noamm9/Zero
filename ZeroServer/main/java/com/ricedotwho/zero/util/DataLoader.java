package com.ricedotwho.zero.util;

import com.google.gson.Gson;
import com.ricedotwho.mcprotocol.protocol.util.Util;
import com.ricedotwho.zero.Zero;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.UtilityClass;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

@UtilityClass
public class DataLoader {
    private final File file = new File("config.json");
    @Getter
    private Data data = new Data();
    @Getter
    private final Map<String, UUIDThing> whitelist = new HashMap<>();

    public void loadData() {
        if (!FileUtils.checkDir(file, new Data())) return;
        Gson gson = new Gson();

        try (FileReader reader = new FileReader(file)) {
            data = gson.fromJson(reader, Data.class);

            if (!data.isWhitelist()) return;
            for (String uuid : data.getUuids()) {
                whitelist.put(uuid, new UUIDThing(uuid));
            }
            Zero.getLogger().info("Whitelist loaded!");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void save() {
        FileUtils.writeJson(data, file);
    }

    public boolean isWhitelistedUUID(String uuid) {
        for (UUIDThing uuidThing : getWhitelist().values()) {
            if (uuidThing.getDashed().equals(uuid) || uuidThing.getStripped().equals(uuid)) return true;
        }
        return false;
    }

    public boolean isWhitelistedUUID(UUID uuid) {
        for (UUIDThing uuidThing : getWhitelist().values()) {
            if (uuidThing.getUuid().equals(uuid)) return true;
        }
        return false;
    }

    public void addWhitelist(String dashed) {
        whitelist.put(dashed, new UUIDThing(dashed));
        data.uuids.add(dashed);
        save();
    }

    public void removeWhitelist(String dashed) {
        whitelist.remove(dashed);
        data.uuids.remove(dashed);
        save();
    }

    @Setter
    @Getter
    public class Data {
        private int hostPort;
        private int maxPlayers;
        private String motd;
        private boolean whitelist;
        private List<String> uuids;
        private final List<String> disabled;
        private final int compressionThreshold;
        private final boolean validateDecompression;
        private final String webSocketAddress;
        private long userId;
        private String iconPng;
        private final String secret;
        public Data() {
            this.hostPort = 25565;
            this.maxPlayers = 5;
            this.motd = "Zero proxy server";
            this.whitelist = true;
            this.uuids = new ArrayList<>();
            this.disabled = List.of();
            this.compressionThreshold = 100;
            this.validateDecompression = false;
            this.webSocketAddress = "wss://ws.zeroproxy.net";
            this.userId = -1;
            this.iconPng = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAEAAAABACAIAAAAlC+aJAAAACXBIWXMAAAsTAAALEwEAmpwYAAAFzGlUWHRYTUw6Y29tLmFkb2JlLnhtcAAAAAAAPD94cGFja2V0IGJlZ2luPSLvu78iIGlkPSJXNU0wTXBDZWhpSHpyZVN6TlRjemtjOWQiPz4gPHg6eG1wbWV0YSB4bWxuczp4PSJhZG9iZTpuczptZXRhLyIgeDp4bXB0az0iQWRvYmUgWE1QIENvcmUgOS4xLWMwMDMgNzkuOTY5MGE4NywgMjAyNS8wMy8wNi0xOToxMjowMyAgICAgICAgIj4gPHJkZjpSREYgeG1sbnM6cmRmPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5LzAyLzIyLXJkZi1zeW50YXgtbnMjIj4gPHJkZjpEZXNjcmlwdGlvbiByZGY6YWJvdXQ9IiIgeG1sbnM6eG1wPSJodHRwOi8vbnMuYWRvYmUuY29tL3hhcC8xLjAvIiB4bWxuczpkYz0iaHR0cDovL3B1cmwub3JnL2RjL2VsZW1lbnRzLzEuMS8iIHhtbG5zOnBob3Rvc2hvcD0iaHR0cDovL25zLmFkb2JlLmNvbS9waG90b3Nob3AvMS4wLyIgeG1sbnM6eG1wTU09Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC9tbS8iIHhtbG5zOnN0RXZ0PSJodHRwOi8vbnMuYWRvYmUuY29tL3hhcC8xLjAvc1R5cGUvUmVzb3VyY2VFdmVudCMiIHhtcDpDcmVhdG9yVG9vbD0iQWRvYmUgUGhvdG9zaG9wIDI2LjExIChXaW5kb3dzKSIgeG1wOkNyZWF0ZURhdGU9IjIwMjYtMDItMjhUMjI6NTE6MjcrMTE6MDAiIHhtcDpNb2RpZnlEYXRlPSIyMDI2LTAzLTIyVDIwOjI1OjM4KzExOjAwIiB4bXA6TWV0YWRhdGFEYXRlPSIyMDI2LTAzLTIyVDIwOjI1OjM4KzExOjAwIiBkYzpmb3JtYXQ9ImltYWdlL3BuZyIgcGhvdG9zaG9wOkNvbG9yTW9kZT0iMyIgeG1wTU06SW5zdGFuY2VJRD0ieG1wLmlpZDoxMTZkNTEzOC01MWRlLTVjNDMtOGU2Ny1hYTZhYjNjNzAwMTIiIHhtcE1NOkRvY3VtZW50SUQ9ImFkb2JlOmRvY2lkOnBob3Rvc2hvcDpjY2YwNmRlZC0xNzI4LTFhNDgtYmJiNC00ZTRlNmNkNTk3OTIiIHhtcE1NOk9yaWdpbmFsRG9jdW1lbnRJRD0ieG1wLmRpZDo3YTA3NTQzYi0xNzhiLWQ5NGUtOGNhNS00ZGE0NzRhMmYzNmUiPiA8eG1wTU06SGlzdG9yeT4gPHJkZjpTZXE+IDxyZGY6bGkgc3RFdnQ6YWN0aW9uPSJjcmVhdGVkIiBzdEV2dDppbnN0YW5jZUlEPSJ4bXAuaWlkOjdhMDc1NDNiLTE3OGItZDk0ZS04Y2E1LTRkYTQ3NGEyZjM2ZSIgc3RFdnQ6d2hlbj0iMjAyNi0wMi0yOFQyMjo1MToyNysxMTowMCIgc3RFdnQ6c29mdHdhcmVBZ2VudD0iQWRvYmUgUGhvdG9zaG9wIDI2LjExIChXaW5kb3dzKSIvPiA8cmRmOmxpIHN0RXZ0OmFjdGlvbj0ic2F2ZWQiIHN0RXZ0Omluc3RhbmNlSUQ9InhtcC5paWQ6MTE2ZDUxMzgtNTFkZS01YzQzLThlNjctYWE2YWIzYzcwMDEyIiBzdEV2dDp3aGVuPSIyMDI2LTAzLTIyVDIwOjI1OjM4KzExOjAwIiBzdEV2dDpzb2Z0d2FyZUFnZW50PSJBZG9iZSBQaG90b3Nob3AgMjYuMTEgKFdpbmRvd3MpIiBzdEV2dDpjaGFuZ2VkPSIvIi8+IDwvcmRmOlNlcT4gPC94bXBNTTpIaXN0b3J5PiA8L3JkZjpEZXNjcmlwdGlvbj4gPC9yZGY6UkRGPiA8L3g6eG1wbWV0YT4gPD94cGFja2V0IGVuZD0iciI/PrTxmyYAAAE6SURBVGje7ZlRDoQwCES5/6l6s92/jXGrDjCUmgzfRnlTCtNqn5eHCUAAAhCAAAQgAAGsBRhj2CFoqdwGDQB/NZgZGASAk/BjDIq06wDCZeNK6Eq1LEBAeO7GSAFUbFmX/Deq2bbZI/L7ABZnj8jvAGiYUJhwtqf8v08/9oxNARxTcsP6OU7MQoDTYJ5GbGi4lt3wFyEZe41AvmtbtYeZrob3eSaAtyqmihJrzwEQdkEBAM6BhmXgHh2la/TGN3G+r1fMnBVzAFmBmPyLJjFkKqMfKgeAPH1is9W6UbCxZGSqOg/gNwBgj+IcKZEl/nccLN/GORNPMa5s0rObz0/JWElQvEC4dXIutvJOidOjO+9lGT6lDSDZfPoBWEbLXl0//QD6QyMAAQhAAAIQgAAEIAABCEAAAmgD+ALc+lR1gcF+NgAAAABJRU5ErkJggg==";
            this.secret = "";
        }

        public void setUserId(long newId) {
            this.userId = newId;
            Zero.getSocketClient().restart();
        }
    }
    @Getter
    public class UUIDThing {
        private final String dashed;
        private final String stripped;
        private final UUID uuid;
        public UUIDThing(String dashed) {
            this.dashed = dashed;
            this.stripped = dashed.replace("-", "");
            this.uuid = UUID.fromString(dashed);
        }
        @Override
        public String toString() {
            return "UUIDThing{" +
                    "dashed=" + this.dashed +
                    "stripped=" + this.stripped +
                    "uuid=" + this.uuid + "}";
        }
    }
}
