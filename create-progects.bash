#!/bin/bash
echo "Microservices creation script"

mkdir microservices
cd microservices

BOOT_VERSION=2.5.6
JAVA_VERSION=16

spring init \
--boot-version=$BOOT_VERSION \
--build=gradle \
--java-version=$JAVA_VERSION \
--packaging=jar \
--name=product-service \
--package-name=nl.svh.microservices.core.product \
--groupId=nl.svh.microservices.core.product \
--dependencies=actuator,webflux \
--version=1.0.0-SNAPSHOT \
product-service

echo 'product-service created'

spring init \
--boot-version=$BOOT_VERSION \
--build=gradle \
--java-version=$JAVA_VERSION \
--packaging=jar \
--name=review-service \
--package-name=nl.svh.microservices.core.review \
--groupId=nl.svh.microservices.core.review \
--dependencies=actuator,webflux \
--version=1.0.0-SNAPSHOT \
review-service

echo 'review-service created'

spring init \
--boot-version=$BOOT_VERSION \
--build=gradle \
--java-version=$JAVA_VERSION \
--packaging=jar \
--name=recommendation-service \
--package-name=nl.svh.microservices.core.recommendation \
--groupId=nl.svh.microservices.core.recommendation \
--dependencies=actuator,webflux \
--version=1.0.0-SNAPSHOT \
recommendation-service

echo 'recommendation-service created'

spring init \
--boot-version=$BOOT_VERSION \
--build=gradle \
--java-version=$JAVA_VERSION \
--packaging=jar \
--name=product-composite-service \
--package-name=nl.svh.microservices.composite.product \
--groupId=nl.svh.microservices.composite.product \
--dependencies=actuator,webflux \
--version=1.0.0-SNAPSHOT \
product-composite-service

echo 'product-composite-service created'

cd ..

spring init \
--boot-version=$BOOT_VERSION \
--build=gradle \
--java-version=$JAVA_VERSION \
--packaging=jar \
--name=api \
--package-name=nl.svh.microservices.api \
--groupId=nl.svh.microservices.api \
--dependencies=webflux \
--version=1.0.0-SNAPSHOT \
api
echo 'api created'

spring init \
--boot-version=$BOOT_VERSION \
--build=gradle \
--java-version=$JAVA_VERSION \
--packaging=jar \
--name=util \
--package-name=nl.svh.microservices.util \
--groupId=nl.svh.microservices.util \
--dependencies=webflux \
--version=1.0.0-SNAPSHOT \
util
echo 'util created'

