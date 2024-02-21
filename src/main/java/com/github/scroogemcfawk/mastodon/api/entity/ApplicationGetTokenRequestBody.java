package com.github.scroogemcfawk.mastodon.api.entity;

import com.google.gson.annotations.SerializedName;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ApplicationGetTokenRequestBody
{
    @SerializedName("client_id")
    String clientId;
    @SerializedName("client_secret")
    String clientSecret;
    @SerializedName("redirect_uri")
    String redirectUri;
    @SerializedName("grant_type")
    String grantType;
    @SerializedName("scope")
    String scope;

    public ApplicationGetTokenRequestBody(String clientId, String clientSecret) {
        this(clientId, clientSecret, "urn:ietf:wg:oauth:2.0:oob", "client_credentials", "read write push");
    }

}
