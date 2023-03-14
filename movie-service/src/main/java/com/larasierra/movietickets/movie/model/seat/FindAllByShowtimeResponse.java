package com.larasierra.movietickets.movie.model.seat;

import java.util.List;

public record FindAllByShowtimeResponse (
    String showtimeId,
    List<PublicSeatResponse> seats
) { }
