package nl.svh.microservices.core.recommendation.services;

import nl.svh.microservices.api.core.recommendation.Recommendation;
import nl.svh.microservices.api.core.recommendation.RecommendationService;
import nl.svh.microservices.api.exception.InvalidInputException;
import nl.svh.microservices.util.http.ServiceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestController
public class RecommendationServiceImpl implements RecommendationService {

    private final static Logger LOGGER = LoggerFactory.getLogger(RecommendationServiceImpl.class);

    private final ServiceUtil serviceUtil;

    @Autowired
    public RecommendationServiceImpl(ServiceUtil serviceUtil) {
        this.serviceUtil = serviceUtil;
    }

    @Override
    public List<Recommendation> getRecommendations(int productId) {
        if (productId < 1) {
            LOGGER.error("Invalid productId: {}", productId);
            throw new InvalidInputException("Invalid productId: " + productId);
        }
        if (productId == 113) {
            LOGGER.debug("No recommendation found for product {}", productId);
            return Collections.emptyList();
        }
        return getRecommendationList(productId);
    }

    private List<Recommendation> getRecommendationList(int productId) {
        List<Recommendation> list = new ArrayList<>();
        list.add(new Recommendation(productId, 1, "Author 1", 1, "Content 1", serviceUtil.getServiceAddress()));
        list.add(new Recommendation(productId, 2, "Author 2", 2, "Content 2", serviceUtil.getServiceAddress()));
        list.add(new Recommendation(productId, 3, "Author 3", 3, "Content 3", serviceUtil.getServiceAddress()));
        LOGGER.debug("/recommendation response size: {}", list.size());
        return list;
    }
}
