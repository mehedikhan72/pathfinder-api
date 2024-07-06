package com.amplifiers.pathfinder.entity.image;

import com.amplifiers.pathfinder.entity.gig.Gig;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "image")
public class Image {
    @Id
    @GeneratedValue
    private Integer id;
    private String basename;
    private String format;

    @Transient
    private String filename;

    @PostLoad
    private void onLoad(){
        this.filename = this.basename + "." + this.format;
    }
}