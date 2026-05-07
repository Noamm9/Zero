package com.ricedotwho.zero.module.impl;

import com.ricedotwho.mcprotocol.protocol.net.client.MinecraftClient;
import com.ricedotwho.mcprotocol.protocol.net.registry.PacketDirection;
import com.ricedotwho.mcprotocol.protocol.packet.ingame.clientbound.ClientboundRespawnPacket;
import com.ricedotwho.mcprotocol.protocol.packet.ingame.clientbound.entity.ClientboundAddEntityPacket;
import com.ricedotwho.mcprotocol.protocol.packet.ingame.clientbound.entity.ClientboundRemoveEntitiesPacket;
import com.ricedotwho.mcprotocol.protocol.packet.ingame.clientbound.level.ClientboundBlockUpdatePacket;
import com.ricedotwho.mcprotocol.protocol.packet.ingame.severbound.player.ServerboundInteractPacket;
import com.ricedotwho.mcprotocol.protocol.packet.ingame.severbound.player.ServerboundMovePlayerPosPacket;
import com.ricedotwho.mcprotocol.protocol.packet.ingame.severbound.player.ServerboundMovePlayerPosRotPacket;
import com.ricedotwho.mcprotocol.protocol.packet.ingame.severbound.player.ServerboundUseItemOnPacket;
import com.ricedotwho.mcprotocol.protocol.packet.login.clientbound.ClientboundLoginFinishedPacket;
import com.ricedotwho.zero.event.packet.PacketContext;
import com.ricedotwho.zero.event.packet.PacketEvent;
import com.ricedotwho.zero.module.Module;
import com.ricedotwho.zero.module.impl.sequence.SequenceManager;
import com.ricedotwho.zero.module.setting.NumberSetting;
import com.ricedotwho.zero.util.Blocks;
import com.ricedotwho.zero.util.Pos;
import org.cloudburstmc.math.vector.Vector3i;
import org.geysermc.mcprotocollib.protocol.data.game.entity.object.Direction;
import org.geysermc.mcprotocollib.protocol.data.game.entity.player.Hand;
import org.geysermc.mcprotocollib.protocol.data.game.entity.player.InteractAction;
import org.geysermc.mcprotocollib.protocol.data.game.entity.type.EntityType;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class CrystalAura extends Module {
    private final NumberSetting placeRange = new NumberSetting("Place Range", 5.0, 1, 6, 0.5);
    private final NumberSetting breakRange = new NumberSetting("Break Range", 5.0, 1, 6, 0.5);
    private final NumberSetting placeDelay = new NumberSetting("Place Delay", 100, 0, 500, 10);
    private final NumberSetting breakDelay = new NumberSetting("Break Delay", 50, 0, 500, 10);

    private final Map<Integer, Pos> crystals = new ConcurrentHashMap<>();
    private final Set<Pos> predictedCrystals = new HashSet<>();
    private final Pos playerPos = new Pos(0, 0, 0);
    private long lastPlace = 0;
    private long lastBreak = 0;

    public CrystalAura(MinecraftClient proxy) {
        super("CrystalAura", proxy);
        register(placeRange, breakRange, placeDelay, breakDelay);
    }

    private void reset() {
        crystals.clear();
        predictedCrystals.clear();
        playerPos.set(0, 0, 0);
        lastPlace = 0;
        lastBreak = 0;
    }

    @PacketEvent(direction = PacketDirection.CLIENTBOUND)
    public void onEntitySpawn(PacketContext<ClientboundAddEntityPacket> ctx) {
        if (!this.isEnabled()) return;
        ClientboundAddEntityPacket packet = ctx.getPacket();
        packet.lazyDecode();

        if (packet.getType() == EntityType.END_CRYSTAL) {
            Pos pos = new Pos(packet.getX(), packet.getY(), packet.getZ());
            crystals.put(packet.getEntityId(), pos);
            predictedCrystals.remove(pos.floor());
        }
    }

    @PacketEvent(direction = PacketDirection.CLIENTBOUND)
    public void onEntityRemove(PacketContext<ClientboundRemoveEntitiesPacket> ctx) {
        if (!this.isEnabled()) return;
        ClientboundRemoveEntitiesPacket packet = ctx.getPacket();
        packet.lazyDecode();

        for (int id : packet.getEntityIds()) {
            crystals.remove(id);
        }
    }

    @PacketEvent(direction = PacketDirection.CLIENTBOUND)
    public void onBlockUpdate(PacketContext<ClientboundBlockUpdatePacket> ctx) {
        if (!this.isEnabled()) return;
        ClientboundBlockUpdatePacket packet = ctx.getPacket();
        packet.lazyDecode();

        Pos pos = new Pos(packet.getEntry().getPosition());
        if (packet.getEntry().getBlock() == 0) {
            predictedCrystals.remove(pos);
        }
    }

    @PacketEvent(direction = PacketDirection.SERVERBOUND)
    public void onPosition(PacketContext<ServerboundMovePlayerPosPacket> ctx) {
        if (!this.isEnabled()) return;
        ServerboundMovePlayerPosPacket packet = ctx.getPacket();
        packet.lazyDecode();
        playerPos.set(packet.getX(), packet.getY(), packet.getZ());
        tick();
    }

    @PacketEvent(direction = PacketDirection.SERVERBOUND)
    public void onPositionRotation(PacketContext<ServerboundMovePlayerPosRotPacket> ctx) {
        if (!this.isEnabled()) return;
        ServerboundMovePlayerPosRotPacket packet = ctx.getPacket();
        packet.lazyDecode();
        playerPos.set(packet.getX(), packet.getY(), packet.getZ());
        tick();
    }

    private void tick() {
        breakCrystals();
        placeCrystals();
    }

    private void breakCrystals() {
        long now = System.currentTimeMillis();
        if (now - lastBreak < breakDelay.getValue().longValue()) return;

        double rangeSq = breakRange.getValue() * breakRange.getValue();

        for (Map.Entry<Integer, Pos> entry : crystals.entrySet()) {
            Pos crystalPos = entry.getValue();
            Pos diff = playerPos.subtract(crystalPos);
            double distSq = diff.x() * diff.x() + diff.y() * diff.y() + diff.z() * diff.z();

            if (distSq <= rangeSq) {
                attack(entry.getKey());
                lastBreak = now;
                return;
            }
        }
    }

    private void placeCrystals() {
        long now = System.currentTimeMillis();
        if (now - lastPlace < placeDelay.getValue().longValue()) return;

        List<Pos> placePositions = findPlacePositions();
        if (placePositions.isEmpty()) return;

        Pos bestPos = placePositions.get(0);
        placeCrystal(bestPos);
        lastPlace = now;
    }

    private List<Pos> findPlacePositions() {
        List<Pos> positions = new ArrayList<>();
        double rangeSq = placeRange.getValue() * placeRange.getValue();

        for (int x = -6; x <= 6; x++) {
            for (int y = -3; y <= 3; y++) {
                for (int z = -6; z <= 6; z++) {
                    Pos blockPos = playerPos.add(x, y, z).floor();
                    Pos diff = playerPos.subtract(blockPos);
                    double distSq = diff.x() * diff.x() + diff.y() * diff.y() + diff.z() * diff.z();

                    if (distSq > rangeSq) continue;
                    if (predictedCrystals.contains(blockPos)) continue;

                    positions.add(blockPos);
                }
            }
        }

        return positions;
    }

    private void placeCrystal(Pos pos) {
        Vector3i blockPos = Vector3i.from(pos.x(), pos.y(), pos.z());
        predictedCrystals.add(pos);
        
        SequenceManager.register(SequenceManager.State.USE, () -> 
            this.getProxy().getModule(SequenceManager.class).sendServerSequenced(sequence -> 
                new ServerboundUseItemOnPacket(blockPos, Direction.UP, Hand.MAIN_HAND, 0.5f, 1.0f, 0.5f, false, false, sequence)
            )
        );
    }

    private void attack(int entityId) {
        sendServer(new ServerboundInteractPacket(entityId, InteractAction.ATTACK, 0, 0, 0, null, false));
    }

    @PacketEvent(direction = PacketDirection.CLIENTBOUND)
    public void onRespawn(PacketContext<ClientboundRespawnPacket> ctx) {
        reset();
    }

    @PacketEvent(direction = PacketDirection.CLIENTBOUND)
    public void onLogin(PacketContext<ClientboundLoginFinishedPacket> ctx) {
        reset();
    }
}
