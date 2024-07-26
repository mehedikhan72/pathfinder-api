package com.amplifiers.pathfinder.entity.faq;

import com.amplifiers.pathfinder.entity.gig.Gig;
import com.amplifiers.pathfinder.entity.gig.GigRepository;
import com.amplifiers.pathfinder.entity.user.User;
import com.amplifiers.pathfinder.exception.ResourceNotFoundException;
import com.amplifiers.pathfinder.exception.UnauthorizedException;
import com.amplifiers.pathfinder.utility.UserUtility;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.text.translate.NumericEntityUnescaper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FAQService {
    private final FAQRepository faqRepository;
    private final GigRepository gigRepository;

    private final UserUtility userUtility;

    public FAQ createFAQ(FAQCreateRequest request, Integer gigId) {

        Gig gig = gigRepository.findById(gigId)
            .orElseThrow(() -> new ResourceNotFoundException("Gig not found"));

        // make sure the request is coming from the seller.
        User user = userUtility.getCurrentUser();
        User gigSeller = gig.getSeller();

        if (!user.getId().equals(gigSeller.getId())) {
            throw new UnauthorizedException("Only the gig seller can create a FAQ for this gig.");
        }

        var faq = FAQ.builder()
                .gig(gig)
                .question(request.getQuestion())
                .answer(request.getAnswer())
                .build();
        return faqRepository.save(faq);
    }

    public List<FAQ>findAllByGigId(Integer id) {
        return faqRepository.findAllByGigId(id);
    }
}
