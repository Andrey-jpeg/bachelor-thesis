package org.sdu.bachelor.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.sdu.bachelor.ContainerBase;
import org.sdu.bachelor.document.FuelRule;
import org.sdu.bachelor.document.Rule;
import org.sdu.bachelor.util.Station;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FuelRulesRepositoryIT extends ContainerBase {

    @Autowired
    private FuelRulesRepository hasFuelRuleRepo;

    @Test
    void getLatestFuelRulesForAllStationsIT() {
        Rule rule1Data = new Rule();
        rule1Data.setAntecedents(new String[]{"A1", "A2"});
        rule1Data.setConsequents(new String[]{"C1"});

        LocalDateTime newestTimestamp = LocalDateTime.of(2020, 1, 1, 1, 1);
        LocalDateTime oldestTimestamp = LocalDateTime.of(2018, 2, 1, 1, 1);

        FuelRule rule1 = new FuelRule();
        rule1.setId("1");
        rule1.setStation(Station.AAAAA);
        rule1.setCreated_timestamp(ZonedDateTime.of(newestTimestamp, ZoneId.of("UTC")));
        rule1.setRules(new Rule[]{rule1Data});

        FuelRule rule1_old = new FuelRule();
        rule1_old.setId("2");
        rule1_old.setStation(Station.AAAAA);
        rule1_old.setCreated_timestamp(ZonedDateTime.of(oldestTimestamp, ZoneId.of("UTC")));
        rule1_old.setRules(new Rule[]{rule1Data});

        hasFuelRuleRepo.save(rule1);
        hasFuelRuleRepo.save(rule1_old);

        List<FuelRule> latestFuelRules = hasFuelRuleRepo.getFuelRulesByStationIsIn(List.of(Station.AAAAA));

        Assertions.assertEquals("1", latestFuelRules.get(0).getId());
    }
}
