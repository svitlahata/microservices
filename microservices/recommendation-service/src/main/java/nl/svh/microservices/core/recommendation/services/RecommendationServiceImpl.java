package nl.svh.microservices.core.recommendation.services;

import nl.svh.microservices.api.core.recommendation.Recommendation;
import nl.svh.microservices.api.core.recommendation.RecommendationService;
import nl.svh.microservices.api.exception.InvalidInputException;
import nl.svh.microservices.core.recommendation.persistence.RecommendationRepository;
import nl.svh.microservices.util.http.ServiceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.logging.Level;

@RestController
public class RecommendationServiceImpl implements RecommendationService {

    private final static Logger LOGGER = LoggerFactory.getLogger(RecommendationServiceImpl.class);

    private final RecommendationRepository repository;
    private final RecommendationMapper mapper;
    private final ServiceUtil serviceUtil;

    @Autowired
    public RecommendationServiceImpl(RecommendationRepository repository,
                                     RecommendationMapper mapper,
                                     ServiceUtil serviceUtil) {
        this.repository = repository;
        this.mapper = mapper;
        this.serviceUtil = serviceUtil;
    }

    @Override
    public Flux<Recommendation> getRecommendations(int productId) {
        if (productId < 1) {
            LOGGER.error("Invalid productId: {}", productId);
            throw new InvalidInputException("Invalid productId: " + productId);
        }
        LOGGER.info("Will get recommendations for product with id={}", productId);
        return repository.findByProductId(productId)
                .log(LOGGER.getName(), Level.FINE)
                .map(mapper::entityToApi)
                .map(this::setServiceAddress);
    }

    @Override
    public Mono<Recommendation> createRecommendation(Recommendation body) {
        if (body.getProductId() < 1) {
            throw new InvalidInputException("Invalid productId: " + body.getProductId());
        }

        return repository.save(
                mapper.apiToEntity(body))
                .onErrorMap(
                        DuplicateKeyException.class,
                        ex -> new InvalidInputException("Duplicate key, Product Id: " + body.getProductId() + "," +
                                " Recommendation Id:" + body.getRecommendationId())
                )
                .log(LOGGER.getName(), Level.FINE)
                .map(mapper::entityToApi)
                .map(this::setServiceAddress);

    }

    @Override
    public Mono<Void> deleteRecommendations(int productId) {
        if (productId < 1) {
            throw new InvalidInputException("Invalid productId: " + productId);
        }

        LOGGER.debug("deleteRecommendations: tries to delete recommendations for the product with productId: {}",
                productId);
        return repository.deleteAll(repository.findByProductId(productId));
    }

    private Recommendation setServiceAddress(Recommendation e) {
        e.setServiceAddress(serviceUtil.getServiceAddress());
        return e;
    }

}
