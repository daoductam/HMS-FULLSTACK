package com.hms.PharmacyMS.clients;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "profile-api")
public interface ProfileClient {
}
