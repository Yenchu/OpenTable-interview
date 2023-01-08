package com.opentable.coding.challenge;

import com.opentable.coding.challenge.dto.CreateShortUrl;
import com.opentable.coding.challenge.model.UrlInfo;
import com.opentable.coding.challenge.util.RandomStringGenerator;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BooleanSupplier;

@Slf4j
public class TestMain {

    static Random random = new Random();

    public static void main(String[] args) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        repeat();
        /*Mono.just(1)
                .repeatWhen(v -> {
                    log.info("v: {}", v);
                    return Flux.range(1, 5);
                })
                .subscribe(s -> log.info("final: {}", s));*/
        log.info("---");
        latch.await(3, TimeUnit.SECONDS);
    }

    static void repeat() {
        AtomicBoolean existed = new AtomicBoolean(true);
        Mono.fromSupplier(() -> RandomStringGenerator.generate(7))
                .flatMap(s -> Mono.just(random.nextBoolean()).map(b -> {
                    log.info("rstr: {}, existed: {}", s, b);
                    existed.set(b);
                    return b;
                }).zipWith(Mono.just(s)))
                .repeat(10, existed::get)
                .filter(t -> !t.getT1())
                .map(Tuple2::getT2)
                .single()
                .subscribe(s -> log.info("final rstr: {}", s));


//        List<String> uniqueShortStr = new ArrayList<>();
//        Mono.just(1)
//                .repeatWhen(repeat -> repeat.map(r -> RandomStringGenerator.generate(7))
//                        .flatMap(s -> {
//                            log.info("s: {}", s);
//                            return Mono.just(random.nextBoolean()).map(b -> !b?s:"");
//                        })
//                        .handle((s, sink) -> {
//                            log.info("handle s: {}", s);
//                            if ("".equals(s)) {sink.next("");}
//                            else {
//                                uniqueShortStr.add(s);
//                                sink.complete();
//                            }
//                        }))
//
//                .then(Mono.just(uniqueShortStr.size() > 0 ? uniqueShortStr.get(uniqueShortStr.size() - 1) : ""))
//                .flatMap(shortUrl -> saveUrlInfo(shortUrl, null))
//                .subscribe(u -> log.info("urlInfo: {}", u));

//        Mono.just(1)
//                .repeatWhen(repeat -> repeat.map(r -> {
//                    String rstr = RandomStringGenerator.generate(7);
//                    boolean existed = random.nextBoolean();
//                    log.info("rstr: {}, existed: {}", rstr, existed);
//                    if (!existed) {
//                        arr.add(rstr);
//                    }
//                    return existed;
//                }).handle((existed, sink) -> {
//                    if (existed) sink.next(existed);
//                    else sink.complete();
//                }))
//                .subscribe();
        //log.info("uniqueShortStr: {}", uniqueShortStr);
    }

    static Mono<String> toMono(List<String> uniqueShortStr) {
        String val = uniqueShortStr.size() > 0 ? uniqueShortStr.get(uniqueShortStr.size() - 1) : "";
        return Mono.justOrEmpty(val);
    }

    static Mono<UrlInfo> saveUrlInfo(String shortUrl, CreateShortUrl createShortUrl) {
        return Mono.just(UrlInfo.builder()
                .shortUrl(shortUrl)
                .longUrl(createShortUrl != null ? createShortUrl.getUrl() : "")
                .build());
    }

    static BooleanSupplier randomBool() {
        return () -> {
            boolean rt = random.nextBoolean();
            log.info("bool: {}", rt);
            return rt;
        };
    }

    static void generateRandomStr() {
        for (int i = 0; i < 20; i++) {
            String str = RandomStringGenerator.generate(7);
            log.info("str: {}", str);
        }
    }
}
