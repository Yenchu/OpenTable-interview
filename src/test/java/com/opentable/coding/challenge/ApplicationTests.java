package com.opentable.coding.challenge;

import com.opentable.coding.challenge.dto.CreateShortUrl;
import com.opentable.coding.challenge.model.UrlInfo;
import com.opentable.coding.challenge.service.ShortUrlService;
import com.opentable.coding.challenge.util.RandomStringGenerator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.opentable.coding.challenge.model.UrlInfo.SHORT_URL_FORMAT;
import static com.opentable.coding.challenge.model.UrlInfo.SHORT_URL_LENGTH;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
class ApplicationTests {

    /**
     * To validate short URL format.
     */
    private Pattern shortUrlPattern = Pattern.compile(SHORT_URL_FORMAT);

    @Autowired
    private ShortUrlService shortUrlService;

    @Test
    public void generateShortUrlStr() {
        String shortUrl = RandomStringGenerator.generate(SHORT_URL_LENGTH);
        assertEquals(SHORT_URL_LENGTH, shortUrl.length(), "Short URL length incorrect");
        assertTrue(validateShortUrlFormat(shortUrl), "Short URL format incorrect");
    }

    @Test
    public void crudShortUrl() {
        AtomicReference<UrlInfo> urlInfo = new AtomicReference<>();

        // create short URL
        String url = "https://www.google.com/";
        Mono<UrlInfo> createUrlInfo = shortUrlService.create(
                        CreateShortUrl.builder()
                                .url(url)
                                .build()
                )
                .doOnNext(u -> urlInfo.set(u));

        StepVerifier
                .create(createUrlInfo)
                .assertNext(u -> assertNotNull(u.getShortUrl()))
                .expectComplete()
                .verify();

        // find by short URL
        Mono<UrlInfo> findUrlInfo = Mono.just(urlInfo.get().getShortUrl())
                .flatMap(shortUrlService::findByShortUrl);
        StepVerifier
                .create(findUrlInfo)
                .assertNext(u -> assertNotNull(u))
                .expectComplete()
                .verify();

        // update short URL
        String newUrl = "https://github.com/";
        Mono<UrlInfo> updateUrlInfo = Mono.just(urlInfo.get())
                .doOnNext(u -> u.setLongUrl(newUrl))
                .flatMap(shortUrlService::update);

        StepVerifier
                .create(updateUrlInfo)
                .assertNext(u -> assertEquals(newUrl, u.getLongUrl()))
                .expectComplete()
                .verify();

        // delete short URL
        Mono<Boolean> deleteUrlInfo = Mono.just(urlInfo.get())
                .flatMap(u -> shortUrlService.delete(u.getShortUrl())
                        .then(shortUrlService.exist(u.getShortUrl())));

        StepVerifier
                .create(deleteUrlInfo)
                .assertNext(b -> assertFalse(b))
                .expectComplete()
                .verify();
    }

    private boolean validateShortUrlFormat(String shortUrl) {
        Matcher matcher = shortUrlPattern.matcher(shortUrl);
        return matcher.matches();
    }
}
