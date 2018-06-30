package com.nju.mdfs.namenode.block;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.transaction.Transactional;

import java.util.List;

@Service
@Transactional
public class BlockService {

    @Autowired
    private BlockRepository blockRepository;


    /**
     * 上传文件到DataNode
     * @param blockid Block ID
     * @param url DataNode URL
     * @param bytes 数据
     * @return 返回状态
     */
    public String uploadBlockToDataNode(String blockid,String url,byte[] bytes){
        MultiValueMap<String,Object> param = new LinkedMultiValueMap<>();

        ByteArrayResource resource = new ByteArrayResource(bytes){
            @Override
            public String getFilename(){
                return blockid;
            }
        };
        param.add("block",resource);
        System.out.println("byte.length = "+bytes.length);
        param.add("blockid",blockid);
        RestTemplate rest = new RestTemplate();
        String response = rest.postForObject(url+"upload/",param,String.class);
        return response;
    }

    /**
     * 下载文件到本地
     * @param blockid Block ID
     * @param url DataNode URL
     * @return 返回的是block块的数据
     */
    public byte[] downloadBlockFromDataNode(String blockid, String url){
        MultiValueMap<String,Object> param = new LinkedMultiValueMap<>();
        param.add("blockid",blockid);

        RestTemplate rest = new RestTemplate();
        ByteArrayResource file = rest.postForObject(url+"download/",param, ByteArrayResource.class);

        return file.getByteArray();
    }

    /**
     * 删除DataNode中的对应Block
     * @param blockid
     * @param url
     * @return 返回删除的状态
     */
    public String delectBlockInDataNode(String blockid,String url){
        MultiValueMap<String,Object> param = new LinkedMultiValueMap<>();
        param.add("blockid",blockid);
        RestTemplate rest = new RestTemplate();
        String response = rest.postForObject(url+"delete/",param,String.class);

        return response;
    }


    /**
     * 增
     * 添加一个Block
     * @param block
     */
    public void addBlock(Block block){
        blockRepository.save(block);
    }

    /**
     * 查
     * 根据nodename，和URL查找对应的block(因为有副本的存在， 所以指定url)
     * @param nodename
     * @return
     */
    public List<Block> getBlockByNodeNameAndDataNodeURL(String nodename,String url){
        return blockRepository.findAllByNodenameAndDataNodeURL(nodename,url);
    }

    /**
     * 删
     * 根据nodename，删除所有对应的Block（包括副本）
     * @param nodename
     */
    public void deleteBlock(String nodename){
        blockRepository.deleteAllByNodename(nodename);
    }

}
