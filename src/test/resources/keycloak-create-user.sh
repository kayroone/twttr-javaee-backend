#!/usr/bin/env bash

#* Keycloak data *#
REALM='public'
TEST_USERNAME='jw'
KEYCLOAK_SERVER='http://localhost:8080/auth'

#* Navigate to admin CLI *#
cd /opt/jboss/keycloak/bin

#* Login to auth server *#
./kcadm.sh config credentials --server ${KEYCLOAK_SERVER} --realm master --user admin --password admin

#* Check for existing test user *#
EXISTING_USER_ID=$(./kcadm.sh get users -r ${REALM} -q username=${TEST_USERNAME} | grep id | cut -d ':' -f 2 | tr -d '", ')
echo "Existing user found: $EXISTING_USER_ID"

if [[ ${EXISTING_USER_ID} ]]; then
    ./kcadm.sh delete users/${EXISTING_USER_ID} -r ${REALM}
    echo "Deleted user with ID: ${EXISTING_USER_ID}"
fi

#* Create new test user *#
NEW_USER_ID=$(./kcadm.sh create users -r ${REALM} -s username=${TEST_USERNAME} -s enabled=true -o --fields id | grep id | cut -d ':' -f 2 | tr -d '" ')
echo "New user with ID $EXISTING_USER_ID created"

if [[ ${NEW_USER_ID} ]]; then
    ./kcadm.sh update users/${NEW_USER_ID}/reset-password -r ${REALM} -s type=password -s value=${TEST_USERNAME} -s temporary=false -n
    ./kcadm.sh add-roles --uusername ${TEST_USERNAME} --rolename user-role -r ${REALM}
    echo "Updated user with ID: ${NEW_USER_ID}"
fi
