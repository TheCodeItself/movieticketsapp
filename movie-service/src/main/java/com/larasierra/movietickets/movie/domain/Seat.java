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
@Table(name = "seat")
@Entity
public class Seat extends BaseEntity<String> {
    //@ValidId
    @Id
    private String seatId;

    @ValidId
    @NotNull
    private String showtimeId;

    @NotNull
    private Boolean available;

    @Size(min = 5)
    @NotNull
    private String purchaseToken;

    @ValidId
    private String orderId;

    @NotNull
    private OffsetDateTime createdAt;

    @Override
    public String getId() {
        return seatId;
    }
}
