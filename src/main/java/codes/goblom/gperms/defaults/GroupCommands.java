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
import java.util.List;
import java.util.logging.Level;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;

/**
 *
 * @author Goblom
 */
@RequiredArgsConstructor
public class GroupCommands implements CommandListener {
    
    private final GoblomPerms system;
    
    @Command (
            alias = "cg",
            description = "Create a group if not already created",
            usage = "[name] (inherits)",
            minArgs = 1,
            permissions = { PermsEnum.GROUP_CREATE },
            help = {
                "The [name] is the name of the group {Required}",
                "The (inherits) is what the group inherits from"
            }
    )
    public void createGroup(final CommandHandler handler) {
        final String name = handler.getArg(0);
        final String inherits = handler.getArg(1);
        
        if (isNone(name)) {
            handler.reply(ChatColor.RED + "That name is invalid. Please choose another.");
            return;
        }
        
        Async.runTask(() -> {
            GroupModel model = system.getDatabase().find(GroupModel.class).where().idEq(name).findUnique();
            
            if (model == null) {
                model = new GroupModel();
                model.setGroupName(name);
                model.setInherits(isNone(inherits) ? "none" : inherits);
                
                system.getDatabase().save(model);
                
                boolean inh = !isNone(inherits);
                
                handler.reply(ChatColor.GREEN + "Successfully created group %s" + (inh ? " that inherits %s." : "."), name, inherits);
            } else {
                handler.reply(ChatColor.RED + "Group " + ChatColor.GOLD + "%s " + ChatColor.RED + " already exists.", name);
            }
        }, (Throwable thrown) -> {
            if (thrown != null) {
                handler.reply("Error creating group: %s", thrown.getMessage());
            }
        });
    }
    
    @Command (
            alias = "si",
            description = "Update a groups inheritance",
            usage = "[group] [inherits]",
            minArgs = 2,
            permissions = { PermsEnum.GROUP_EDIT }
    )
    public void setInherits(final CommandHandler handler) {
        final String group = handler.getArg(0);
        final String inherits = (handler.getArg(1).isEmpty() ? "none" : handler.getArg(1));
        
        Async.runTask(() -> {
            GroupModel model = system.getDatabase().find(GroupModel.class).where().idEq(group).findUnique();
            
            if (model == null) {
                handler.reply(ChatColor.RED + "Error: Group does not exist");
            } else {
                model.setInherits(inherits);
                system.getDatabase().update(model);
                
                handler.reply(ChatColor.GREEN + "Successfully made %s inherit %s", group, inherits);
            }
        }, (Throwable error) -> {
            if (error != null) {
                handler.reply("Error: %s", error.getMessage());
            }
        });
    }
    
    @Command (
            alias = "lg",
            description = "List all groups in the database",
            permissions = { PermsEnum.GROUP_LIST }
    )
    public void listGroups(final CommandHandler handler) {
        Async.runTask(() -> {
            List<GroupModel> list = system.getDatabase().find(GroupModel.class).findList();
            
            if (list.isEmpty()) {
                handler.reply(ChatColor.RED  + "Error: No groups in database.");
                return;
            }
            
            list.forEach((GroupModel model) -> {
                String msg = ChatColor.BLUE + "- " + ChatColor.GOLD + model.getGroupName() + ChatColor.BLUE + (!isNone(model.getInherits()) ? " inherits " + ChatColor.GREEN + model.getInherits() + ".": "");
                handler.reply(msg);
            });
        }, (Throwable error) -> {
            if (error != null) {
                handler.reply(ChatColor.RED + "There was an error while getting the list of groups. Please check console");
                system.getLogger().log(Level.SEVERE, "List group error", error);
            }
        });
    }
    
    private boolean isNone(String str) {
        return str.equalsIgnoreCase("none") || str.isEmpty();
    }
}
