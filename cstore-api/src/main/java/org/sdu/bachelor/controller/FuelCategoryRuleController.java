package org.sdu.bachelor.controller;

import org.sdu.bachelor.document.FuelCategoryRule;
import org.sdu.bachelor.service.FuelCategoryRulesService;
import org.sdu.bachelor.util.Station;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class FuelCategoryRuleController {

    private final FuelCategoryRulesService fuelCategoryRulesService;

    @Autowired
    public FuelCategoryRuleController(FuelCategoryRulesService fuelCategoryRulesService) {
        this.fuelCategoryRulesService = fuelCategoryRulesService;
    }

    @QueryMapping
    public List<FuelCategoryRule> getFuelCategoryRules(@Argument List<Station> stations) {
        return fuelCategoryRulesService.getFuelCategoryRules(stations);
    }
}
