package com.hms.GatewayMS.resource;

import io.smallrye.common.annotation.Blocking;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.ext.web.client.WebClient;
import io.vertx.mutiny.ext.web.client.HttpResponse;
import io.vertx.core.http.HttpMethod;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.Map;

@Path("/{service}/{path:.*}")
public class GatewayResource {

    @Inject
    Vertx vertx;

    private WebClient client;

    @ConfigProperty(name = "gateway.routes")
    Map<String, String> routes;

    @PostConstruct
    void init() {
        this.client = WebClient.create(vertx);
    }

    @GET
    @Blocking
    public Uni<Response> proxyGet(@PathParam("service") String service, 
                               @PathParam("path") String path, 
                               @Context HttpHeaders headers, 
                               @Context UriInfo uriInfo,
                               byte[] body,
                               @Context jakarta.ws.rs.container.ContainerRequestContext requestContext) {
        return doProxy(service, path, headers, uriInfo, body, requestContext);
    }

    @POST
    @Blocking
    public Uni<Response> proxyPost(@PathParam("service") String service, 
                               @PathParam("path") String path, 
                               @Context HttpHeaders headers, 
                               @Context UriInfo uriInfo,
                               byte[] body,
                               @Context jakarta.ws.rs.container.ContainerRequestContext requestContext) {
        return doProxy(service, path, headers, uriInfo, body, requestContext);
    }

    @PUT
    @Blocking
    public Uni<Response> proxyPut(@PathParam("service") String service, 
                               @PathParam("path") String path, 
                               @Context HttpHeaders headers, 
                               @Context UriInfo uriInfo,
                               byte[] body,
                               @Context jakarta.ws.rs.container.ContainerRequestContext requestContext) {
        return doProxy(service, path, headers, uriInfo, body, requestContext);
    }

    @DELETE
    @Blocking
    public Uni<Response> proxyDelete(@PathParam("service") String service, 
                               @PathParam("path") String path, 
                               @Context HttpHeaders headers, 
                               @Context UriInfo uriInfo,
                               byte[] body,
                               @Context jakarta.ws.rs.container.ContainerRequestContext requestContext) {
        return doProxy(service, path, headers, uriInfo, body, requestContext);
    }

    private Uni<Response> doProxy(String service, 
                               String path, 
                               HttpHeaders headers, 
                               UriInfo uriInfo,
                               byte[] body,
                               jakarta.ws.rs.container.ContainerRequestContext requestContext) {
        
        String targetBaseUrl = routes.get(service);
        if (targetBaseUrl == null) {
            System.err.println("Service not found in routes: " + service);
            return Uni.createFrom().item(Response.status(Response.Status.NOT_FOUND).entity("Service not found: " + service).build());
        }

        String targetUrl = targetBaseUrl + "/" + service + "/" + path;
        System.out.println("Proxying " + requestContext.getMethod() + " to: " + targetUrl);
        
        // Add query parameters if any
        String query = uriInfo.getRequestUri().getQuery();
        if (query != null) {
            targetUrl += "?" + query;
        }

        HttpMethod method = HttpMethod.valueOf(requestContext.getMethod());
        
        var request = client.requestAbs(method, targetUrl);

        // Forward headers
        requestContext.getHeaders().forEach((name, values) -> {
            if (!name.equalsIgnoreCase(HttpHeaders.CONTENT_LENGTH) && 
                !name.equalsIgnoreCase(HttpHeaders.HOST) &&
                !name.equalsIgnoreCase(HttpHeaders.AUTHORIZATION) &&
                !name.equalsIgnoreCase("Transfer-Encoding")) {
                values.forEach(value -> request.putHeader(name, value));
            }
        });

        if (body != null && body.length > 0) {
            return request.sendBuffer(io.vertx.mutiny.core.buffer.Buffer.buffer(body))
                    .map(this::mapResponse);
        } else {
            return request.send()
                    .map(this::mapResponse);
        }
    }

    private Response mapResponse(HttpResponse<io.vertx.mutiny.core.buffer.Buffer> vertxResponse) {
        try {
            Response.ResponseBuilder builder = Response.status(vertxResponse.statusCode());
            vertxResponse.headers().forEach(entry -> {
                if (!entry.getKey().equalsIgnoreCase(HttpHeaders.CONTENT_LENGTH) && !entry.getKey().equalsIgnoreCase("Transfer-Encoding")) {
                    builder.header(entry.getKey(), entry.getValue());
                }
            });
            if (vertxResponse.body() != null) {
                builder.entity(vertxResponse.body().getBytes());
            }
            return builder.build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError().entity("Error mapping response: " + e.getMessage()).build();
        }
    }
}
