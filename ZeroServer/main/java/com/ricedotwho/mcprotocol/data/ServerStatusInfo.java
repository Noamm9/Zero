package com.ricedotwho.mcprotocol.data;

import lombok.*;
import net.kyori.adventure.text.Component;
import org.geysermc.mcprotocollib.protocol.data.status.PlayerInfo;
import org.geysermc.mcprotocollib.protocol.data.status.VersionInfo;
import org.jetbrains.annotations.Nullable;

@Data
@Setter(AccessLevel.NONE)
@AllArgsConstructor
public class ServerStatusInfo {
    private @NonNull Component description;
    private @Nullable PlayerInfo playerInfo;
    private @Nullable VersionInfo versionInfo;
    private String iconPng;
    private byte[] iconBytes;
    private boolean enforcesSecureChat;
}
