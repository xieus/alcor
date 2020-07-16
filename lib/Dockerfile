# Dockerfile for Port Manager

FROM apacheignite/ignite:2.8.1

MAINTAINER Liguang Xie <lxie@futurewei.com>

EXPOSE 10800

# Generate container image and run container
COPY ./target/common-0.1.0-SNAPSHOT.jar /opt/ignite/apache-ignite/libs/common-0.1.0-SNAPSHOT.jar

CMD /opt/ignite/apache-ignite/bin/ignite.sh

