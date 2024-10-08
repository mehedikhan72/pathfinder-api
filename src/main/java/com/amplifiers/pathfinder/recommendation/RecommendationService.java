package com.amplifiers.pathfinder.recommendation;

import com.amplifiers.pathfinder.entity.gig.Gig;
import com.recombee.api_client.RecombeeClient;
import com.recombee.api_client.api_requests.*;
import com.recombee.api_client.bindings.RecommendationResponse;
import com.recombee.api_client.util.Region;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final Dotenv dotenv = Dotenv.configure().load();
    private final String privateToken = dotenv.get("RECOMBEE_PRIVATE_TOKEN");
    private final String databaseId = dotenv.get("RECOMBEE_DATABASE_ID");


//    fuck it. let's just do it like that.

//    private final String privateToken = "1234";
//    private final String databaseId = "1234";

    private final RecombeeClient client = new RecombeeClient(
            databaseId,
            privateToken
    ).setRegion(Region.EU_WEST);

    public void sendGigValues(Gig gig) {
        System.out.println("private token - " + privateToken);
        try {
            client.send(new SetItemValues(gig.getId().toString(),
                            new HashMap<String, Object>() {{
                                put("title", gig.getTitle());
                                put("category", gig.getCategory());
                                put("price", gig.getPrice());
                                put("sellerId", gig.getSeller().getId());
                                put("description", gig.getDescription());
                                put("tags", gig.getTags());
                                put("accepted", gig.isAccepted());
                                put("paused", gig.isPaused());
                            }}
                    ).setCascadeCreate(true)
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateItem(Integer itemId, HashMap<String, Object> values) {
        try {
            client.send(new SetItemValues(itemId.toString(), values));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addDetailView(Integer userId, Integer gigId) {
        try {
            System.out.println("private token - " + privateToken);
            client.send(new AddDetailView(userId.toString(), gigId.toString())
                    .setCascadeCreate(true)
            );
        } catch (Exception e) {
            System.out.println("Error found in add detail");
            throw new RuntimeException("error adding detail to recombee");
        }
    }

    public void addPurchaseView(Integer gigId, Integer userId, String recommId) {
        try {
            client.send(new AddPurchase(userId.toString(), gigId.toString())
                    .setCascadeCreate(true)
                    .setRecommId(recommId)
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public RecommendationResponse getRecommendationsForUser(Integer userId, Integer numItems, String scenario) {
        try {
            if (scenario != null) {
                if (userId == -1) {
                    // popular gigs for anonymous users
                    return client.send(new RecommendItemsToUser("dummyUser123", numItems)
                            .setScenario(scenario)
                            .setCascadeCreate(true)
                            .setFilter("'accepted' == true AND 'paused' == false")
                    );
                } else {
                    return client.send(new RecommendItemsToUser(userId.toString(), numItems)
                            .setScenario(scenario)
                            .setCascadeCreate(true)
                            .setFilter("'accepted' == true AND 'paused' == false")
                    );
                }

            } else {
                // basic recommendation
                return client.send(new RecommendItemsToUser(userId.toString(), numItems)
                        .setFilter("'accepted' == true AND 'paused' == false")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public RecommendationResponse getRecommendationsForItem(Integer itemId, Integer userId, Integer numItems, String scenario) {
        try {
            if (scenario != null) {
                // for similar items.
                return client.send(new RecommendItemsToItem(itemId.toString(), userId.toString(), numItems)
                        .setScenario(scenario)
                        .setCascadeCreate(true)
                        .setFilter("'accepted' == true AND 'paused' == false")
                );
            } else {
                return client.send(new RecommendItemsToItem(itemId.toString(), userId.toString(), numItems)
                        .setCascadeCreate(true)
                        .setFilter("'accepted' == true AND 'paused' == false")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public RecommendationResponse search(String query, Integer userId, Integer numItems) {
        try {
            return client.send(new SearchItems(userId.toString(), query, numItems)
                    .setCascadeCreate(true)
                    .setFilter("'accepted' == true AND 'paused' == false")
            );
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public RecommendationResponse getNextRecommendationsForUser(String recommId, Integer numItems) {
        try {
            return client.send(new RecommendNextItems(recommId, numItems));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
