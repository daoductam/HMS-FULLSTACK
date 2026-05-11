package com.hms.GatewayMS.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class TokenFilter implements ContainerRequestFilter {

    private static final String SECRET =
            "6980396a36a308bfc93d146548bddc4ba36e51e0302fe8328a1df49e8ac46ae670d0cb933e2f939ef16c674088b1bd64e07ae0c866877528a841521df93b31f2";

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String path = requestContext.getUriInfo().getPath();

        // Bypass security for specific paths
        if (path.equals("user/login") || path.equals("/user/login")
                || path.equals("user/register") || path.equals("/user/register")
                || path.contains("pharmacy/payment")
                || path.contains("payment/ipn-callback")
                || path.contains("payment/test")) {
            requestContext.getHeaders().add("X-Secret-Key", "SECRET");
            return;
        }

        String authHeader = requestContext.getHeaderString("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Authorization header is missing or invalid")
                    .build());
            return;
        }

        String token = authHeader.substring(7);
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8)))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            
            // Inject headers for internal microservices
            requestContext.getHeaders().add("X-Secret-Key", "SECRET");
            
            Object roleObj = claims.get("role");
            Object profileIdObj = claims.get("profileId");
            
            if (roleObj != null) {
                System.out.println("Adding X-User-Role: " + roleObj.toString());
                requestContext.getHeaders().add("X-User-Role", roleObj.toString());
            }
            if (profileIdObj != null) {
                System.out.println("Adding X-Profile-Id: " + profileIdObj.toString());
                requestContext.getHeaders().add("X-Profile-Id", profileIdObj.toString());
            }

        } catch (Exception e) {
            e.printStackTrace(); // In lỗi cụ thể ra console Gateway
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Token is invalid: " + e.getMessage())
                    .build());
        }
    }
}
