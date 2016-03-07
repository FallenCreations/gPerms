/*
 * Copyright 2015 Goblom.
 * 
 * All Rights Reserved unless otherwise explicitly stated.
 */
package codes.goblom.gperms.models;

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
@Table( name = "gperms_groups_permissions" )
public class GroupPermissionModel {
    @Id
    @Column(unique = true, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Setter
    private String permissionNode;
    
    @Column
    @Getter
    @Setter
    private String forGroup;
    
    @Column ( name = "_value" )
    @Getter
    @Setter
    private boolean value;
}
