package org.sdu.bachelor.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.sdu.bachelor.util.Station;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@AllArgsConstructor
@Document(collection = "cluster_data")
public class ClusterData {
    @Id
    private String id;
    private Station station;
    private double price_median;
    private double price_mean;
    private double gas_percentage;
    private int basket_count;
    private double price_std;
    private double latitude;
    private double longitude;
    private double cash_percentage;
    private int includes_partner;
    private double product_percentage;
    private double product_and_gas_percentage;
    private int group;
}
