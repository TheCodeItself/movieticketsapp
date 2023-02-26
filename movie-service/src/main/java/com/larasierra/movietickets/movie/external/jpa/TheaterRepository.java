package com.larasierra.movietickets.movie.external.jpa;

import com.larasierra.movietickets.movie.domain.Theater;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TheaterRepository extends JpaRepository<Theater, String> {
    List<Theater> findAllByCity(String city);
}
