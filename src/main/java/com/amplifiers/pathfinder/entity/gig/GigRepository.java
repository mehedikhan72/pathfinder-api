package com.amplifiers.pathfinder.entity.gig;

import com.amplifiers.pathfinder.entity.tag.Tag;
import com.amplifiers.pathfinder.entity.user.User;
import com.amplifiers.pathfinder.utility.Category;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface GigRepository extends JpaRepository<Gig, Integer>, JpaSpecificationExecutor<Gig> {
    Optional<Gig> findById(int id);

    List<Gig> findGigsBySeller(User seller);

    List<Gig> findGigsBySellerAndAcceptedTrueAndPausedFalse(User seller);

    Page<Gig> findByCategoryAndAcceptedTrueAndPausedFalse(Pageable pageable, String category);

    interface Specs {
        static Specification<Gig> isAcceptedAndNotPaused() {
            return ((root, query, builder) -> builder.and(builder.isTrue(root.get("accepted")), builder.isFalse(root.get("paused"))));
        }

        static Specification<Gig> isLike(String keyword) {
            String finalKeyword = keyword.toLowerCase();
            return (root, query, builder) -> {
                Join<Gig, Tag> gigTags = root.join("tags", JoinType.LEFT);

                var likeTitle = builder.like(builder.lower(root.get("title")), "%" + finalKeyword + "%");
                var likeDesc = builder.like(builder.lower(root.get("description")), "%" + finalKeyword + "%");
                var likeOffer = builder.like(builder.lower(root.get("offerText")), "%" + finalKeyword + "%");
                var likeTags = builder.like(builder.lower(gigTags.get("name")), "%" + finalKeyword + "%");

                return builder.or(likeTitle, likeOffer, likeDesc, likeTags);
            };
        }

        static Specification<Gig> isRatingAbove(Float ratingAbove) {
            return (root, query, builder) -> {
                if (ratingAbove == 0) return null;
                return builder.greaterThanOrEqualTo(root.get("rating"), ratingAbove);
            };
        }

        static Specification<Gig> isPriceUnder(Float budget) {
            return (root, query, builder) -> builder.lessThanOrEqualTo(root.get("price"), budget);
        }

        static Specification<Gig> inCategory(Category category) {
            return (root, query, builder) -> builder.like(root.get("category"), category.name());
        }

        static Specification<Gig> hasTags(List<Tag> tags) {
            return (root, query, builder) -> {
                Predicate[] predicates = tags.stream().map(tag -> builder.isMember(tag, root.get("tags"))).toArray(Predicate[]::new);

                return builder.and(predicates);
            };
        }
    }

    Page<Gig> findByAccepted(boolean accepted, Pageable pageable);
}
