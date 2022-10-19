#!/bin/bash
source ./.env
cd 'hasura/backend-quickstart'
hasura migrate apply --admin-secret $HASURA_ADMIN_SECRET
hasura metadata apply --admin-secret $HASURA_ADMIN_SECRET

