/*
 * Copyright 2015 Goblom.
 * 
 * All Rights Reserved unless otherwise explicitly stated.
 */
package codes.goblom.gperms.models;

import codes.goblom.gperms.GoblomPerms;
import com.avaje.ebean.ExpressionList;
import com.google.common.collect.Sets;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Goblom
 */
@Entity
@Table( name = "gperms_groups" )
public class GroupModel {
    @Id
    @Column(unique = true, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Setter
    private String groupName;
    
    @Column
    @Getter
    @Setter
    private String inherits = "";
    
    public boolean doesNotInherit() {
        return inherits == null || inherits.equals("") || inherits.equalsIgnoreCase("none");
    }
    
    /**
     * Not ran Async. Can cause lag if ran on main thread.
     */
    public Set<GroupPermissionModel> getPermissionNodes(GoblomPerms system, boolean andInherits) {
        Set<GroupPermissionModel> perms = Sets.newConcurrentHashSet();
        ExpressionList<GroupPermissionModel> expressionList = system.getDatabase().find(GroupPermissionModel.class).where().ieq("forGroup", groupName);
        
        expressionList.findSet().forEach((model) -> perms.add(model));
        
        if (andInherits) {
            GroupModel nextGroup = null;
            
            do {
                if (!doesNotInherit()) {
                    nextGroup = system.getDatabase().find(GroupModel.class).where().idEq(inherits).findUnique();

                    if (nextGroup != null) {
                        Set<GroupPermissionModel> nextPerms = nextGroup.getPermissionNodes(system, andInherits);

                        if (!nextPerms.isEmpty()) {
                            nextPerms.forEach((model) -> perms.add(model));
                        }
                    }
                }
            } while (nextGroup != null && !nextGroup.doesNotInherit());
        }
        
        return perms;
    }
}
