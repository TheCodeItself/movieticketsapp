package com.larasierra.movietickets.shopping.external.apiclient;

import com.larasierra.movietickets.shopping.model.showtime.ShowtimeApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("movie-service")
public interface ShowtimeApiClient {
    @GetMapping("/showtime/{id}")
    ShowtimeApiResponse findById(@PathVariable("id") String showtimeId);
}
