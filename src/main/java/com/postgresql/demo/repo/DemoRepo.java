package com.postgresql.demo.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.postgresql.demo.model.Demo;

@RepositoryRestResource

public interface DemoRepo extends JpaRepository<Demo, Long>{

}
