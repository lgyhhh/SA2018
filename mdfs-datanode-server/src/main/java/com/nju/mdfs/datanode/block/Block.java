package com.nju.mdfs.datanode.block;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Block {


    @Id
    private String id;


    private String blockid;

    private int size;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
    public String getBlockid() {
        return blockid;
    }

    public void setBlockid(String blockid) {
        this.blockid = blockid;
    }
}
