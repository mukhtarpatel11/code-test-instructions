package com.simple.url.shortener.controller;

import com.simple.url.shortener.dto.ShortenUrlRequest;
import com.simple.url.shortener.dto.ShortenUrlResponse;
import com.simple.url.shortener.dto.UrlInfo;
import com.simple.url.shortener.model.UrlMapping;
import com.simple.url.shortener.services.SimpleUrlShortenerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Map;

@Controller
@Validated
public class SimpleUrlShortenerController {
    private final SimpleUrlShortenerService service;

    @Value("${server.port}")
    private int serverPort;

    public SimpleUrlShortenerController(SimpleUrlShortenerService service) {
        this.service = service;
    }

    private String getBaseUrl() {
        return "http://localhost:" + serverPort;
    }

    @PostMapping(path = "/shorten", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> shortenUrl(@Valid @RequestBody ShortenUrlRequest request) {
        try {
            UrlMapping created = service.createShortUrl(request.getFullUrl(), request.getCustomAlias());
            String shortUrl = getBaseUrl() + "/" + created.getAlias();
            return ResponseEntity.status(HttpStatus.CREATED).body(new ShortenUrlResponse(shortUrl));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{alias}")
    public ResponseEntity<?> redirectToFullUrl(@PathVariable String alias) {
        return service.getByAlias(alias)
                .map(mapping -> ResponseEntity.status(HttpStatus.FOUND).location(URI.create(mapping.getFullUrl())).build())
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Alias not found"));
    }

    @DeleteMapping("/{alias}")
    public ResponseEntity<?> deleteAlias(@PathVariable String alias) {
        try {
            service.deleteByAlias(alias);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Alias not found");
        }
    }

    @GetMapping(path = "/urls", produces = "application/json")
    public ResponseEntity<List<UrlInfo>> listAllUrls() {
        List<UrlInfo> list = service.getAll().stream()
                .map(mapping -> new UrlInfo(mapping.getAlias(), mapping.getFullUrl(), getBaseUrl() + "/" + mapping.getAlias()))
                .toList();
        return ResponseEntity.ok(list);
    }

    /**
     * Basic web page
     */
    @GetMapping("/")
    public String index() {
        return "index";
    }

    @PostMapping("/shorten-url")
    public String shortenFromForm(@RequestParam String fullUrl,
                                 @RequestParam(required = false) String customAlias,
                                 Model model) {
        try {
            UrlMapping created = service.createShortUrl(fullUrl, customAlias);
            String shortUrl = getBaseUrl() + "/" + created.getAlias();
            model.addAttribute("shortUrl", shortUrl);
            model.addAttribute("fullUrl", fullUrl);
            return "index";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "index";
        }
    }
}