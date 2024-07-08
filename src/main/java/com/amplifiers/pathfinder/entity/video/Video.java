package com.amplifiers.pathfinder.entity.video;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

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

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime created_at;

    @Transient
    private String filename;

    @Column(columnDefinition="text")
    private String presignedUrl;
    private LocalDateTime presignedUrlExpire;
    @PostLoad
    private void onLoad(){
        this.filename = this.basename + "." + this.format;
    }
}