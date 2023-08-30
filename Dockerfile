FROM openjdk:17-jdk

# 시간대 설정 명령어 추가
RUN ln -snf /usr/share/zoneinfo/Asia/Seoul /etc/localtime && echo Asia/Seoul > /etc/timezone

WORKDIR /app
EXPOSE 8080
COPY build/libs/parasol-0.0.1-SNAPSHOT.jar /app/app.jar
CMD ["java", "-jar", "app.jar"]