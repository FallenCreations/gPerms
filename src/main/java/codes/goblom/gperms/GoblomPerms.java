/*
 * Copyright 2015 Goblom.
 * 
 * All Rights Reserved unless otherwise explicitly stated.
 */
package codes.goblom.gperms;

import codes.goblom.gperms.commands.Executor;
import codes.goblom.gperms.defaults.GroupCommands;
import codes.goblom.gperms.defaults.PlayerCommands;
import codes.goblom.gperms.misc.Async;
import codes.goblom.gperms.models.GroupModel;
import codes.goblom.gperms.models.GroupPermissionModel;
import codes.goblom.gperms.models.PlayerGroupModel;
import codes.goblom.gperms.models.PlayerTransferModel;
import com.avaje.ebean.EbeanServer;
import java.util.Arrays;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Goblom
 */
public class GoblomPerms extends JavaPlugin {
    
    private Database db;
    
    @Override
    public void onEnable() {
        saveDefaultConfig();
        
        Async.runTask(() -> {
            GoblomPerms.this.db = new Database(GoblomPerms.this);
            GoblomPerms.this.db.prepare(getConfigOption("database", "gperms"), Arrays.asList(
                    GroupModel.class, GroupPermissionModel.class,
                    PlayerGroupModel.class, PlayerTransferModel.class
            ));
        }, (Throwable error) -> {
            boolean disable = false;
            if (error != null) {
                disable = true;
                getLogger().log(Level.SEVERE, "There was an error while trying to connect to the database.", error);
            }
            
            if (getDatabase() == null) {
                disable = true;
                getLogger().severe("We were unable to connect to the database! gPerms disabled!");
            }
            
            if (disable) {
                Bukkit.getPluginManager().disablePlugin(GoblomPerms.this);
            }
        });
        
        Executor exe = new Executor("[gPerms]"); //Color later
                 exe.addListener(new GroupCommands(this));
                 exe.addListener(new PlayerCommands(this));
                 
        PluginCommand gperms = getCommand("gperms");
                      gperms.setExecutor(exe);
                      gperms.setDescription("The gPerms main command");
                      gperms.setAliases(Arrays.asList("gp", "permissions", "perms"));
                      gperms.setPermissionMessage(ChatColor.RED + "You have insufficient permissions to use that command.");
                      gperms.setPermission(PermsEnum.COMMAND_USE.toString()); //Should i require a permission in order to use the command?
    }
    
    public final <T> T getConfigOption(String key, T def) {
        FileConfiguration config = getConfig();
        
        if (!config.contains(key)) {
            config.set(key, def);
            saveConfig();
            return def;
        }
        
        return (T) config.get(key);
    }
    
    @Override
    public EbeanServer getDatabase() {
        return db.getDatabase();
    }
}
