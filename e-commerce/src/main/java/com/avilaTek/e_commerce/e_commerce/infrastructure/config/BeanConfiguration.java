package com.avilaTek.e_commerce.e_commerce.infrastructure.config;

import com.avilaTek.e_commerce.e_commerce.application.service.AuthServiceImpl;
import com.avilaTek.e_commerce.e_commerce.application.service.ProductServiceImpl;
import com.avilaTek.e_commerce.e_commerce.application.service.UserServiceImpl;

import com.avilaTek.e_commerce.e_commerce.domain.port.input.AuthService;
import com.avilaTek.e_commerce.e_commerce.domain.port.input.ProductService;
import com.avilaTek.e_commerce.e_commerce.domain.port.input.UserService;

import com.avilaTek.e_commerce.e_commerce.domain.port.output.PasswordEncoder;
import com.avilaTek.e_commerce.e_commerce.domain.port.output.ProductRepository;
import com.avilaTek.e_commerce.e_commerce.domain.port.output.TokenProvider;
import com.avilaTek.e_commerce.e_commerce.domain.port.output.UserRepository;

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
}
