package me.declanmc96.announcer;

import org.bukkit.command.*;
import org.bukkit.*;

public class announcerCommandExecutor implements CommandExecutor
{
    private final announcer plugin;
    public boolean success;
    
    announcerCommandExecutor(final announcer plugin) {
        this.plugin = plugin;
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        boolean success = false;
        if (args.length == 0 || args[0].equalsIgnoreCase("version") || args[0].equalsIgnoreCase("info")) {
            success = this.onVersionCommand(sender, command, label, args);
        }
        else if ("help".equalsIgnoreCase(args[0])) {
            success = this.onHelpCommand(sender, command, label, args);
        }
        else if ("add".equalsIgnoreCase(args[0])) {
            success = this.onAddCommand(sender, command, label, args);
        }
        else if ("broadcast".equalsIgnoreCase(args[0]) || "now".equalsIgnoreCase(args[0])) {
            success = this.onBroadcastCommand(sender, command, label, args);
        }
        else if ("say".equalsIgnoreCase(args[0]) || "once".equalsIgnoreCase(args[0])) {
            success = this.onBroadcastOnceCommand(sender, command, label, args);
        }
        else if ("list".equalsIgnoreCase(args[0])) {
            success = this.onListCommand(sender, command, label, args);
        }
        else if ("delete".equalsIgnoreCase(args[0])) {
            success = this.onDeleteCommand(sender, command, label, args);
        }
        else if ("interval".equalsIgnoreCase(args[0])) {
            success = this.onIntervalCommand(sender, command, label, args);
        }
        else if ("prefix".equalsIgnoreCase(args[0])) {
            success = this.onPrefixCommand(sender, command, label, args);
        }
        else if ("random".equalsIgnoreCase(args[0])) {
            success = this.onRandomCommand(sender, command, label, args);
        }
        else if ("enable".equalsIgnoreCase(args[0])) {
            success = this.onEnableCommand(sender, command, label, args);
        }
        else {
            success = ("reload".equalsIgnoreCase(args[0]) && this.onReloadCommand(sender, command, label, args));
        }
        if (!success) {
            sender.sendMessage(ChatColor.RED + "Invalid arguments! " + "Use '/announce help' to get a list of valid commands.");
        }
        return true;
    }
    
    boolean onVersionCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        sender.sendMessage(String.format("%s === %s [Version %s] === ", ChatColor.LIGHT_PURPLE, this.plugin.getDescription().getName(), this.plugin.getDescription().getVersion()));
        sender.sendMessage(String.format("Author: %s", this.plugin.getDescription().getAuthors().get(0)));
        sender.sendMessage(String.format("Version: %s", this.plugin.getDescription().getVersion()));
        sender.sendMessage("Features:");
        sender.sendMessage("- InGame Configuration");
        sender.sendMessage("- Permissions Support");
        sender.sendMessage("");
        sender.sendMessage(ChatColor.GRAY + "Use '/announce help' to get a list of valid commands.");
        return true;
    }
    
    boolean onHelpCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        sender.sendMessage(String.format("%s === %s [Version %s] === ", ChatColor.LIGHT_PURPLE, this.plugin.getDescription().getName(), this.plugin.getDescription().getVersion()));
        if (sender.hasPermission("announcer.add")) {
            sender.sendMessage(ChatColor.GRAY + "/announce add <message>" + ChatColor.WHITE + " - Adds a new announcement");
        }
        if (sender.hasPermission("announcer.broadcast")) {
            sender.sendMessage(ChatColor.GRAY + "/announce broadcast [<index>]" + ChatColor.WHITE + " - Broadcast an announcement NOW (From the predefined list)");
        }
        if (sender.hasPermission("announcer.broadcast")) {
            sender.sendMessage(ChatColor.GRAY + "/announce say [<message>]" + ChatColor.WHITE + " - Broadcast an announcement NOW");
        }
        if (sender.hasPermission("announcer.delete")) {
            sender.sendMessage(ChatColor.GRAY + "/announce delete <index>" + ChatColor.WHITE + " - Removes the announcement with the passed index");
        }
        if (sender.hasPermission("announcer.moderate")) {
            sender.sendMessage(ChatColor.GRAY + "/announce enable [true|false]" + ChatColor.WHITE + " - Enables or disables the announcer.");
            sender.sendMessage(ChatColor.GRAY + "/announce interval <seconds>" + ChatColor.WHITE + " - Sets the seconds between the announcements.");
            sender.sendMessage(ChatColor.GRAY + "/announce prefix <message>" + ChatColor.WHITE + " - Sets the prefix for all announcements.");
            sender.sendMessage(ChatColor.GRAY + "/announce list" + ChatColor.WHITE + " - Lists all announcements");
            sender.sendMessage(ChatColor.GRAY + "/announce random [true|false]" + ChatColor.WHITE + " - Enables or disables the random announcing mode.");
        }
        if (sender.hasPermission("announcer.admin")) {
            sender.sendMessage(ChatColor.GRAY + "/announce reload" + ChatColor.WHITE + " - Reloads the config.yml");
        }
        return true;
    }
    
    boolean onAddCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (sender.hasPermission("announcer.add")) {
            if (args.length > 1) {
                final StringBuilder messageToAnnounce = new StringBuilder();
                for (int index = 1; index < args.length; ++index) {
                    messageToAnnounce.append(args[index]);
                    messageToAnnounce.append(" ");
                }
                this.plugin.addAnnouncement(messageToAnnounce.toString());
                sender.sendMessage(ChatColor.GREEN + "Added announcement successfully!");
            }
            else {
                sender.sendMessage(ChatColor.RED + "You need to pass a message to announce!");
            }
            return true;
        }
        return false;
    }
    
    boolean onBroadcastCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (sender.hasPermission("announcer.broadcast")) {
            if (args.length == 2) {
                try {
                    final int index = Integer.parseInt(args[1]);
                    if (index > 0 && index <= this.plugin.numberOfAnnouncements()) {
                        this.plugin.announce(index);
                    }
                    else {
                        sender.sendMessage(ChatColor.RED + "There isn't any announcement with the passed index!");
                        sender.sendMessage(ChatColor.RED + "Use '/announce list' to view all available announcements.");
                    }
                }
                catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.RED + "Index must be a integer!");
                }
            }
            else if (args.length == 1) {
                this.plugin.announce();
            }
            else {
                sender.sendMessage(ChatColor.RED + "Invalid number of arguments! Use /announce help to view the help!");
            }
            return true;
        }
        return false;
    }
    
    boolean onBroadcastOnceCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (sender.hasPermission("announcer.broadcast")) {
            if (args.length >= 2) {
                String toSend = "";
                for (int i = 1; args.length != i; ++i) {
                    toSend = String.valueOf(toSend) + args[i] + " ";
                }
                this.plugin.announce(toSend);
            }
            else {
                sender.sendMessage(ChatColor.RED + "No text to broadcast! Use /announce help to view the help!");
            }
            return true;
        }
        return false;
    }
    
    boolean onListCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (sender.hasPermission("announcer.moderate")) {
            if (args.length == 1 || args.length == 2) {
                int page = 1;
                if (args.length == 2) {
                    try {
                        page = Integer.parseInt(args[1]);
                    }
                    catch (NumberFormatException e) {
                        sender.sendMessage(ChatColor.RED + "Invalid page number!");
                    }
                }
                sender.sendMessage(ChatColor.GREEN + String.format(" === Announcements [Page %d/%d] ===", page, this.plugin.announcementMessages.size() / 7 + 1));
                final int indexStart = Math.abs(page - 1) * 7;
                for (int indexStop = Math.min(page * 7, this.plugin.announcementMessages.size()), index = indexStart + 1; index <= indexStop; ++index) {
                    sender.sendMessage(String.format("%d - %s", index, chatColorHelper.replaceColorCodes(this.plugin.getAnnouncement(index))));
                }
            }
            else {
                sender.sendMessage(ChatColor.RED + "Invalid number of arguments! Use '/announce help' to view the help.");
            }
            return true;
        }
        return false;
    }
    
    boolean onDeleteCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (sender.hasPermission("announcer.delete")) {
            if (args.length == 2) {
                try {
                    final int index = Integer.parseInt(args[1]);
                    if (index > 0 && index <= this.plugin.numberOfAnnouncements()) {
                        sender.sendMessage(String.format("%sRemoved announcement: '%s'", ChatColor.GREEN, this.plugin.getAnnouncement(index)));
                        this.plugin.removeAnnouncement(index);
                    }
                    else {
                        sender.sendMessage(ChatColor.RED + "There isn't any announcement with the passed index!");
                        sender.sendMessage(ChatColor.RED + "Use '/announce list' to view all available announcements.");
                    }
                }
                catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.RED + "Index must be a integer!");
                }
            }
            else {
                sender.sendMessage(ChatColor.RED + "Too many arguments! Use '/announce help' to view the help.");
            }
            return true;
        }
        return false;
    }
    
    boolean onIntervalCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (sender.hasPermission("announcer.moderate")) {
            if (args.length == 2) {
                try {
                    this.plugin.setAnnouncementInterval(Integer.parseInt(args[1]));
                    sender.sendMessage(ChatColor.GREEN + "Set interval of scheduled announcements successfully!");
                }
                catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.RED + "Interval must be a number!");
                }
                catch (ArithmeticException e2) {
                    sender.sendMessage(ChatColor.RED + "Interval must be greater than 0!");
                }
            }
            else if (args.length == 1) {
                sender.sendMessage(String.format("%sPeriod duration is %d", ChatColor.LIGHT_PURPLE, this.plugin.getAnnouncementInterval()));
            }
            else {
                sender.sendMessage(ChatColor.RED + "Too many arguments! Use '/announce help' to view the help!");
            }
            return true;
        }
        return false;
    }
    
    boolean onPrefixCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (sender.hasPermission("announcer.moderate")) {
            if (args.length > 1) {
                final StringBuilder prefixBuilder = new StringBuilder();
                for (int index = 1; index < args.length; ++index) {
                    prefixBuilder.append(args[index]);
                    prefixBuilder.append(" ");
                }
                this.plugin.setAnnouncementPrefix(prefixBuilder.toString());
                sender.sendMessage(ChatColor.GREEN + "Set prefix for all announcements successfully!");
            }
            else {
                sender.sendMessage(String.format("%sPrefix is %s", ChatColor.LIGHT_PURPLE, chatColorHelper.replaceColorCodes(this.plugin.getAnnouncementPrefix())));
            }
            return true;
        }
        return false;
    }
    
    boolean onRandomCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (sender.hasPermission("announcer.moderate")) {
            if (args.length == 2) {
                if ("true".equalsIgnoreCase(args[1])) {
                    this.plugin.setRandom(true);
                    sender.sendMessage(ChatColor.GREEN + "Random mode enabled!");
                }
                else if ("false".equalsIgnoreCase(args[1])) {
                    this.plugin.setRandom(false);
                    sender.sendMessage(ChatColor.GREEN + "Sequential mode enabled!");
                }
                else {
                    sender.sendMessage(ChatColor.RED + "Use true or false to enable or disable! " + "Use '/announce help' to view the help.");
                }
            }
            else if (args.length == 1) {
                if (this.plugin.isRandom()) {
                    sender.sendMessage(ChatColor.LIGHT_PURPLE + "Random mode is enabled.");
                }
                else {
                    sender.sendMessage(ChatColor.LIGHT_PURPLE + "Sequential mode is enabled.");
                }
            }
            else {
                sender.sendMessage(ChatColor.RED + "Invalid number of arguments! Use '/announce help' to view the help.");
            }
            return true;
        }
        return false;
    }
    
    boolean onEnableCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (sender.hasPermission("announcer.moderate")) {
            if (args.length == 2) {
                if ("true".equalsIgnoreCase(args[1])) {
                    this.plugin.setAnnouncerEnabled(true);
                    sender.sendMessage(ChatColor.GREEN + "Announcer enabled!");
                }
                else if ("false".equalsIgnoreCase(args[1])) {
                    this.plugin.setAnnouncerEnabled(false);
                    sender.sendMessage(ChatColor.GREEN + "Announcer disabled!");
                }
                else {
                    sender.sendMessage(ChatColor.RED + "Use ture or false to enable or disable! " + "Use '/announce help' to view the help.");
                }
            }
            else if (args.length == 1) {
                if (this.plugin.isRandom()) {
                    sender.sendMessage(ChatColor.LIGHT_PURPLE + "Announcer is enabled.");
                }
                else {
                    sender.sendMessage(ChatColor.LIGHT_PURPLE + "Announcer is disabled.");
                }
            }
            else {
                sender.sendMessage(ChatColor.RED + "Invalid number of arguments! Use '/announce help' to view the help.");
            }
            return true;
        }
        return false;
    }
    
    boolean onReloadCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (sender.hasPermission("announcer.moderate")) {
            if (args.length == 1) {
                this.plugin.reloadConfiguration();
                sender.sendMessage(ChatColor.LIGHT_PURPLE + "Configuration reloaded.");
            }
            else {
                sender.sendMessage(ChatColor.RED + "Any arguments needed! Use '/announce help' to view the help.");
            }
            return true;
        }
        return false;
    }
}
