package com.nju.mdfs.namenode.namenode;

import com.nju.mdfs.namenode.block.Block;
import com.nju.mdfs.namenode.block.BlockService;
import com.nju.mdfs.namenode.datanode.DataNode;
import com.nju.mdfs.namenode.datanode.DataNodeService;
import com.nju.mdfs.namenode.node.Node;
import com.nju.mdfs.namenode.node.NodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class NameNodeService {

    @Value(value = "${block.size}")
    public int BLOCK_SIZE;

    @Value(value = "${block.replicas}")
    public int BLOCK_REPLICAS;

    /**
     * 文件目录系统
     */
    @Autowired
    private NodeService nodeService;

    @Autowired
    private BlockService blockService;

    @Autowired
    private DataNodeService dataNodeService;
    /**
     * 上传文件服务
     * @param file 待上传的文件
     * @param nodename  文件在文件系统中的绝对路径
     * @throws Exception 异常状态
     */
    public String uploadFile(MultipartFile file,String nodename)
            throws Exception
    {
        if(nodeService.getNodeByName(nodename)!=null)
            return nodename +" exsits";

        byte[] bytes = file.getBytes();
        int byteSize = bytes.length;


        System.out.println("byteSize = "+byteSize);


        int numOfBlock = byteSize/BLOCK_SIZE;
        numOfBlock = byteSize%BLOCK_SIZE==0?numOfBlock:numOfBlock+1;

        nodeService.addNode(nodename,false,byteSize);
        for(int i = 0;i<numOfBlock;i++){
            byte[] paramBlock;
            if(i<numOfBlock-1)
                paramBlock = new byte[BLOCK_SIZE];
            else {
                if(byteSize%BLOCK_SIZE!=0)
                    paramBlock = new byte[byteSize % BLOCK_SIZE];
                else
                    paramBlock = new byte[BLOCK_SIZE];
            }
            int t =0;
            for(int j=i*BLOCK_SIZE;j<(i+1)*BLOCK_SIZE&&j<byteSize;j++,t++){
                paramBlock[t] =bytes[j];
            }
            List<DataNode> dataNodeList = dataNodeService.getAllDataNode();
            for(DataNode dataNode : dataNodeList) {
                Block block = new Block();
                String uuid = UUID.randomUUID().toString();
                block.setId(uuid);
                block.setDataNodeURL(dataNode.getUrl());
                block.setFilename(file.getOriginalFilename());
                block.setLocation(nodeService.getNodeByName(nodename).getLocation());


                System.out.println("t = "+t);

                block.setSize(t);
                block.setNodename(nodename);
                blockService.addBlock(block);
                blockService.uploadBlockToDataNode(block.getId(), dataNode.getUrl(), paramBlock);
            }

        }
        return "SUCCESS";
    }

    /**
     * 下载文件并拼接
     * @param nodename
     * @return
     */
    public byte[] downloadFile(String nodename) throws Exception{
        if(nodeService.getNodeByName(nodename)==null)
            return null;
        List<DataNode> dataNodeList = dataNodeService.getAllDataNode();
        byte[] response = new byte[nodeService.getNodeByName(nodename).getByteSize()];
        int flag = 0;
        for(DataNode dataNode :dataNodeList) {
            System.out.println("dataNodeListURL = " +dataNode.getUrl());
            List<Block> blockList = blockService.getBlockByNodeNameAndDataNodeURL(nodename,dataNode.getUrl());
            for(Block block:blockList){
                byte[] ret =  blockService.downloadBlockFromDataNode(block.getId(), dataNode.getUrl());
                System.arraycopy(ret,0,response,flag,ret.length);
                flag+=ret.length;
            }
            if(blockList!=null) {
                return response;
            }
            else
                System.out.println("BlockList = NULL");
        }
        return null;
    }

    /**
     * 删除一个文件的所有Block
     * @param nodename
     * @return 状态
     */
    public String deleteBlock(String nodename){
        if(nodeService.getNodeByName(nodename) == null){
            return "No Such File or Directory";
        }
        //先删除DataNode上的数据
        List<DataNode> dataNodeList = dataNodeService.getAllDataNode();
        for(DataNode dataNode :dataNodeList){
            List<Block> blockList = blockService.getBlockByNodeNameAndDataNodeURL(nodename,dataNode.getUrl());
            for(Block block:blockList){
                String res = blockService.delectBlockInDataNode(block.getId(),block.getDataNodeURL());
                System.out.println("从DataNode 删除 BlockId = "+block.getId()+",URL = "+block.getDataNodeURL()+"状态="+res);
            }
        }

        //再删除NameNode存储的Block信息
        blockService.deleteBlock(nodename);

        return "SUCCESS";
    }

    /**
     * 删除目录上对应的文件节点
     * @param nodename
     * @return 状态
     */
    public String deleteNode(String nodename){
       return nodeService.deleteByName(nodename);
    }


    /**
     * 注册DataNode
     * @param url
     */
    public void addDataNode(String url){
        dataNodeService.addDataNode(url);
    }

    /**
     * 判断文件是否是目录类型
     * @param namenode
     * @return 如果是目录，则返回true
     */

    public Boolean isDirectory(String namenode){
        if(nodeService.getNodeByName(namenode)==null)
            return true;
        return nodeService.getNodeByName(namenode).getIsDirectory();
    }

    public String getDirectoryTree(String root,String tabstr){
        List<Node> nodes = nodeService.getNodeByLocation(root);
        String ret="";
        for(Node node:nodes){
            String nodename = node.getName();
            nodename = nodename.substring(nodename.lastIndexOf('/'));
            ret = ret += tabstr+ nodename +"\n" + getDirectoryTree(node.getName(),tabstr+"   ");
        }
        return ret;
    }
}
