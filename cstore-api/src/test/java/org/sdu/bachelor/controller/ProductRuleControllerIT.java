package org.sdu.bachelor.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sdu.bachelor.ContainerBase;
import org.sdu.bachelor.document.ProductRule;
import org.sdu.bachelor.repository.ProductRuleRepository;
import org.sdu.bachelor.util.Daypart;
import org.sdu.bachelor.util.Station;
import org.sdu.bachelor.util.Weekday;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProductRuleControllerIT extends ContainerBase {

    @Autowired
    private RestTemplateBuilder restTemplateBuilder;
    @Autowired
    private ProductRuleRepository productRuleRepository;
    private RestTemplate restTemplate;
    @LocalServerPort
    private int port;

    @BeforeEach
    public void setup() {
        restTemplate = restTemplateBuilder.build();

        ProductRule productRuleA = ProductRule.builder()
                .id("1")
                .created_timestamp(ZonedDateTime.of(LocalDateTime.of(2023, 5, 10, 1, 1), ZoneId.of("UTC"))) //Wed 17 May 2023
                .station(Station.AAAAA)
                .weekday(Weekday.Monday)
                .daypart(Daypart.Morning)
                .build();

        ProductRule productRuleB = ProductRule.builder()
                .id("3")
                .created_timestamp(ZonedDateTime.of(LocalDateTime.of(2023, 5, 16, 1, 1), ZoneId.of("UTC"))) //Tue 16 May 2023
                .station(Station.BBBBB)
                .weekday(Weekday.Monday)
                .daypart(Daypart.Morning)
                .build();

        ProductRule productRuleC = ProductRule.builder()
                .id("4")
                .created_timestamp(ZonedDateTime.of(LocalDateTime.of(2023, 4, 17, 1, 1), ZoneId.of("UTC"))) //Wed 17 May 2023
                .station(Station.CCCCC)
                .weekday(Weekday.Monday)
                .daypart(Daypart.Morning)
                .build();

        productRuleRepository.saveAll(List.of(productRuleA, productRuleB, productRuleC));
    }

    @Test
    void getProductRulesByStationWeekdayAndDaypartIT() {
        String graphQLQuery = "{ \"query\": \"query { getProductRulesByStationAndWeekdayAndDaypart (stations: [STARN] weekdays: [Monday] dayparts: [Morning]) { id } }\" }";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        URI uri = UriComponentsBuilder.fromUriString("http://localhost")
                .port(port)
                .path("/graphql")
                .build()
                .toUri();

        HttpEntity<String> requestEntity = new HttpEntity<>(graphQLQuery, headers);
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(uri, requestEntity, String.class);
        String expectedBody = "{\"data\":{\"getProductRulesByStationAndWeekdayAndDaypart\":[{\"id\":\"1\"}]}}";
        Assertions.assertEquals(expectedBody, responseEntity.getBody());
    }

    @Test
    void getProductRulesByStationWeekdayAndDaypartMultipleStationsIT() {
        String graphQLQuery = "{ \"query\": \"query { getProductRulesByStationAndWeekdayAndDaypart (stations: [STARN, STBIL] weekdays: [Monday] dayparts: [Morning]) { id } }\" }";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        URI uri = UriComponentsBuilder.fromUriString("http://localhost")
                .port(port)
                .path("/graphql")
                .build()
                .toUri();

        HttpEntity<String> requestEntity = new HttpEntity<>(graphQLQuery, headers);
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(uri, requestEntity, String.class);
        List<String> resultList = (Arrays.stream(responseEntity.getBody().split(",")).toList());

        List<String> expectedIds = List.of("1", "3");

        // Check if each expected ID appears exactly once in the resultList
        Assertions.assertEquals(2, resultList.size());
        for (String expectedId : expectedIds) {
            Assertions.assertTrue(resultList.stream().anyMatch(result -> result.contains(expectedId)));
        }
    }

    @Test
    void getProductRulesByStationWeekdayAndDaypartNoInputIT() {
        String graphQLQuery = "{ \"query\": \"query { getProductRulesByStationAndWeekdayAndDaypart (stations: [] weekdays: [] dayparts: []) { id } }\" }";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        URI uri = UriComponentsBuilder.fromUriString("http://localhost")
                .port(port)
                .path("/graphql")
                .build()
                .toUri();

        HttpEntity<String> requestEntity = new HttpEntity<>(graphQLQuery, headers);
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(uri, requestEntity, String.class);

        String expectedBody = "{\"data\":{\"getProductRulesByStationAndWeekdayAndDaypart\":[]}}";
        Assertions.assertEquals(expectedBody, responseEntity.getBody());
    }
}
