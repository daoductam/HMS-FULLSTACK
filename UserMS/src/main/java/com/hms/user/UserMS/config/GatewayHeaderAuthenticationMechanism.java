package com.hms.user.UserMS.config;

import io.quarkus.security.identity.IdentityProviderManager;
import io.quarkus.security.identity.SecurityIdentity;
import io.quarkus.security.runtime.QuarkusPrincipal;
import io.quarkus.security.runtime.QuarkusSecurityIdentity;
import io.smallrye.mutiny.Uni;
import io.vertx.ext.web.RoutingContext;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class GatewayHeaderAuthenticationMechanism implements io.quarkus.vertx.http.runtime.security.HttpAuthenticationMechanism {

    @Override
    public Uni<SecurityIdentity> authenticate(RoutingContext context, IdentityProviderManager identityProviderManager) {
        String role = context.request().getHeader("X-User-Role");
        String profileId = context.request().getHeader("X-Profile-Id");

        if (role != null) {
            System.out.println("GatewayHeaderAuthenticationMechanism: Authenticating user with role " + role);
            QuarkusSecurityIdentity.Builder builder = QuarkusSecurityIdentity.builder()
                    .setPrincipal(new QuarkusPrincipal(profileId != null ? profileId : "user"))
                    .addRole(role.toUpperCase());
            return Uni.createFrom().item(builder.build());
        }
        return Uni.createFrom().nullItem();
    }

    @Override
    public Uni<io.quarkus.vertx.http.runtime.security.ChallengeData> getChallenge(RoutingContext context) {
        return Uni.createFrom().nullItem();
    }
}
