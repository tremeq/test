package net.devscape.project.supremechat.chathead;

import net.devscape.project.supremechat.SupremeChat;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Manages automatic resource pack distribution to players.
 *
 * The ChatHead feature requires a resource pack to render player heads properly.
 * This manager automatically sends the pack to players when they join the server.
 *
 * Features:
 * - Automatic pack sending on player join
 * - Respects server.properties resource-pack setting (won't override)
 * - Configurable URL and prompt message
 * - Optional SHA1 hash for verification
 * - Can be completely disabled in config
 *
 * @author SupremeChat
 * @version 1.0
 */
public class ResourcePackManager implements Listener {

    private final JavaPlugin plugin;
    private final boolean enabled;
    private final String resourcePackUrl;
    private final String resourcePackHash;  // SHA1, can be null
    private final String promptMessage;
    private final boolean forceResourcePack;

    /**
     * Creates a new ResourcePackManager.
     *
     * @param plugin The plugin instance
     */
    public ResourcePackManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.enabled = plugin.getConfig().getBoolean("chathead.resourcepack.auto-send", true);
        // Use default ChatHeadFont pack if no URL configured
        this.resourcePackUrl = plugin.getConfig().getString("chathead.resourcepack.url", SupremeChat.DEFAULT_RESOURCE_PACK);
        this.resourcePackHash = plugin.getConfig().getString("chathead.resourcepack.sha1", null);
        this.promptMessage = plugin.getConfig().getString("chathead.resourcepack.prompt",
            "ChatHead Resource Pack is required for displaying player heads in chat");
        this.forceResourcePack = plugin.getConfig().getBoolean("chathead.resourcepack.force", false);

        if (enabled) {
            plugin.getLogger().info("ChatHead resource pack manager initialized");
            plugin.getLogger().info("URL: " + resourcePackUrl);
            if (resourcePackUrl.equals(SupremeChat.DEFAULT_RESOURCE_PACK)) {
                plugin.getLogger().info("Using default ChatHeadFont pack (you can change this in config.yml)");
            }
            if (resourcePackHash != null && !resourcePackHash.isEmpty()) {
                plugin.getLogger().info("SHA1: " + resourcePackHash);
            } else {
                plugin.getLogger().info("No SHA1 hash configured - using URL-only mode");
            }
        } else {
            plugin.getLogger().info("ChatHead resource pack auto-send is DISABLED");
        }
    }

    /**
     * Handles player join event to send resource pack.
     * Uses LOWEST priority to run early, but after authentication.
     *
     * @param event The player join event
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!enabled) {
            return;
        }

        Player player = event.getPlayer();

        // Don't override resource pack set in server.properties
        // getServer().getResourcePack() returns the pack from server.properties
        if (!plugin.getServer().getResourcePack().isEmpty()) {
            plugin.getLogger().fine("Skipping resource pack send for " + player.getName() +
                " - server.properties pack is already set");
            return;
        }

        // Send the resource pack to the player
        sendResourcePack(player);
    }

    /**
     * Sends the resource pack to a player.
     * Uses the new 1.20.3+ API if available, falls back to legacy method.
     *
     * @param player The player to send the pack to
     */
    private void sendResourcePack(Player player) {
        try {
            // Try new 1.20.3+ API first (with UUID and prompt)
            if (hasNewResourcePackAPI()) {
                sendResourcePackNew(player);
            } else {
                // Fall back to older API
                sendResourcePackLegacy(player);
            }

            plugin.getLogger().fine("Sent resource pack to " + player.getName());

        } catch (Exception e) {
            plugin.getLogger().warning("Failed to send resource pack to " + player.getName() + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Checks if the new resource pack API is available (1.20.3+).
     *
     * @return true if new API is available
     */
    private boolean hasNewResourcePackAPI() {
        try {
            // Check if the new method exists
            Player.class.getMethod("setResourcePack", String.class, String.class, boolean.class,
                net.kyori.adventure.text.Component.class);
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    /**
     * Sends resource pack using new 1.20.3+ API with Adventure components.
     *
     * @param player The player to send the pack to
     */
    private void sendResourcePackNew(Player player) {
        try {
            // Convert legacy color codes to Adventure component
            net.kyori.adventure.text.Component prompt =
                net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacySection()
                    .deserialize(promptMessage);

            if (resourcePackHash != null && !resourcePackHash.isEmpty()) {
                // With SHA1 hash verification
                player.setResourcePack(resourcePackUrl, resourcePackHash, forceResourcePack, prompt);
            } else {
                // Without SHA1 hash
                player.setResourcePack(resourcePackUrl, (String) null, forceResourcePack, prompt);
            }
        } catch (Exception e) {
            // If Adventure API fails, fall back to legacy
            plugin.getLogger().warning("Failed to use new resource pack API, falling back to legacy");
            sendResourcePackLegacy(player);
        }
    }

    /**
     * Sends resource pack using legacy API (pre-1.20.3).
     *
     * @param player The player to send the pack to
     */
    private void sendResourcePackLegacy(Player player) {
        if (resourcePackHash != null && !resourcePackHash.isEmpty()) {
            // Try setResourcePack with hash (1.16+)
            try {
                player.setResourcePack(resourcePackUrl, resourcePackHash);
            } catch (NoSuchMethodError e) {
                // Very old version, just URL
                player.setResourcePack(resourcePackUrl);
            }
        } else {
            // Just URL, no hash
            player.setResourcePack(resourcePackUrl);
        }
    }

    /**
     * Checks if the resource pack manager is enabled.
     *
     * @return true if auto-sending is enabled
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Gets the configured resource pack URL.
     *
     * @return The resource pack URL
     */
    public String getResourcePackUrl() {
        return resourcePackUrl;
    }

    /**
     * Gets the configured SHA1 hash.
     *
     * @return The SHA1 hash, or null if not configured
     */
    public String getResourcePackHash() {
        return resourcePackHash;
    }

    /**
     * Checks if resource pack is forced (kicks players who decline).
     *
     * @return true if pack is required
     */
    public boolean isForceResourcePack() {
        return forceResourcePack;
    }
}
