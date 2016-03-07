/*
 * Copyright 2015 Goblom.
 * 
 * All Rights Reserved unless otherwise explicitly stated.
 */
package codes.goblom.gperms.data;

import org.bukkit.OfflinePlayer;

/**
 * 
 * @author Goblom
 */
// TODO: implement
public interface Group {
    
    String getName();
    
    void delete();
    
    boolean exists(); // Return true if group exists in database
    
    void create(); //Maybe allow the command to handle that?
    
    GroupPermission getPermission(String node);
    
    void setPermission(String node, boolean value);
    
    void addPlayer(String name);
    
    void addPlayer(OfflinePlayer player);
    
    boolean hasPlayer(String player);
    
    boolean hasPlayer(OfflinePlayer player);
        
    void addPermission(GroupPermission permission);
}
