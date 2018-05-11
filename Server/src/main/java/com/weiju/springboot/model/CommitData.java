package com.weiju.springboot.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import javax.persistence.*;

@Entity
@Table(name = "commit_data")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommitData {

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Commit getCommitId() {
        return commitId;
    }

    public void setCommitId(Commit commitId) {
        this.commitId = commitId;
    }

    public String getItemPath() {
        return itemPath;
    }

    public void setItemPath(String itemPath) {
        this.itemPath = itemPath;
    }

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(cascade = CascadeType.REFRESH, targetEntity = Commit.class)
    @JoinColumn(name = "commitid", referencedColumnName = "commitid")
    private Commit commitId;

    @Column(name = "item_path")
    private String itemPath;
}
