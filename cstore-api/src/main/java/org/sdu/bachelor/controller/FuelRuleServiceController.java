package org.sdu.bachelor.controller;

import org.sdu.bachelor.document.FuelRule;
import org.sdu.bachelor.service.FuelRuleService;
import org.sdu.bachelor.util.Station;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class FuelRuleServiceController {

    private final FuelRuleService fuelRuleService;

    @Autowired
    public FuelRuleServiceController(FuelRuleService fuelRuleService) {
        this.fuelRuleService = fuelRuleService;
    }

    @QueryMapping
    public List<FuelRule> getFuelRules(@Argument List<Station> stations) {
        return fuelRuleService.getFuelRules(stations);
    }
}
