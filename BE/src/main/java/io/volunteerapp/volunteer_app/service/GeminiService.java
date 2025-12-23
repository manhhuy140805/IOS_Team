package io.volunteerapp.volunteer_app.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.volunteerapp.volunteer_app.model.Event;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GeminiService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.model}")
    private String model;

    public GeminiService() {
        this.webClient = WebClient.builder()
                .baseUrl("https://generativelanguage.googleapis.com")
                .build();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Analyze events and return matching event IDs based on user query
     */
    public List<Integer> analyzeEventsForSearch(List<Event> events, String userQuery) {
        if (events == null || events.isEmpty()) {
            return new ArrayList<>();
        }

        // Build events data string for prompt
        StringBuilder eventsData = new StringBuilder();
        eventsData.append("[\n");
        for (int i = 0; i < events.size(); i++) {
            Event event = events.get(i);
            eventsData.append("  {\n");
            eventsData.append("    \"id\": ").append(event.getId()).append(",\n");
            eventsData.append("    \"title\": \"").append(escapeJson(event.getTitle())).append("\",\n");
            eventsData.append("    \"description\": \"")
                    .append(escapeJson(event.getDescription() != null ? event.getDescription() : "")).append("\",\n");
            eventsData.append("    \"location\": \"")
                    .append(escapeJson(event.getLocation() != null ? event.getLocation() : "")).append("\",\n");
            eventsData.append("    \"category\": \"")
                    .append(escapeJson(event.getCategory() != null ? event.getCategory() : "")).append("\",\n");
            eventsData.append("    \"status\": \"")
                    .append(escapeJson(event.getStatus() != null ? event.getStatus() : "")).append("\",\n");
            eventsData.append("    \"eventStartTime\": \"")
                    .append(event.getEventStartTime() != null ? event.getEventStartTime().toString() : "")
                    .append("\",\n");
            eventsData.append("    \"eventEndTime\": \"")
                    .append(event.getEventEndTime() != null ? event.getEventEndTime().toString() : "").append("\",\n");
            eventsData.append("    \"rewardPoints\": ")
                    .append(event.getRewardPoints() != null ? event.getRewardPoints() : 0).append("\n");
            eventsData.append("  }");
            if (i < events.size() - 1) {
                eventsData.append(",");
            }
            eventsData.append("\n");
        }
        eventsData.append("]");

        // Build prompt
        String prompt = buildPrompt(eventsData.toString(), userQuery);

        // Call Gemini API
        String response = callGeminiApi(prompt);

        // Parse response to get event IDs
        return parseEventIds(response);
    }

    private String buildPrompt(String eventsData, String userQuery) {
        return """
                Bạn là một AI assistant giúp tìm kiếm sự kiện tình nguyện.

                Dưới đây là danh sách các sự kiện hiện có trong hệ thống:
                %s

                Yêu cầu của người dùng: "%s"

                Hãy phân tích yêu cầu và chọn các sự kiện phù hợp nhất dựa trên:
                - Tiêu đề (title)
                - Mô tả (description)
                - Địa điểm (location)
                - Danh mục (category)
                - Thời gian (eventStartTime, eventEndTime)
                - Điểm thưởng (rewardPoints)

                Trả về ONLY một JSON array chứa các ID của sự kiện phù hợp, ví dụ: [1, 5, 12]
                Nếu không có sự kiện nào phù hợp, trả về: []
                Chỉ trả về JSON array, không có text giải thích nào khác.
                """.formatted(eventsData, userQuery);
    }

    private String callGeminiApi(String prompt) {
        try {
            // Build request body
            Map<String, Object> requestBody = new HashMap<>();

            Map<String, Object> content = new HashMap<>();
            Map<String, String> part = new HashMap<>();
            part.put("text", prompt);
            content.put("parts", List.of(part));
            requestBody.put("contents", List.of(content));

            String url = "/v1beta/models/" + model + ":generateContent?key=" + apiKey;

            String response = webClient.post()
                    .uri(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            // Parse response to extract text
            if (response != null) {
                JsonNode rootNode = objectMapper.readTree(response);
                JsonNode candidatesNode = rootNode.path("candidates");
                if (candidatesNode.isArray() && candidatesNode.size() > 0) {
                    JsonNode contentNode = candidatesNode.get(0).path("content");
                    JsonNode partsNode = contentNode.path("parts");
                    if (partsNode.isArray() && partsNode.size() > 0) {
                        return partsNode.get(0).path("text").asText();
                    }
                }
            }
            return "[]";
        } catch (Exception e) {
            System.err.println("Error calling Gemini API: " + e.getMessage());
            e.printStackTrace();
            return "[]";
        }
    }

    private List<Integer> parseEventIds(String response) {
        List<Integer> eventIds = new ArrayList<>();
        try {
            // Clean up response - remove markdown code blocks if present
            String cleanResponse = response.trim();
            if (cleanResponse.startsWith("```json")) {
                cleanResponse = cleanResponse.substring(7);
            }
            if (cleanResponse.startsWith("```")) {
                cleanResponse = cleanResponse.substring(3);
            }
            if (cleanResponse.endsWith("```")) {
                cleanResponse = cleanResponse.substring(0, cleanResponse.length() - 3);
            }
            cleanResponse = cleanResponse.trim();

            // Parse JSON array
            eventIds = objectMapper.readValue(cleanResponse, new TypeReference<List<Integer>>() {
            });
        } catch (JsonProcessingException e) {
            System.err.println("Error parsing Gemini response: " + e.getMessage());
            System.err.println("Response was: " + response);
        }
        return eventIds;
    }

    private String escapeJson(String text) {
        if (text == null)
            return "";
        return text.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
