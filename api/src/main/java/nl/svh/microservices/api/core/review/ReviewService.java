package nl.svh.microservices.api.core.review;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


public interface ReviewService {

    @GetMapping(value = "/review", produces = "application/json")
    List<Review> getReviews(@RequestParam(name = "productId") int productId);

    @PostMapping(value = "/review", produces = "application/json", consumes = "application/json")
    Review createReview(@RequestBody Review review);

    @DeleteMapping(value = "/review")
    void deleteReviews(@RequestParam int productId);
}
