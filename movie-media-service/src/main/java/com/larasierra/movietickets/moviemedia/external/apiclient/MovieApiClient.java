package com.larasierra.movietickets.moviemedia.external.apiclient;

import com.larasierra.movietickets.moviemedia.model.movie.MovieApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "movie-service")
public interface MovieApiClient {

    @GetMapping("/movie/{id}")
    MovieApiResponse findById(@PathVariable("id") String movieId);

}
