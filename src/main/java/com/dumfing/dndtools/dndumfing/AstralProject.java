package com.dumfing.dndtools.dndumfing;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.InternalStructure;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.utility.MinecraftReflection;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import lombok.AllArgsConstructor;
import net.minecraft.network.protocol.game.PacketPlayOutEntityHeadRotation;
import net.minecraft.network.protocol.game.PacketPlayOutNamedEntitySpawn;
import net.minecraft.network.protocol.game.PacketPlayOutPlayerInfo;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.network.PlayerConnection;
import net.minecraft.world.level.EnumGamemode;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_19_R1.CraftServer;
import org.bukkit.craftbukkit.v1_19_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import javax.swing.text.html.parser.Entity;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.random.RandomGenerator;

@AllArgsConstructor
public class AstralProject {
    Map<UUID, BodyInfo> playerBodies;
    @AllArgsConstructor
    static class BodyInfo {
        Integer entityId;
        double x, y, z;
        float yaw, pitch;
    }
    public void enableAstralProject(Player target){
        target.setGameMode(GameMode.SPECTATOR);
        UUID fakeUuid = UUID.randomUUID();
        int entityId = RandomGenerator.getDefault().nextInt();
        EntityPlayer sourceData = ((CraftPlayer) target).getHandle();
        Property textures = (Property)sourceData.getBukkitEntity().getProfile().getProperties().get("textures").toArray()[0];
        GameProfile gameProfile = new GameProfile(fakeUuid, target.getName());
        gameProfile.getProperties().put("textures", textures);

        PacketPlayOutPlayerInfo playerInfoPacket = new PacketPlayOutPlayerInfo(
                PacketPlayOutPlayerInfo.EnumPlayerInfoAction.a,
                new EntityPlayer(
                    ((CraftServer) Bukkit.getServer()).getServer(),
                    ((CraftWorld)target.getWorld()).getHandle(),
                        gameProfile,
                        null
                )
        );
        PlayerConnection c = ((CraftPlayer)target).getHandle().b;
        c.a(playerInfoPacket);
        Location targetLocation = target.getLocation();
        PacketContainer spawnPlayerPacket = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.NAMED_ENTITY_SPAWN);
        spawnPlayerPacket.getIntegers().write(0, entityId);
        spawnPlayerPacket.getUUIDs().write(0, fakeUuid);
        spawnPlayerPacket.getDoubles()
                .write(0, targetLocation.getX())
                .write(1, targetLocation.getY())
                .write(2, targetLocation.getZ());
        spawnPlayerPacket.getBytes()
                .write(0, (byte)((int)(targetLocation.getYaw() * 256.f / 360.f)))
                .write(1, (byte)((int)(targetLocation.getPitch() * 256.f / 360.f)));
        ProtocolLibrary.getProtocolManager().sendServerPacket(target, spawnPlayerPacket);

        PacketContainer rotateHeadPacket = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_HEAD_ROTATION);
        rotateHeadPacket.getIntegers().write(0, entityId);
        rotateHeadPacket.getBytes().write(0, (byte)((int)(targetLocation.getYaw() * 256.f / 360.f)));
        ProtocolLibrary.getProtocolManager().sendServerPacket(target, rotateHeadPacket);
        playerBodies.put(target.getUniqueId(), new BodyInfo(entityId, targetLocation.getX(), targetLocation.getY(), targetLocation.getZ(), targetLocation.getYaw(), targetLocation.getPitch()));
    }

    public void disableAstralProject(Player target){
        if(playerBodies.containsKey(target.getUniqueId())) {
            BodyInfo playerBody = playerBodies.get(target.getUniqueId());
            target.teleport(new Location(target.getWorld(), playerBody.x, playerBody.y, playerBody.z, playerBody.yaw, playerBody.pitch));
            PacketContainer hideBodyPacket = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_DESTROY);
            hideBodyPacket.getIntLists().write(0, List.of(playerBody.entityId));
            ProtocolLibrary.getProtocolManager().sendServerPacket(target, hideBodyPacket);
        }
        target.setGameMode(GameMode.ADVENTURE);
    }

}
