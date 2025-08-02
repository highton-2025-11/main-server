# 애플리케이션을 실행하기 위한 기본 이미지로 JRE-slim을 사용합니다.
# JRE 이미지는 JDK보다 용량이 가벼워 최종 이미지 크기를 줄여줍니다.

FROM openjdk:17-jdk-slim

# 컨테이너 내 작업 디렉토리를 /app으로 설정합니다.
WORKDIR /app

# 현재 디렉토리의 app.jar 파일을 컨테이너의 /app 디렉토리로 복사합니다.
# 이 Dockerfile을 사용하기 전에, 반드시 프로젝트를 빌드하여 app.jar을 만들어야 합니다.
COPY app.jar /app/app.jar

# 애플리케이션이 외부에서 접근할 포트를 지정합니다.
EXPOSE 9000

# 컨테이너가 시작될 때 Ktor 애플리케이션을 실행하는 명령입니다.
CMD ["java", "-jar", "app.jar"]