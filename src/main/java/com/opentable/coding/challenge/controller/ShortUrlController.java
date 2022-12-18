package com.opentable.coding.challenge.controller;

import com.opentable.coding.challenge.dto.CreateShortUrl;
import com.opentable.coding.challenge.model.UrlInfo;
import com.opentable.coding.challenge.service.ShortUrlService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.net.URI;

@RestController
@Slf4j
public class ShortUrlController {

    private ShortUrlService shortUrlService;

    public ShortUrlController(ShortUrlService shortUrlService) {
        this.shortUrlService = shortUrlService;
    }

    @PostMapping("/short-url")
    public Mono<UrlInfo> create(@Valid @RequestBody CreateShortUrl createShortUrl) {
        log.info("create short url: {}", createShortUrl);
        return shortUrlService.create(createShortUrl);
    }

    @GetMapping("/short-url/{shortUrl}")
    public Mono<Void> find(@PathVariable String shortUrl, ServerHttpResponse response) {
        return shortUrlService.findByShortUrl(shortUrl).map(urlInfo -> {
            response.setStatusCode(HttpStatus.PERMANENT_REDIRECT);
            response.getHeaders().setLocation(URI.create(urlInfo.getLongUrl()));
            return urlInfo;
        }).then(response.setComplete());
    }

    @PutMapping("/short-url")
    public Mono<UrlInfo> update(@Valid @RequestBody UrlInfo updatedUrlInfo) {
        log.info("update short url: {}", updatedUrlInfo);
        return shortUrlService.update(updatedUrlInfo);
    }

    @DeleteMapping("/short-url/{shortUrl}")
    public Mono<Void> delete(@PathVariable String shortUrl) {
        log.info("delete short url: {}", shortUrl);
        return shortUrlService.delete(shortUrl);
    }

    @GetMapping("/short-url")
    public Flux<UrlInfo> findAll() {
        return shortUrlService.findAll();
    }
}
