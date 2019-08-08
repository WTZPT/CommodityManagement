package com.jesper;

import com.jesper.util.MongoDBUtil;
import com.mongodb.gridfs.GridFSDBFile;
import org.junit.Test;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

public class MongoDBUtilTest {
    @Test
    public void uploadFileTest(){
        MongoDBUtil mongoDBUtil = new MongoDBUtil();
        File file = new File("C:\\Users\\jiangyunxiong\\Desktop\\cat.jpg");
        String id = UUID.randomUUID().toString().replaceAll("-", "");

        LinkedHashMap<String,Object> metaMap = new LinkedHashMap<String, Object>();
        System.out.println("我的id是:" + id );
        metaMap.put("contentType","cat");
        metaMap.put("_id",id);


        mongoDBUtil.uploadFile(file,id,metaMap);
        System.out.println("Upload File Success");
    }

    @Test
    public void deleteFileTest(){
        MongoDBUtil mongoDBUtil = new MongoDBUtil();
        String id = "94b6c9aee0cb4c98b6f670a953dd5589";

        mongoDBUtil.deleteFile(id);
        System.out.println("Delete File Success");
    }

    @Test
    public void getFileByIdTest(){
        MongoDBUtil mongoDBUtil = new MongoDBUtil();
        String id = "92b2d9bb14ee44dbbeac339abfa30d2c";
        String dbName = "jesper";
        String collectionName = "myimage";
        GridFSDBFile fileById = mongoDBUtil.getFileById(id);
        try {
            System.out.println(fileById.writeTo(new File("C:\\Users\\jiangyunxiong\\Desktop\\jyxmust.jpg")));
        }catch (Exception ex){
            ex.printStackTrace();
        }
        System.out.println("get File by Id Success");
    }

    @Test
    public void getAllFileTest(){
        MongoDBUtil mongoDBUtil = new MongoDBUtil();
        String dbName = "jesper";
        String collectionName = "myimage";
        List<GridFSDBFile> files = mongoDBUtil.getAllFile();
        for (GridFSDBFile file:files) {
            //file.writeTo(new File("${path}"))
            System.out.println(file.getId() + ":" + file.getFilename());
        }
        System.out.println("get All File Sucess!!");
    }

    @Test
    public void batchDeleteFileByIds(){
        MongoDBUtil mongoDBUtil = new MongoDBUtil();
        String[] ids = {"163f5484cd60416ebb71c64814205b41","b815a254f0514867b8e9745b4bb974b5"};
        String dbName = "jesper";
        String collectionName = "myimage";
        mongoDBUtil.batchDeleteFileByIds(ids);
        System.out.println("batch delete Files success");
    }
}
