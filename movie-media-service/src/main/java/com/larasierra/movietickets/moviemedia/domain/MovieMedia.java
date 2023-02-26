package com.larasierra.movietickets.moviemedia.domain;

import com.larasierra.movietickets.shared.domain.BaseEntity;
import com.larasierra.movietickets.shared.validation.ValidId;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "movie_media")
@Entity
public class MovieMedia extends BaseEntity<String> {

    @ValidId
    @Id
    private String movieMediaId;

    @ValidId
    @NotNull
    private String movieId;

    @Size(min = 1, max = 1000)
    @NotNull
    private String url;

    @Size(min = 1, max = 1000)
    @NotNull
    private String filename;

    @Size(min = 1, max = 255)
    @NotNull
    private String contentType;

    @Min(1)
    @NotNull
    private Long fileLength;

    @Convert(converter = MovieMediaRoleConverter.class)
    @NotNull
    private MovieMediaRole mediaRole;

    @NotNull
    private Boolean available;

    @NotNull
    private OffsetDateTime createdAt;

    @Override
    public String getId() {
        return movieMediaId;
    }
}
