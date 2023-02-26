package com.larasierra.movietickets.movie.domain;

import com.larasierra.movietickets.shared.domain.BaseEntity;
import com.larasierra.movietickets.shared.validation.ValidId;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "showtime")
@Entity
public class Showtime extends BaseEntity<String> {

    @ValidId
    @Id
    private String showtimeId;

    @ValidId
    @NotNull
    private String screenId;

    @ValidId
    @NotNull
    private String theaterId;

    @ValidId
    @NotNull
    private String movieId;

    @NotNull
    private OffsetDateTime startDate;

    @Min(100)
    @NotNull
    private Integer standardPrice;

    @Min(100)
    @NotNull
    private Integer preferentialPrice;

    @NotNull
    private OffsetDateTime createdAt;

    @Override
    public String getId() {
        return showtimeId;
    }
}
