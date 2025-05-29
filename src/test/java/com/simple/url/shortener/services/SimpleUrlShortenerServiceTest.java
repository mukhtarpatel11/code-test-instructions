package com.simple.url.shortener.services;

import com.simple.url.shortener.model.UrlMapping;
import com.simple.url.shortener.repository.UrlMappingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;

public class SimpleUrlShortenerServiceTest {
    private UrlMappingRepository repository;
    private SimpleUrlShortenerService service;

    @BeforeEach
    void setup() {
        repository = mock(UrlMappingRepository.class);
        service = new SimpleUrlShortenerService(repository);
    }

    @Test
    void testCreateWithCustomAlias_success() {
        when(repository.existsByAlias("custom")).thenReturn(false);
        when(repository.save(any(UrlMapping.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UrlMapping mapping = service.createShortUrl("https://www.amazon.co.uk", "custom");
        assertEquals("custom", mapping.getAlias());
        assertEquals("https://www.amazon.co.uk", mapping.getFullUrl());

        verify(repository).save(any());
    }

    @Test
    void testCreateWithCustomAlias_duplicateAlias() {
        when(repository.existsByAlias("custom")).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            service.createShortUrl("https://www.amazon.co.uk", "custom");
        });

        assertEquals("Alias already taken", ex.getMessage());
    }

    @Test
    void testCreateWithoutAlias_generatesAlias() {
        when(repository.existsByAlias(anyString())).thenReturn(false);
        when(repository.save(any(UrlMapping.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UrlMapping mapping = service.createShortUrl("https://www.amazon.co.uk", null);

        assertNotNull(mapping.getAlias());
        assertEquals(5, mapping.getAlias().length());
        assertEquals("https://www.amazon.co.uk", mapping.getFullUrl());
    }

    @Test
    void testDeleteAlias_success() {
        when(repository.existsById("alias")).thenReturn(true);
        doNothing().when(repository).deleteById("alias");

        assertDoesNotThrow(() -> service.deleteByAlias("alias"));

        verify(repository).deleteById("alias");
    }

    @Test
    void testDeleteAlias_notFound() {
        when(repository.existsById("alias")).thenReturn(false);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            service.deleteByAlias("alias");
        });
        assertEquals("Alias not found", ex.getMessage());
    }

    @Test
    void testGetByAlias_found() {
        UrlMapping mapping = new UrlMapping("alias", "https://www.amazon.co.uk");
        when(repository.findById("alias")).thenReturn(Optional.of(mapping));

        Optional<UrlMapping> result = service.getByAlias("alias");
        assertTrue(result.isPresent());
        assertEquals("https://www.amazon.co.uk", result.get().getFullUrl());
    }

    @Test
    void testGetByAlias_notFound() {
        when(repository.findById("alias")).thenReturn(Optional.empty());
        Optional<UrlMapping> result = service.getByAlias("alias");
        assertFalse(result.isPresent());
    }
}
