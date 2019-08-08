package com.jesper.util;

import com.mongodb.*;
import com.mongodb.client.MongoDatabase;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSFile;

import java.io.File;
import java.util.*;


/**
 * @author think
 */
public class MongoDBUtil {

    private static String dbName = "springboot-db";
    String collectionName = "myImage";

//    /**
//     * @return 返回mongon
//     * @MethodName    : getMongoDB
//     * @Description    : 获取数据连接
//     */
//    private Mongo getMongoDB() {
//        List<ServerAddress> addresses = new ArrayList<>();
//        // ServerAddress()两个参数分别为 服务器地址 和 端口
//        ServerAddress serverAddress = new ServerAddress("192.168.87.144", 27017);
//        addresses.add(serverAddress);
//
//        //MongoCredential.createScramSha1Credential()三个参数分别为 用户名 数据库名称 密码
//        MongoCredential mongoCredential = MongoCredential.createScramSha1Credential("admin", dbName, "admin".toCharArray());
//
//        // 通过连接认证获取MongoDB连接
//        MongoClient mongoClient = new MongoClient(addresses, mongoCredential, new MongoClientOptions.Builder().build());
//
////        // 连接到数据库
////        MongoDatabase mongoDatabase = mongoClient.getDatabase(dbName);
////        return mongoDatabase;
//        Mongo mongo = mongoClient;
//        return mongo;
//    }


    private static Mongo MongoConnectionHolder() {
        List<ServerAddress> addresses = new ArrayList<>();
        // ServerAddress()两个参数分别为 服务器地址 和 端口
        ServerAddress serverAddress = new ServerAddress("192.168.87.135", 27017);
        addresses.add(serverAddress);

        //MongoCredential.createScramSha1Credential()三个参数分别为 用户名 数据库名称 密码
        MongoCredential mongoCredential = MongoCredential.createScramSha1Credential("admin", dbName, "admin".toCharArray());

        // 通过连接认证获取MongoDB连接
        MongoClient mongoClient = new MongoClient(addresses, mongoCredential, new MongoClientOptions.Builder().build());
        Mongo mongo = mongoClient;
        return mongo;
    }

    /**
     * @return 返回mongon
     * @MethodName    : getMongo
     * @Description    : 获取数据连接
     */
    private Mongo getMongo() {
        return MongoConnectionHolder();
    }

    /**
     * @param file                                                                     ：文件，File类型
     * @param id                                                                       ：唯一标示文件，可根据id查询到文件.必须设置

     * @param map：放入你想要保存的属性，例如文件类型（“congtentType”".jpg"）,字符串类型，区分大小写，如果属性没有的话会自动创建并保存
     * @MethodName    : uploadFile
     * @Description    : 上传文件
     */
    public void uploadFile(File file, String id, LinkedHashMap<String, Object> map) {
        //把mongoDB的数据库地址配置在外部。
        try {
            //获取链接，并定位数据库信息

            Mongo mongo = getMongo();
            DB db = mongo.getDB(dbName);
            GridFS gridFS = new GridFS(db, collectionName);

            // 创建gridfsfile文件
            GridFSFile gridFSFile = gridFS.createFile(file);
            //判断是否已经存在文件，如果存在则先删除
            GridFSDBFile gridFSDBFile = getFileById(id);
            if (gridFSDBFile != null) {
                deleteFile(id);
            }
            //将文件属性设置到
            gridFSFile.put("_id", id);
            //循环设置的参数
            if (map != null && map.size() > 0) {
                for (String key : map.keySet()) {
                    gridFSFile.put(key, map.get(key));
                }
            }
            //保存上传
            gridFSFile.save();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * @param id：文件对应的id

     * @MethodName    : deleteFile
     * @Description    : 删除文件
     */
    public void deleteFile(String id) {

        try {
            //获得mongoDB数据库连接。
            Mongo mongo = getMongo();
            //获得库
            DB db = mongo.getDB(dbName);
            //获得子集
            GridFS gridFS = new GridFS(db, collectionName);
            //删除文件
            DBObject query = new BasicDBObject("_id", id);
            gridFS.remove(query);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 批量删除文件
     *
     * @param ids

     * @MethodName    : deleteFileByIds
     * @Description    : TODO
     */
    public void batchDeleteFileByIds(String[] ids) {
        try {
            //获得mongoDB数据库连接。
            Mongo mongo = getMongo();
            //获得库
            DB db = mongo.getDB(dbName);
            //获得子集
            GridFS gridFS = new GridFS(db, collectionName);
            Map<String, String> map = new HashMap<String, String>();
            for (int i = 0; i < ids.length; i++) {
                //删除文件
                DBObject query = new BasicDBObject("_id", ids[i]);
                gridFS.remove(query);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param id                 ：文件Id
     * @return GridFSDBFile
     * @MethodName    : getFileById
     * @Description    : 根据Id获得文件
     */
    public GridFSDBFile getFileById(String id) {
        GridFSDBFile gridFSDBFile = null;
        try {
            //获得mongoDB数据库连接。
            Mongo mongo = getMongo();
            //获得库
            DB db = mongo.getDB(dbName);
            //获得子集
            GridFS gridFS = new GridFS(db, collectionName);
            //获得文件
            DBObject query = new BasicDBObject("_id", id);
            gridFSDBFile = gridFS.findOne(query);

        } catch (Exception e) {
            e.printStackTrace();
        }
        //返回数据
        return gridFSDBFile;

    }

    /**
     * 查询集合中所有文件
     *
     * @return
     * @MethodName    : getAllFile
     * @Description    : TODO
     */
    public List<GridFSDBFile> getAllFile() {
        List<GridFSDBFile> gridFSDBFileList = null;
        try {
            //获得mongoDB数据库连接。
            Mongo mongo = getMongo();
            //获得库
            DB db = mongo.getDB(dbName);
            //获得子集
            GridFS gridFS = new GridFS(db, collectionName);
            //获得文件
            DBObject query = new BasicDBObject();//空的构造
            gridFSDBFileList = gridFS.find(query);
            mongo.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //返回数据
        return gridFSDBFileList;
    }
}
