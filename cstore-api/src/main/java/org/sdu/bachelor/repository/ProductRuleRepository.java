package org.sdu.bachelor.repository;

import org.sdu.bachelor.document.ProductRule;
import org.sdu.bachelor.util.Daypart;
import org.sdu.bachelor.util.Station;
import org.sdu.bachelor.util.Weekday;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRuleRepository extends MongoRepository<ProductRule, String> {

    List<ProductRule> getProductRulesByStationIsInAndWeekdayIsInAndDaypartIsIn(List<Station> station, List<Weekday> weekday, List<Daypart> daypart);
}
