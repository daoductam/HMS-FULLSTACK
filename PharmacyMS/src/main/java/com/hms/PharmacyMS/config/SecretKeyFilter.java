package com.hms.PharmacyMS.config;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.IOException;

@Provider
public class SecretKeyFilter implements ContainerRequestFilter {

    @ConfigProperty(name = "hms.secret-key")
    String secretKey;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String requestKey = requestContext.getHeaderString("X-Secret-Key");

        if (secretKey != null && !secretKey.equals(requestKey)) {
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Invalid Secret Key")
                    .build());
        }
    }
}
