FROM eclipse-temurin:21-jdk-jammy

# 빌드 시 생성된 JAR 파일을 복사 (settings.gradle의 rootProject.name + version)
# plain jar가 아닌 실행 가능한 jar만 복사하기 위해 파일명을 지정하거나 와일드카드를 신중하게 사용합니다.
ARG JAR_FILE=build/libs/demo-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar

ENTRYPOINT ["java", "-jar", "/app.jar"]