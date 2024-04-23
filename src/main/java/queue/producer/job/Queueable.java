package queue.producer.job;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import queue.common.model.Job;
import queue.common.model.JobOptions;
import queue.common.repository.JobRepository;

@Slf4j
@Component
abstract public class Queueable {
  public Queueable(){
    this.queueOn();
  }

  @Autowired
  JobRepository jobRepository;

  String queue;

  public void queueOn(){
    this.queue="default";
  }
  private JobOptions parseJobOptions(String options) throws JsonProcessingException {
    ObjectMapper objectMapper= new ObjectMapper();
    return objectMapper.readValue(options, JobOptions.class);
  }

// only string arguments supported
  public void dispatch(String[] args, String options) throws JsonProcessingException {
    JobOptions jobOptions= parseJobOptions(options);
    Job job= new Job(this.getClass().getName(),args);
    if(jobOptions.getQueueOn()!=null){
      this.queue= jobOptions.getQueueOn();
    }
    job.setQueue(this.queue);
    job.setUp(jobOptions);
  jobRepository.save(job);
  log.info("Enqueued job id="+ job.getId() +" args="+ job.getArgs());
  }



  public void dispatchMany(List<String[]> args, String options) throws JsonProcessingException {
    List<Job> jobs= new ArrayList<>();
    for(String[] cur: args){
      Job curJob=new Job(this.getClass().getName(),cur);
      curJob.setUp(options);
      jobs.add(curJob);
    }
    jobRepository.saveAll(jobs);
    log.info("Enqueued "+ args.size() +" jobs");
  }

  abstract public void handle(String[] args) throws Exception;

}
