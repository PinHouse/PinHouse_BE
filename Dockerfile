### JDK 이미지를 베이스로 설정
FROM eclipse-temurin:21-jdk

### 작업 디렉토리 설정
WORKDIR /app

#### jar 파일 복사
ARG JAR_FILE=build/libs/server-0.0.1-SNAPSHOT.jar

### 해당 파일을 COPY하기
COPY ${JAR_FILE} app.jar

### 필수 실행은 ENTRYPOINT로 하기
ENTRYPOINT ["java", "-jar", "app.jar"]
