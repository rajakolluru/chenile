curl -X POST http://localhost:8180/realms/tenant0/protocol/openid-connect/token \
-H 'content-type: application/x-www-form-urlencoded' \
-d 'client_id=authz-servlet&client_secret=secret' \
-d 'username=t0-premium&password=t0-premium&grant_type=password' | jq .access_token

