package me.alexpresso.zuniverstk.services.request;

import com.fasterxml.jackson.core.type.TypeReference;

import java.io.IOException;

public interface RequestService {
    default Object request(String uri, String method, TypeReference<?> type) throws IOException, InterruptedException {
        return this.request(uri, method, null, type);
    }

    Object request(String uri, String method, Object data, TypeReference<?> type) throws IOException, InterruptedException;
}
