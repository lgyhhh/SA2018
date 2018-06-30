package com.nju.mdfs.namenode.node;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.List;

@Entity
public class Node {
    @Id
    @GeneratedValue
    private int id;
    /**
     * 节点名字
     */
    private String name;
    /**
     * 是否为目录
     */
    private Boolean isDirectory;
    /**
     * 文件类型
     */
    private String filetype;
    /**
     * 文件名字
     */
    private String filename;
    /**
     * 存储位置
     */
    private String location;

    /**
     * 文件大小
     */
    private int byteSize;


    public int getByteSize() {
        return byteSize;
    }

    public void setByteSize(int byteSize) {
        this.byteSize = byteSize;
    }
    public Node(){

    }
    public Node(String name,Boolean isDirectory){
        this.name = name;
        this.isDirectory = isDirectory;
        if(!isDirectory){
            int indexf = name.lastIndexOf('/');
            if(indexf==0){
                location = "/root";
            }
            else
                location = name.substring(0,indexf);
            int indexd = name.indexOf('.');
            if(indexd>0) {
                filename = name.substring(indexf + 1, indexd);
                filetype = name.substring(indexd + 1);
            }
            else
                filename = name.substring(indexf+1);
        }
        else{
            int indexf = name.lastIndexOf('/');
            if(indexf==0)
                location = "/root";
            else
                location = name.substring(0,indexf);
        }
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getIsDirectory() {
        return isDirectory;
    }

    public void setIsDirectory(Boolean isDirectory) {
        this.isDirectory = isDirectory;
    }

    public String getFiletype() {
        return filetype;
    }

    public void setFiletype(String filetype) {
        this.filetype = filetype;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
