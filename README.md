# spring-boot-kafka
```
docker run --rm -p 2181:2181 -p 3030:3030 -p 8081-8083:8081-8083 -p 9581-9585:9581-9585 -p 9092:9092 -e ADV_HOST=127.0.0.1 landoop/fast-data-dev:latest
```

```
docker run --rm -it -p 8000:8000 -e "KAFKA_REST_PROXY_URL=http://localhost:8082"  landoop/kafka-topics-ui
```


show all topics and paritions on http://localhost:8000

---
docker run -e ADV_HOST=172.31.28.10 -e EULA="https://dl.lenses.stream/d/?id=a2fdac84-f256-420e-8f46-8a2be96b1962" --rm -p 3030:3030 -p 9092:9092 -p 2181:2181 -p 8081:8081 -p 9581:9581 -p 9582:9582 -p 9584:9584 -p 9585:9585 landoop/kafka-lenses-dev &

docker run -e ADV_HOST=34.231.47.10 \
  -d --name kafka \
  -e EULA="https://dl.lenses.stream/d/?id=a2fdac84-f256-420e-8f46-8a2be96b1962" \
  --rm \
  -p 3030:3030 -p 9092:9092 \
  -p 2181:2181 -p 8081:8081 \
  -p 9581:9581 -p 9582:9582 \
  -p 9584:9584 -p 9585:9585 \
  -p 8082:8082 -p 8083:8083 \
  -p 9991:9991 \
  -e SAMPLEDATA=0 \
  -e RUNNING_SAMPLEDATA=0 \
  landoop/kafka-lenses-dev
