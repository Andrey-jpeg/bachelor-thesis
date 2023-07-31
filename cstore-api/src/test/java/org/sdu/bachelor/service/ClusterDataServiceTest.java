package org.sdu.bachelor.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sdu.bachelor.document.Cluster;
import org.sdu.bachelor.document.ClusterData;
import org.sdu.bachelor.repository.ClusterDataRepository;
import org.sdu.bachelor.util.Station;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class ClusterDataServiceTest {

    @InjectMocks
    ClusterDataService clusterDataService;

    @Mock
    ClusterDataRepository clusterDataRepository;

    @Test
    void getClustersMultipleClustersTest() {
        ClusterData clusterDataA = ClusterData.builder().station(Station.AAAAA).group(0).build();
        ClusterData clusterDataB = ClusterData.builder().station(Station.BBBBB).group(0).build();
        ClusterData clusterDataC = ClusterData.builder().station(Station.CCCCC).group(1).build();
        ClusterData clusterDataD = ClusterData.builder().station(Station.AAAAA).group(1).build();
        ClusterData clusterDataE = ClusterData.builder().station(Station.BBBBB).group(2).build();
        List<ClusterData> clusterDataList = new ArrayList<>(Arrays.asList(clusterDataA,clusterDataB,clusterDataC, clusterDataD, clusterDataE));

        Mockito.when(clusterDataRepository.findAll()).thenReturn(clusterDataList);

        List<Cluster> result = clusterDataService.getClusters();

        Cluster zero = new Cluster(Arrays.asList(clusterDataA, clusterDataB));
        Cluster one = new Cluster(Arrays.asList(clusterDataC, clusterDataD));
        Cluster two = new Cluster(Arrays.asList(clusterDataE));
        List<Cluster> expectedResult = new ArrayList<>(Arrays.asList(zero,one,two));

        Assertions.assertEquals(expectedResult, result);
    }

    @Test
    void getClustersOneClusterTest() {
        ClusterData clusterDataA = ClusterData.builder().station(Station.AAAAA).group(0).build();
        ClusterData clusterDataB = ClusterData.builder().station(Station.BBBBB).group(0).build();
        List<ClusterData> clusterDataList = new ArrayList<>(Arrays.asList(clusterDataA,clusterDataB));

        Mockito.when(clusterDataRepository.findAll()).thenReturn(clusterDataList);

        List<Cluster> result = clusterDataService.getClusters();

        Cluster zero = new Cluster(Arrays.asList(clusterDataA, clusterDataB));
        List<Cluster> expectedResult = new ArrayList<>(List.of(zero));

        Assertions.assertEquals(expectedResult, result);
    }

    @Test
    void getClustersNoClustersTest() {
        List<ClusterData> clusterDataList = new ArrayList<>(List.of());

        Mockito.when(clusterDataRepository.findAll()).thenReturn(clusterDataList);

        List<Cluster> result = clusterDataService.getClusters();
        List<Cluster> expectedResult = new ArrayList<>(List.of());

        Assertions.assertEquals(expectedResult, result);
    }
}
