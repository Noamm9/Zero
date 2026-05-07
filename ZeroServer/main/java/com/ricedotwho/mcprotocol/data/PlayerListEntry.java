package com.ricedotwho.mcprotocol.data;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import org.geysermc.mcprotocollib.auth.GameProfile;
import org.geysermc.mcprotocollib.protocol.data.game.entity.player.GameMode;
import org.jspecify.annotations.Nullable;

import java.security.PublicKey;
import java.util.Arrays;
import java.util.UUID;

@Setter
@Getter
public class PlayerListEntry {
    private @NonNull UUID profileId;
    private @Nullable GameProfile profile;
    @Getter
    private boolean listed;
    @Getter
    private int latency;
    @Getter
    private GameMode gameMode;
    private @Nullable Component displayName;
    private boolean showHat;
    private int listOrder;
    private UUID sessionId;
    private long expiresAt;
    private @Nullable PublicKey publicKey;
    private byte @Nullable [] keySignature;

    public PlayerListEntry(UUID profileId) {
        this(profileId, null, false, 0, GameMode.SURVIVAL, null, false, 0, null, 0L, null, null);
    }

    public boolean equals(final @Nullable Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof PlayerListEntry other)) {
            return false;
        } else {
            if (!other.canEqual(this)) {
                return false;
            } else if (this.isListed() != other.isListed()) {
                return false;
            } else if (this.getLatency() != other.getLatency()) {
                return false;
            } else if (this.isShowHat() != other.isShowHat()) {
                return false;
            } else if (this.getListOrder() != other.getListOrder()) {
                return false;
            } else if (this.getExpiresAt() != other.getExpiresAt()) {
                return false;
            } else {
                Object this$profileId = this.getProfileId();
                Object other$profileId = other.getProfileId();
                if (!this$profileId.equals(other$profileId)) {
                    return false;
                }

                Object this$profile = this.getProfile();
                Object other$profile = other.getProfile();
                if (this$profile == null) {
                    if (other$profile != null) {
                        return false;
                    }
                } else if (!this$profile.equals(other$profile)) {
                    return false;
                }

                Object this$gameMode = this.getGameMode();
                Object other$gameMode = other.getGameMode();
                if (this$gameMode == null) {
                    if (other$gameMode != null) {
                        return false;
                    }
                } else if (!this$gameMode.equals(other$gameMode)) {
                    return false;
                }

                Object this$displayName = this.getDisplayName();
                Object other$displayName = other.getDisplayName();
                if (this$displayName == null) {
                    if (other$displayName != null) {
                        return false;
                    }
                } else if (!this$displayName.equals(other$displayName)) {
                    return false;
                }

                Object this$sessionId = this.getSessionId();
                Object other$sessionId = other.getSessionId();
                if (this$sessionId == null) {
                    if (other$sessionId != null) {
                        return false;
                    }
                } else if (!this$sessionId.equals(other$sessionId)) {
                    return false;
                }

                Object this$publicKey = this.getPublicKey();
                Object other$publicKey = other.getPublicKey();
                if (this$publicKey == null) {
                    if (other$publicKey != null) {
                        return false;
                    }
                } else if (!this$publicKey.equals(other$publicKey)) {
                    return false;
                }

                if (!Arrays.equals(this.getKeySignature(), other.getKeySignature())) {
                    return false;
                } else {
                    return true;
                }
            }
        }
    }

    protected boolean canEqual(final @Nullable Object other) {
        return other instanceof PlayerListEntry;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + (this.isListed() ? 79 : 97);
        result = result * 59 + this.getLatency();
        result = result * 59 + (this.isShowHat() ? 79 : 97);
        result = result * 59 + this.getListOrder();
        long $expiresAt = this.getExpiresAt();
        result = result * 59 + (int)($expiresAt >>> 32 ^ $expiresAt);
        Object $profileId = this.getProfileId();
        result = result * 59 + ($profileId == null ? 43 : $profileId.hashCode());
        Object $profile = this.getProfile();
        result = result * 59 + ($profile == null ? 43 : $profile.hashCode());
        Object $gameMode = this.getGameMode();
        result = result * 59 + ($gameMode == null ? 43 : $gameMode.hashCode());
        Object $displayName = this.getDisplayName();
        result = result * 59 + ($displayName == null ? 43 : $displayName.hashCode());
        Object $sessionId = this.getSessionId();
        result = result * 59 + ($sessionId == null ? 43 : $sessionId.hashCode());
        Object $publicKey = this.getPublicKey();
        result = result * 59 + ($publicKey == null ? 43 : $publicKey.hashCode());
        result = result * 59 + Arrays.hashCode(this.getKeySignature());
        return result;
    }

    public @org.checkerframework.checker.nullness.qual.NonNull String toString() {
        String var10000 = String.valueOf(this.getProfileId());
        return "PlayerListEntry(profileId=" + var10000 + ", profile=" + this.getProfile() + ", listed=" + this.isListed() + ", latency=" + this.getLatency() + ", gameMode=" + this.getGameMode() + ", displayName=" + this.getDisplayName() + ", showHat=" + this.isShowHat() + ", listOrder=" + this.getListOrder() + ", sessionId=" + this.getSessionId() + ", expiresAt=" + this.getExpiresAt() + ", publicKey=" + this.getPublicKey() + ", keySignature=" + Arrays.toString(this.getKeySignature()) + ")";
    }

    public PlayerListEntry(final @NonNull UUID profileId, final @Nullable GameProfile profile, final boolean listed, final int latency, final GameMode gameMode, final @Nullable Component displayName, final boolean showHat, final int listOrder, final UUID sessionId, final long expiresAt, final @Nullable PublicKey publicKey, final byte @Nullable [] keySignature) {
        if (profileId == null) {
            throw new NullPointerException("profileId is marked non-null but is null");
        } else {
            this.profileId = profileId;
            this.profile = profile;
            this.listed = listed;
            this.latency = latency;
            this.gameMode = gameMode;
            this.displayName = displayName;
            this.showHat = showHat;
            this.listOrder = listOrder;
            this.sessionId = sessionId;
            this.expiresAt = expiresAt;
            this.publicKey = publicKey;
            this.keySignature = keySignature;
        }
    }
}