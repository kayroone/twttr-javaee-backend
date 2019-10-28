#!/usr/bin/env bash
cd keycloak/bin
./kcadm.sh config credentials --server http://localhost:8080/auth --realm master --user admin --password admin

USERID=$(./kcadm.sh create users -r public -s username=jw -s enabled=true -o --fields id | jq '.id' | tr -d '"')
echo $USERID
./kcadm.sh update users/$USERID/reset-password -r public -s type=password -s value=default -s temporary=false -n
./kcadm.sh add-roles --username jw --rolename user-role -r public
