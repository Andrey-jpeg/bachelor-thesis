package org.sdu.bachelor.repository;

import org.sdu.bachelor.document.FuelRule;
import org.sdu.bachelor.util.Station;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FuelRulesRepository extends MongoRepository<FuelRule, String> {
    List<FuelRule> getFuelRulesByStationIsIn(List<Station> stations);
}
