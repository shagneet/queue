--run producer


mvn spring-boot:run \
-Dspring.profiles.active=producer \
-Pproducer


--run consumer

mvn spring-boot:run \
-Dspring.profiles.active=consumer \
-Pconsumer

mvn spring-boot:run \
-Dspring.profiles.active=consumer \
-Pconsumer \
-Dspring-boot.run.arguments='critical:3,default:1'

--enqueue jobs
http://localhost:9090/queue/2

