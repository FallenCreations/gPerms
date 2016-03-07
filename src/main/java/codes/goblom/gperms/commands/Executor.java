/*
 * Copyright 2015 Goblom.
 * 
 * All Rights Reserved unless otherwise explicitly stated.
 */
package codes.goblom.gperms.commands;

import codes.goblom.gperms.PermsEnum;
import com.google.common.collect.Lists;
import java.lang.reflect.Method;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 *
 * @author Goblom
 */
@RequiredArgsConstructor
public class Executor implements CommandExecutor {

    private final @NonNull String prefix;
    private final List<CommandData> commandMap = Lists.newArrayList();
    
    public void addListener(CommandListener listener) {
        Class<?> clazz = listener.getClass();
        
        while (clazz != null) {
            for (Method method : clazz.getMethods()) {
                Command subCommand = method.getAnnotation(Command.class);
                if (subCommand != null) {
                    commandMap.add(new CommandData(method.getName(), subCommand.alias(), method, listener));
                }
            }

            clazz = clazz.getSuperclass();
        }
    }
    
    private CommandData getDataForName(String name) {
        for (CommandData data : commandMap) {
            if (data.getLongName().equalsIgnoreCase(name) || data.getAlias().equalsIgnoreCase(name)) {
                return data;
            }
        }
        
        return null;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        if (args.length == 0 || (args.length >= 1 && args[0].equalsIgnoreCase("help"))) {
            try {
                if (args[0].equalsIgnoreCase("help") && !args[1].isEmpty()) {
                    CommandData data = getDataForName(args[1]);
                    Command cmd = data.getMethod().getAnnotation(Command.class);
                    
                    if (cmd.help().length != 0) {
                        sendMessage(sender, "Usage: /" + command.getName() + " " + args[1] + " " + cmd.usage());
                        for (String line : cmd.help()) {
                            sendMessage(sender, line);
                        }
                    } else {
                        sendMessage(sender, ChatColor.RED + "That command does not have any help.");
                    }
                }
                
                return true;
            } catch (Exception e) { }
            List<CommandData> commands = Lists.newArrayList();
            
            sendMessage(sender, ChatColor.GOLD + "Available Commands:");
            commandMap.stream().filter((data) -> !(commands.contains(data))).forEach((data) -> {
                Command cmd = data.getMethod().getAnnotation(Command.class);
                
                if (hasPermission(sender, cmd.permissions())) {
                    sendMessage(sender, "/" + command.getName() + " " + data.getMethod().getName().toLowerCase() /*(cmd.alias().isEmpty() ? data.getMethod().getName().toLowerCase() : cmd.alias())*/ + " " + cmd.usage() + ChatColor.BLUE + " " + cmd.description());
                }
            });
        } else {
            CommandData data = getDataForName(args[0]);
            if (data == null) {
                sendMessage(sender, ChatColor.RED + "Error: That command does not exist");
                return true;
            }
            
            Method method = data.getMethod();
            CommandHandler handler = new CommandHandler(prefix, sender, args);
            Command cmd = method.getAnnotation(Command.class);
            
            if (cmd.allowConsole() || handler.isSenderPlayer()) {
                if (hasPermission(sender, cmd.permissions())) {
                    if (args.length > cmd.minArgs()) {
                        try {
                            method.invoke(data.getListener(), handler);
                        } catch (Throwable t) {
                            sendMessage(sender, ChatColor.RED + "Error: " + t.getMessage());
                        }
                    } else {
                        sendMessage(sender, ChatColor.RED + "Error: Not enough arguments. Use /" + command.getName() + " for help");
                    }
                } else {
                    sendMessage(sender, ChatColor.RED + "Error: Invalid Permissions. Please contact your server admin if you feel this an error.");
                }
            } else {
                sendMessage(sender, ChatColor.RED + "Error: Only players can use this command.");
            }
            
        }
        
        return true;
    }
    
    public void sendMessage(CommandSender sender, String message) {
        sender.sendMessage(color(prefix + " " + message));
    }
    
    @AllArgsConstructor
    @Data
    private static class CommandData {
        private final @NonNull String longName;
        private final @NonNull String alias;
        private final @NonNull Method method;
        private final @NonNull CommandListener listener;
        
        @Override
        public int hashCode() {
            return (method.getName() + listener.getClass().getCanonicalName()).hashCode();
        }
        
        @Override
        public boolean equals(Object obj) {
            if (obj instanceof CommandData) {
                return this.hashCode() == obj.hashCode();
            }
            
            return false;
        }
        
        @Override
        public String toString() {
            return method.getDeclaringClass().getCanonicalName() + ":" + method.getName();
        }
    }
    
    private String color(String str) {
        return ChatColor.translateAlternateColorCodes('&', str);
    }
    
    private boolean hasPermission(CommandSender sender, PermsEnum[] permissions) {
        if (permissions.length == 0 || PermsEnum.ADMIN.has(sender)) return true;
        
        for (PermsEnum perm : permissions) {
            if (perm.has(sender)) {
                return true;
            }
        }
        
        return false;
    }
}
