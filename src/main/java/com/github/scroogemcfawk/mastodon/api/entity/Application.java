package com.github.scroogemcfawk.mastodon.api.entity;


import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public final class Application
{
    @SerializedName("name")
    String name;
    @SerializedName("website")
    String website;
    @SerializedName("client_id")
    String clientId;
    @SerializedName("client_secret")
    String clientSecret;
    @SerializedName("vapid_key")
    String vapidKey;
}
