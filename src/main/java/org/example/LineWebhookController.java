package org.example;

import org.springframework.web.bind.annotation.*;
import org.json.*;

@RestController
@RequestMapping("/line")
public class LineWebhookController {

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
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "OK";
    }
}
