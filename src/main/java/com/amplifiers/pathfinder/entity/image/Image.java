package com.amplifiers.pathfinder.entity.image;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "image")
@JsonSerialize(using = ImageSerializer.class)
public class Image {
    @Id
    @GeneratedValue
    private Integer id;
    private String basename;
    private String format;

    private OffsetDateTime createdAt;

    @Transient
    private String filename;

    @PostLoad
    private void onLoad(){
        this.filename = this.basename + "." + this.format;
    }

}

class ImageSerializer extends StdSerializer<Image> {
    public ImageSerializer() {
        this(null);
    }

    public ImageSerializer(Class<Image> t) {
        super(t);
    }

    @Override
    public void serialize(Image value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeString(value.getFilename());
    }

}