package queue.consumer.job;
import com.google.gson.Gson;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import javax.persistence.criteria.CriteriaBuilder.In;
import javax.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import queue.common.model.Job;
import queue.common.repository.JobRepository;
import queue.consumer.models.WorkerArgs;
import queue.producer.job.Queueable;

@Slf4j
@Component
public class Worker {

  private int pollingInterval;
  private String workerId;

  private String queueFilter;

  private List<Pair<String, Integer>> queues;


  @Autowired
  JobRepository jobRepository;

  public Worker(){
  this.pollingInterval=5;
  this.workerId="wrk_"+RandomStringUtils.random(6, true, false);
  }


  public void run(WorkerArgs workerArgs) throws InterruptedException {
    preprocessArgs(workerArgs);
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
  private void preprocessArgs(WorkerArgs workerArgs){
    Map<String, Integer> map=workerArgs.getQueues();
    this.queues=map.entrySet()
        .stream()
        .map( e-> Pair.of(e.getKey(), e.getValue()) )
        .collect(Collectors.toList());
    log.info("workerargs getQueue():"+ queues);
    if(queues.size()==1){
      queueFilter= queues.get(0).getLeft();
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
    this.queueFilter= this.queues.size()>0 ? getQueueFilter() : null;
    if(this.queueFilter == null){
      log.info("Checking all queues");
    }else{
      log.info("Checking queues:"+ queueFilter);
    }

    List<Job> jobs=findJobs("waiting", null, new Date(), queueFilter);
    return jobs.stream().findFirst();
  }

  public List<Job> findJobs(String state, String reservedBy, Date nextExecutionAt, String queue) {
    return jobRepository.findAll((root, query, criteriaBuilder) -> {
      List<Predicate> predicates = new ArrayList<>();
      predicates.add(criteriaBuilder.equal(root.get("state"), state));
      predicates.add(criteriaBuilder.isNull(root.get("reservedBy")));
      if (nextExecutionAt != null) {
        predicates.add(criteriaBuilder.or(
            criteriaBuilder.isNull(root.get("nextExecutedAt")),
            criteriaBuilder.lessThanOrEqualTo(root.get("nextExecutedAt"), nextExecutionAt)
        ));
      }
      if (queue != null) {
        predicates.add(criteriaBuilder.equal(root.get("queue"), queue));
      }
      return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    });
  }

  private String getQueueFilter(){
    Random random = new Random();
    Pair<String , Integer> maxQueue = queues.stream()
        .max((entry1, entry2) -> {
          double priority1 = new Double(entry1.getRight());
          double priority2 = new Double(entry2.getRight());
          double randomValue1 = Math.pow(random.nextDouble(), 1.0 / priority1);
          double randomValue2 = Math.pow(random.nextDouble(), 1.0 / priority2);
          return Double.compare(randomValue1, randomValue2);
        }).get();
    log.info("Queue:"+maxQueue.getLeft()+", Priority:"+maxQueue.getRight());
    String queueToCheck = maxQueue.getLeft();
    return queueToCheck;
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
