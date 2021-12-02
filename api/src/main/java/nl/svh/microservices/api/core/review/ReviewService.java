package nl.svh.microservices.api.core.review;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


public interface ReviewService {
    Mono<Review> createReview(Review body);

    @GetMapping(value = "/review", produces = "application/json")
    Flux<Review> getReviews(@RequestParam(name = "productId") int productId);

    Mono<Void> deleteReviews(@RequestParam int productId);
}
