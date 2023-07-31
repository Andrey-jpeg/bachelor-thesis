import http from 'k6/http';

export let options = {
  vus: 1,
  iterations: 30,
  thresholds: {
    http_req_duration: ['p(95)<300'], // 95% of requests should be below 300ms
  },
};

export default function () {
    const intervals = [
        [new Date(2022, 1, 5).toISOString(),new Date(2022, 1, 6).toISOString()],
        [new Date(2022, 2, 15).toISOString(),new Date(2022, 2, 16).toISOString()],
        [new Date(2022, 5, 20).toISOString(),new Date(2022, 5, 21).toISOString()],
        [new Date(2022, 7, 8).toISOString(),new Date(2022, 7, 9).toISOString()],
        [new Date(2022, 10, 1).toISOString(),new Date(2022, 10, 2).toISOString()],
    ]

    const randomIndex = Math.floor(Math.random() * intervals.length);
    const randomInterval = intervals[randomIndex]

  const gqlBody = {
    query: `query($start: ZonedDateTime!, $end:ZonedDateTime!, $stations:[Station]!){
        getBasketsForStationInInterval(stations: $stations, start:$start, end:$end){
          id,
          station,
          product,
          product_category,
          price,
          volume,
          price_sum,
          timestamp
          }
      }`,
    variables: {
        "stations":[""],
        "start": randomInterval[0],
        "end":randomInterval[1]
      },
  };

  const response = http.post('http://localhost:8080/graphql', JSON.stringify(gqlBody), {
    headers: {
      'Content-Type': 'application/json',
    },
  });
}