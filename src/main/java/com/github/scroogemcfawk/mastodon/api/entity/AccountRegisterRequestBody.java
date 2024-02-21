package com.github.scroogemcfawk.mastodon.api.entity;

import com.google.gson.annotations.SerializedName;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor()
public class AccountRegisterRequestBody
{
    @SerializedName("username")
    private String username;

    @SerializedName("email")
    private String email;

    @SerializedName("password")
    private String password;

    @SerializedName("agreement")
    private Boolean agreement;

    @SerializedName("locale")
    private String locale;

    @SerializedName("reason")
    private String reason;
}
