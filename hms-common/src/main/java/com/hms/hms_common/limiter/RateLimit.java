package com.hms.hms_common.limiter;

import jakarta.enterprise.util.Nonbinding;
import jakarta.interceptor.InterceptorBinding;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@InterceptorBinding
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimit {
    
    @Nonbinding
    int limit() default 5; // Maximum number of requests allowed in the duration window
    
    @Nonbinding
    long duration() default 60; // Window duration in seconds
    
    @Nonbinding
    String keyPrefix() default "rate_limit";
}
