package com.simple.url.shortener.dto;

public class UrlInfo {
    String alias;
    String fullUrl;
    String shortUrl;

    public UrlInfo() {
    }

    public UrlInfo(String alias, String fullUrl, String shortUrl) {
        this.alias = alias;
        this.fullUrl = fullUrl;
        this.shortUrl = shortUrl;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getFullUrl() {
        return fullUrl;
    }

    public void setFullUrl(String fullUrl) {
        this.fullUrl = fullUrl;
    }

    public String getShortUrl() {
        return shortUrl;
    }

    public void setShortUrl(String shortUrl) {
        this.shortUrl = shortUrl;
    }
}
