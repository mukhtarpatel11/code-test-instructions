package com.simple.url.shortener.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.simple.url.shortener.model.UrlMapping;

@Repository
public interface UrlMappingRepository extends JpaRepository<UrlMapping, String> {
    boolean existsByAlias(String alias);
}