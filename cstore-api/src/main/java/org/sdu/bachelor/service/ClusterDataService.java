package org.sdu.bachelor.service;

import org.sdu.bachelor.document.Cluster;
import org.sdu.bachelor.document.ClusterData;
import org.sdu.bachelor.repository.ClusterDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ClusterDataService {

    private final ClusterDataRepository clusterDataRepository;

    @Autowired
    public ClusterDataService(ClusterDataRepository clusterDataRepository) {
        this.clusterDataRepository = clusterDataRepository;
    }

    public List<Cluster> getClusters() {
        List<Cluster> result = new ArrayList<>();
        List<ClusterData> clusterData = clusterDataRepository.findAll();

        Map<Integer, List<ClusterData>> clusters = new HashMap<>();

        clusterData.forEach(station -> {
            List<ClusterData> stationList = clusters.getOrDefault(station.getGroup(), new ArrayList<ClusterData>());
            stationList.add(station);
            clusters.put(station.getGroup(), stationList);
        });

        for (Map.Entry<Integer, List<ClusterData>> entry : clusters.entrySet()) {
            result.add(new Cluster(entry.getValue()));
        }
        return result;
    }
}
