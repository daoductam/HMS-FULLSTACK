package com.hms.hms_common.limiter;

import io.quarkus.redis.datasource.RedisDataSource;
import io.vertx.mutiny.redis.client.Response;
import io.vertx.core.http.HttpServerRequest;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import jakarta.ws.rs.ClientErrorException;
import jakarta.ws.rs.core.SecurityContext;
import java.lang.reflect.Method;
import java.util.UUID;

@Interceptor
@RateLimit
@Priority(Interceptor.Priority.PLATFORM_BEFORE + 5)
public class RateLimitInterceptor {

    @Inject
    RedisDataSource redisDataSource;

    @Inject
    HttpServerRequest request;

    @Inject
    SecurityContext securityContext;

    @AroundInvoke
    public Object intercept(InvocationContext context) throws Exception {
        Method method = context.getMethod();
        RateLimit rateLimit = method.getAnnotation(RateLimit.class);
        if (rateLimit == null) {
            rateLimit = method.getDeclaringClass().getAnnotation(RateLimit.class);
        }

        String ip = "unknown";
        if (request != null && request.remoteAddress() != null) {
            ip = request.remoteAddress().host();
        }

        String user = "anonymous";
        if (securityContext != null && securityContext.getUserPrincipal() != null) {
            user = securityContext.getUserPrincipal().getName();
        }

        String identifier = !"anonymous".equals(user) ? user : ip;
        String key = rateLimit.keyPrefix() + ":" + identifier + ":" + method.getDeclaringClass().getSimpleName() + ":" + method.getName();

        long now = System.currentTimeMillis();
        long durationMs = rateLimit.duration() * 1000;
        String clearBefore = String.valueOf(now - durationMs);
        String nowStr = String.valueOf(now);
        String durationStr = String.valueOf(rateLimit.duration());

        try {
            // Remove entries older than the window
            redisDataSource.execute("ZREMRANGEBYSCORE", key, "-inf", clearBefore);

            // Count requests in window
            Response zcardResponse = redisDataSource.execute("ZCARD", key);
            long currentRequests = zcardResponse != null ? zcardResponse.toLong() : 0;

            if (currentRequests >= rateLimit.limit()) {
                throw new ClientErrorException("Too many requests", jakarta.ws.rs.core.Response.Status.TOO_MANY_REQUESTS);
            }

            // Add this request
            redisDataSource.execute("ZADD", key, nowStr, nowStr + ":" + UUID.randomUUID().toString());

            // Set TTL on key
            redisDataSource.execute("EXPIRE", key, durationStr);

        } catch (ClientErrorException e) {
            throw e;
        } catch (Exception e) {
            // Log warning but let request pass if Redis fails to ensure high availability
            System.err.println("Rate Limiter Redis error: " + e.getMessage());
        }

        return context.proceed();
    }
}
