package com.github.scroogemcfawk.mastodon.api;

import com.github.scroogemcfawk.mastodon.api.entity.Mastodon;

import java.io.IOException;


public class Main
{
    public static void main(String[] args) throws IOException
    {
        try {
            Mastodon mastodon = new Mastodon("techhub.social", false);
            mastodon.verifyAppCred();
//            mastodon.getHomeTimeline();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
