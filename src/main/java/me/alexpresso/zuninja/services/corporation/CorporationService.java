package me.alexpresso.zuninja.services.corporation;

import me.alexpresso.zuninja.classes.corporation.Corporation;

import java.io.IOException;

public interface CorporationService {
    Corporation fetchCorporation(final String id) throws IOException, InterruptedException;
}
