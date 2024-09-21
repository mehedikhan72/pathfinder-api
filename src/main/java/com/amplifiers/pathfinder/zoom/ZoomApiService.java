package com.amplifiers.pathfinder.zoom;

import com.amplifiers.pathfinder.entity.session.Session;
import com.amplifiers.pathfinder.entity.user.User;
import com.amplifiers.pathfinder.entity.user.UserService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class ZoomApiService {

    @Autowired
    private ZoomAuthenticationHelper zoomAuthenticationHelper;

    @Autowired
    RestTemplate restTemplate;

    @Value("${zoom.oauth2.api-url}")
    private String zoomApiUrl;

    private static String BEARER_AUTHORIZATION = "Bearer %s";

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
            System.out.println(
                String.format("Unable to get all meetings due to %s. Response code: %d", e.getMessage(), response.getStatusCode())
            );
            e.printStackTrace();
        }
        return null;
    }

    public ResponseEntity<String> getAllMeetings(User user) {
        ResponseEntity<String> response = null;
        try {
            HttpHeaders authHeader = createBearerAuthHeader(zoomAuthenticationHelper.getAuthenticationToken(user));
            HttpEntity<String> entity = new HttpEntity<>(authHeader);
            response = restTemplate.exchange(getUserMeetingListUrl(), HttpMethod.GET, entity, String.class);

            return response;
        } catch (Exception e) {
            //sout is used for demo purposes you could use @Slf4j
            System.out.println(
                String.format("Unable to get all meetings due to %s. Response code: %d", e.getMessage(), response.getStatusCode())
            );
            e.printStackTrace();
        }
        return response;
    }

    public String getUserMeetingListUrl() {
        StringBuilder sb = new StringBuilder(String.format(ZOOM_USER_BASE_URL, zoomApiUrl));
        sb.append("/me/meetings");
        return sb.toString();
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
