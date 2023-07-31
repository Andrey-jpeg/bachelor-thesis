package org.sdu.bachelor.service;

import org.sdu.bachelor.document.ProductRule;
import org.sdu.bachelor.repository.ProductRuleRepository;
import org.sdu.bachelor.util.Daypart;
import org.sdu.bachelor.util.Station;
import org.sdu.bachelor.util.Weekday;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductRuleService {

    private final ProductRuleRepository productRuleRepository;

    @Autowired
    public ProductRuleService(ProductRuleRepository productRuleRepository) {
        this.productRuleRepository = productRuleRepository;
    }

    public List<ProductRule> getProductRulesByStationAndWeekdayAndDaypart(List<Station> stations, List<Weekday> weekdays, List<Daypart> dayparts) {
        return productRuleRepository.getProductRulesByStationIsInAndWeekdayIsInAndDaypartIsIn(stations, weekdays, dayparts);
    }
}
