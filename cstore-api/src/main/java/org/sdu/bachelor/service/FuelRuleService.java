package org.sdu.bachelor.service;

import org.sdu.bachelor.document.FuelRule;
import org.sdu.bachelor.repository.FuelRulesRepository;
import org.sdu.bachelor.util.Station;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FuelRuleService {
    private final FuelRulesRepository fuelRulesRepository;

    @Autowired
    public FuelRuleService(FuelRulesRepository fuelRulesRepository) {
        this.fuelRulesRepository = fuelRulesRepository;
    }

    public List<FuelRule> getFuelRules(List<Station> stations) {
        return fuelRulesRepository.getFuelRulesByStationIsIn(stations);
    }
}
