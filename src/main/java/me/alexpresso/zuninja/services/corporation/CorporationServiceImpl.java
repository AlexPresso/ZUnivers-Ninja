package me.alexpresso.zuninja.services.corporation;

import com.fasterxml.jackson.core.type.TypeReference;
import me.alexpresso.zuninja.classes.corporation.Corporation;
import me.alexpresso.zuninja.services.request.RequestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class CorporationServiceImpl implements CorporationService {

    private final static Logger logger = LoggerFactory.getLogger(CorporationServiceImpl.class);

    private final RequestService requestService;


    public CorporationServiceImpl(RequestService requestService) {
        this.requestService = requestService;
    }


    @Override
    public Corporation fetchCorporation(final String id) throws IOException, InterruptedException {
        return (Corporation) this.requestService.request(
            String.format("/public/corporation/%s", id),
            "GET",
            new TypeReference<Corporation>() {}
        );
    }
}
