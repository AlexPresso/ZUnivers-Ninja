package me.alexpresso.zuninja.services.request;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.alexpresso.zuninja.classes.mode.GameMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Service
public class RequestServiceImpl implements RequestService {

    private final static Logger logger = LoggerFactory.getLogger(RequestServiceImpl.class);

    private final ObjectMapper mapper;

    @Value(value = "${zunivers.apiBaseUrl}")
    private String apiBaseUrl;
    @Value(value = "${zunivers.frontBaseUrl}")
    private String frontBaseUrl;
    @Value(value = "${toolkit.gameMode}")
    private GameMode gameMode;


    public RequestServiceImpl() {
        this.mapper = new ObjectMapper();
    }


    @Override
    public Object request(final String uri, final String method, final Object data, final TypeReference<?> type) throws IOException, InterruptedException {
        logger.debug("Requesting {} - {}", method, uri);

        final HttpRequest.Builder builder = HttpRequest.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .timeout(Duration.ofMinutes(1))
            .headers(
                "accept", "application/json, text/plain, */**",
                "accept-language", "fr-FR,fr;q=0.9,en-US;q=0.8,en;q=0.7",
                "origin", this.frontBaseUrl,
                "referer", this.frontBaseUrl,
                "dnt", "1",
                "sec-ch-ua-platform", "\"Chromium\";v=\"94\", \"Google Chrome\";v=\"94\", \";Not A Brand\";v=\"99\"",
                "sec-ch-ua-mobile", "?0",
                "sec-ch-ua-platform", "Windows",
                "sec-fetch-dest", "empty",
                "sec-fetch-mode", "cors",
                "sec-fetch-site", "same-site",
                "user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/94.0.4606.81 Safari/537.36",
                "x-zunivers-rulesettype", this.gameMode.name()
            ).uri(URI.create(String.format("https://%s%s", this.apiBaseUrl, uri)));

        if (method.equalsIgnoreCase("GET")) {
            builder.GET();
        } else {
            builder.POST(HttpRequest.BodyPublishers.ofString(data != null ? this.mapper.writeValueAsString(data) : ""));
        }

        final var response = HttpClient.newHttpClient()
            .send(builder.build(), HttpResponse.BodyHandlers.ofString());

        if(response.statusCode() == 200)
            return this.mapper.readValue(response.body(), type);

        return null;
    }
}
