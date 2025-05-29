package com.simple.url.shortener.services;

import com.simple.url.shortener.model.UrlMapping;
import com.simple.url.shortener.repository.UrlMappingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;

@Service
public class SimpleUrlShortenerService {

    private static final String ALPHANUM = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int SHORT_ALIAS_LENGTH = 5;
    private static final SecureRandom random = new SecureRandom();

    private final UrlMappingRepository repository;

    @Autowired
    public SimpleUrlShortenerService(UrlMappingRepository repository) {
        this.repository = repository;
    }

    public UrlMapping createShortUrl(String fullUrl, String customAlias) {
        String alias = customAlias;

        if (alias == null || alias.isEmpty()) {
            alias = generateUniqueAlias();
        } else {
            if (repository.existsByAlias(alias)) {
                throw new IllegalArgumentException("Alias already taken");
            }
        }

        UrlMapping urlMapping = new UrlMapping(alias, fullUrl);
        return repository.save(urlMapping);
    }

    public Optional<UrlMapping> getByAlias(String alias) {
        return repository.findById(alias);
    }

    public List<UrlMapping> getAll() {
        return repository.findAll();
    }

    public void deleteByAlias(String alias) {
        if (!repository.existsById(alias)) {
            throw new IllegalArgumentException("Alias not found");
        }
        repository.deleteById(alias);
    }

    private String generateUniqueAlias() {
        String alias;
        do {
            alias = randomString(SHORT_ALIAS_LENGTH);
        } while (repository.existsByAlias(alias));
        return alias;
    }

    private String randomString(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(ALPHANUM.charAt(random.nextInt(ALPHANUM.length())));
        }
        return sb.toString();
    }
}