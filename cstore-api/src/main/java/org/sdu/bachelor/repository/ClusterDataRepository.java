package org.sdu.bachelor.repository;

import org.sdu.bachelor.document.ClusterData;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClusterDataRepository extends MongoRepository<ClusterData, String> {

}
