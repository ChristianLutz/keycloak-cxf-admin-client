/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.keycloak.admin.client;


import org.apache.cxf.jaxrs.client.WebClient;

import static org.keycloak.OAuth2Constants.CLIENT_CREDENTIALS;
import static org.keycloak.OAuth2Constants.PASSWORD;

/**
 * Provides a {@link Keycloak} client builder with the ability to customize the underlying
 * {@link WebClient CXF JAX-RS client} used to communicate with the Keycloak server.
 * <p>
 * <p>Example usage with a connection pool size of 20:</p>
 * <pre>
 *   Keycloak keycloak = KeycloakBuilder.builder()
 *     .serverUrl("https://sso.example.com/auth")
 *     .realm("realm")
 *     .username("user")
 *     .password("pass")
 *     .clientId("client")
 *     .clientSecret("secret")
 *     .webClient(new ResteasyClientBuilder().connectionPoolSize(20).build())
 *     .build();
 * </pre>
 * <p>Example usage with grant_type=client_credentials and SSL enabled</p>
 * <pre>
 *   Keycloak keycloak = KeycloakBuilder.builder()
 *     .serverUrl("https://sso.example.com/auth")
 *     .realm("example")
 *     .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
 *     .clientId("client")
 *     .clientSecret("secret")
 *     .trustStoreLocation("pathToTrustStore")
 *     .build();
 * </pre>
 *
 * @author Scott Rossillo
 * @see javax.ws.rs.client.ClientBuilder
 */
public class KeycloakBuilder {
    private String serverUrl;
    private String realm;
    private String username;
    private String password;
    private String clientId;
    private String clientSecret;
    private String trustStoreLocation;
    private String grantType = PASSWORD;
    private WebClient webClient;

    public KeycloakBuilder serverUrl(String serverUrl) {
        this.serverUrl = serverUrl;
        return this;
    }

    public KeycloakBuilder realm(String realm) {
        this.realm = realm;
        return this;
    }

    public KeycloakBuilder grantType(String grantType) {
        Config.checkGrantType(grantType);
        this.grantType = grantType;
        return this;
    }

    public KeycloakBuilder username(String username) {
        this.username = username;
        return this;
    }

    public KeycloakBuilder password(String password) {
        this.password = password;
        return this;
    }

    public KeycloakBuilder clientId(String clientId) {
        this.clientId = clientId;
        return this;
    }

    public KeycloakBuilder clientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
        return this;
    }

    public KeycloakBuilder trustStoreLocation(String trustStoreLocation) {
        this.trustStoreLocation = trustStoreLocation;
        return this;
    }

    public KeycloakBuilder webClient(WebClient webClient) {
        this.webClient = webClient;
        return this;
    }

    /**
     * Builds a new Keycloak client from this builder.
     */
    public Keycloak build() {
        if (serverUrl == null) {
            throw new IllegalStateException("serverUrl required");
        }

        if (realm == null) {
            throw new IllegalStateException("realm required");
        }

        if (PASSWORD.equals(grantType)) {
            if (username == null) {
                throw new IllegalStateException("username required");
            }

            if (password == null) {
                throw new IllegalStateException("password required");
            }
        } else if (CLIENT_CREDENTIALS.equals(grantType)) {
            if (clientSecret == null) {
                throw new IllegalStateException("clientSecret required with grant_type=client_credentials");
            }
        }

        if (clientId == null) {
            throw new IllegalStateException("clientId required");
        }

        return new Keycloak(serverUrl, realm, username, password, clientId, clientSecret, grantType, webClient, trustStoreLocation);
    }

    private KeycloakBuilder() {
    }

    /**
     * Returns a new Keycloak builder.
     */
    public static KeycloakBuilder builder() {
        return new KeycloakBuilder();
    }
}
