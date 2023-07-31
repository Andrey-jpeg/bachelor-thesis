package org.sdu.bachelor.service;

import org.sdu.bachelor.document.Basket;
import org.sdu.bachelor.repository.BasketRepository;
import org.sdu.bachelor.util.Station;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;

@Service
public class BasketService {

    private final BasketRepository basketRepository;

    @Autowired
    public BasketService(BasketRepository basketRepository) {
        this.basketRepository = basketRepository;
    }

    public Set<BasketRepository.AveragePriceSum> getAverageHourlySum(ZonedDateTime startDateTime,
                                                                     ZonedDateTime endDateTime,
                                                                     Station station) {
        startDateTime = startDateTime.withMinute(0).withSecond(0);
        endDateTime = endDateTime.withMinute(0).withSecond(0);
        Set<BasketRepository.AveragePriceSum> result = basketRepository.getBasketsHourlySum(startDateTime, endDateTime, station);

        int hours = (endDateTime.getHour() - startDateTime.getHour()) - 1;
        for (int i = 0; i <= hours; i++) {

            ZonedDateTime currentDateTime = startDateTime.plusHours(i).withZoneSameInstant(ZoneOffset.UTC);
            boolean noneMatch = result.stream().noneMatch(element -> currentDateTime.equals(element.getTimestamp()));
            if (noneMatch) {
                BasketRepository.AveragePriceSum priceSum = new BasketRepository.AveragePriceSum(currentDateTime, 0.0);
                result.add(priceSum);
            }
        }

        return result;
    }


    public List<Basket> getBasketsForStationInInterval(List<Station> stations,
                                                       ZonedDateTime start,
                                                       ZonedDateTime end) {
        return basketRepository.findBasketsByStationIsInAndTimestampIsBetween(stations, start, end);
    }

}
