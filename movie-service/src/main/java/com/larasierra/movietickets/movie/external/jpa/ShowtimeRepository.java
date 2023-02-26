package com.larasierra.movietickets.movie.external.jpa;

import com.larasierra.movietickets.movie.domain.Showtime;
import jakarta.annotation.Nullable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.OffsetDateTime;
import java.util.List;

public interface ShowtimeRepository extends JpaRepository<Showtime, String> {
    /**
     * Finds all showtimes for the specified theaterId that match the specified criteria. If the movieId parameter is
     * null, all movies playing at the theater are included in the result.
     *
     * @param theaterId the id of the theater where the showtimes are being held
     * @param movieId the id of the movie to include in the result, or null to include all movies (optional)
     * @param startDate the minimum startDate for the showtimes to be included in the result (inclusive)
     * @param endDate the maximum startDate for the showtimes to be included in the result (exclusive)
     * @return a list of Showtime entities that match the specified criteria
     */
    @Query("""
        select s
        from Showtime s where s.theaterId = ?1
                        and (?2 is null or s.movieId = ?2)
                        and s.startDate >= ?3
                        and s.startDate < ?4
    """)
    List<Showtime> findAllByTheaterIdAndStartDate(String theaterId, @Nullable String movieId, OffsetDateTime startDate, OffsetDateTime endDate);
}
