package minecraft.deathchest.blouflash.org.deathchest;

import minecraft.deathchest.blouflash.org.deathchest.listeners.PlayerDeathListener;
import org.bukkit.plugin.java.JavaPlugin;

public final class Deathchest extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        new PlayerDeathListener(this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
