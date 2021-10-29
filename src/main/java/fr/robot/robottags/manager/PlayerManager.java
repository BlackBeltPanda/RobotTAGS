package fr.robot.robottags.manager;

import fr.robot.robottags.Main;
import fr.robot.robottags.object.Tag;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class PlayerManager {

    public static HashMap<UUID, String> playerTags = new HashMap<>();

    public static boolean ENABLED_DEFAULT_TAG = false;
    public static String DEFAULT_TAG = "default";

    public static void init() {
        ENABLED_DEFAULT_TAG = ConfigManager.getConfig().get().getBoolean("default-tag.enabled");
        DEFAULT_TAG = ConfigManager.getConfig().get().getString("default-tag.tag");
    }

    public static void setTag(UUID playerUUID, String tagID) {
        playerTags.put(playerUUID, tagID);
    }

    public static void load(UUID playerUUID) {
        Main.sendDebug("Chargement du tag pour un joueur. (" + playerUUID.toString() + ")");

        String tagID = null;
        switch (StorageManager.getMode()) {
            case YML:
                tagID = ConfigManager.getDatabase().get().getString(playerUUID.toString());
                break;
            case MYSQL:
                tagID = MysqlManager.getTag(playerUUID);
                break;
        }

        if(tagID != null && TagManager.exist(tagID)) {
            setTag(playerUUID, tagID);
            Main.sendDebug("Le joueur possède le tag " + tagID + " (" + StorageManager.getMode() + ")");
        } else {
            Main.sendDebug("Le joueur n'a été vu avec aucun tag existant." +  " (" + StorageManager.getMode() + ")");
        }
    }

    public static void save(UUID playerUUID) {
        String tagID = getTagId(playerUUID);
        if(tagID == null) {
            Main.sendDebug("Le joueur avec l'UUID " + playerUUID + " ne possède pas de tag.");
            return;
        }
        switch (StorageManager.getMode()) {
            case YML:
                ConfigManager.getDatabase().get().set(playerUUID.toString(), tagID);
                ConfigManager.getDatabase().save();
                break;
            case MYSQL:
                MysqlManager.setTag(playerUUID, tagID);
                break;
        }
        Main.sendDebug("Le joueur avec l'UUID " + playerUUID + " a été sauvegardé avec le tag " + tagID + " + (" + StorageManager.getMode() + ")");
    }

    public static void clear(UUID playerUUID) {
        switch (StorageManager.getMode()) {
            case YML:
                ConfigManager.getDatabase().get().set(playerUUID.toString(), null);
                ConfigManager.getDatabase().save();
                break;
            case MYSQL:
                MysqlManager.setTag(playerUUID, null);
                break;
        }
        playerTags.remove(playerUUID);
    }

    public static void save(Player player) {
        save(player.getUniqueId());
    }

    public static String getTagId(UUID playerUUID) {
        if(playerTags.containsKey(playerUUID) && TagManager.exist(playerTags.get(playerUUID)))
            return playerTags.get(playerUUID);
        return null;
    }

    public static String getTagId(Player player) {
        return getTagId(player.getUniqueId());
    }

    public static Tag getTag(UUID playerUUD) {
        return TagManager.getTag(getTagId(playerUUD));
    }

    public static Tag getTag(Player player) {
        return getTag(player.getUniqueId());
    }

    public static void setTag(Player player, String tagID) {
        setTag(player.getUniqueId(), tagID);
    }

    public static void load(Player player) {
        load(player.getUniqueId());
    }
}
