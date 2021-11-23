package nl.svh.microservices.core.review.services;

import nl.svh.microservices.api.core.review.Review;
import nl.svh.microservices.api.core.review.ReviewService;
import nl.svh.microservices.api.exception.InvalidInputException;
import nl.svh.microservices.core.review.persistence.ReviewEntity;
import nl.svh.microservices.core.review.persistence.ReviewRepository;
import nl.svh.microservices.util.http.ServiceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ReviewServicesImpl implements ReviewService {

    private final static Logger LOGGER = LoggerFactory.getLogger(ReviewServicesImpl.class);

    private final ReviewRepository repository;

    private final ReviewMapper mapper;
    private final ServiceUtil serviceUtil;

    public ReviewServicesImpl(ReviewRepository repository,
                              ReviewMapper mapper,
                              ServiceUtil serviceUtil) {
        this.repository = repository;
        this.mapper = mapper;
        this.serviceUtil = serviceUtil;
    }


    @Override
    public List<Review> getReviews(int productId) {
        if (productId < 1) {
            throw new InvalidInputException("Invalid productId: " + productId);
        }

        List<ReviewEntity> entityList = repository.findByProductId(productId);
        List<Review> reviews = mapper.entityListToApiList(entityList);
        reviews.forEach(e -> e.setServiceAddress(serviceUtil.getServiceAddress()));
        LOGGER.debug("getReviews: response size: {}", reviews.size());
        return reviews;
    }

    @Override
    public Review createReview(Review body) {
        try {
            ReviewEntity entity = mapper.apiToEntity(body);
            ReviewEntity newEntity = repository.save(entity);

            LOGGER.debug("createReview: created a review entity: {}/{}", body.getProductId(), body.getReviewId());
            return mapper.entityToApi(newEntity);

        } catch (DataIntegrityViolationException dive) {
            throw new InvalidInputException("Duplicate key, Product Id: " + body.getProductId() + ", Review Id:" + body.getReviewId());
        }
    }

    @Override
    public void deleteReviews(int productId) {
        LOGGER.debug("deleteReviews: tries to delete reviews for the product with productId: {}", productId);
        repository.deleteAll(repository.findByProductId(productId));
    }
}
