package com.amplifiers.pathfinder.utility;

import com.amplifiers.pathfinder.entity.user.User;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

//    private final Dotenv dotenv = Dotenv.configure().load();
//    private final String apiKey = dotenv.get("MAILGUN_API_KEY");
//    private final String domain = dotenv.get("MAILGUN_DOMAIN");

    private final String apiKey;
    private final String domain;

    public EmailService() {
        this.apiKey = null;
        this.domain = null;
    }

    // Constructor with environment property extraction
    public EmailService(Environment env) {
        this.apiKey = env.getProperty("mailgun.api-key");
        this.domain = env.getProperty("mailgun.domain");
    }

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
