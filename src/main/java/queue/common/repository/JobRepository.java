package queue.common.repository;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import queue.common.model.Job;

@Repository
public interface JobRepository extends CrudRepository<Job, UUID> {

  List<Job> findByStateAndReservedByAndNextExecutedAtLessThanEqual(String state, String reservedBy, Date nextExecutedAt);

   @Transactional
  @Modifying
  @Query("UPDATE Job j SET j.reservedBy = :workerId WHERE j.id = :jobId AND j.reservedBy IS NULL")
   int updateReservedByForJob(String jobId, String workerId);
}