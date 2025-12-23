package me.alexpresso.zuninja.services.corporation;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import me.alexpresso.zuninja.classes.corporation.Corporation;
import me.alexpresso.zuninja.services.request.RequestService;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class CorporationServiceImpl implements CorporationService {

    private final RequestService requestService;


    @Override
    public Corporation fetchCorporation(final String id) throws IOException, InterruptedException {
        return (Corporation) this.requestService.request(
            String.format("/public/corporation/%s", id),
            "GET",
            new TypeReference<Corporation>() {}
        );
    }
}
