Run service:

- (docker-compose -f docker-compose.yaml up -d)
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

Project CRaC

- ./mvnw -DskipTests clean package
- docker build . -t crac
- docker run --privileged -it -e SPRING_DATASOURCE_HOSTNAME="host.docker.internal" --rm -p 8080:8080
  --name crac crac
- (ls -la /opt/crac-files/, ls -la /opt/app/)
- java -XX:CRaCCheckpointTo=/opt/crac-files -jar /opt/app/service-0.0.1-SNAPSHOT.jar
- docker exec -it -u root crac /bin/bash > jcmd $PID JDK.checkpoint
- vs (docker exec -it jcmd service JDK.checkpoint)
- java -XX:CRaCRestoreFrom=/opt/crac-files