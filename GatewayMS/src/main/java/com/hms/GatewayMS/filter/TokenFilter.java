package com.hms.GatewayMS.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class TokenFilter extends AbstractGatewayFilterFactory<TokenFilter.Config> {
    private static final String SECRET =
            "6980396a36a308bfc93d146548bddc4ba36e51e0302fe8328a1df49e8ac46ae670d0cb933e2f939ef16c674088b1bd64e07ae0c866877528a841521df93b31f2";
    public TokenFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {
            String path = exchange.getRequest().getPath().toString();
            if (path.equals("/user/login") || path.equals("/user/register") 
                || path.contains("/pharmacy/payment") 
                || path.contains("/payment/ipn-callback")
                || path.contains("/payment/test")) {
                return chain.filter(exchange.mutate().request(r -> r.header(
                        "X-Secret-Key", "SECRET")).build());
            }
            HttpHeaders header = exchange.getRequest().getHeaders();
            if (!header.containsKey(HttpHeaders.AUTHORIZATION)) {
                throw new RuntimeException("Authorization header is missing");
            }
            String authHeader = header.getFirst(HttpHeaders.AUTHORIZATION);
            if (authHeader==null || !authHeader.startsWith("Bearer ")) {
                throw new RuntimeException("Authorization header is invalid");
            }
            String token=authHeader.substring(7);
            try {
                Claims claims = Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token).getBody();
                String role = claims.get("role", String.class);
                Long profileId = claims.get("profileId", Long.class);
                exchange = exchange.mutate().request(r -> r.header(
                        "X-Secret-Key", "SECRET")).build();

//                String role = claims.get("role", String.class);
//                Long profileId = claims.get("profileId", Long.class);
//                exchange = exchange.mutate().request(r -> r
//                        .header("X-Secret-Key", "SECRET")
//                        .header("X-User-Role", role) // Gửi Role
//                        .header("X-Profile-Id", String.valueOf(profileId)) // Gửi Profile ID
//                ).build();
            } catch (Exception e ) {
                throw new RuntimeException("Token is invalid");
            }
            return chain.filter(exchange);
        });
    }


    public static class Config{

    }
}
