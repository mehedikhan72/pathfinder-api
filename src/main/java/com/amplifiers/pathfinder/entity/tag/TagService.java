package com.amplifiers.pathfinder.entity.tag;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TagService {
    private final TagRepository tagRepository;

    public Tag createTag(TagCreateRequest tagCreateRequest) {
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
        return tagRepository.findByQuery(query);
    }
}
