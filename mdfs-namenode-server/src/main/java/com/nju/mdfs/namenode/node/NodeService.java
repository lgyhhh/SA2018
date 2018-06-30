package com.nju.mdfs.namenode.node;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class NodeService {
    @Autowired
    private NodeRepository nodeRepository;

    /**
     * 增
     * 直接增加一个Node
     * @param node
     */
    public void addNode(Node node){

        nodeRepository.save(node);
    }
    /**
     * 增
     * 根据输入的名字循环增加整条路径的Node
     * @param path
     * @param isDirectory
     * @param byteSize
     */
    public void addNode(String path,Boolean isDirectory,int byteSize){
        String restPath = path.substring(1);
        String forwardPath = "";
        int index = 0;
        while((index = restPath.indexOf('/'))>=0){
            String dir = restPath.substring(0,index);
            forwardPath +=("/"+dir);
            if(getNodeByName(forwardPath)==null)
                addNode(new Node(forwardPath,true));
            restPath = restPath.substring(index+1);
        }
        Node node = new Node(path,isDirectory);
        node.setByteSize(byteSize);
        addNode(node);
    }

    /**
     * 删
     * 删之前务必确保DataNode已经删除了对应的文件
     * TODO 如果删除的节点是目录，需要判断是否为空或循环删除目录下所有文件，这里只删除文件
     * @param name
     * @return 状态
     */
    public String deleteByName(String name){
        Node node = nodeRepository.findByName(name);
        if(node==null)
            return "No Such Node";
        if(!node.getIsDirectory()) {//不是目录
            nodeRepository.deleteByName(name);
            return "SUCCESS";
        }
        else
        {
            return "Can't Delete the Directory";
        }
    }

    /**
     * 查
     * 根据名字获取Node
     * @param name
     * @return
     */
    public Node getNodeByName(String name){

        return nodeRepository.findByName(name);
    }

    /**
     * 查
     * 根据Location获取当前目录下的所有内容。
     * @param location
     * @return
     */
    public List<Node> getNodeByLocation(String location){
        return nodeRepository.findByLocation(location);
    }

    /**
     * 改
     * 目前未使用
     * TODO 根据要求进行修改后期重命名之类可以用
     */
    public void updateNode(Node node){
        return;
    }
}
