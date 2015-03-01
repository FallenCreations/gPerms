/*
 * Copyright 2015 Goblom.
 * 
 * All Rights Reserved unless otherwise explicitly stated.
 */
package codes.goblom.gperms.commands;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Goblom
 */
@AllArgsConstructor
public class CommandHandler {
    @Setter
    private String prefix;
        
    @Getter
    private final CommandSender commandSender;
    
    @Getter
    private final String[] args;
    
    public String getArg(int index) {
        try {
            return args[index + 1];
        } catch (Exception e) { }
        return "";
    }
    
    public boolean getBoolean(int index, boolean def) {
        try {
            return Boolean.valueOf(getArg(index));
        } catch (Exception e) { }
        
        return def;
    }
    
    public int getArgsLength() {
        return getArgs().length - 1;
    }
    
    public String combine(int start, char spacer) {
        StringBuilder sb = new StringBuilder();
        
        for (int i = start; i < getArgsLength(); i++) {
            sb.append(getArg(i)).append(spacer);
        }
        
        return sb.toString();
    }
    
    public boolean isSenderPlayer() {
        return commandSender instanceof Player;
    }
    
    public Player getPlayer() {
        return (Player) commandSender;
    }
    
    public void reply(String message, Object... info) {
        String p = prefix == null ? "" : prefix;
        message = p + " " + message;
        
        getCommandSender().sendMessage(ChatColor.translateAlternateColorCodes('&', String.format(message, info)));
    } 
}
