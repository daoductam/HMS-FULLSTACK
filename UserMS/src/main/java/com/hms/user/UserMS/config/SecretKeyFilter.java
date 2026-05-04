package com.hms.user.UserMS.config;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;

@Provider
public class SecretKeyFilter implements ContainerRequestFilter {

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String path = requestContext.getUriInfo().getPath();
        
        // Skip check for login and register if needed, 
        // but the original Spring code was very strict: permit only if X-Secret-Key == SECRET
        
        String secretKey = requestContext.getHeaderString("X-Secret-Key");
        if (!"SECRET".equals(secretKey)) {
            // Optional: allow some paths without the key if necessary, 
            // but following the original logic:
            requestContext.abortWith(Response.status(Response.Status.FORBIDDEN)
                    .entity("Invalid or missing X-Secret-Key")
                    .build());
        }
    }
}
