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
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "theater")
@Entity
public class Theater extends BaseEntity<String> {

    @ValidId
    @Id
    private String theaterId;

    @Size(min = 2, max = 250)
    @NotNull
    private String theaterName;

    @Size(min = 2, max = 250)
    @NotNull
    private String city;

    @Size(min = 2, max = 500)
    @NotNull
    private String direction;

    @NotNull
    private OffsetDateTime createdAt;

    @Override
    public String getId() {
        return theaterId;
    }
}
