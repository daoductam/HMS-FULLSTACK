package com.hms.ProfileMS.config;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;

@Provider
public class SecretKeyFilter implements ContainerRequestFilter {

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String secretKey = requestContext.getHeaderString("X-Secret-Key");
        
        // Match the original Spring Security logic
        if (!"SECRET".equals(secretKey)) {
            requestContext.abortWith(Response.status(Response.Status.FORBIDDEN)
                    .entity("Invalid Secret Key")
                    .build());
        }
    }
}
