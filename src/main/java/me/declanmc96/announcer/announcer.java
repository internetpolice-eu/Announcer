package me.declanmc96.announcer;

import org.bukkit.plugin.java.*;
import java.util.logging.*;
import org.bukkit.entity.*;
import java.util.*;
import org.bukkit.plugin.*;
import java.io.*;
import org.bukkit.scheduler.*;
import org.bukkit.event.*;
import org.bukkit.command.*;
import org.bukkit.*;
import org.bukkit.event.player.*;

public class announcer extends JavaPlugin implements Listener
{
    protected List<String> announcementMessages;
    protected String announcementPrefix;
    protected long announcementInterval;
    protected boolean enabled;
    protected boolean random;
    private announcerThread announcerThread;
    private Logger logger;
    public static boolean update;
    public static String name;
    public static long size;
    public List<Player> arrayOfPlayer;
    
    static {
        announcer.update = false;
        announcer.name = "";
        announcer.size = 0L;
    }
    
    public announcer() {
        this.arrayOfPlayer = new ArrayList<>();
        this.announcerThread = new announcerThread(this);
    }
    
    public void onEnable() {
        this.logger = this.getServer().getLogger();
        if (!new File(this.getDataFolder(), "config.yml").exists()) {
            this.saveDefaultConfig();
        }
        this.reloadConfiguration();
        final BukkitScheduler scheduler = this.getServer().getScheduler();
        scheduler.scheduleSyncRepeatingTask(this, this.announcerThread, this.announcementInterval * 20L, this.announcementInterval * 20L);
        final announcerCommandExecutor announcerCommandExecutor = new announcerCommandExecutor(this);
        this.getCommand("announce").setExecutor(announcerCommandExecutor);
        this.getCommand("announcer").setExecutor(announcerCommandExecutor);
        this.getCommand("acc").setExecutor(announcerCommandExecutor);
        this.getServer().getPluginManager().registerEvents(this, this);
        this.logger.info(String.format("%s is enabled!\n", this.getDescription().getFullName()));
    }
    
    public void onDisable() {
        this.logger.info(String.format("%s is disabled!\n", this.getDescription().getFullName()));
    }
    
    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        this.arrayOfPlayer.add(player);
    }
    
    public void announce() {
        this.announcerThread.run();
    }
    
    public void announce(final int index) {
        this.announce(this.announcementMessages.get(index - 1));
    }
    
    public void announce(final String line) {
        final String[] messages = line.split("&n");
        String[] arrayOfString1;
        for (int j = (arrayOfString1 = messages).length, i = 0; i < j; ++i) {
            final String message = arrayOfString1[i];
            if (message.startsWith("/")) {
                this.getServer().dispatchCommand((CommandSender)this.getServer().getConsoleSender(), message.substring(1));
            }
            else if (this.getServer().getOnlinePlayers().size() > 0) {
                String messageToSend = chatColorHelper.replaceColorCodes(String.format("%s%s", this.announcementPrefix, message));
                final int m = Bukkit.getOnlinePlayers().size();
                if (!this.arrayOfPlayer.isEmpty()) {
                    for (int k = 0; k < m; ++k) {
                        final Player player = this.arrayOfPlayer.get(k);
                        if (player.hasPermission("announcer.receiver")) {
                            if (messageToSend.contains("[player]")) {
                                messageToSend = messageToSend.replace("[player]", player.getDisplayName());
                            }
                            if (messageToSend.contains("[online]")) {
                                messageToSend = messageToSend.replace("[online]", String.valueOf(this.getServer().getOnlinePlayers().size()) + "/" + this.getServer().getMaxPlayers());
                            }
                            if (messageToSend.contains("[health]")) {
                                double hp2 = player.getHealth();
                                hp2 /= 2.0;
                                double hp3 = player.getMaxHealth();
                                hp3 /= 2.0;
                                messageToSend = messageToSend.replace("[health]", String.valueOf(hp2) + "/" + hp3);
                            }
                            if (!messageToSend.isEmpty()) {
                                player.sendMessage(messageToSend);
                            }
                        }
                    }
                }
            }
        }
    }
    
    public void saveConfiguration() {
        this.getConfig().set("announcement.messages", (Object)this.announcementMessages);
        this.getConfig().set("announcement.interval", (Object)this.announcementInterval);
        this.getConfig().set("announcement.prefix", (Object)this.announcementPrefix);
        this.getConfig().set("announcement.enabled", (Object)this.enabled);
        this.getConfig().set("announcement.random", (Object)this.random);
        this.saveConfig();
    }
    
    public void reloadConfiguration() {
        this.reloadConfig();
        this.announcementPrefix = this.getConfig().getString("announcement.prefix", "&c[Announcement] ");
        this.announcementMessages = (List<String>)this.getConfig().getStringList("announcement.messages");
        this.announcementInterval = this.getConfig().getInt("announcement.interval", 1000);
        this.enabled = this.getConfig().getBoolean("announcement.enabled", true);
        this.random = this.getConfig().getBoolean("announcement.random", false);
    }
    
    public String getAnnouncementPrefix() {
        return this.announcementPrefix;
    }
    
    public void setAnnouncementPrefix(final String announcementPrefix) {
        this.announcementPrefix = announcementPrefix;
        this.saveConfig();
    }
    
    public long getAnnouncementInterval() {
        return this.announcementInterval;
    }
    
    public void setAnnouncementInterval(final long announcementInterval) {
        this.announcementInterval = announcementInterval;
        this.saveConfiguration();
        final BukkitScheduler scheduler = this.getServer().getScheduler();
        scheduler.cancelTasks((Plugin)this);
        scheduler.scheduleSyncRepeatingTask((Plugin)this, (Runnable)this.announcerThread, announcementInterval * 20L, announcementInterval * 20L);
    }
    
    public void addAnnouncement(final String message) {
        this.announcementMessages.add(message);
        this.saveConfiguration();
    }
    
    public String getAnnouncement(final int index) {
        return this.announcementMessages.get(index - 1);
    }
    
    public int numberOfAnnouncements() {
        return this.announcementMessages.size();
    }
    
    public void removeAnnouncements() {
        this.announcementMessages.clear();
        this.saveConfiguration();
    }
    
    public void removeAnnouncement(final int index) {
        this.announcementMessages.remove(index - 1);
        this.saveConfiguration();
    }
    
    public boolean isAnnouncerEnabled() {
        return this.enabled;
    }
    
    public void setAnnouncerEnabled(final boolean enabled) {
        this.enabled = enabled;
        this.saveConfiguration();
    }
    
    public boolean isRandom() {
        return this.random;
    }
    
    public void setRandom(final boolean random) {
        this.random = random;
        this.saveConfiguration();
    }
    
    @EventHandler
    public void onLeave(final PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        if (this.arrayOfPlayer.contains(player)) {
            this.arrayOfPlayer.remove(player);
        }
    }
}
