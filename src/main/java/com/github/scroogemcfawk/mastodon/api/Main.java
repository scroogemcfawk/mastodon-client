package com.github.scroogemcfawk.mastodon.api;

import java.awt.image.PackedColorModel;
import java.io.IOException;


public class Main
{
    public static void main(String[] args) throws IOException
    {
        var config = new Configuration();
        var app = new ApplicationClient(config);
//        app.writeApp();

        try {

//            app.print();

//            var accountToken = app.registerAccount("hehehaha", "havasege@pelagius.net", "pass1234", true, "", "");
            var accountToken = app.accountToken;
            if (accountToken != null) {
                System.out.println(app.verifyAccount(null));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        config.close();
    }
}
