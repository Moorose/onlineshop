package net.thumbtack.onlineshop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class OnlineShopServer {

    public static final String COOKIE_NAME = "JAVASESSIONID";

    public static void main(String[] args) {
        SpringApplication.run(OnlineShopServer.class, args);
    }

}
