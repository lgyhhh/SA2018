package com.nju.mdfs.namenode.datanode;

import javax.persistence.Id;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

/**
 * DataNode
 */
@Entity
public class DataNode {


    @Id
    public String url;

    public int numOfBlock;

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public boolean valid = true;

    public DataNode(){

    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getNumOfBlock() {
        return numOfBlock;
    }

    public void setNumOfBlock(int numOfBlock) {
        this.numOfBlock = numOfBlock;
    }



}
