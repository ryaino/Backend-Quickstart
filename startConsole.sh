#!/bin/bash
source ./.env
cd 'hasura/backend-quickstart'
hasura console --admin-secret "$HASURA_ADMIN_SECRET"