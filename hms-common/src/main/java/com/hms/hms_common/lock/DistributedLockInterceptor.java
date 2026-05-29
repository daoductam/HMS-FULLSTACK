package com.hms.hms_common.lock;

import io.quarkus.redis.datasource.RedisDataSource;
import io.vertx.mutiny.redis.client.Response;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.UUID;

@Interceptor
@DistributedLock
@Priority(Interceptor.Priority.PLATFORM_BEFORE + 10)
public class DistributedLockInterceptor {

    @Inject
    RedisDataSource redisDataSource;

    @AroundInvoke
    public Object intercept(InvocationContext context) throws Exception {
        Method method = context.getMethod();
        DistributedLock distributedLock = method.getAnnotation(DistributedLock.class);
        if (distributedLock == null) {
            distributedLock = method.getDeclaringClass().getAnnotation(DistributedLock.class);
        }

        String keyExpr = distributedLock.keyExpression();
        String lockKey = "lock:" + resolveKey(keyExpr, context);
        String token = UUID.randomUUID().toString();
        String leaseTimeStr = String.valueOf(distributedLock.leaseTime());
        long waitTime = distributedLock.waitTime();

        boolean acquired = false;
        long end = System.currentTimeMillis() + waitTime;

        while (System.currentTimeMillis() < end) {
            try {
                // Execute SET lockKey token NX PX leaseTime
                Response response = redisDataSource.execute("SET", lockKey, token, "NX", "PX", leaseTimeStr);
                if (response != null && "OK".equalsIgnoreCase(response.toString())) {
                    acquired = true;
                    break;
                }
            } catch (Exception e) {
                // Ignore and retry
            }
            Thread.sleep(100);
        }

        if (!acquired) {
            throw new RuntimeException("Could not acquire lock for key: " + lockKey);
        }

        try {
            return context.proceed();
        } finally {
            try {
                // Release lock safely via Lua script to avoid deleting other clients' locks
                String luaScript = "if redis.call('get', KEYS[1]) == ARGV[1] then " +
                        "return redis.call('del', KEYS[1]) " +
                        "else " +
                        "return 0 " +
                        "end";
                redisDataSource.execute("EVAL", luaScript, "1", lockKey, token);
            } catch (Exception e) {
                System.err.println("Error releasing lock: " + e.getMessage());
            }
        }
    }

    private String resolveKey(String expression, InvocationContext context) {
        if (expression == null || expression.trim().isEmpty()) {
            return context.getMethod().getName();
        }

        // Simplistic dynamic key resolver: e.g. "doctorId" or "dto.doctorId"
        String[] parts = expression.split("\\.");
        String rootParamName = parts[0];

        Object[] parameterValues = context.getParameters();
        Parameter[] parameters = context.getMethod().getParameters();

        for (int i = 0; i < parameters.length; i++) {
            if (parameters[i].getName().equals(rootParamName) || rootParamName.equalsIgnoreCase(parameters[i].getType().getSimpleName()) || parameters.length == 1) {
                Object obj = parameterValues[i];
                if (parts.length == 1) {
                    return String.valueOf(obj);
                } else {
                    // Nested property reflection
                    return String.valueOf(getNestedProperty(obj, parts, 1));
                }
            }
        }
        return expression; // Fallback
    }

    private Object getNestedProperty(Object obj, String[] parts, int index) {
        if (obj == null) return null;
        try {
            Field field = null;
            Class<?> clazz = obj.getClass();
            while (clazz != null && field == null) {
                try {
                    field = clazz.getDeclaredField(parts[index]);
                } catch (NoSuchFieldException e) {
                    clazz = clazz.getSuperclass();
                }
            }
            if (field != null) {
                field.setAccessible(true);
                Object val = field.get(obj);
                if (index == parts.length - 1) {
                    return val;
                } else {
                    return getNestedProperty(val, parts, index + 1);
                }
            }
        } catch (Exception e) {
            // Ignore
        }
        return null;
    }
}
