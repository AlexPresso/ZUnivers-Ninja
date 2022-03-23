package me.alexpresso.zuninja.services.vortex;

import com.fasterxml.jackson.core.type.TypeReference;
import me.alexpresso.zuninja.classes.vortex.VortexDetail;
import me.alexpresso.zuninja.classes.vortex.VortexStats;
import me.alexpresso.zuninja.services.request.RequestService;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class VortexServiceImpl implements VortexService {

    private final RequestService requestService;


    public VortexServiceImpl(final RequestService rs) {
        this.requestService = rs;
    }



    @Override
    public String fetchCurrentVortexPack() throws IOException, InterruptedException {
        final var vortex = (VortexDetail) this.requestService.request("/public/tower/season", "GET", new TypeReference<VortexDetail>() {});
        return vortex.getPackId();
    }

    @Override
    public VortexStats fetchUserVortexStats(final String discordTag) throws IOException, InterruptedException {
        return (VortexStats) this.requestService.request(
            String.format("/public/tower/%s", URLEncoder.encode(discordTag, StandardCharsets.UTF_8)),
            "GET",
            new TypeReference<VortexStats>() {}
        );
    }
}
