package com.larasierra.movietickets.movie.external.jpa;

import com.larasierra.movietickets.movie.domain.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieRepository extends JpaRepository<Movie, String> {
}
