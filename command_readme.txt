

docker network create -d bridge cloud-net
docker network create -d bridge store-net
docker run -d --name emqx --network cloud-net  -p 1883:1883 -p 8083:8083 -p 8084:8084 -p 8883:8883 -p 18083:18083  emqx/emqx:latest
docker network disconnect cloud-net emqx
docker network connect cloud-net emqx


docker build -t pos/cloud .
docker run -d --name cloud --network cloud-net  -p 8001:8001  pos/cloud:latest

docker network disconnect cloud-net cloud
docker network connect cloud-net cloud



docker build -t pos/store .
docker run -d --name store --network cloud-net --network store-net -p 8000:8000  pos/store:latest