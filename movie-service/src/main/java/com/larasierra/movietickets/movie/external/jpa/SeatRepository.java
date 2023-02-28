package com.larasierra.movietickets.movie.external.jpa;

import com.larasierra.movietickets.movie.domain.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, String> {
    List<Seat> findAllByShowtimeId(String showtimeId);
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.MANDATORY)
    @Modifying
    @Query("""
        update Seat s
        set s.purchaseToken = :userToken
        where s.seatId = :seatId and s.purchaseToken = :seatToken and s.available = true""")
    int reserveForCart(@Param("seatId") String seatId, @Param("seatToken") String seatToken, @Param("userToken") String userToken);

    @Transactional(isolation = Isolation.READ_COMMITTED)
    @Modifying
    @Query("""
        update Seat s
        set s.purchaseToken = null
        where s.seatId = :seatId and s.purchaseToken = :userToken and s.available = true""")
    void removePurchaseToken(@Param("seatId") String seatId, @Param("userToken") String userToken);

    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.MANDATORY)
    @Query(value = """
        update seat
        set available = false
        where seat_id IN (:seats) and purchase_token = :userToken and available = true
        returning *""", nativeQuery = true)
    List<Seat> reserveForCart(@Param("seats") List<String> seats, @Param("userToken") String userToken);
}
