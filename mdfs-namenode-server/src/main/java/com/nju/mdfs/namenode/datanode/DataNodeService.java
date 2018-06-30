package com.nju.mdfs.namenode.datanode;

import com.nju.mdfs.namenode.block.Block;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class DataNodeService {
    @Autowired
    private DataNodeRepository dataNodeRepository;

    /**
     * 增——添加一个DataNode
     */
    public void addDataNode(String url){
        if(dataNodeRepository.findByUrl(url)==null) {
            DataNode dataNode = new DataNode();
            dataNode.setUrl(url);
            dataNode.setNumOfBlock(0);
            dataNodeRepository.save(dataNode);
            System.out.println("注册成功\n\n\n\n\n");
        }
        else
            System.out.println("已注册\n\n\n\n");
    }
    /**
     * 删——删除一个DataNode
     * 删除DataNode这种操作很危险，没有想好处理方案，这里不进行实现
     */

    /**
     * 查——根据url查找DataNode
     */
    public DataNode getDataNodeByURL(String url){
        return dataNodeRepository.findByUrl(url);
    }
    /**
     * 查——获取所有DataNode
     */
    public List<DataNode> getAllDataNode(){
        return dataNodeRepository.findAllByValid(true);
    }
    /**
     * 改-更改DataNode的Block信息
     */
    public void updataDataNode(String url,int numBlock){
        DataNode dataNode = dataNodeRepository.findByUrl(url);
        dataNode.setNumOfBlock(numBlock);
        dataNodeRepository.save(dataNode);
    }
}
