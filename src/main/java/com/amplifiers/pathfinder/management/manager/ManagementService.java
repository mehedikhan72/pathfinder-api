package com.amplifiers.pathfinder.management.manager;

import com.amplifiers.pathfinder.entity.gig.Gig;
import com.amplifiers.pathfinder.entity.gig.GigRepository;
import com.amplifiers.pathfinder.recommendation.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.amplifiers.pathfinder.exception.ResourceNotFoundException;

import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class ManagementService {
    private final GigRepository gigRepository;
    private final RecommendationService recommendationService;

    public Page<Gig> getUnaccpetedGigs(Pageable pageable) {
        return gigRepository.findByAccepted(false, pageable);
    }

    public String acceptGig(Integer gigId) {
        Gig gig = gigRepository.findById(gigId)
                .orElseThrow(() -> new ResourceNotFoundException("Gig with id " + gigId + " not found."));
        gig.setAccepted(true);
        gigRepository.save(gig);

        // updates the accepted field in recombee database.
        HashMap<String, Object> values = new HashMap<>();
        values.put("accepted", true);
        recommendationService.updateItem(gigId, values);

        return "Gig with id " + gigId + " accepted.";
    }
}
