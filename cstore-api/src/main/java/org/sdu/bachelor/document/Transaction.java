package org.sdu.bachelor.document;

import lombok.Data;
import org.sdu.bachelor.util.Station;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.ZonedDateTime;

@Data
@Document(collection = "transactions")
public class Transaction {

    @Id
    private String id;
    private String guid;
    private String external;
    private Station station;
    private String product_category;
    private ZonedDateTime timestamp;
    private double ticket_number;
    private double volume;
    private double price;
    private int payment_type;
    private String product;
}
