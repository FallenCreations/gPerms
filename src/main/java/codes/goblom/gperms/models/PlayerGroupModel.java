/*
 * Copyright 2015 Goblom.
 * 
 * All Rights Reserved unless otherwise explicitly stated.
 */
package codes.goblom.gperms.models;

import codes.goblom.gperms.GoblomPerms;
import codes.goblom.gperms.misc.Async;
import codes.goblom.gperms.misc.Callback;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 *
 * @author Goblom
 */
@Entity
@Table ( name = "gperms_player" )
public class PlayerGroupModel {
    @Id
    @Column(unique = true, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Setter
    private String uuid;
    
    @Column
    @Getter
    @Setter
    private String lastKnownName, permission_group;
    
    public void getPermissionNodes(GoblomPerms system, boolean all, Callback<Map<String, Boolean>, Void> whatDo) {
        Player player = Bukkit.getPlayer(UUID.fromString(uuid));
        
        if (player == null || permission_group == null || permission_group.equals("")|| permission_group.equalsIgnoreCase("none")) return;
        
        Async.runTask(() -> {
            GroupModel myGroup = system.getDatabase().find(GroupModel.class).where().idEq(permission_group).findUnique();
            if (myGroup != null) {
                Set<GroupPermissionModel> perms = myGroup.getPermissionNodes(system, all);
                Map<String, Boolean> map = Maps.newConcurrentMap();
                
                perms.forEach((model) -> {
                    String node = model.getPermissionNode();
                    boolean set = model.isValue();
                    
                    map.put(node, set);
                });
                
                whatDo.call(map, null);
            }
        }, (Throwable t) -> { });
    }
}
