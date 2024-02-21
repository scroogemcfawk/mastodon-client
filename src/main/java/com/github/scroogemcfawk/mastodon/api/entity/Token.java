package com.github.scroogemcfawk.mastodon.api.entity;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.security.Timestamp;

@Data
@AllArgsConstructor
public class Token
{
    @SerializedName("access_token")
    String accessToken;
    @SerializedName("token_type")
    String tokenType;
    @SerializedName("scope")
    String scope;
    @SerializedName("created_at")
    Integer createdAt;
}
