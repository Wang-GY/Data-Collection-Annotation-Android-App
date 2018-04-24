package com.weiju.springboot.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import javax.persistence.*;

@Entity
@Table(name = "commit_data")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommitData {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @ManyToOne(cascade = CascadeType.REFRESH, targetEntity = Commit.class)
    @JoinColumn(name = "commitid", referencedColumnName = "commitid")
    private int commitId;

    @Column(name = "item_path")
    private String itemPath;
}
