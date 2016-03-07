/*
 * Copyright 2015 Goblom.
 * 
 * All Rights Reserved unless otherwise explicitly stated.
 */
package codes.goblom.gperms.defaults;

import codes.goblom.gperms.GoblomPerms;
import codes.goblom.gperms.PermsEnum;
import codes.goblom.gperms.commands.Command;
import codes.goblom.gperms.commands.CommandHandler;
import codes.goblom.gperms.commands.CommandListener;
import codes.goblom.gperms.misc.Async;
import codes.goblom.gperms.models.GroupModel;
import codes.goblom.gperms.models.PlayerGroupModel;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;

/**
 *
 * @author Goblom
 */
@RequiredArgsConstructor
public class PlayerCommands implements CommandListener {
    
    private final GoblomPerms system;
    
    @Command (
            alias = "sg",
            description = "Set a players group",
            minArgs = 2,
            usage = "[player] [group]",
            permissions = { PermsEnum.PLAYER_SETGROUP }
    )
    public void setGroup(CommandHandler handler) {
        String player = handler.getArg(0);
        String group = handler.getArg(1);
        
        Async.runTask(() -> {
            UUID uuid = null;
            try { uuid = UUID.fromString(player); } catch (Exception e) { }
            
            PlayerGroupModel model;
            GroupModel groupModel = system.getDatabase().find(GroupModel.class).where().idEq(group).findUnique();
            
            if (uuid == null ) {
                model = system.getDatabase().find(PlayerGroupModel.class).where().ieq("lastKnownName", player).findUnique();
            } else {
                model = system.getDatabase().find(PlayerGroupModel.class).where().idEq(uuid.toString()).findUnique();
            }
            
            if (model == null) {
                handler.reply(ChatColor.RED + "Error: That player has not logged in yet.");
            }
            
            if (groupModel == null) {
                handler.reply(ChatColor.RED + "Error: That group does not exist.");
            }
            
            model.setPermission_group(group);
            system.getDatabase().update(model);
            handler.reply(ChatColor.GREEN + "Successfully added %s to %s.", player, group);
        }, (Throwable error) -> { 
            if (error != null) {
                handler.reply("Error: %s", error.getMessage());
            }
        });
    }
}
