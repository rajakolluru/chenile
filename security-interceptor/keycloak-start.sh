docker rm keycloak
docker run --name keycloak \
  -e KEYCLOAK_ADMIN=admin \
  -e KEYCLOAK_ADMIN_PASSWORD=admin \
  -p 8180:8180 \
  quay.io/keycloak/keycloak:25.0.1 \
  start-dev \
  --http-port=8180 --features=organization 
