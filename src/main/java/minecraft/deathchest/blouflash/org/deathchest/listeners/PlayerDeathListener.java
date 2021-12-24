package minecraft.deathchest.blouflash.org.deathchest.listeners;

import minecraft.deathchest.blouflash.org.deathchest.Deathchest;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class PlayerDeathListener implements Listener {

    private final FileConfiguration _config;
    private final Plugin _plugin;

    public PlayerDeathListener(Deathchest plugin) {
        _plugin = plugin;
        _config = plugin.getConfig();
        _plugin.getServer().getPluginManager().registerEvents(this, _plugin);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        // Only do something if items would be dropped
        if(event.getDrops().isEmpty()) {
            return;
        }

        final Player player = event.getEntity().getPlayer();
        if(player == null) {
            return;
        }

        //Grabbing items from dead player!
        // IMPORTANT: make sure this gets declared before the drops in the event get cleared
        final ItemStack[] items = event.getDrops().toArray(new ItemStack[event.getDrops().size()]);

        final Location loc = player.getLocation();

        final class PlaceChestTask implements Runnable {
            @Override
            public void run() {
                //Getting location for the chest.
                final double x = loc.getX();

                final Location x1 = loc.clone();
                final Location x2 = loc.clone();

                x2.setX(x + 1);

                final Block block1 = x1.getBlock();
                final Block block2 = x2.getBlock();

                //Placing chests
                //Single chest
                block1.setType(Material.CHEST);
                //Double chest
                if (items.length >= 27) {
                    block2.setType(Material.CHEST);

                    final Chest chest1 = (Chest) block1.getState();
                    final Chest chest2 = (Chest) block2.getState();

                    final org.bukkit.block.data.type.Chest chestData1 = (org.bukkit.block.data.type.Chest) chest1.getBlockData();
                    final org.bukkit.block.data.type.Chest chestData2 = (org.bukkit.block.data.type.Chest) chest2.getBlockData();

                    chestData1.setType(org.bukkit.block.data.type.Chest.Type.LEFT);
                    block1.setBlockData(chestData1, true);
                    chestData2.setType(org.bukkit.block.data.type.Chest.Type.RIGHT);
                    block2.setBlockData(chestData2, true);
                }

                //Updating the chest's name
                final Chest bChest = (Chest) x1.getBlock().getState();
                final String chestName = _config.getString("chest-name");
                if(chestName != null && !chestName.isEmpty()) {
                    final String chestNameResolved = chestName.replace("%playername%", player.getName());
                    bChest.setCustomName(chestNameResolved);
                }
                bChest.update();

                //Adding items to chest
                bChest.getInventory().setContents(items);
            }
        }

        int chestSpawnDelay = _config.getInt("chest-spawn-delay");
        if(chestSpawnDelay > 0) {
            _plugin.getServer().getScheduler().scheduleSyncDelayedTask(_plugin, new PlaceChestTask(), chestSpawnDelay);
        } else {
            new PlaceChestTask().run();
        }

        //Clearing the drops. So it won't drop on the ground.
        event.getDrops().clear();

        //Sending messages to player. With chest location as well.
        String messageGeneral = _config.getString("information-message-general");
        if(messageGeneral != null && !messageGeneral.isEmpty()) {
            player.sendRawMessage(ChatColor.translateAlternateColorCodes('&', messageGeneral));
        }

        String messageLocation = _config.getString("information-message-location");
        if(messageLocation != null && !messageLocation.isEmpty()) {
            String messageLocationResolved = messageLocation
                    .replace("%x%", Integer.toString(loc.getBlockX()))
                    .replace("%y%", Integer.toString(loc.getBlockY()))
                    .replace("%z%", Integer.toString(loc.getBlockZ()));
            player.sendRawMessage(ChatColor.translateAlternateColorCodes('&',messageLocationResolved));
        }
    }
}
