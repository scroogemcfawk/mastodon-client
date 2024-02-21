package com.github.scroogemcfawk.mastodon.api;


import com.github.scroogemcfawk.mastodon.api.entity.*;
import com.google.gson.Gson;
import lombok.NonNull;

import java.io.*;
import java.util.Objects;


public class ApplicationClient
{
    private Configuration config;
    private MastodonApiService apiService;
    private Application app;
    private Token appToken;
    Token accountToken;
    private File storage;

    private void initClientDir() {
        storage = new File(".storage");
        if (!storage.exists()) {
            System.out.println("created dir");
            storage.mkdirs();
        }
        System.out.println(storage.getAbsoluteFile());
    }

    public void loadApp() {
        try (BufferedReader br = new BufferedReader(new FileReader(".storage/secret.txt"))) {
            try {
                app = new Gson().fromJson(br.readLine(), Application.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                appToken = new Gson().fromJson(br.readLine(), Token.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                accountToken = new Gson().fromJson(br.readLine(), Token.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    void writeApp() throws IOException
    {
        var storagePath = storage.getAbsoluteFile();
        var out = new PrintWriter(new FileWriter(storagePath + "/secret.txt"));
        var gson = new Gson();
        out.println(gson.toJson(app));
        out.println(gson.toJson(appToken));
        out.println(gson.toJson(accountToken));
        out.close();
    }

    boolean tryLoadFromFile() {
        try {
            loadApp();
            return true;
        } catch (Exception e) {
            System.out.println("Could not load app from file.");
            return false;
        }
    }

    public ApplicationClient(Configuration config) throws IOException
    {
        this.config = config;
        this.apiService = config.getService();
        if (!tryLoadFromFile()) {
            System.out.println("Register app");
            this.app = registerApplication();
        }
        initClientDir();
    }

    private Application registerApplication() throws IOException
    {
        var requestBody = new ApplicationRegisterRequestBody();
        var call = apiService.registerApp(requestBody);
        var response = call.execute();
        if (response.isSuccessful()) return response.body();
        throw new IOException("Failed to register application");
    }

    private Token getAppToken() throws IOException
    {
        var call = apiService.getToken(new ApplicationGetTokenRequestBody(app.getClientId(), app.getClientSecret()));
        var response = call.execute();
        if (response.isSuccessful()) return response.body();
        throw new IOException("Failed to register application");
    }

    private void ensureAppToken() throws IOException
    {
        if (appToken == null) appToken = getAppToken();
    }

    private String getAuthHeader() throws IOException
    {
        ensureAppToken();
        return appToken.getTokenType() + " " + appToken.getAccessToken();
    }

    public void print() {
        System.out.println(app);
        System.out.println("Application" + appToken);
    }

    boolean isValid() {
        try
        {
            ensureAppToken();
            var header = getAuthHeader();
            var verifyCall = apiService.verifyApp(header);
            var verifyResponse = verifyCall.execute();
            var app = verifyResponse.body();
            return Objects.equals(this.app.getVapidKey(), app.getVapidKey());
        } catch (Exception e) {
            System.err.println("Could not verify token: " + e.getMessage());
            return false;
        }
    }

    public Token registerAccount(
            @NonNull String username,
            @NonNull String email,
            @NonNull String password,
            @NonNull Boolean agreement,
            String locale, String reason) throws IOException
    {
        if (locale == null || locale.isBlank()) locale = "en-US";
        if (reason == null) reason = "";

        var header = getAuthHeader();
        var body = new AccountRegisterRequestBody(username, email, password, agreement, locale, reason);

        var call = apiService.registerAccount(header, body);
        var response = call.execute();

        if (response.isSuccessful()) {
            accountToken = response.body();
            writeApp();
            return accountToken;
        }

        System.out.println(response.code());
        System.out.println(response.errorBody().string());

        return null;
    }

    public boolean verifyAccount(Token token) throws IOException
    {
        if (token == null) {
            if (accountToken == null) return false;
            token = accountToken;
        }

        var call = apiService.verifyAccount("Bearer " + token.getAccessToken());
        var response = call.execute();

        if (response.isSuccessful()) {
            System.out.println(response.body().string());
            return true;
        }

        System.out.println(response.errorBody().string());

        return false;
    }

}
