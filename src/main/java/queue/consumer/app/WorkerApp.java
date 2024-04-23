package queue.consumer.app;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
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
import queue.consumer.models.WorkerArgs;
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
  public void run(String[] args) throws InterruptedException, JsonProcessingException {
    WorkerArgs workerArgs= processArgs(args);
     log.info("RUN method");
     worker.run(workerArgs);
     log.info("AFTER RUN method");
  }
  // "{'queues':[['priority',3],['low',1]]}"

  public WorkerArgs processArgs(String[] args) throws JsonProcessingException {

    WorkerArgs resp= new WorkerArgs();

    if(args.length>0) {
      String str=args[0];
      HashMap<String, Integer> map = (HashMap<String, Integer>) Arrays.asList(str.split(",")).stream().map(s -> s.split(":")).collect(
          Collectors.toMap(e -> e[0], e -> Integer.parseInt(e[1])));
      resp.setQueues(map);
    }
    return resp;
  }

  public static void main(String[] args) {
    log.info("main method");
    new SpringApplicationBuilder(WorkerApp.class)
        .web(WebApplicationType.NONE)
        .run(args);
  }
}

