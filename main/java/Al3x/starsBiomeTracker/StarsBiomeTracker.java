package Al3x.starsBiomeTracker;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.Map;

public class StarsBiomeTracker extends JavaPlugin {

    private Map<String, String> customNames = new HashMap<>();
    private BukkitRunnable biomeTask;
    private boolean debug;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadConfig();
        startBiomeTask();
        getLogger().info("StarsBiomeTracker включен!");
    }

    @Override
    public void onDisable() {
        if (biomeTask != null) biomeTask.cancel();
    }

    private void loadConfig() {
        FileConfiguration config = getConfig();
        debug = config.getBoolean("debug", false);

        if (config.contains("custom-names")) {
            for (String biomeKey : config.getConfigurationSection("custom-names").getKeys(false)) {
                String customName = config.getString("custom-names." + biomeKey);
                customNames.put(biomeKey, customName);
                if (debug) {
                    getLogger().info("Загружен биом: " + biomeKey + " -> " + customName);
                }
            }
        }
        if (debug) {
            getLogger().info("Всего загружено биомов: " + customNames.size());
        }
    }

    private void startBiomeTask() {
        biomeTask = new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    try {
                        Location loc = player.getLocation();
                        String fullBiomeName = loc.getBlock().getBiome().getKey().toString();

                        String displayText = customNames.get(fullBiomeName);

                        if (displayText != null) {
                            player.sendActionBar(
                                    net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacySection()
                                            .deserialize(displayText)
                            );
                        }

                    } catch (Exception e) {
                        if (debug) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        };
        biomeTask.runTaskTimer(this, 0L, getConfig().getInt("update-interval", 10));
    }
}