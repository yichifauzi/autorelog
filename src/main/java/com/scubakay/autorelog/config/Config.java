package com.scubakay.autorelog.config;

import com.scubakay.autorelog.AutoRelogClient;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;

import java.io.IOException;
import java.nio.file.Path;

public class Config {
    private final static int DEFAULT_DELAY = 60;
    private final static int DEFAULT_INTERVAL = 30;

    private int delay;
    private int interval;

    public int getDelay() {
        return delay;
    }

    public void setDelay(int interval) {
        this.delay = interval;
        save();
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
        save();
    }

    final HoconConfigurationLoader loader = HoconConfigurationLoader.builder()
            .path(Path.of("config/autorelog.conf"))
            .build();

    CommentedConfigurationNode root;

    public Config() {
        load();
    }

    private void load() {
        try {
            root = loader.load();
            if (root.empty()) {
                delay = DEFAULT_DELAY;
                interval = DEFAULT_INTERVAL;
                AutoRelogClient.LOGGER.info("No AutoRelog config found, loading default");
            } else {
                delay = root.node("delay").empty() ? DEFAULT_DELAY : root.node("delay").getInt();
                interval = root.node("interval").empty() ? DEFAULT_INTERVAL : root.node("interval").getInt();
                AutoRelogClient.LOGGER.info("Loaded AutoRelog config");
            }
            save();
        } catch (IOException e) {
            AutoRelogClient.LOGGER.info("An error occurred while loading AutoRelog configuration: " + e.getMessage());
            if (e.getCause() != null) {
                e.getCause().printStackTrace();
            }
        }
    }

    private void save() {
        try {
            root.node("delay").set(Integer.class, delay);
            root.node("interval").set(Integer.class, interval);
            loader.save(root);
        } catch (final ConfigurateException e) {
            AutoRelogClient.LOGGER.info("Unable to save your AutoRelog configuration! Sorry! " + e.getMessage());
        }
    }
}