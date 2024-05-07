package com.github.scroogemcfawk.mastodon.api;

import org.junit.jupiter.api.Test;

class ApplicationClientTest
{
//    public static ApplicationClient client;
//
//    @BeforeAll
//    public static void setupClient() throws IOException
//    {
//        // проверка корректности регистрации клиента и получения токена для авторизации
//        client = new ApplicationClient(new Configuration());
//        assertTrue(client.isValid());
//    }
//
//    @Test
//    public void registerAccount() throws IOException
//    {
//        var token = client.registerAccount("supercoolusername", "your@email.com", "password1234", true, "en", "");
//        assertNotNull(token);
//        System.out.println("Check your email for account confirmation letter.");
//    }
//
//    @Test
//    public void verifyAccount() throws IOException
//    {
//        assertTrue(client.verifyAccount(null));
//    }

    @Test
    public void testRegister() {
        Mastodon mastodon = new Mastodon("mas.to", true);
//        System.out.printf(mastodon.verifyAppCred().toString());
//        for (var timeline : mastodon.getPublicTimeline().getPart()) {
//            System.out.println(timeline.toString());
//        }
        System.out.println(mastodon.getRules());
        // 401 for some reason
        mastodon.register("luwraintestusername2281337", "jessicavicious@fthcapital.com", "easypass123", true, "en-US", false, null);
    }


}
