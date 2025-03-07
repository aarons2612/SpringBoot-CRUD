package com.postgresql.demo.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.postgresql.demo.model.Demo;
import java.util.Optional;

@RepositoryRestResource

public interface DemoRepo extends JpaRepository<Demo, Long> {
    boolean existsByEmail(String email);
    Optional<Demo> findByEmail(String email);
}
