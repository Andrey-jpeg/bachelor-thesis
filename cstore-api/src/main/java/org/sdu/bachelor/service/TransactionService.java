package org.sdu.bachelor.service;

import org.sdu.bachelor.document.Transaction;
import org.sdu.bachelor.repository.TransactionRepository;
import org.sdu.bachelor.util.Station;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public List<Transaction> getTransactionsInInterval(List<Station> stations, ZonedDateTime start, ZonedDateTime end) {
        return transactionRepository.findTransactionsByStationIsInAndTimestampIsBetween(stations, start, end);
    }

}
