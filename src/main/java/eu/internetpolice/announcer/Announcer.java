package eu.internetpolice.announcer;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.List;

public class Announcer extends JavaPlugin {
    protected List<String> announcementMessages;
    protected long announcementInterval;
    protected boolean enabled;
    protected boolean random;
    private AnnouncerThread announcerThread;
    
    public Announcer() {
        this.announcerThread = new AnnouncerThread(this);
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        reloadConfiguration();
        final BukkitScheduler scheduler = getServer().getScheduler();
        scheduler.scheduleSyncRepeatingTask(this, announcerThread, announcementInterval * 20L, announcementInterval * 20L);
        final AnnouncerCommandExecutor announcerCommandExecutor = new AnnouncerCommandExecutor(this);
        getCommand("announce").setExecutor(announcerCommandExecutor);
    }

    @Override
    public void onDisable() {
        getServer().getScheduler().cancelTasks(this);
    }
    
    public void announce() {
        this.announcerThread.run();
    }
    
    public void announce(final int index) {
        announce(announcementMessages.get(index - 1));
    }
    
    public void announce(final String line) {
        final String[] messages = line.split("&n");
        for (String message : messages) {
            getServer().getOnlinePlayers().stream()
                .filter(player -> player.hasPermission("announcer.receiver"))
                .forEach(player -> getServer().dispatchCommand(getServer().getConsoleSender(),
                    "tellraw " + player.getName() + " " + message));
        }
    }
    
    public void saveConfiguration() {
        getConfig().set("announcement.messages", announcementMessages);
        getConfig().set("announcement.interval", announcementInterval);
        getConfig().set("announcement.enabled", enabled);
        getConfig().set("announcement.random", random);
        saveConfig();
    }
    
    public void reloadConfiguration() {
        reloadConfig();
        announcementMessages = getConfig().getStringList("announcement.messages");
        announcementInterval = getConfig().getInt("announcement.interval", 1000);
        enabled = getConfig().getBoolean("announcement.enabled", true);
        random = getConfig().getBoolean("announcement.random", false);
    }
    
    public void addAnnouncement(final String message) {
        announcementMessages.add(message);
        saveConfiguration();
    }
    
    public String getAnnouncement(final int index) {
        return announcementMessages.get(index - 1);
    }
    
    public int numberOfAnnouncements() {
        return announcementMessages.size();
    }
    
    public boolean isAnnouncerEnabled() {
        return enabled;
    }
    
    public void setAnnouncerEnabled(final boolean enabled) {
        this.enabled = enabled;
        saveConfiguration();
    }
    
    public boolean isRandom() {
        return random;
    }
}
