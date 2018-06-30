package com.nju.mdfs.namenode.block;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import static javax.persistence.GenerationType.AUTO;

@Entity
public class Block {

    @Id
    private String id;

    private String dataNodeURL;
    private String filename;

    private String nodename;
    private String location;
    private int size;

    public String getDataNodeURL() {
        return dataNodeURL;
    }

    public void setDataNodeURL(String dataNodeURL) {
        this.dataNodeURL = dataNodeURL;
    }

    public String getFilename() {
        return filename;
    }

    public String getNodename() {
        return nodename;
    }

    public void setNodename(String nodename) {
        this.nodename = nodename;
    }
    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
