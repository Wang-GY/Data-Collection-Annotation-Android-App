package com.weiju.springboot.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import javax.persistence.*;

@Entity
@Table(name = "commit_label")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TaskLabel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @ManyToOne(cascade = CascadeType.REFRESH, targetEntity = Task.class)
    @JoinColumn(name = "taskid", referencedColumnName = "taskid")
    private int taskId;

    @Column(name = "label")
    private String label;

}
