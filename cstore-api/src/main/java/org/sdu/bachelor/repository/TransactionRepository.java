package org.sdu.bachelor.repository;

import org.sdu.bachelor.document.Transaction;
import org.sdu.bachelor.util.Station;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends MongoRepository<Transaction, String> {
    List<Transaction> findTransactionsByStationIsInAndTimestampIsBetween(List<Station> stations, ZonedDateTime start, ZonedDateTime end);
}