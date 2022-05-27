#!/bin/sh
./mvnw -Pprod clean verify -DskipTests=true
scp -i "rsa.pem" ./target/pathogene-0.0.1-SNAPSHOT.jar  ubuntu@ec2-23-23-30-165.compute-1.amazonaws.com:~/oracle
ssh -i "rsa.pem" ubuntu@ec2-23-23-30-165.compute-1.amazonaws.com
nohup java -jar ./oracle/pathogene-0.0.1-SNAPSHOT.jar &
exit
