package io.volunteerapp.volunteer_app.service;

import com.fasterxml.jackson.core.JsonProcessingException;
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

    // Result class để chứa cả event IDs và explanation
    public static class AiAnalysisResult {
        public List<Integer> eventIds;
        public String explanation;

        public AiAnalysisResult() {
            this.eventIds = new ArrayList<>();
            this.explanation = "";
        }

        public AiAnalysisResult(List<Integer> eventIds, String explanation) {
            this.eventIds = eventIds;
            this.explanation = explanation;
        }
    }

    public AiAnalysisResult analyzeEventsForSearch(List<Event> events, String interests, String location,
            String query) {
        if (events == null || events.isEmpty()) {
            return new AiAnalysisResult(new ArrayList<>(), "Hiện tại chưa có sự kiện nào trong hệ thống.");
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
        String prompt = buildPrompt(eventsData.toString(), interests, location, query);

        // Call Gemini API
        String response = callGeminiApi(prompt);

        // Parse response to get event IDs and explanation
        return parseAiResponse(response);
    }

    private String buildPrompt(String eventsData, String interests, String location, String query) {
        StringBuilder userInput = new StringBuilder();

        if (interests != null && !interests.trim().isEmpty()) {
            userInput.append("Sở thích/Thói quen: ").append(interests).append("\n");
        }
        if (location != null && !location.trim().isEmpty()) {
            userInput.append("Địa điểm mong muốn: ").append(location).append("\n");
        }
        if (query != null && !query.trim().isEmpty()) {
            userInput.append("Yêu cầu thêm: ").append(query).append("\n");
        }

        if (userInput.length() == 0) {
            userInput.append("Gợi ý các hoạt động tình nguyện phù hợp nhất");
        }

        return """
                Bạn là một trợ lý AI thân thiện, chuyên gợi ý các hoạt động tình nguyện từ thiện.
                Phong cách của bạn: ấm áp, động viên, truyền cảm hứng về tinh thần thiện nguyện.

                Dưới đây là danh sách các sự kiện tình nguyện hiện có:
                %s

                Thông tin từ người dùng:
                %s

                NHIỆM VỤ:
                1. Phân tích sở thích, địa điểm và yêu cầu của người dùng
                2. Chọn các sự kiện PHÙ HỢP NHẤT (ưu tiên trả về sự kiện, gần đúng là được)
                3. Viết một đoạn giải thích ngắn gọn, ấm áp, truyền cảm hứng về lý do gợi ý

                QUAN TRỌNG: Trả về ĐÚNG ĐỊNH DẠNG JSON sau:
                {
                    "eventIds": [1, 2, 3],
                    "explanation": "Đoạn giải thích của bạn ở đây..."
                }

                Trong explanation, hãy:
                - Nói về lý do các sự kiện phù hợp với sở thích người dùng
                - Khích lệ tinh thần tình nguyện
                - Giữ ngắn gọn (2-3 câu)
                - Phong cách thân thiện, truyền cảm hứng

                Nếu không tìm thấy sự kiện phù hợp, vẫn trả về JSON với eventIds rỗng và explanation động viên.
                CHỈ TRẢ VỀ JSON, KHÔNG CÓ TEXT KHÁC.
                """.formatted(eventsData, userInput.toString());
    }

    private String callGeminiApi(String prompt) {
        try {
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
            return "{}";
        } catch (Exception e) {
            System.err.println("Error calling Gemini API: " + e.getMessage());
            e.printStackTrace();
            return "{}";
        }
    }

    private AiAnalysisResult parseAiResponse(String response) {
        AiAnalysisResult result = new AiAnalysisResult();
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

            // Parse JSON
            JsonNode rootNode = objectMapper.readTree(cleanResponse);

            // Get event IDs
            JsonNode eventIdsNode = rootNode.path("eventIds");
            if (eventIdsNode.isArray()) {
                for (JsonNode idNode : eventIdsNode) {
                    result.eventIds.add(idNode.asInt());
                }
            }

            // Get explanation
            JsonNode explanationNode = rootNode.path("explanation");
            if (!explanationNode.isMissingNode()) {
                result.explanation = explanationNode.asText();
            } else {
                result.explanation = "Chúng tôi đã tìm thấy một số hoạt động tình nguyện phù hợp với bạn! 💚";
            }

        } catch (JsonProcessingException e) {
            System.err.println("Error parsing Gemini response: " + e.getMessage());
            System.err.println("Response was: " + response);
            result.explanation = "Chúng tôi gặp khó khăn khi phân tích. Hãy thử lại nhé! 💪";
        }
        return result;
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
