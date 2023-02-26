package com.larasierra.movietickets.movie.domain;

import com.larasierra.movietickets.shared.domain.BaseEntity;
import com.larasierra.movietickets.shared.validation.ValidId;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "movie")
@Entity
public class Movie extends BaseEntity<String> {

    @ValidId
    @Id
    private String movieId;

    @Size(min = 2, max = 1000)
    @NotNull
    private String title;

    @Size(min = 2, max = 250)
    @NotNull
    private String country;

    @Size(min = 2, max = 500)
    @NotNull
    private String genre;

    @NotNull
    private Integer runtime;

    @Size(min = 2, max = 100)
    @NotNull
    private String rating;

    @Size(max = 2000)
    @NotNull
    private String synopsis;

    @NotNull
    private OffsetDateTime releaseDate;

    @NotNull
    private OffsetDateTime createdAt;

    @Override
    public String getId() {
        return movieId;
    }
}
