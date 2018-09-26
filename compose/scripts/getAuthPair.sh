#!/usr/bin/env bash

IFS='=' read -r -a initial <<< $(cat ~/.wskprops | tail -n 1)
IFS=':' read -r -a pair <<< "${initial[1]}"
echo "username: ${pair[0]}"
echo "password: ${pair[1]}"
