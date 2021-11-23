package nl.svh.microservices.composite.product.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.svh.microservices.api.core.product.Product;
import nl.svh.microservices.api.core.product.ProductService;
import nl.svh.microservices.api.core.recommendation.Recommendation;
import nl.svh.microservices.api.core.recommendation.RecommendationService;
import nl.svh.microservices.api.core.review.Review;
import nl.svh.microservices.api.core.review.ReviewService;
import nl.svh.microservices.api.exception.InvalidInputException;
import nl.svh.microservices.api.exception.NotFoundException;
import nl.svh.microservices.util.http.HttpErrorInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.HttpMethod.GET;

@Component
public class ProductCompositeIntegration implements ProductService, RecommendationService, ReviewService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductCompositeIntegration.class);

    private final RestTemplate restTemplate;
    private final ObjectMapper mapper;
    private final String productServiceUrl;
    private final String recommendationServiceUrl;
    private final String reviewServiceUrl;

    @Autowired
    public ProductCompositeIntegration(
            RestTemplate restTemplate,
            ObjectMapper mapper,
            @Value("${app.product-service.host}") String productServiceHost,
            @Value("${app.product-service.port}") int productServicePort,
            @Value("${app.recommendation-service.host}") String recommendationServiceHost,
            @Value("${app.recommendation-service.port}") int recommendationServicePort,
            @Value("${app.review-service.host}") String reviewServiceHost,
            @Value("${app.review-service.port}") int reviewServicePort) {

        this.restTemplate = restTemplate;
        this.mapper = mapper;

        productServiceUrl = "http://" + productServiceHost + ":" + productServicePort + "/product/";
        recommendationServiceUrl = "http://" + recommendationServiceHost + ":" + recommendationServicePort +
                "/recommendation?productId=";
        reviewServiceUrl = "http://" + reviewServiceHost + ":" + reviewServicePort + "/review?productId=";
    }

    @Override
    public Product getProduct(int productId) {
        try {
            String url = productServiceUrl + productId;
            LOGGER.debug("Will call getProduct API on URL: {}", url);
            Product product = restTemplate.getForObject(url, Product.class);
            LOGGER.debug("Found a product with id: {}", product.getProductId());
            return product;
        } catch (HttpClientErrorException e) {
            throw handleHttpClientException(e);
        }
    }


    @Override
    public Product createProduct(Product body) {
        try {
            LOGGER.debug("Will post a new product to URL:{}", productServiceUrl);
            Product product = restTemplate.postForObject(productServiceUrl, body, Product.class);
            LOGGER.debug("Created a product with id {}", product.getProductId());
            return product;
        } catch (HttpClientErrorException exception) {
            throw handleHttpClientException(exception);
        }

    }

    private RuntimeException handleHttpClientException(HttpClientErrorException ex) {
        switch (ex.getStatusCode()) {

            case NOT_FOUND:
                return new NotFoundException(getErrorMessage(ex));

            case UNPROCESSABLE_ENTITY:
                return new InvalidInputException(getErrorMessage(ex));

            default:
                LOGGER.warn("Got an unexpected HTTP error: {}, will rethrow it", ex.getStatusCode());
                LOGGER.warn("Error body: {}", ex.getResponseBodyAsString());
                return ex;
        }
    }

    @Override
    public void deleteProduct(int productId) {
        try {
            String url = productServiceUrl + "/" + productId;
            LOGGER.debug("Will call the deleteProduct API on URL: {}", url);

            restTemplate.delete(url);
        } catch (HttpClientErrorException exception) {
            throw handleHttpClientException(exception);
        }
    }

    private String getErrorMessage(HttpClientErrorException ex) {
        try {
            return mapper.readValue(ex.getResponseBodyAsString(), HttpErrorInfo.class).getMessage();
        } catch (IOException ioex) {
            return ex.getMessage();
        }
    }


    @Override
    public List<Review> getReviews(int productId) {
        try {
            String url = reviewServiceUrl + productId;
            LOGGER.debug("Will call getRecommendations API on URL: {}", url);
            List<Review> reviews = restTemplate
                    .exchange(url, GET, null, new ParameterizedTypeReference<List<Review>>() {
                    })
                    .getBody();
            LOGGER.debug("Found {} reviews for a product with id: {}", reviews.size(), productId);
            return reviews;
        } catch (Exception ex) {
            LOGGER.warn("Got an exception while requesting reviews, return zero reviews: {}", ex.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public Review createReview(Review body) {
        try {
            String url = reviewServiceUrl;
            LOGGER.debug("Will post a new review to URL: {}", url);
            Review review = restTemplate.postForObject(url, body, Review.class);
            LOGGER.debug("Created a review with id: {}", review.getProductId());
            return review;
        } catch (HttpClientErrorException exception) {
            throw handleHttpClientException(exception);
        }
    }

    @Override
    public void deleteReviews(int productId) {
        try {
            String url = reviewServiceUrl + productId;
            LOGGER.debug("Will call the deleteReviews API on URL: {}", url);
            restTemplate.delete(url);
        } catch (HttpClientErrorException exception) {
            throw handleHttpClientException(exception);
        }
    }

    @Override
    public List<Recommendation> getRecommendations(int productId) {
        try {
            String url = reviewServiceUrl + "?productId=" + productId;
            LOGGER.debug("Will call getRecommendations API on URL: {}", url);
            List<Recommendation> recommendations = restTemplate
                    .exchange(url, GET, null, new ParameterizedTypeReference<List<Recommendation>>() {
                    }).getBody();
            LOGGER.debug("Found {} recommendations for a product with id: {}", recommendations.size(), productId);
            return recommendations;
        } catch (Exception e) {
            LOGGER.warn("Got an exception while requesting recommendations, return zero recommendations: {}",
                    e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public Recommendation createRecommendation(Recommendation body) {
        try {
            String url = recommendationServiceUrl;
            LOGGER.debug("Will post a new recommendation to URL: {}", url);

            Recommendation recommendation = restTemplate.postForObject(url, body, Recommendation.class);
            LOGGER.debug("Created a recommendation with id: {}", recommendation.getProductId());

            return recommendation;

        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    @Override
    public void deleteRecommendations(int productId) {
        try {
            String url = recommendationServiceUrl  + productId;
            LOGGER.debug("Will call the deleteRecommendations API on URL: {}", url);
            restTemplate.delete(url);
        } catch (HttpClientErrorException exception) {
            throw handleHttpClientException(exception);
        }
    }
}
