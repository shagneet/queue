package queue.producer.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import queue.common.model.JobOptions;
import queue.common.repository.JobRepository;
import queue.producer.job.CriticalJob;
import queue.producer.job.DoSomeStuff;
import queue.producer.job.RegularJob;

@RestController
@RequestMapping("/queue")
@Slf4j
public class EnqueueController {
  @Autowired
  JobRepository jobRepository;

  @Autowired
  DoSomeStuff doSomeStuff;

  @Autowired
  CriticalJob criticalJob;

  @Autowired
  RegularJob regularJob;

  @GetMapping("/{count}")
  public void enqueueJob(@PathVariable(value = "count") Long count) throws Exception {
//    List<String[]> args=LongStream.range(0, count)
//            .mapToObj(i -> new String[]{"Nellie"+i , "Buster"+i})
//                .collect(Collectors.toList());
//    JobOptions options= new JobOptions();
//    options.setWait(count%15);
//    ObjectMapper objectMapper= new ObjectMapper();
//    String jsonString=objectMapper.writeValueAsString(options);
//          doSomeStuff.dispatchMany(args, jsonString);
    IntStream.range(0, 3).forEach(i -> {
      try {
        regularJob.dispatch(new String[]{"arg"}, null);
      } catch (JsonProcessingException e) {
        throw new RuntimeException(e);
      }
    });
    criticalJob.dispatch(null, null);
    List<String[]> args=LongStream.range(0, 4)
            .mapToObj(i -> new String[]{"args"})
                .collect(Collectors.toList());
    JobOptions options= new JobOptions();
    options.setQueueOn("critical");
        ObjectMapper objectMapper= new ObjectMapper();
    String jsonString=objectMapper.writeValueAsString(options);
    regularJob.dispatchMany(args,jsonString );


   // log.info("Enqueued "+count+" jobs");

  }

}
