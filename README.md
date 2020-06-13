# docker compose volume mounts not work on Windows
in a CMD window, SET COMPOSE_CONVERT_WINDOWS_PATHS=1

# Start MySQL connector
curl -i -X POST -H "Accept:application/json" -H  "Content-Type:application/json" http://localhost:8083/connectors/ -d @mysql-source.json 

# Start elasticsearch sink connector
curl -i -X POST -H "Accept:application/json" -H  "Content-Type:application/json" http://localhost:8084/connectors/ -d @elasticsearch-sink.json