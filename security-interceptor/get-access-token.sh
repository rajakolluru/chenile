curl -X POST http://localhost:8180/realms/quickstart/protocol/openid-connect/token \
-H 'content-type: application/x-www-form-urlencoded' \
-d 'client_id=authz-servlet&client_secret=secret' \
-d 'username=jdoe&password=jdoe&grant_type=password' | jq .access_token

