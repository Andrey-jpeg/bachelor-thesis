package org.sdu.bachelor.document;

import lombok.Data;
import org.sdu.bachelor.util.Station;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.ZonedDateTime;

@Data
@Document(collection = "fuel_rules")
public class FuelRule {
    @Id
    private String id;
    private Station station;
    private int sample_size;
    private ZonedDateTime created_timestamp;
    private Rule[] rules;
}
