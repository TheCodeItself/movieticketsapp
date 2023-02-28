package com.larasierra.movietickets.movie.application;

import com.larasierra.movietickets.movie.domain.Seat;
import com.larasierra.movietickets.movie.external.jpa.SeatRepository;
import com.larasierra.movietickets.movie.model.seat.*;
import com.larasierra.movietickets.shared.exception.AppResourceLockedException;
import com.larasierra.movietickets.shared.util.IdUtil;
import jakarta.transaction.Transactional;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class SeatService {
    private final SeatRepository seatRepository;

    public SeatService(SeatRepository seatRepository) {
        this.seatRepository = seatRepository;
    }

    public void createSeats(String showtimeId) {
        // create seats for the showtime
        // every seat id will start with the showtime, following by the seat number, separated by a "-" (0SRT4T4FR9L27-A14)
        // the seat number by default will be the row letter following by the seat number in that row (2nd row and 5th seat will be B5)
    }

    @PreAuthorize("hasRole('enduser')")
    @Transactional
    public void reserveForCart(String seatId, ReserveSeatForCartRequest request) {
        int count = seatRepository.reserveForCart(seatId, request.seatToken(), request.purchaseToken());
        if (count < 1) {
            throw new AppResourceLockedException();
        }
    }

    @PreAuthorize("hasRole('enduser')")
    public void removePurchaseToken(String seatId, RemovePurchaseTokenRequest request) {
        seatRepository.removePurchaseToken(seatId, request.purchaseToken());
    }

    @PreAuthorize("hasRole('enduser')")
    @Transactional
    public List<FullSeatResponse> reserveForOrder(ReserveSeatForOrderRequest request) {
        List<Seat> seats = seatRepository.reserveForCart(request.seats(), request.userToken());

        if (seats.size() != request.seats().size()) {
            throw new AppResourceLockedException();
        }

        return seats.stream()
                .map(this::toFullResponse)
                .toList();
    }

    /**
     * Find the seat information for the given seatId. The seat will include the purchase token if it has expired and is still available for be purchase.
     * @param seatId the id of the seat
     * @return the seat for the given id
     */
    @PreAuthorize("permitAll()")
    public Optional<PublicSeatResponse> findById(String seatId) {
        return seatRepository.findById(seatId)
                .map(this::toPublicResponse);
    }

    /**
     * Find all the seats for a given showtimeId. Each seat will include the purchase token if it has expired and is still available for be purchase.
     * @param showtimeId the id of the showtime to which the seats belong
     * @return a list of the seats found
     */
    @PreAuthorize("permitAll()")
    public List<PublicSeatResponse> findAllByShowtimeId(String showtimeId) {
        return seatRepository.findAllByShowtimeId(showtimeId).stream()
                .map(this::toPublicResponse)
                .toList();
    }

    private FullSeatResponse toFullResponse(Seat seat) {
        return new FullSeatResponse(
                seat.getSeatId(),
                seat.getShowtimeId(),
                seat.getAvailable(),
                seat.getPurchaseToken(),
                seat.getOrderId(),
                seat.getCreatedAt()
        );
    }

    private PublicSeatResponse toPublicResponse(Seat seat) {
        String purchaseToken = shouldIncludePurchaseToken(seat) ? seat.getPurchaseToken()
                                                        : null;
        return new PublicSeatResponse(
                seat.getSeatId(),
                seat.getShowtimeId(),
                seat.getAvailable(),
                purchaseToken,
                seat.getCreatedAt()
        );
    }

    /**
     * Determine if the purchase token needs to be added to the response. This method returns false when the
     * seat is not available, or it has an orderId, or it does not have a token, or if the token has not expired yet.
     * It will return true only if the user that generated the token at first did not complete the purchase before the
     * token expired
     * @param seat the seat to be evaluated
     * @return true if the purchase token must be included in the response
     */
    private boolean shouldIncludePurchaseToken(Seat seat) {
        // if it is not available, or it has an orderId, or it does not have a token, do not include the token
        if (!seat.getAvailable() || seat.getOrderId() != null || seat.getPurchaseToken() == null) {
            return false;
        }

        String timeSortedId = seat.getPurchaseToken().substring(0, 13);

        Instant instant = IdUtil.extractInstant(timeSortedId);
        Instant expiration = instant.plusSeconds(60 * 5);

        // it must include the token only if expiration date is before the current date
        return expiration.isBefore(Instant.now());
    }
}
