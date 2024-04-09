package queue.common.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.gson.Gson;
import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.*;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Setter
@Entity
@Table(name = "jobs")
@JsonIgnoreProperties(value = { "created_at", "updated_at" }, allowGetters = true)
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
@AllArgsConstructor
public class Job{

  @GeneratedValue(generator = "uuid")
  @GenericGenerator(name = "uuid", strategy = "uuid")
  @Column(columnDefinition = "CHAR(32)")
  @Id
  private String id;


  @Column(name = "name")
  private String name;

  @Column(name = "args")
  private String args;

  @Column(name = "queue")
  private String queue;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name= "created_at", nullable = false, updatable = false)
  @CreatedDate
  private Date createdAt;

  @Column(name="updated_at", nullable = false)
  @Temporal(TemporalType.TIMESTAMP)
  @LastModifiedDate
  private Date updateAt;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "next_executed_at")
  private Date nextExecutedAt= new Date();

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "last_executed_at")
  private Date lastExecutedAt;

  @Column(name = "attempts")
  private int attempts;

  @Column(name = "state", columnDefinition ="varchar(255) default 'waiting'")
  private String state;

  @Column(name = "error_details")
  private String errorDetails;

  @Column(name = "reserved_by")
  private String reservedBy;

  public Job(String name,String[] args){
    this.name=name;
    Gson gson = new Gson();
    this.args=gson.toJson(args);
    this.state="waiting";
  }


}