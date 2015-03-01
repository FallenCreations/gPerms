/*
 * Copyright 2015 Goblom.
 * 
 * All Rights Reserved unless otherwise explicitly stated.
 */
package codes.goblom.gperms.defaults;

import codes.goblom.gperms.PermsEnum;
import codes.goblom.gperms.commands.Command;
import codes.goblom.gperms.commands.CommandHandler;
import codes.goblom.gperms.commands.CommandListener;
import org.bukkit.ChatColor;

/**
 *
 * @author Goblom
 */
public class PlayerCommands implements CommandListener {
    
    @Command (
            alias = "sg",
            description = "Set a players group",
            minArgs = 2,
            usage = "[player] [group]",
            permissions = { PermsEnum.PLAYER_SETGROUP }
    )
    
    public void setGroup(CommandHandler handler) {
        handler.reply(ChatColor.RED + "todo");
    }
}
