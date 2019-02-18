FROM openjdk:8

ADD target/IMAPCloud-*-server.tar.gz /
WORKDIR /IMAPCloud-server/

RUN echo "spring.datasource.url: jdbc:mysql://db/ic" >> application.yml

EXPOSE 9000/tcp

CMD java -jar IMAPCloud.jar
