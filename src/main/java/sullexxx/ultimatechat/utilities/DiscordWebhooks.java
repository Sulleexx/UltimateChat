package sullexxx.ultimatechat.utilities;


import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import sullexxx.ultimatechat.UltimateChat;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class DiscordWebhooks {
    public static String createWebhook(String channelId, String webhookName, String token) {
        if (!UltimateChat.getInstance().getConfig().getBoolean("Discord.Enable")) return null;
        String apiUrl = "https://discord.com/api/v10/channels/" + channelId + "/webhooks";

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(apiUrl);
            post.setHeader("Authorization", "Bot " + token);
            post.setHeader("Content-type", "application/json");

            JSONObject json = new JSONObject();
            json.put("name", webhookName);

            post.setEntity(new StringEntity(json.toString(), StandardCharsets.UTF_8));

            try (CloseableHttpResponse response = httpClient.execute(post)) {
                String responseString = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
                return new JSONObject(responseString).getString("url");
            }
        } catch (IOException e) {
            System.out.println("Ошибка при создании вебхука: " + e.getMessage());
        }
        return null;
    }

    public static void sendWebhookMessage(String webhookUrl, String content, String username) {
        if (!UltimateChat.getInstance().getConfig().getBoolean("Discord.Enable")) return;
        if (webhookUrl == null) return;
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(webhookUrl);
            JSONObject json = new JSONObject();
            json.put("content", content);
            json.put("username", username);
            json.put("avatar_url", "https://cravatar.eu/helmavatar/" + username + "/55.png");

            post.setEntity(new StringEntity(json.toString(), StandardCharsets.UTF_8));
            post.setHeader("Content-type", "application/json");

            httpClient.execute(post).close();
        } catch (IOException e) {
            System.out.println("Не удалось отправить сообщение, вот ошибка: " + e.getMessage());
        }
    }

    public static void deleteWebhook(String webhookUrl, String token) {
        if (webhookUrl == null) return;

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            String[] splitUrl = webhookUrl.split("/");
            String webhookId = splitUrl[splitUrl.length - 2];
            String webhookToken = splitUrl[splitUrl.length - 1];
            String apiUrl = "https://discord.com/api/v10/webhooks/" + webhookId + "/" + webhookToken;

            HttpDelete delete = new HttpDelete(apiUrl);
            delete.setHeader("Authorization", "Bot " + token);

            try (CloseableHttpResponse response = httpClient.execute(delete)) {
                System.out.println("Вебхук удален с статусом: " + response.getStatusLine().getStatusCode());
            }
        } catch (IOException e) {
            System.out.println("Ошибка при удалении вебука: " + e.getMessage());
        }
    }
}
