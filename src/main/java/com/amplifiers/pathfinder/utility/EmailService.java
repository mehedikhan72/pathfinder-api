package com.amplifiers.pathfinder.utility;

import com.amplifiers.pathfinder.entity.user.User;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

//    private final Dotenv dotenv = Dotenv.configure().load();
//    private final String apiKey = dotenv.get("MAILGUN_API_KEY");
//    private final String domain = dotenv.get("MAILGUN_DOMAIN");

    @Value("${MAILGUN_API_KEY}")
    private String apiKey;

    @Value("${MAILGUN_DOMAIN}")
    private String domain;

    public void sendEmail(User user, String subject, String message) throws UnirestException {

        HttpResponse<String> response = Unirest.post("https://api.eu.mailgun.net/v3/" + domain + "/messages")
                .basicAuth("api", apiKey)
                .queryString("from", "Team pathPhindr <info@mg.pathphindr.com>")
                .queryString("to", user.getEmail())
                .queryString("subject", subject)
                .queryString("text", message)
                .asString();

        System.out.println("Response Status: " + response.getStatus());

        System.out.println("Response Body: " + response.getBody());
    }
}
