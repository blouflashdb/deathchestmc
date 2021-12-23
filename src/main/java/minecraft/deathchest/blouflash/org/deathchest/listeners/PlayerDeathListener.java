package minecraft.deathchest.blouflash.org.deathchest.listeners;

import minecraft.deathchest.blouflash.org.deathchest.Deathchest;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerDeathListener implements Listener {

    public PlayerDeathListener(Deathchest plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        if(event.getDrops().isEmpty()) {
            return;
        }

        org.bukkit.block.data.type.Chest chestData1;
        org.bukkit.block.data.type.Chest chestData2;

        Player player = event.getEntity().getPlayer();
        Location loc = player.getLocation();

        //Grabbing items from dead player!
        final ItemStack[] items = event.getDrops().toArray(new ItemStack[event.getDrops().size()]);

        //Getting location for the chest.
        final double x = loc.getX();

        final Location x1 = loc.clone();
        final Location x2 = loc.clone();

        x2.setX(x + 1);

        Block block1 = x1.getBlock();
        Block block2 = x2.getBlock();

        //Placing chests
        //Single chest
        block1.setType(Material.CHEST);
        if (items.length >= 27) {
            block2.setType(Material.CHEST);

            Chest chest1 = (Chest) block1.getState();
            Chest chest2 = (Chest) block2.getState();

            chestData1 = (org.bukkit.block.data.type.Chest) chest1.getBlockData();
            chestData2 = (org.bukkit.block.data.type.Chest) chest2.getBlockData();

            chestData1.setType(org.bukkit.block.data.type.Chest.Type.LEFT);
            block1.setBlockData(chestData1, true);
            chestData2.setType(org.bukkit.block.data.type.Chest.Type.RIGHT);
            block2.setBlockData(chestData2, true);
        }

        //Updating the chest
        Chest bChest = (Chest) x1.getBlock().getState();
        bChest.setCustomName(player.getName() + "'s death chest!");
        bChest.update();

        //Adding items to chest
        bChest.getInventory().setContents(items);

        //Clearing the drops. So it won't drop on the ground.
        event.getDrops().clear();

        //Sending messages to player. With chest location as well.
        player.sendRawMessage("You died.. Get to your chest! Quick! Before someone else gets it!");
        player.sendRawMessage("Coordinates for your chest: x: " + ChatColor.RED + loc.getBlockX() +
                ChatColor.GREEN + " y: " + ChatColor.RED + loc.getBlockY() + ChatColor.GREEN + " z: " + ChatColor.RED + loc.getBlockZ());
    }
}
