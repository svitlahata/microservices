package nl.svh.microservices.core.review.services;

import nl.svh.microservices.api.core.review.Review;
import nl.svh.microservices.api.core.review.ReviewService;
import nl.svh.microservices.api.exception.InvalidInputException;
import nl.svh.microservices.core.review.persistence.ReviewEntity;
import nl.svh.microservices.core.review.persistence.ReviewRepository;
import nl.svh.microservices.util.http.ServiceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.util.List;
import java.util.logging.Level;

@RestController
public class ReviewServicesImpl implements ReviewService {

    private final static Logger LOGGER = LoggerFactory.getLogger(ReviewServicesImpl.class);

    private final ReviewRepository repository;
    private final ReviewMapper mapper;
    private final ServiceUtil serviceUtil;
    private final Scheduler jdbcScheduler;

    public ReviewServicesImpl(@Qualifier("jdbcScheduler") Scheduler jdbcScheduler,
                              ReviewRepository repository,
                              ReviewMapper mapper,
                              ServiceUtil serviceUtil) {
        this.repository = repository;
        this.mapper = mapper;
        this.serviceUtil = serviceUtil;
        this.jdbcScheduler = jdbcScheduler;
    }


    @Override
    public Flux<Review> getReviews(int productId) {
        if (productId < 1) {
            throw new InvalidInputException("Invalid productId: " + productId);
        }
        LOGGER.info("Will get reviews for product with id={}", productId);
        return Mono.fromCallable(() -> internalGetReviews(productId))
                .flatMapMany(Flux::fromIterable)
                .log(LOGGER.getName(), Level.FINE)
                .subscribeOn(jdbcScheduler);
    }

    private List<Review> internalGetReviews(int productId) {
        List<ReviewEntity> entityList = repository.findByProductId(productId);
        List<Review> reviews = mapper.entityListToApiList(entityList);
        reviews.forEach(e -> e.setServiceAddress(serviceUtil.getServiceAddress()));
        LOGGER.debug("getReviews: response size: {}", reviews.size());
        return reviews;
    }


    @Override
    public Mono<Review> createReview(Review body) {
        if (body.getProductId() < 1) {
            throw new InvalidInputException("Invalid productId: " + body.getProductId());
        }
        return Mono.fromCallable(()-> internalCreateReview(body))
                .subscribeOn(jdbcScheduler);
    }

    private Review internalCreateReview(Review body) {
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
    public Mono<Void> deleteReviews(int productId) {
        if (productId < 1) {
            throw new InvalidInputException("Invalid productId: " + productId);
        }
        return Mono.fromRunnable(() -> internalDeleteReviews(productId))
                .subscribeOn(jdbcScheduler)
                .then();
    }

    private void internalDeleteReviews(int productId) {
        LOGGER.debug("deleteReviews: tries to delete reviews for the product with productId: {}", productId);
        repository.deleteAll(repository.findByProductId(productId));
    }
}
