package org.sdu.bachelor.document;

import lombok.Data;

@Data
public class Rule {
    private String[] antecedents;
    private String[] consequents;
    private double antecedent_support;
    private double consequent_support;
    private double support;
    private double confidence;
    private double lift;
    private double leverage;
    private double conviction;
    private double zhangs_metric;


}
