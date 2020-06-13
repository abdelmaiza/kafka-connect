# docker compose volume mounts not work on Windows
in a CMD window, SET COMPOSE_CONVERT_WINDOWS_PATHS=1


curl -H "Accept:application/json" localhost:8083/
curl -H "Accept:application/json" localhost:8083/connectors/

# Start MySQL connector
curl -i -X POST -H "Accept:application/json" -H  "Content-Type:application/json" http://localhost:8083/connectors/ -d @mysql-source.json

# Start elasticsearch sink connector
curl -i -X POST -H "Accept:application/json" -H  "Content-Type:application/json" http://localhost:8084/connectors/ -d @elasticsearch-sink.json

docker exec -it kafka bash
cd /bin

# inventory
./kafka-topics.sh --list --bootstrap-server 192.168.0.14:9092
./kafka-console-consumer.sh --bootstrap-server 192.168.0.14:9092 --topic dbserver1.inventory.addresses --from-beginning
./kafka-console-consumer.sh --bootstrap-server 192.168.0.14:9092 --topic dbserver1.inventory.customers --from-beginning
./kafka-console-consumer.sh --bootstrap-server 192.168.0.14:9092 --topic dbserver1.inventory.geom --from-beginning
./kafka-console-consumer.sh --bootstrap-server 192.168.0.14:9092 --topic dbserver1.inventory.orders --from-beginning
./kafka-console-consumer.sh --bootstrap-server 192.168.0.14:9092 --topic dbserver1.inventory.products --from-beginning
./kafka-console-consumer.sh --bootstrap-server 192.168.0.14:9092 --topic dbserver1.inventory.products_on_hand --from-beginning


# to find connector
https://www.confluent.io/hub/