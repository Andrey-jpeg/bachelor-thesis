package org.sdu.bachelor.controller;

import org.sdu.bachelor.document.Cluster;
import org.sdu.bachelor.service.ClusterDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class ClusterDataController {
    private final ClusterDataService clusterDataService;

    @Autowired
    public ClusterDataController(ClusterDataService clusterDataService) {
        this.clusterDataService = clusterDataService;
    }

    @QueryMapping
    public List<Cluster> getClusters() {
        return clusterDataService.getClusters();
    }
}
