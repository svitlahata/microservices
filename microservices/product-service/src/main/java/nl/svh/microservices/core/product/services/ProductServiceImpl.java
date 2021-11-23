package nl.svh.microservices.core.product.services;


import nl.svh.microservices.api.core.product.Product;
import nl.svh.microservices.api.core.product.ProductService;
import nl.svh.microservices.api.exception.InvalidInputException;
import nl.svh.microservices.api.exception.NotFoundException;
import nl.svh.microservices.core.product.persistence.ProductEntity;
import nl.svh.microservices.core.product.persistence.ProductRepository;
import nl.svh.microservices.util.http.ServiceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductServiceImpl implements ProductService {

    private static final Logger LOG = LoggerFactory.getLogger(ProductServiceImpl.class);

    private final ProductRepository repository;
    private final ProductMapper mapper;
    private final ServiceUtil serviceUtil;

    @Autowired
    public ProductServiceImpl(ProductRepository repository,
                              ProductMapper mapper,
                              ServiceUtil serviceUtil) {
        this.repository = repository;
        this.mapper = mapper;
        this.serviceUtil = serviceUtil;
    }

    @Override
    public Product getProduct(int productId) {
        LOG.debug("Product return the found product for productId={}", productId);

        if (productId < 1) {
            throw new InvalidInputException("Invalid productId: " + productId);
        }

        ProductEntity productEntity = repository.findByProductId(productId).orElseThrow(() -> new NotFoundException(
                "No product found for " +
                        "productId: " + productId));

        Product product = mapper.entityToApi(productEntity);
        product.setServiceAddress(serviceUtil.getServiceAddress());

        return product;
    }

    @Override
    public Product createProduct(Product product) {
        try {
            ProductEntity entity = mapper.apiToEntity(product);
            ProductEntity newEntity = repository.save(entity);

            LOG.debug("createProduct: entity created for {}", product.getProductId());
            return mapper.entityToApi(newEntity);
        } catch (DuplicateKeyException exception) {
            throw new InvalidInputException("Duplicate key, Product Id: " + product.getProductId());
        }
    }

    @Override
    public void deleteProduct(int productId) {
        LOG.debug("deleteProduct: tries to delete an entity with productId {}", productId);
        repository.findByProductId(productId).ifPresent(e -> repository.delete(e));
    }
}
