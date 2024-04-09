--run producer


mvn spring-boot:run \
-Dspring.profiles.active=producer \
-Pproducer


--run consumer

mvn spring-boot:run \
-Dspring.profiles.active=consumer \
-Pconsumer

--enqueue jobs
http://localhost:9090/queue/2