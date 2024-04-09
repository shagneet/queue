package queue.consumer.job;
import com.google.gson.Gson;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.tomcat.jni.Time;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import queue.common.model.Job;
import queue.common.repository.JobRepository;
import queue.producer.job.Queueable;

@Slf4j
@Component
public class Worker {

  private int pollingInterval;
  private String workerId;

  @Autowired
  JobRepository jobRepository;

  public Worker(){
  this.pollingInterval=5;
  this.workerId="wrk_"+RandomStringUtils.random(6, true, false);
  }

  public void run() throws InterruptedException {
    log.info("Worker "+this.workerId+" is ready");
    while(true){
      Optional<Job> job=nextJob();
      if(job.isPresent()){
        String error=null;
        Job currentJob=job.get();
        try {
          executeJob(currentJob);
        }catch(Exception e){
          error=e.toString();
        }
        cleanup(currentJob, Optional.ofNullable(error));
      }else{
        Thread.sleep(this.pollingInterval *1000);
      }
    }
  }

  Optional<Job> nextJob(){
    Optional<Job> job= checkForJobs();
    if(!job.isPresent()){
      return job;
    }
   return  reserveJob(job.get());
  }
  Optional<Job> checkForJobs(){
    List<Job> jobs=jobRepository.findByStateAndReservedByAndNextExecutedAtLessThanEqual("waiting", null, new Date());
    return jobs.stream().findFirst();
  }
  Optional<Job> reserveJob(Job job){
    int updateRows=jobRepository.updateReservedByForJob(job.getId(), null);
    if(updateRows==1){
      return Optional.of(job);
    }
    return null;
  }

  void executeJob(Job job) throws Exception {
    try {
      // Get the class object by its name
      Class<?> clazz = Class.forName(job.getName());
      // Get the constructor of the class
      Constructor<?> constructor = clazz.getConstructor();
      // Instantiate the class using the constructor
      Queueable instance = (Queueable)constructor.newInstance();
      // Call the handle method (assuming it exists) on the instance
      // You'll need to define the handle method in your class
      Gson gson = new Gson();
      // Parse the JSON array into a string array
      String[] stringArray = gson.fromJson(job.getArgs(), String[].class);
      instance.handle( stringArray);
      log.info("Processed job id "+job.getId()+" result=succeeded args="+job.getArgs());
    } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException |
             InvocationTargetException e) {
      throw new Exception(e.toString());
    }
  }

  void cleanup(Job job, Optional<String> error){
    job.setReservedBy(null);
    job.setAttempts(job.getAttempts()+1);
    job.setLastExecutedAt(new Date());
    if(error.isPresent()){
      job.setState("failed");
      job.setErrorDetails(error.get());
    }else{
      job.setState("succeeded");
    }

    jobRepository.save(job);
  }


}
