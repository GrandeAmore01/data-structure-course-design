package com.datastruct.visualizer.llm;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;

import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Invokes DeepSeek Chat Completion API.
 */
public class DeepSeekGateway implements LlmGateway {

    private static final String ENDPOINT = "https://api.deepseek.com/v1/chat/completions";

    private final OkHttpClient client;
    private final ObjectMapper mapper = new ObjectMapper();
    private final String apiKey;
    private final String model;

    public DeepSeekGateway(String apiKey, String model) {
        this.apiKey = apiKey;
        this.model = model == null ? "deepseek-chat" : model;
        this.client = new OkHttpClient.Builder()
                .callTimeout(Duration.ofSeconds(60))
                .build();
    }

    @Override
    public String chat(List<ChatMessage> messages) throws IOException {
        Map<String, Object> body = new HashMap<>();
        body.put("model", model);
        body.put("messages", messages);
        body.put("temperature", 0.2);

        Request request = new Request.Builder()
                .url(ENDPOINT)
                .addHeader("Authorization", "Bearer " + apiKey)
                .post(RequestBody.create(mapper.writeValueAsBytes(body), MediaType.get("application/json")))
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("DeepSeek error: " + response);
            }
            JsonNode node = mapper.readTree(response.body().byteStream());
            return node.at("/choices/0/message/content").asText();
        }
    }
}

