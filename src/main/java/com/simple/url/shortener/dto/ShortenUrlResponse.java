package com.simple.url.shortener.dto;

public class ShortenUrlResponse {
    String shortUrl;

    public ShortenUrlResponse(String shortUrl) {
        this.shortUrl = shortUrl;
    }

    public ShortenUrlResponse() {
    }

    public String getShortUrl() {
        return shortUrl;
    }

    public void setShortUrl(String shortUrl) {
        this.shortUrl = shortUrl;
    }
}
