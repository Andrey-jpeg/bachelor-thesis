package org.sdu.bachelor.repository;

import org.sdu.bachelor.document.FuelCategoryRule;
import org.sdu.bachelor.util.Station;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FuelCategoryRulesRepository extends MongoRepository<FuelCategoryRule, String> {

    List<FuelCategoryRule> getFuelCategoryRulesByStationIsIn(List<Station> stations);
}
