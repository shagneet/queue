package queue.producer.job;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import queue.common.model.Job;
import queue.common.repository.JobRepository;

@Slf4j
@Component
abstract public class Queueable {

  @Autowired
  JobRepository jobRepository;

// only string arguments supported
  public void dispatch(String[] args, String options){
    Job job= new Job(this.getClass().getName(),args);
  jobRepository.save(job);
  log.info("Enqueued job id="+ job.getId() +" args="+ job.getArgs());
  }

  abstract public void handle(String[] args) throws Exception;

}
