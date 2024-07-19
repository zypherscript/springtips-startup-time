Run service:

- docker-compose -f docker-compose.yaml up -d
- ./mvnw spring-boot:run

JRE:

- ./mvnw -DskipTests clean package
- java -jar target/service-0.0.1-SNAPSHOT.jar

AOT on the JRE:

- java -Dspring.aot.enabled=true -jar target/service-0.0.1-SNAPSHOT.jar

AppCDS

- ./mvnw -DskipTests clean package
- java -Djarmode=tools -jar target/service-0.0.1-SNAPSHOT.jar extract
- java -Dspring.aot.enabled=true -Dspring.context.exit=onRefresh \                 
  -XX:ArchiveClassesAtExit=service-0.0.1-SNAPSHOT/application.jsa \
  -jar service-0.0.1-SNAPSHOT/service-0.0.1-SNAPSHOT.jar
- java -Dspring.aot.enabled=true -XX:SharedArchiveFile=service-0.0.1-SNAPSHOT/application.jsa \
  -jar service-0.0.1-SNAPSHOT/service-0.0.1-SNAPSHOT.jar

GraalVM

- rm -rf target
- (https://www.graalvm.org/22.0/docs/getting-started/macos/)
- ./mvnw -DskipTests native:compile
- target ./service