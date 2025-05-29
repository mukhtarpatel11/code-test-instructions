package com.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.simple.url.shortener.SimpleUrlShortenerApplication;
import com.simple.url.shortener.dto.ShortenUrlRequest;
import com.simple.url.shortener.model.UrlMapping;
import com.simple.url.shortener.services.SimpleUrlShortenerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = SimpleUrlShortenerApplication.class)
@AutoConfigureMockMvc
class SimpleUrlShortenerControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SimpleUrlShortenerService service;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        service.getAll().forEach(mapping -> service.deleteByAlias(mapping.getAlias()));
    }

    @Test
    void shouldCreateShortUrl() throws Exception {
        ShortenUrlRequest request = new ShortenUrlRequest("https://example.com", "myalias");

        mockMvc.perform(post("/shorten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.shortUrl", containsString("myalias")));
    }

    @Test
    void shouldRedirectToFullUrl() throws Exception {
        UrlMapping mapping = service.createShortUrl("https://example.com", "alias1");

        mockMvc.perform(get("/alias1"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "https://example.com"));
    }

    @Test
    void shouldReturn404ForInvalidAlias() throws Exception {
        mockMvc.perform(get("/notfound"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Alias not found"));
    }

    @Test
    void shouldDeleteAlias() throws Exception {
        service.createShortUrl("https://delete-me.com", "deleteAlias");

        mockMvc.perform(delete("/deleteAlias"))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/deleteAlias"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn404OnDeleteInvalidAlias() throws Exception {
        mockMvc.perform(delete("/missing"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Alias not found"));
    }

    @Test
    void shouldListAllUrls() throws Exception {
        service.createShortUrl("https://url1.com", "url1");
        service.createShortUrl("https://url2.com", "url2");

        mockMvc.perform(get("/urls"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void shouldFailOnDuplicateAlias() throws Exception {
        service.createShortUrl("https://url.com", "duplicate");

        ShortenUrlRequest request = new ShortenUrlRequest("https://another.com", "duplicate");

        mockMvc.perform(post("/shorten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString("Alias already taken")));
    }
}