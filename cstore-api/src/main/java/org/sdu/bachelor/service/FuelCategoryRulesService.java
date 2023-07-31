package org.sdu.bachelor.service;

import org.sdu.bachelor.document.FuelCategoryRule;
import org.sdu.bachelor.repository.FuelCategoryRulesRepository;
import org.sdu.bachelor.util.Station;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FuelCategoryRulesService {

    private final FuelCategoryRulesRepository fuelCategoryRulesRepository;

    @Autowired
    public FuelCategoryRulesService(FuelCategoryRulesRepository fuelCategoryRulesRepository) {
        this.fuelCategoryRulesRepository = fuelCategoryRulesRepository;
    }


    public List<FuelCategoryRule> getFuelCategoryRules(List<Station> stations) {
        return fuelCategoryRulesRepository.getFuelCategoryRulesByStationIsIn(stations);
    }
}
