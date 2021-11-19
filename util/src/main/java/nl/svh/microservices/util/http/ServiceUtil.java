package nl.svh.microservices.util.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Component
public class ServiceUtil {
    private static final String UNKNOWN_HOST_NAME = "Unknown host name";
    private static final String UNKNOWN_IP_ADDRESS = "Unknown IP address";
    private final Logger logger = LoggerFactory.getLogger(ServiceUtil.class);

    private final String port;

    private String serviceAddress;

    @Autowired
    public ServiceUtil(@Value("${server.port}") String port) {
        this.port = port;
    }

    public String getServiceAddress() {
        if (serviceAddress == null) {
            serviceAddress = findMyHostName() + "/" + findMyIpAddress() + ":" + port;
        }
        return serviceAddress;

    }

    private String findMyHostName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            logger.error(UNKNOWN_HOST_NAME);
            return UNKNOWN_HOST_NAME;
        }
    }

    private String findMyIpAddress() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            logger.error(UNKNOWN_IP_ADDRESS);
            return UNKNOWN_IP_ADDRESS;
        }
    }

}
