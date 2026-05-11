package com.hms.user.UserMS.config;

import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;
import java.security.Principal;

@Provider
@Priority(Priorities.AUTHENTICATION - 100) // High priority to run before most things
public class HeaderRoleFilter implements ContainerRequestFilter {

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String role = requestContext.getHeaderString("X-User-Role");
        String profileId = requestContext.getHeaderString("X-Profile-Id");

        if (role != null) {
            System.out.println("HeaderRoleFilter: found X-User-Role = " + role);
            final SecurityContext currentSecurityContext = requestContext.getSecurityContext();
            
            requestContext.setSecurityContext(new SecurityContext() {
                @Override
                public Principal getUserPrincipal() {
                    return () -> profileId != null ? profileId : "user";
                }

                @Override
                public boolean isUserInRole(String r) {
                    System.out.println("Checking role: " + r + " against " + role);
                    return role.equalsIgnoreCase(r) || role.equalsIgnoreCase("ROLE_" + r);
                }

                @Override
                public boolean isSecure() {
                    return currentSecurityContext.isSecure();
                }

                @Override
                public String getAuthenticationScheme() {
                    return "GATEWAY_HEADER";
                }
            });
        } else {
            System.out.println("HeaderRoleFilter: X-User-Role header not found");
        }
    }
}
