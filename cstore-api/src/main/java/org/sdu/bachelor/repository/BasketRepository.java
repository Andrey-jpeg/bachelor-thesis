package org.sdu.bachelor.repository;

import lombok.Value;
import org.sdu.bachelor.document.Basket;
import org.sdu.bachelor.util.Station;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.TreeSet;

@Repository
public interface BasketRepository extends MongoRepository<Basket, String> {

    @Aggregation(pipeline = {
            "{$match: {$and: [{timestamp: {$gte: ?0, $lt: ?1}}, {station: ?2}]}}",
            "{$group: {_id: {hour: {$hour: '$timestamp'}, year: {$year: '$timestamp'}, month: {$month: '$timestamp'}, day: {$dayOfMonth: '$timestamp'}}, hourlySum: {$sum: '$price_sum'}, documentCount: {$sum: 1}}}",
            "{$project: {_id: 0, timestamp: {$dateFromParts: {year: '$_id.year', month: '$_id.month', day: '$_id.day', hour: '$_id.hour'}}, averageHourlySum: {$divide: ['$hourlySum', '$documentCount']}}}",
    })
    TreeSet<AveragePriceSum> getBasketsHourlySum(ZonedDateTime startDateTime, ZonedDateTime endDateTime, Station station);

    List<Basket> findBasketsByStationIsInAndTimestampIsBetween(List<Station> stations, ZonedDateTime start, ZonedDateTime end);

    @Value
    @Document(collection = "baskets")
    class AveragePriceSum implements Comparable<AveragePriceSum> {
        @Field("timestamp")
        ZonedDateTime timestamp;

        @Field("averageHourlySum")
        double average_hourly_sum;

        @Override
        public int compareTo(AveragePriceSum other) {
            return this.timestamp.compareTo(other.timestamp);
        }

    }
}
