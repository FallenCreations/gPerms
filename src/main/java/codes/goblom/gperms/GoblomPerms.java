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
import codes.goblom.gperms.misc.Callback;
import codes.goblom.gperms.misc.EmptyCallback;
import codes.goblom.gperms.models.GroupModel;
import codes.goblom.gperms.models.GroupPermissionModel;
import codes.goblom.gperms.models.PlayerGroupModel;
import codes.goblom.gperms.models.PlayerTransferModel;
import com.avaje.ebean.EbeanServer;
import java.util.Arrays;
import java.util.Map;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Goblom
 */
public class GoblomPerms extends JavaPlugin implements Listener {
    
    private Database db;
    private Executor exe;
    
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
        
        Executor exe = this.exe = new Executor("[gPerms]"); //Color later
                 exe.addListener(new GroupCommands(this));
                 exe.addListener(new PlayerCommands(this));
                 
        PluginCommand gperms = getCommand("gperms");
                      gperms.setExecutor(exe);
                      gperms.setDescription("The gPerms main command");
                      gperms.setAliases(Arrays.asList("gp", "permissions", "perms"));
                      gperms.setPermissionMessage(ChatColor.RED + "You have insufficient permissions to use that command.");
                      gperms.setPermission(PermsEnum.COMMAND_USE.toString()); //Should i require a permission in order to use the command?
                      
        Bukkit.getPluginManager().registerEvents(this, this);
    }
    
    @EventHandler
    void onPlayerLogin(PlayerJoinEvent event) {
        this.exe.sendMessage(event.getPlayer(), "We are loading your permissions. It might take a while.");
        
        Runnable r = () -> {
            PlayerGroupModel model = getDatabase().find(PlayerGroupModel.class).where().idEq(event.getPlayer().getUniqueId().toString()).findUnique();
            if (model == null) {
                model = new PlayerGroupModel();
                model.setUuid(event.getPlayer().getUniqueId().toString());
                model.setPermission_group(getConfigOption("default-group", "none"));
            }
            
            model.setLastKnownName(event.getPlayer().getName());
            
            PermissionAttachment attachment = event.getPlayer().addAttachment(this);
            Callback<Map<String, Boolean>, Void> whatDo = (Map<String, Boolean> perms, Throwable error) -> {
                perms.forEach((string, has) -> {
                    attachment.setPermission(string, has);
                });
                
                return null;
            };
            
            model.getPermissionNodes(this, true, whatDo);
        };
        
        Async.runTask(r, (Throwable error) -> {
            if (error != null) {
                this.exe.sendMessage(event.getPlayer(), "Error loading permissions. [" + error.getMessage() + "]");
                this.exe.sendMessage(event.getPlayer(), "It is recommended that attempt to rejoin the server.");
                
                return;
            }
            
            this.exe.sendMessage(event.getPlayer(), "Permissions Loaded.");
        });
    }
    
    @EventHandler
    void onPlayerQuit(PlayerQuitEvent event) {
        // remove attachment
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
