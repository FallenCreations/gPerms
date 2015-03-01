/*
 * Copyright 2015 Goblom.
 * 
 * All Rights Reserved unless otherwise explicitly stated.
 */
package codes.goblom.gperms;

import org.bukkit.permissions.Permissible;

/**
 *
 * @author Goblom
 */
public enum PermsEnum {
    ADMIN,
    COMMAND_USE,
    GROUP_CREATE, 
    GROUP_EDIT, 
    GROUP_LIST,
    PLAYER_SETGROUP,
    
    ;
    
    public boolean has(Permissible perm) {
        return perm.hasPermission(toString());
    }
    
    @Override
    public String toString() {
        return "gperms." + name().toLowerCase().replace("_", ".");
    }
}
