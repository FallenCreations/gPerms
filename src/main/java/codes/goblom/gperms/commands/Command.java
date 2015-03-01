/*
 * Copyright 2015 Goblom.
 * 
 * All Rights Reserved unless otherwise explicitly stated.
 */
package codes.goblom.gperms.commands;

import codes.goblom.gperms.PermsEnum;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @author Goblom
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Command {
    
    public String alias() default "";
    
    public String description() default "";
    
    public String usage() default "";
    
    public PermsEnum[] permissions() default { };
    
    public int minArgs() default 0;
    
    public boolean allowConsole() default true;
    
    public String[] help() default { };
}
