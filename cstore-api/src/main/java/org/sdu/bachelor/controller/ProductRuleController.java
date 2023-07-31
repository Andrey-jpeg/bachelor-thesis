package org.sdu.bachelor.controller;

import org.sdu.bachelor.document.ProductRule;
import org.sdu.bachelor.service.ProductRuleService;
import org.sdu.bachelor.util.Daypart;
import org.sdu.bachelor.util.Station;
import org.sdu.bachelor.util.Weekday;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class ProductRuleController {
    private final ProductRuleService productRuleService;

    @Autowired
    public ProductRuleController(ProductRuleService productRuleService) {
        this.productRuleService = productRuleService;
    }

    @QueryMapping
    public List<ProductRule> getProductRulesByStationAndWeekdayAndDaypart(@Argument List<Station> stations, @Argument List<Weekday> weekdays, @Argument List<Daypart> dayparts) {
        return productRuleService.getProductRulesByStationAndWeekdayAndDaypart(stations, weekdays, dayparts);
    }
}
