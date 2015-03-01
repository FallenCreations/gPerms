/*
 * Copyright 2015 Goblom.
 * 
 * All Rights Reserved unless otherwise explicitly stated.
 */
package codes.goblom.gperms.misc;

/**
 *
 * @author Goblom
 */
public interface Callback<T,R> {
    
    public R call(T type, Throwable error);
}
