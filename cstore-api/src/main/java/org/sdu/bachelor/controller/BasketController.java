package org.sdu.bachelor.controller;

import org.sdu.bachelor.document.Basket;
import org.sdu.bachelor.repository.BasketRepository;
import org.sdu.bachelor.service.BasketService;
import org.sdu.bachelor.util.Station;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;

@Controller
public class BasketController {
    private final BasketService basketService;

    @Autowired
    public BasketController(BasketService basketService) {
        this.basketService = basketService;
    }

    @QueryMapping
    public Set<BasketRepository.AveragePriceSum> getAverageHourlySums(@Argument Station station, @Argument ZonedDateTime start, @Argument ZonedDateTime end) {
        return basketService.getAverageHourlySum(start, end, station);
    }

    @QueryMapping
    public List<Basket> getBasketsForStationInInterval(@Argument List<Station> stations, @Argument ZonedDateTime start, @Argument ZonedDateTime end) {
        return basketService.getBasketsForStationInInterval(stations, start, end);
    }
}
