package com.larasierra.movietickets.moviemedia.external.jpa;

import com.larasierra.movietickets.moviemedia.domain.MovieMedia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface MovieMediaRepository extends JpaRepository<MovieMedia, String> {

    @Transactional
    @Modifying
    @Query("update MovieMedia m set m.available = true where m.movieMediaId = :movieMediaId")
    void markAvailable(@Param("movieMediaId") String movieMediaId);
    @Transactional
    @Modifying
    @Query("delete from MovieMedia m where m.movieMediaId = ?1")
    void deleteByMovieMediaId(String movieMediaId);

    List<MovieMedia> findAllByMovieId(String movieId);
}
