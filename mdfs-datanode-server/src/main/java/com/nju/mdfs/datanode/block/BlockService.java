package com.nju.mdfs.datanode.block;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

@Service
@Transactional
public class BlockService {

    @Autowired
    private BlockRepository blockRepository;

    @Value(value = "${spring.application.name}")
    private String datanodeURL;

    /**
     * 增
     * 添加一个block
     * @param size block的大小
     * @param blockid block的id
     * @return
     */
    public boolean addBlock(int size,String blockid){
        Block block = new Block();
            block.setId(blockid);
            block.setBlockid(blockid);
            block.setSize(size);
            blockRepository.save(block);
            return true;
    }

    /**
     * 查
     * 根据blockid获取block
     * @param blockid
     * @return
     */
    public Block getBlock(String blockid){
        return blockRepository.findByBlockid(blockid);
    }

    /**
     * 删
     * 根据blockid删除block
     * @param blockid
     * @return
     */
    public String deleteBlock(String blockid) throws Exception{
        if(blockRepository.findByBlockid(blockid)==null){
            return "no such file";
        }
        else
        {
            blockRepository.deleteByBlockid(blockid);
            File file = new File("./"+datanodeURL+"/"+blockid);
            if(file.exists()){
               file.delete();
               return "SUCCESS";
            }
            else{
                return "NO SUCH BLOCK IN FILE";
            }
        }
    }


    /**
     * 存储文件，blockid为文件名，存在{spring.application.name}/文件夹下
     * @param bytes
     * @param blockid
     * @return
     */
    public String storeFile(byte[] bytes, String blockid) {
        addBlock(bytes.length,blockid);
        File file = new File("./"+datanodeURL);
        try{
            if(!file.exists()||!file.isDirectory())
            {
                file.mkdir();
            }
            file = new File("./"+datanodeURL+"/"+blockid);
            if(!file.exists()){

                file.createNewFile();
                FileOutputStream os = new FileOutputStream(file);
                os.write(bytes);
                os.close();
            }
            return "SUCCESS";
        }catch(IOException e){
            e.printStackTrace();
            return "FAILURE";
        }
    }

    /**
     * 根据blockid读取文件
     * @param blockid
     * @return
     */
    public byte[] readFile(String blockid){
        File file = new File("./"+datanodeURL+"/"+blockid);
        try{
            FileInputStream is = new FileInputStream(file);
            Block block = getBlock(blockid);
            byte[] bytes = new byte[block.getSize()];
            is.read(bytes,0,block.getSize());
            is.close();
            return bytes;
        }catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;

    }
}
