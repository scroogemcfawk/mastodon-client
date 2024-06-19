package com.github.scroogemcfawk.mastodon;

import com.github.scroogemcfawk.mastodon.api.Mastodon;

import java.io.IOException;


public class Main
{
    public static void main(String[] args) throws IOException
    {
        try {
            Mastodon mastodon = new Mastodon("techhub.social", false, false);
            mastodon.verifyAppCred();
//            mastodon.getHomeTimeline();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
