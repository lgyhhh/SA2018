package com.nju.mdfs.datanode.block;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
public class BlockController {
    @Autowired
    private BlockService blockService;

    @PostMapping("/upload")
    public String reciveFile(@RequestParam("block") MultipartFile file, @RequestParam("blockid") String blockid){

        try {
            System.out.println("Recevie the block blockid = " + blockid + "bytes.lenth = " + file.getBytes().length);
            return blockService.storeFile(file.getBytes(), blockid);
        }catch (IOException e){
            e.printStackTrace();
            return "FAILURE";
        }
    }

    @PostMapping("/download")
    public ByteArrayResource sentFile(@RequestParam("blockid") String blockid){
        System.out.println("DownLoading blockid = " + blockid );

        byte[] bytes = blockService.readFile(blockid);
        ByteArrayResource resource = new ByteArrayResource(bytes){
            @Override
            public String getFilename(){
                return blockid;
            }
        };
        System.out.println("Size = "+bytes.length);
        return resource;
    }

    @PostMapping("/delete")
    public String deleteFile(@RequestParam("blockid")String blockid){
        System.out.println("Delete blockid = " + blockid);
        try {

            return blockService.deleteBlock(blockid);
        }catch (Exception e){
            e.printStackTrace();
            return "IOException";
        }
    }
}
