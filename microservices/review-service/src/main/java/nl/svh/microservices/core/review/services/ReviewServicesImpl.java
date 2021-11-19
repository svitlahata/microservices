package nl.svh.microservices.core.review.services;

import nl.svh.microservices.api.core.review.Review;
import nl.svh.microservices.api.core.review.ReviewService;
import nl.svh.microservices.api.exception.InvalidInputException;
import nl.svh.microservices.util.http.ServiceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestController
public class ReviewServicesImpl implements ReviewService {

    private final static Logger LOGGER = LoggerFactory.getLogger(ReviewServicesImpl.class);

    private final ServiceUtil serviceUtil;

    public ReviewServicesImpl(ServiceUtil serviceUtil) {
        this.serviceUtil = serviceUtil;
    }


    @Override
    public List<Review> getReviews(int productId) {
        if (productId < 1) {
            throw new InvalidInputException("Invalid productId: " + productId);
        }

        if (productId == 213) {
            LOGGER.debug("No reviews found for productId: {}", productId);
            return Collections.emptyList();
        }

        List<Review> list = new ArrayList<>();
        list.add(new Review(productId, 1, "Author 1", "Subject 1", "Content 1", serviceUtil.getServiceAddress()));
        list.add(new Review(productId, 2, "Author 2", "Subject 2", "Content 2", serviceUtil.getServiceAddress()));
        list.add(new Review(productId, 3, "Author 3", "Subject 3", "Content 3", serviceUtil.getServiceAddress()));

        LOGGER.debug("/reviews response size: {}", list.size());

        return list;
    }
}
