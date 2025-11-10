package com.avilaTek.e_commerce.e_commerce.infrastructure.config;

import com.avilaTek.e_commerce.e_commerce.application.service.auth.AuthServiceImpl;
import com.avilaTek.e_commerce.e_commerce.application.service.order.OrderServiceImpl;
import com.avilaTek.e_commerce.e_commerce.application.service.product.ProductServiceImpl;
import com.avilaTek.e_commerce.e_commerce.application.service.user.UserServiceImpl;

import com.avilaTek.e_commerce.e_commerce.domain.port.input.auth.AuthService;
import com.avilaTek.e_commerce.e_commerce.domain.port.input.order.OrderService;
import com.avilaTek.e_commerce.e_commerce.domain.port.input.product.ProductService;
import com.avilaTek.e_commerce.e_commerce.domain.port.input.user.UserService;

import com.avilaTek.e_commerce.e_commerce.domain.port.output.*;

import com.avilaTek.e_commerce.e_commerce.domain.port.output.order.OrderRepository;
import com.avilaTek.e_commerce.e_commerce.domain.port.output.product.ProductRepository;
import com.avilaTek.e_commerce.e_commerce.domain.port.output.user.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {
    @Bean
    public UserService userService(UserRepository userRepository) {
        return new UserServiceImpl(userRepository);
    }

    @Bean
    public AuthService authService(UserRepository userRepository,
                                   PasswordEncoder passwordEncoder,
                                   TokenProvider tokenProvider) {
        return new AuthServiceImpl(userRepository, passwordEncoder, tokenProvider);
    }

    @Bean
    public ProductService productService(ProductRepository productRepository) {
        return new ProductServiceImpl(productRepository);
    }

    @Bean
    public OrderService orderService(OrderRepository orderRepository,
                                     ProductRepository productRepository) {
        return new OrderServiceImpl(orderRepository, productRepository);
    }
}
