package com.amplifiers.pathfinder.zoom;

import com.amplifiers.pathfinder.entity.session.Session;
import com.amplifiers.pathfinder.entity.user.User;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class ZoomApiService {

    private ZoomAuthenticationHelper zoomAuthenticationHelper;

    private final RestTemplate restTemplate;

    private final String zoomApiUrl = "https://api.zoom.us";

    private static final String BEARER_AUTHORIZATION = "Bearer %s";

    private static final String ZOOM_USER_BASE_URL = "%s/v2/users";

    public HashMap<String, String> startMeeting(User user, Session session) {
        ResponseEntity<String> response = null;
        try {
            HttpHeaders authHeader = createBearerAuthHeader(zoomAuthenticationHelper.getAuthenticationToken(user));

            HashMap<String, String> body = new HashMap<>();
            body.put("agenda", "Session ID : " + session.getId());
            body.put("type", "1");

            HttpEntity<HashMap<String, String>> entity = new HttpEntity<>(body, authHeader);
            response = restTemplate.exchange(getUserMeetingListUrl(), HttpMethod.POST, entity, String.class);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(response.getBody());

            HashMap<String, String> map = new HashMap<>();
            map.put("start_url", jsonNode.get("start_url").asText());
            map.put("join_url", jsonNode.get("join_url").asText());

            return map;
        } catch (Exception e) {
            //sout is used for demo purposes you could use @Slf4j
            System.out.printf("Unable to get all meetings due to %s. Response code: %d%n", e.getMessage(), response.getStatusCode());
            e.printStackTrace();
        }
        return null;
    }

    public String getUserMeetingListUrl() {
        return String.format(ZOOM_USER_BASE_URL, zoomApiUrl) + "/me/meetings";
    }

    public static HttpHeaders createBearerAuthHeader(String token) {
        HttpHeaders headers = createHTTPHeader();
        String authToken = String.format(BEARER_AUTHORIZATION, token);
        headers.set(HttpHeaders.AUTHORIZATION, authToken);
        return headers;
    }

    private static HttpHeaders createHTTPHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}
