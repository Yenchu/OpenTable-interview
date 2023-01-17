package com.opentable.coding.challenge.advice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.reactive.handler.WebFluxResponseStatusExceptionHandler;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class ExceptionHandler extends WebFluxResponseStatusExceptionHandler {

    private ObjectMapper objectMapper;

    public ExceptionHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public static Map<String, Object> getErrMsg(Throwable ex) {
        Map<String, Object> errMsg = new HashMap<>();
        if (ex instanceof WebExchangeBindException) {
            List<String> msgs = ((WebExchangeBindException) ex).getBindingResult()
                    .getAllErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.toList());
            errMsg.put("message", msgs);
        } else {
            errMsg.put("message", ex.getMessage());
        }
        return errMsg;
    }

    public static Mono<Void> respond(ObjectMapper objectMapper, ServerHttpResponse response, Throwable ex) {
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> errMsg = getErrMsg(ex);
        String respBody;
        try {
            respBody = objectMapper.writeValueAsString(errMsg);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
            respBody = errMsg.get("message").toString();
        }

        byte[] bytes = respBody.getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = response.bufferFactory().wrap(bytes);
        return response.writeWith(Mono.just(buffer));
    }

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        String errLog = String.format("%s %s error: %s", request.getMethod(), request.getURI().getRawPath(), ex.getMessage());
        log.error(errLog, ex.getCause());

        setResponseStatus(response, ex);
        return respond(objectMapper, response, ex);
    }

    private void setResponseStatus(ServerHttpResponse response, Throwable ex) {
        boolean statusSet = false;
        int code = determineRawStatusCode(ex);
        if (code != -1) {
            if (response.setRawStatusCode(code)) {
                if (ex instanceof ResponseStatusException) {
                    ((ResponseStatusException) ex).getResponseHeaders()
                            .forEach((name, values) ->
                                    values.forEach(value -> response.getHeaders().add(name, value)));
                }
                statusSet = true;
            }
        } else {
            Throwable cause = ex.getCause();
            if (cause != null) {
                setResponseStatus(response, cause);
                statusSet = true;
            }
        }
        if (!statusSet) {
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
