package com.avilaTek.e_commerce.e_commerce.domain.port.output.user;

import com.avilaTek.e_commerce.e_commerce.domain.model.user.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    User save(User user);
    Optional<User> findById(Long id);
    Optional<User> findByEmail(String email);
    List<User> findAll();
    boolean existsByEmail(String email);
    void deleteById(Long id);
}
