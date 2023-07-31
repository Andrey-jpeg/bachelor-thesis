package org.sdu.bachelor.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.sdu.bachelor.util.Daypart;
import org.sdu.bachelor.util.Station;
import org.sdu.bachelor.util.Weekday;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.ZonedDateTime;

@Builder
@AllArgsConstructor
@Data
@Document(collection = "product_rules")
public class ProductRule {
    @Id
    private String id;
    private Station station;
    private Weekday weekday;
    private Daypart daypart;
    private ZonedDateTime created_timestamp;
    private int sample_size;
    private Rule[] rules;
}
