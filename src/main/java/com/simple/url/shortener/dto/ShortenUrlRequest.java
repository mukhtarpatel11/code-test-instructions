package com.simple.url.shortener.dto;

import jakarta.validation.constraints.NotBlank;

public class ShortenUrlRequest {
    @NotBlank
    String fullUrl;

    String customAlias;

    public ShortenUrlRequest(String fullUrl) {
        this.fullUrl = fullUrl;
    }

    public ShortenUrlRequest() {
    }

    public @NotBlank String getFullUrl() {
        return fullUrl;
    }

    public void setFullUrl(@NotBlank String fullUrl) {
        this.fullUrl = fullUrl;
    }

    public String getCustomAlias() {
        return customAlias;
    }

    public void setCustomAlias(String customAlias) {
        this.customAlias = customAlias;
    }
}
