package com.amplifiers.pathfinder.zoom;

import com.amplifiers.pathfinder.entity.user.User;
import com.amplifiers.pathfinder.entity.user.UserRepository;
import io.github.cdimascio.dotenv.Dotenv;
import jakarta.validation.constraints.NotNull;
import java.util.Base64;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class ZoomAuthenticationHelper {

    private final Dotenv dotenv = Dotenv.configure().load();
    private final String zoomClientId = dotenv.get("ZOOM_CLIENT_ID");

    private final String zoomClientSecret = dotenv.get("ZOOM_CLIENT_SECRET");

    private final String zoomIssuerUrl = dotenv.get("ZOOM_ISSUER");
    private final Integer tokenExpirationInMinutes = 20;
    private final Integer oneSecondInMilliseconds = 1000;

    @Autowired
    private final RestTemplate restTemplate;

    private final UserRepository userRepository;

    public String getAuthenticationToken(User user) throws Exception {
        ZoomAuthResponse res;

        if (user.getZoomAuthResponse() == null) {
            try {
                res = fetchToken(user.getZoomAuthorizationCode());
            } catch (Exception e) {
                user.setZoomAuthorizationCode(null);
                user.setZoomAuthResponse(null);
                userRepository.save(user);
                throw e;
            }

            user.setZoomAuthResponse(res);
            userRepository.save(user);

            return user.getZoomAuthResponse().getAuthToken();
        }

        if (checkIfTokenWillExpire(user.getZoomAuthResponse())) {
            try {
                res = refreshToken(user.getZoomAuthResponse());
            } catch (Exception e) {
                user.setZoomAuthorizationCode(null);
                user.setZoomAuthResponse(null);
                userRepository.save(user);
                throw e;
            }

            user.setZoomAuthResponse(res);
            userRepository.save(user);
        }

        return user.getZoomAuthResponse().getAuthToken();
    }

    //determine new token should be retrieved
    private boolean checkIfTokenWillExpire(ZoomAuthResponse zoomAuthResponse) {
        Calendar now = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

        long differenceInMillis = zoomAuthResponse.getExpiresIn() - now.getTimeInMillis();

        // Token is already expired
        if (differenceInMillis < 0) {
            return true;
        }
        //Token has less than 20 minutes to expire
        return TimeUnit.MILLISECONDS.toMinutes(differenceInMillis) < tokenExpirationInMinutes;
    }

    private ZoomAuthResponse fetchToken(String authCode) {
        String credentials = zoomClientId + ":" + zoomClientSecret;
        String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.add("Authorization", "Basic " + encodedCredentials);
        headers.add("Host", "zoom.us");

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("code", authCode);
        map.add("grant_type", "authorization_code");
        map.add("redirect_uri", "http://localhost:5173/zoom-auth");

        return getZoomAuthResponse(headers, map);
    }

    private ZoomAuthResponse refreshToken(ZoomAuthResponse zoomAuthResponse) {
        String credentials = zoomClientId + ":" + zoomClientSecret;
        String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.add("Authorization", "Basic " + encodedCredentials);
        headers.add("Host", "zoom.us");

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("refresh_token", zoomAuthResponse.getRefreshToken());
        map.add("grant_type", "refresh_token");

        return getZoomAuthResponse(headers, map);
    }

    @NotNull
    private ZoomAuthResponse getZoomAuthResponse(HttpHeaders headers, MultiValueMap<String, String> map) {
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);
        String url = zoomIssuerUrl + "/token";

        ZoomAuthResponse res = restTemplate.exchange(url, HttpMethod.POST, entity, ZoomAuthResponse.class).getBody();

        Calendar now = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        res.setExpiresIn(res.getExpiresIn() * oneSecondInMilliseconds + now.getTimeInMillis());

        return res;
    }
}
