package com.larasierra.movietickets.movie.domain;

import com.larasierra.movietickets.shared.domain.BaseEntity;
import com.larasierra.movietickets.shared.validation.ValidId;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
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
@Table(name = "screen")
@Entity
public class Screen extends BaseEntity<String> {
    @ValidId
    @Id
    private String screenId;

    @ValidId
    @NotNull
    private String theaterId;

    @Size(min = 1, max = 10)
    @NotNull
    private String screenName;

    @Max(500L)
    @NotNull
    private Short seatingCapacity;

    @NotNull
    private OffsetDateTime createdAt;

    @Override
    public String getId() {
        return screenId;
    }
}
