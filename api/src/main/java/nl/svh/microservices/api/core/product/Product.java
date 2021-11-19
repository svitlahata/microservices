package nl.svh.microservices.api.core.product;

import static org.apache.logging.log4j.util.Strings.EMPTY;

public class Product {

    private final int productId;
    private final String name;
    private final int weight;
    private final String serviceAddress;

    public Product() {
        this.productId = 0;
        this.name = EMPTY;
        this.weight = 0;
        serviceAddress = EMPTY;
    }

    public Product(int productId, String name, int weight, String serviceAddress) {
        this.productId = productId;
        this.name = name;
        this.weight = weight;
        this.serviceAddress = serviceAddress;
    }

    public int getProductId() {
        return productId;
    }

    public String getName() {
        return name;
    }

    public int getWeight() {
        return weight;
    }

    public String getServiceAddress() {
        return serviceAddress;
    }
}
