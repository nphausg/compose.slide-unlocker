#!/bin/bash
cd ..
find . -name .gradle -exec rm -rf {} \;
find . -name build -exec rm -rf {} \;
find . -name '.DS_Store' -type f -exec rm -rf {} \;
./gradlew clean
./gradlew --refresh-dependencies