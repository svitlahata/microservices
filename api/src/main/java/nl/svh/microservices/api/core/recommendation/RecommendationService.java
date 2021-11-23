package nl.svh.microservices.api.core.recommendation;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface RecommendationService {

    @GetMapping(value = "recommendation", produces = "application/json")
    List<Recommendation> getRecommendations(@RequestParam(name = "productId") int productId);

    @PostMapping(
            value = "/recommendation",
            consumes = "application/json",
            produces = "application/json")
    Recommendation createRecommendation(@RequestBody Recommendation body);

    @DeleteMapping(value = "/recommendation")
    void deleteRecommendations(@RequestParam(value = "productId") int productId);
}
