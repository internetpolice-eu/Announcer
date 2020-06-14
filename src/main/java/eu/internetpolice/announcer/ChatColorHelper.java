package eu.internetpolice.announcer;

import org.bukkit.ChatColor;

public class ChatColorHelper
{
    public static String replaceColorCodes(String message) {
        ChatColor[] arrayOfChatColor;
        for (int j = (arrayOfChatColor = ChatColor.values()).length, i = 0; i < j; ++i) {
            final ChatColor color = arrayOfChatColor[i];
            message = message.replaceAll(String.format("&%c", color.getChar()), color.toString());
        }
        return message;
    }
}
