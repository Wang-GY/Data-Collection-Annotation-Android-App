package com.weiju.springboot.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import javax.persistence.*;
import java.util.List;

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

    public void setCommitid(int id){
        commitid = id;
    }

    public String getCommitTime() {
        return commitTime;
    }
    public void setCommitTime(String time){
        this.commitTime = time;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public User getCommitter() {
        return committer;
    }

    public void setCommitter(User committer) {
        this.committer = committer;
    }

    @Column(name = "commit_time")
    private String commitTime;


    @ManyToOne(cascade = CascadeType.REFRESH, targetEntity = Task.class)
    @JoinColumn(name = "task", referencedColumnName = "taskid")
    private Task task;

    @Column(name = "size")
    private int size;

    @ManyToOne(cascade = CascadeType.REFRESH, targetEntity = User.class)
    @JoinColumn(name = "committer", referencedColumnName = "userid")
    private User committer;

    @OneToMany(mappedBy = "commitId", cascade = CascadeType.ALL, fetch = FetchType.LAZY, targetEntity = CommitData.class)
    private List<CommitData> commitDataList;
}
