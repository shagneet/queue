package queue.consumer.app;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Component;
import queue.consumer.job.Worker;

@Slf4j
@Profile("consumer")
@SpringBootApplication(scanBasePackages= {"queue.common","queue.consumer"})
@EnableJpaRepositories({ "queue.common","queue.consumer"})
@EntityScan({ "queue.common","queue.consumer"})
@Component
public class WorkerApp implements  CommandLineRunner{

  @Autowired
  Worker worker;

  @Override
  public void run(String[] args) throws InterruptedException {
     log.info("RUN method");
     worker.run();
     log.info("AFTER RUN method");
  }

  public static void main(String[] args) {
    log.info("main method");
    new SpringApplicationBuilder(WorkerApp.class)
        .web(WebApplicationType.NONE)
        .run(args);
  }
}

