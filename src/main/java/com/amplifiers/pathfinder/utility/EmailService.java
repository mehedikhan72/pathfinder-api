package com.amplifiers.pathfinder.utility;

import com.amplifiers.pathfinder.entity.user.User;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    String apiKey = "2c9117ed3960408e855767a23c6e3563-2b755df8-02d6f02b";
    String domain = "mg.pathphindr.com";

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
