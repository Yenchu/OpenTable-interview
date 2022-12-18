package com.opentable.coding.challenge.service;

import com.opentable.coding.challenge.dto.CreateShortUrl;
import com.opentable.coding.challenge.model.UrlInfo;
import com.opentable.coding.challenge.repository.UrlInfoRepository;
import com.opentable.coding.challenge.util.RandomStringGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.util.concurrent.atomic.AtomicBoolean;

import static com.opentable.coding.challenge.model.UrlInfo.SHORT_URL_LENGTH;

@Service
@Slf4j
public class ShortUrlService {

    private UrlInfoRepository urlInfoRepository;

    public ShortUrlService(UrlInfoRepository urlInfoRepository) {
        this.urlInfoRepository = urlInfoRepository;
    }

    public Mono<UrlInfo> create(CreateShortUrl createShortUrl) {
        return generateUniqueShortUrl().flatMap(shortUrl -> createUrlInfo(shortUrl, createShortUrl));
    }

    public Mono<UrlInfo> findByShortUrl(String shortUrl) {
        return urlInfoRepository.findById(shortUrl);
    }

    public Mono<UrlInfo> update(UrlInfo updatedUrlInfo) {
        return urlInfoRepository.findById(updatedUrlInfo.getShortUrl())
                .flatMap(urlInfo -> updateUrlInfo(urlInfo, updatedUrlInfo));
    }

    public Mono<Void> delete(String shortUrl) {
        return urlInfoRepository.deleteById(shortUrl);
    }

    public Mono<Boolean> exist(String shortUrl) {
        return urlInfoRepository.existsById(shortUrl);
    }

    public Flux<UrlInfo> findAll() {
        return urlInfoRepository.findAll();
    }

    private Mono<String> generateUniqueShortUrl() {
        AtomicBoolean shortUrlExisted = new AtomicBoolean(true);
        return Mono.fromSupplier(() -> RandomStringGenerator.generate(SHORT_URL_LENGTH))
                // check if this short url already existed in db
                .flatMap(shortUrl -> urlInfoRepository.existsById(shortUrl).map(existed -> {
                    shortUrlExisted.set(existed);
                    return existed;
                }).zipWith(Mono.just(shortUrl)))
                // repeat generating new short url if existing in db
                .repeat(shortUrlExisted::get)
                // filter out the short urls existing in db
                .filter(t -> !t.getT1())
                .map(Tuple2::getT2)
                .single();
    }

    private Mono<UrlInfo> createUrlInfo(String shortUrl, CreateShortUrl createShortUrl) {
        UrlInfo urlInfo = UrlInfo.builder()
                .shortUrl(shortUrl)
                .longUrl(createShortUrl.getUrl())
                .build();
        return urlInfoRepository.save(urlInfo);
    }

    private Mono<UrlInfo> updateUrlInfo(UrlInfo urlInfo, UrlInfo updatedUrlInfo) {
        if (urlInfo != null) {
            urlInfo.setLongUrl(updatedUrlInfo.getLongUrl());
        } else {
            urlInfo = updatedUrlInfo;
        }
        return urlInfoRepository.save(urlInfo);
    }
}
