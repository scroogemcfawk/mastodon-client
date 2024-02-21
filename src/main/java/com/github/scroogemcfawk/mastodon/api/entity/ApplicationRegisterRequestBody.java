package com.github.scroogemcfawk.mastodon.api.entity;


import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public final class ApplicationRegisterRequestBody
{
    @SerializedName("client_name")
    String clientName;
    @SerializedName("redirect_uris")
    String redirectUris;
    @SerializedName("scopes")
    String scopes;
    @SerializedName("website")
    String website;

    public ApplicationRegisterRequestBody() {
        this("mastodon api test", "urn:ietf:wg:oauth:2.0:oob", "read write push", "");
    }
}
