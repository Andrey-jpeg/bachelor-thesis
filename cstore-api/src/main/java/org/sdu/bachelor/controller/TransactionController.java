package org.sdu.bachelor.controller;

import org.sdu.bachelor.document.Transaction;
import org.sdu.bachelor.service.TransactionService;
import org.sdu.bachelor.util.Station;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.time.ZonedDateTime;
import java.util.List;

@Controller
public class TransactionController {

    private final TransactionService transactionService;

    @Autowired
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @QueryMapping
    public List<Transaction> getTransactionsForStationsInInterval(@Argument List<Station> stations, @Argument ZonedDateTime start, @Argument ZonedDateTime end) {
        return transactionService.getTransactionsInInterval(stations, start, end);
    }
}
