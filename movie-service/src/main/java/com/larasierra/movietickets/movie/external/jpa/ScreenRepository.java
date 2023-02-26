package com.larasierra.movietickets.movie.external.jpa;

import com.larasierra.movietickets.movie.domain.Screen;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScreenRepository extends JpaRepository<Screen, String> {
    List<Screen> findAllByTheaterId(String theaterId);
}
