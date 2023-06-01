package io.github.moonlightmaya.script.apis.entity;

import io.github.moonlightmaya.util.EntityUtils;
import io.github.moonlightmaya.util.NbtUtils;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.entity.player.PlayerEntity;
import petpet.external.PetPetReflector;
import petpet.external.PetPetWhitelist;
import petpet.lang.run.PetPetClass;
import petpet.lang.run.PetPetException;
import petpet.types.PetPetTable;

@PetPetWhitelist
public class PlayerAPI {

    /**
     * booleans
     */
    @PetPetWhitelist
    public static boolean hasCape(PlayerEntity player) {
        PlayerListEntry entry = EntityUtils.getPlayerListEntry(player.getUuid());
        return entry != null && entry.hasCape();
    }
    @PetPetWhitelist
    public static boolean hasSkin(PlayerEntity player) {
        PlayerListEntry entry = EntityUtils.getPlayerListEntry(player.getUuid());
        return entry != null && entry.hasSkinTexture();
    }
    @PetPetWhitelist
    public static boolean isCutie(PlayerEntity player) {
        return true;
    }
    @PetPetWhitelist
    public static boolean isSkinLayerVisible(PlayerEntity player, String part) {
        try {
            if (part.equalsIgnoreCase("left_pants") || part.equalsIgnoreCase("right_pants"))
                part += "_leg";
            return player.isPartVisible(PlayerModelPart.valueOf(part.toUpperCase()));
        } catch (Exception ignored) {
            throw new PetPetException("Invalid skin layer: " + part);
        }
    }
    @PetPetWhitelist
    public static boolean isFishing(PlayerEntity player) {
        return player.fishHook != null;
    }

    /**
     * numbers
     */
    @PetPetWhitelist
    public static double experienceLevel(PlayerEntity player) {
        return player.experienceLevel;
    }
    @PetPetWhitelist
    public static double experienceProgress(PlayerEntity player) {
        return player.experienceProgress;
    }
    @PetPetWhitelist
    public static double chargedAttackDelay(PlayerEntity player) {
        return player.getAttackCooldownProgressPerTick();
    }
    @PetPetWhitelist
    public static double food(PlayerEntity player) {
        return player.getHungerManager().getFoodLevel();
    }
    @PetPetWhitelist
    public static double saturation(PlayerEntity player) {
        return player.getHungerManager().getSaturationLevel();
    }
    @PetPetWhitelist
    public static double exhaustion(PlayerEntity player) {
        return player.getHungerManager().getExhaustion();
    }

    /**
     * other
     */
    @PetPetWhitelist
    public static String modelType(PlayerEntity player) {
        PlayerListEntry entry = EntityUtils.getPlayerListEntry(player.getUuid());
        return (entry != null ? entry.getModel() : DefaultSkinHelper.getModel(player.getUuid())).toUpperCase();
    }
    @PetPetWhitelist
    public static String gamemode(PlayerEntity player) {
        PlayerListEntry entry = EntityUtils.getPlayerListEntry(player.getUuid());
        return entry == null ? null : entry.getGameMode().name();
    }
    @PetPetWhitelist
    public static PetPetTable<String, Object> shoulderEntity(PlayerEntity player, boolean rightShoulder) {
        return NbtUtils.toPetPet(rightShoulder ? player.getShoulderEntityRight() : player.getShoulderEntityLeft());
    }

    //Extras

    @PetPetWhitelist
    public String __tostring(PlayerEntity player) {
        return player.getName().getString() + " (Player)";
    }

}
