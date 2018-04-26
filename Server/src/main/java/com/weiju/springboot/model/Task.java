package com.weiju.springboot.model;

import javax.persistence.*;

@Entity
@Table(name = "tasks")
public class Task {

    @Id
    @Column(name = "taskid")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int taskid;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "start_time")
    private String start_time; //WHY don't use DATETIME OF MYSQL

    @Column(name = "type")
    private int type;

    @Column(name = "size")
    private int size;

    @Column(name = "data_path")
    private String data_path;

    @ManyToOne(cascade = CascadeType.REFRESH, targetEntity = User.class)
    @JoinColumn(name = "creator", referencedColumnName = "userid")
    private int creator;

    @Column(name = "progress")
    private String progress;

    @Column(name = "formater")
    private String formater;

    @Column(name = "deadline")
    private String deadline;

    public void setCreator(int creator) {
        this.creator = creator;
    }

    public int getCreator() {
        return creator;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getTaskid() {
        return taskid;
    }

    public int getType() {
        return type;
    }

    public void setData_path(String data_path) {
        this.data_path = data_path;
    }

    public String getData_path() {
        return data_path;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTaskid(int taskid) {
        this.taskid = taskid;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setFormater(String formater) {
        this.formater = formater;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getFormater() {
        return formater;
    }

    public String getProgress() {
        return progress;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setProgress(String progress) {
        this.progress = progress;
    }

    public int getSize() {
        return size;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public String getDeadline() {
        return deadline;
    }

}
