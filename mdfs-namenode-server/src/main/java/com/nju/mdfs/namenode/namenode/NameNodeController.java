package com.nju.mdfs.namenode.namenode;

import com.netflix.appinfo.InstanceInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.eureka.server.event.*;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.net.URLDecoder;

@RestController
public class NameNodeController {
    @Autowired
    private NameNodeService nameNodeService;

    /**
     * 获取前端发送的请求
     * 只注解一次，对象只有一个，然而被spring代理了，获取的是当前线程的请求
     */
    @Autowired
    private HttpServletRequest request;


    /**
     * 上传文件接口
     * (/**)是通配符，如果是Post请求，"/"后面无论接任何请求都由该方法进行处理
     */
    @PostMapping("/**")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file){
        String nodename = request.getRequestURI();

        try {
            nodename = URLDecoder.decode(nodename,"UTF-8");
            if(nodename.charAt(nodename.length()-1)!='/')
                nodename += ("/" +file.getOriginalFilename());
            else
                nodename+=file.getOriginalFilename();
            String response = nameNodeService.uploadFile(file, nodename);
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.add("nodename",nodename);
            return new ResponseEntity<String>(response,responseHeaders,HttpStatus.CREATED);
        }catch (Exception e){
            e.printStackTrace();
            HttpHeaders responseHeaders = new HttpHeaders();
            return new ResponseEntity<String>("FAILURE",responseHeaders,HttpStatus.CREATED);
        }
    }

    /**
     * 下载文件或者查询目录接口
     * @return
     */
    @GetMapping("/**")
    public ResponseEntity<?> downloadOrFileTree(){
        String nodename = request.getRequestURI();
        try{
            nodename = URLDecoder.decode(nodename,"UTF-8");
        }catch (Exception e){
            e.printStackTrace();
        }
        //如果查询的是目录
        if(nameNodeService.isDirectory(nodename)){
            String directoryTree = nameNodeService.getDirectoryTree(nodename,"");
            HttpHeaders responseHeaders = new HttpHeaders();
            return new ResponseEntity<String>(directoryTree,responseHeaders,HttpStatus.CREATED);
        }
        else
        {
            try {
                byte[] response = nameNodeService.downloadFile(nodename);
                System.out.println("response.length = "+response.length);
                String filename = nodename.substring(nodename.lastIndexOf('/') + 1);
                HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.add("Accept-Ranges", "bytes");
                httpHeaders.add("Content-Length", response.length + "");
                httpHeaders.add("Content-Disposition", "attachment; filename=\""+filename+"\"");
                httpHeaders.add("Content-Type", "multipart/form-data;charset=utf-8");
                ResponseEntity<byte[]> entity = new ResponseEntity<>(response,httpHeaders,HttpStatus.OK);
                System.out.println("我要返回啦");
                return entity;

            }catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }
    }

    /**
     * 删除文件接口
     * 先删除Block，后删除Node
     * @return
     */
    @DeleteMapping("/**")
    public String deleteFileOrDirectory(){
        String nodename = request.getRequestURI();
        String response = "";
        try{
            nodename = URLDecoder.decode(nodename,"UTF-8");
            String blockresponse = nameNodeService.deleteBlock(nodename);
            response += "blockState: "+blockresponse+"\n";
            String noderesponse = nameNodeService.deleteNode(nodename);
            response += "nodeState: "+noderesponse+"\n";
            return response;
        }catch (Exception e){
            e.printStackTrace();
            return "SOMETHING WRONG";
        }
    }

    /**
     * 注册服务发现相关
     * https://www.jianshu.com/p/b46bce4411b0
     */

    /**
     * 服务下线
     * @param event
     */
    @EventListener
    public void listen(EurekaInstanceCanceledEvent event) {
        System.err.println(event.getServerId() + "\t" + event.getAppName() + " 服务下线");
    }

    /**
     * 服务注册
     * @param event
     */
    @EventListener
    public void listen(EurekaInstanceRegisteredEvent event) {
        InstanceInfo instanceInfo = event.getInstanceInfo();
        String url = instanceInfo.getHomePageUrl();
        System.err.println(instanceInfo.getAppName() + "进行注册");
        nameNodeService.addDataNode(url);
    }

    /**
     * 服务续约
     * @param event
     */
    @EventListener
    public void listen(EurekaInstanceRenewedEvent event) {
        System.err.println(event.getServerId() + "\t" + event.getAppName() + " 服务进行续约");
    }

    /**
     * 注册服务启动
     * @param event
     */
    @EventListener
    public void listen(EurekaRegistryAvailableEvent event) {
        System.err.println("注册中心 启动");
    }

    /**
     * Eureka server启动
     * @param event
     */
    @EventListener
    public void listen(EurekaServerStartedEvent event) {
        System.err.println("Eureka Server 启动");
    }


}
