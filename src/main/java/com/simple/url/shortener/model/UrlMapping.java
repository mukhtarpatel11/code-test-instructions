package com.simple.url.shortener.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "url_mappings")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UrlMapping {
    @Id
    @NotBlank
    private String alias;

    @NotBlank
    private String fullUrl;
}