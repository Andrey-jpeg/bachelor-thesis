package org.sdu.bachelor.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.sdu.bachelor.util.Station;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.ZonedDateTime;
import java.util.List;

@Builder
@AllArgsConstructor
@Data
@Document(collection = "baskets")
public class Basket {
    private String id;
    private Station station;
    private List<String> product_category;
    private ZonedDateTime timestamp;
    private double ticket_number;
    private List<Double> volume;
    private List<Double> price;
    private double price_sum;
    private List<Integer> payment_type;
    private List<String> product;
}
