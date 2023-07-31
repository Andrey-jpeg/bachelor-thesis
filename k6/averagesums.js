import http from 'k6/http';
import { textSummary } from 'https://jslib.k6.io/k6-summary/0.0.2/index.js';


export let options = {
  vus: 1,
  iterations: 24*7*4*6,
  thresholds: {
    http_req_duration: ['p(95)<10'], // 95% of requests should be below 10ms
  },
};

export function setup() {
  const start = new Date("2022-06-01T12:00:00");
  const end = new Date(start.getTime() + (60 * 60 * 1000)); // Incremented by 1 hour
  return {
    start: start.toISOString(),
    end: end.toISOString(),
  };
}

export default function (data) {
  const { start, end } = data;

  const gqlBody = {
    query: `query($start: ZonedDateTime!, $end: ZonedDateTime!, $station: Station!) {
      getAverageHourlySums(start: $start, end: $end, station: $station) {
        timestamp
        average_hourly_sum
      }
    }`,
    variables: {
      start: start,
      end: end,
      station: "",
    },
  };

  const response = http.post('http://localhost:8080/graphql', JSON.stringify(gqlBody), {
    headers: {
      'Content-Type': 'application/json',
    },
  });

  const currentStart = new Date(start);
  const updatedStart = new Date(currentStart.getTime() + (60 * 60 * 1000)); // Incremented by 1 hour

  const currentEnd = new Date(end)
  const updatedEnd = new Date(currentEnd.getTime() + (60 * 60 * 1000)); // Incremented by 1 hour


  data.start = updatedStart.toISOString();
  data.end = updatedEnd.toISOString();
}