FROM openjdk:11.0-jre-buster
WORKDIR /
RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && dpkg-reconfigure -f noninteractive tzdata
ENV FORMAT_MESSAGES_PATTERN_DISABLE_LOOKUPS true
COPY build/libs/*.jar /app.jar
CMD ["java", "-Dlog4j2.formatMsgNoLookups=true", "-jar", "app.jar"]