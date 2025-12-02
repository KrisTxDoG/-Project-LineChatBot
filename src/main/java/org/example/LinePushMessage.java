package org.example;
import okhttp3.*;

import java.io.InputStream;
import java.util.Properties;

public class LinePushMessage {

    static Properties props = new Properties();

    static {
        try (InputStream input = LinePushMessage.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (input != null) {
                props.load(input);
            } else {
                System.err.println("找不到 application.properties");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 長期 Channel Token
    private static final String CHANNEL_ACCESS_TOKEN = props.getProperty("line.bot.token");
    
    private static final OkHttpClient client = new OkHttpClient();

    public static void push(String to, String text) throws Exception {

        System.out.println("Token: " + CHANNEL_ACCESS_TOKEN);


        // line api url
        String url = "https://api.line.me/v2/bot/message/push";

        MediaType mediaType = MediaType.get("application/json; charset=utf-8");

        String json = """
                {
                    "to" : "%s",
                    "messages" : [
                    {
                        "type" : "text",
                        "text" : "%s"
                    }
                  ]
                }
                """.formatted(to, text);

        RequestBody body = RequestBody.create(json, mediaType);

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + CHANNEL_ACCESS_TOKEN )
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();

        Response response = client.newCall(request).execute();
        System.out.println("Line 回應 : " + response.body().string());
    }
}
