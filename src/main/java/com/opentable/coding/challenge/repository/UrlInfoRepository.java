package com.opentable.coding.challenge.repository;

import com.opentable.coding.challenge.model.UrlInfo;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface UrlInfoRepository extends ReactiveMongoRepository<UrlInfo, String> {
}
