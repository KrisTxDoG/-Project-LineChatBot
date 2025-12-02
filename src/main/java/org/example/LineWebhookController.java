package org.example;

import okhttp3.HttpUrl;
import org.springframework.web.bind.annotation.*;
import org.json.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

@RestController
@RequestMapping("/line")
public class LineWebhookController {

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

    private static final String CHANNEL_ACCESS_TOKEN = props.getProperty("line.bot.token");


    @PostMapping("/webhook")
    public String handleWebhook(@RequestBody String body) {
        System.out.println("收到 Webhook: " + body);

        // 解析 userId
        try {
            JSONObject json = new JSONObject(body);
            JSONArray events = json.getJSONArray("events");
            for (int i = 0; i < events.length(); i++) {
                JSONObject event = events.getJSONObject(i);
                String userId = event.getJSONObject("source").getString("userId");
                System.out.println("User ID: " + userId);
                String replyToken = event.getString("replyToken"); // 取得回覆 token

                // 取得使用者訊息
                String message = event.getJSONObject("message").getString("text");

                System.out.println("輸出的訊息為 : " + message);

                // 自動回覆
                String replyMessage = "你說了: " + message;
                replay(replyMessage, replyToken);


            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        return "OK";
    }

    public void replay(String message, String replayToken) throws IOException {
        URL url = new URL("https://api.line.me/v2/bot/message/reply");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Authorization", "Bearer " + CHANNEL_ACCESS_TOKEN);


        String jsonMessage = "{"
                + "\"replyToken\":\"" + replayToken + "\","
                + "\"messages\":[{\"type\":\"text\",\"text\":\"" + message + "\"}]"
                + "}";

        OutputStream os = conn.getOutputStream();
        os.write(jsonMessage.getBytes("UTF-8"));
        os.flush();
        os.close();

        System.out.println("LINE 回覆狀態: " + conn.getResponseCode());

        conn.disconnect();
    }
}
