package com.amplifiers.pathfinder.entity.tag;

import com.amplifiers.pathfinder.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TagService {
    private final TagRepository tagRepository;

    public Tag createTag(TagCreateRequest tagCreateRequest) {
        if (tagCreateRequest.getName().isBlank()) {
            throw new ValidationException("Tag cannot be blank.");
        }
        Tag tag = Tag.builder()
                .name(tagCreateRequest.getName())
                .build();
        return tagRepository.save(tag);
    }

    public List<Tag> findAll() {
        return tagRepository.findAll();
    }

    public Optional<Tag> findByName(String name) {
        return tagRepository.findByName(name);
    }

    public List<Tag> findByQuery(String query) {
        if (query == null) return tagRepository.findAll();
        return tagRepository.findByQuery(query);
    }
}
