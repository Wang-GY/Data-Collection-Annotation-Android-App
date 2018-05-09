package com.weiju.springboot.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import javax.persistence.*;

@Entity
@Table(name = "commits")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Commit {
    @Id
    @Column(name = "commitid")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int commitid;

    public int getCommitid() {
        return commitid;
    }


    public int getCommitTime() {
        return commitTime;
    }


    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getCommitterId() {
        return committerId;
    }

    public void setCommitterId(int committerId) {
        this.committerId = committerId;
    }

    @Column(name = "commit_time")
    private int commitTime;


    @ManyToOne(cascade = CascadeType.REFRESH, targetEntity = Task.class)
    @JoinColumn(name = "task", referencedColumnName = "taskid")
    private int taskId;

    @Column(name = "size")
    private int size;

    @ManyToOne(cascade = CascadeType.REFRESH, targetEntity = User.class)
    @JoinColumn(name = "commiter", referencedColumnName = "userid")
    private int committerId;

}
