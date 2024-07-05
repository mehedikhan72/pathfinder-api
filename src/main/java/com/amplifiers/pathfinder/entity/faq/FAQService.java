package com.amplifiers.pathfinder.entity.faq;

import com.amplifiers.pathfinder.entity.gig.Gig;
import com.amplifiers.pathfinder.entity.gig.GigRepository;
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

    public FAQ createFAQ(FAQCreateRequest request, Integer gig_id) {

        Gig gig = gigRepository.findById(gig_id)
            .orElseThrow(() -> new IllegalArgumentException("Gig not found"));

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
