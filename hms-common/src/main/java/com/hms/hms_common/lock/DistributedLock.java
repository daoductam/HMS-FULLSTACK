package com.hms.hms_common.lock;

import jakarta.enterprise.util.Nonbinding;
import jakarta.interceptor.InterceptorBinding;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@InterceptorBinding
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface DistributedLock {
    
    @Nonbinding
    String keyExpression() default "";
    
    @Nonbinding
    long leaseTime() default 10000; // Time in milliseconds before the lock is auto-released
    
    @Nonbinding
    long waitTime() default 5000;  // Time in milliseconds to wait for the lock
}
