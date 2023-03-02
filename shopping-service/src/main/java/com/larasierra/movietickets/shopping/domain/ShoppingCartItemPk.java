package com.larasierra.movietickets.shopping.domain;

import com.larasierra.movietickets.shared.validation.ValidId;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Embeddable
public class ShoppingCartItemPk implements Serializable {
    @Serial
    private static final long serialVersionUID = 4212907383302304486L;

    @ValidId
    @NotNull
    @Column(name = "user_id")
    private String userId;

    @NotNull
    @Column(name = "seat_id")
    private String seatId;
}
