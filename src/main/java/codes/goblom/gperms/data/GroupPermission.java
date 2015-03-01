/*
 * Copyright 2015 Goblom.
 * 
 * All Rights Reserved unless otherwise explicitly stated.
 */
package codes.goblom.gperms.data;

/**
 *
 * @author Goblom
 */
public interface GroupPermission {
    
    String getNode();
    
    boolean value();
    
    void setValue();
    
    Group getGroup();
    
    void setGroup(Group group);
}
