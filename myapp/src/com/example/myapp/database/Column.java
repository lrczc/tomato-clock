package com.example.myapp.database;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by shizhao.czc on 2014/8/25.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Column {
    public enum Type {
        TEXT, INTEGER, LONG, BOOLEAN, BLOB,
    }

    Type type() default Type.TEXT;

    boolean unique() default false;

    boolean notnull() default false;
}
