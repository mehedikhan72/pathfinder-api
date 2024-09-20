package com.amplifiers.pathfinder.entity.video;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "video")
public class Video {
    @Id
    @GeneratedValue
    private Integer id;
    private String basename;
    private String format;

    private OffsetDateTime createdAt;

    @Transient
    private String filename;

    @Column(columnDefinition = "text")
    private String presignedUrl;
    private OffsetDateTime presignedUrlExpire;
    @PostLoad
    private void onLoad() {
        this.filename = this.basename + "." + this.format;
    }
}
