package me.alexpresso.zuniverstk.services.request;

import com.fasterxml.jackson.core.type.TypeReference;

import java.io.IOException;

public interface RequestService {
    Object request(String uri, String method, Object data, TypeReference<?> type) throws IOException, InterruptedException;
}
