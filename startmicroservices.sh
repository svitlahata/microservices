#!/bin/bash

#java -jar microservices/product-service/build/libs/*.jar &
#java -jar microservices/product-composite-service/build/libs/*.jar &
#java -jar microservices/recommendation-service/build/libs/*.jar &
#java -jar microservices/review-service/build/libs/*.jar &

./gradlew clean build &&  docker-compose build && ./test-em-all.bash start stop

