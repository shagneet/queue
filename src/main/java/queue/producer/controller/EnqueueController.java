package queue.producer.controller;

import java.util.stream.IntStream;
import java.util.stream.LongStream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import queue.common.repository.JobRepository;
import queue.producer.job.DoSomeStuff;

@RestController
@RequestMapping("/queue")
@Slf4j
public class EnqueueController {
  @Autowired
  JobRepository jobRepository;

  @Autowired
  DoSomeStuff doSomeStuff;

  @GetMapping("/{count}")
  public void enqueueJob(@PathVariable(value = "count") Long count) {
    String[] args= new String[]{"Nellie", "Buster"};
    LongStream.range(0, count)
        .forEach(i -> {
          doSomeStuff.dispatch(args, null);
              });

    log.info("Enqueued "+count+" jobs");

  }

}
