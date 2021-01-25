FROM openjdk:11.0-jre-buster
WORKDIR /
RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && dpkg-reconfigure -f noninteractive tzdata
COPY build/libs/*.jar /app.jar
COPY application.yml /application.yml
CMD ["java", "-jar", "app.jar"]