#!/bin/bash

for (( i = 0; i < 20000; i++ )); do
    redis-cli ping
#    sleep 2
done