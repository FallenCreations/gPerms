/*
 * Copyright 2015 Goblom.
 * 
 * All Rights Reserved unless otherwise explicitly stated.
 */
package codes.goblom.gperms.misc;

import codes.goblom.gperms.GoblomPerms;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

/**
 *
 * @author Goblom
 */
public class Async {
    
    private static final GoblomPerms PLUGIN = (GoblomPerms) JavaPlugin.getProvidingPlugin(Async.class);
    
    public static BukkitTask runTask(Runnable r, EmptyCallback onComplete) {
        return Bukkit.getScheduler().runTaskAsynchronously(PLUGIN, () -> {
            Throwable thrown = null;
            
            try {
                r.run();
            } catch (Throwable t) {
                thrown = t;
            }
            
            onComplete.call(thrown);
        });
    }
    
    public static BukkitTask runTaskLater(Runnable r, long ticksLater, EmptyCallback onComplete) {
        return Bukkit.getScheduler().runTaskLaterAsynchronously(PLUGIN, () -> {
            Throwable thrown = null;
            
            try {
                r.run();
            } catch (Throwable t) {
                thrown = t;
            }
            
            onComplete.call(thrown);
        }, ticksLater);
    }
    
    public static BukkitTask runTaskTimer(Runnable r, long delay, long timer, EmptyCallback onRun) {
        return Bukkit.getScheduler().runTaskTimerAsynchronously(PLUGIN, () -> {
            Throwable thrown = null;
            
            try {
                r.run();
            } catch (Throwable t) {
                thrown = t;
            }
            
            onRun.call(thrown);
        }, delay, timer);
    }
}
