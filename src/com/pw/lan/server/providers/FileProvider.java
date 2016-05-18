package com.pw.lan.server.providers;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by aradej on 2016-05-17.
 */
public class FileProvider {
    private static final String ROOT = "root";

    public Map<String,String> getFiles(){
        return getFiles(ROOT);
    }

    public Map<String,String> getFiles(String path){
        Map<String,String> structure = new HashMap<>();
        File[] files = (new File(path)).listFiles();
        if( files !=null && files.length>0){
            for (File file : files) {
                if(file.isDirectory()){
                    structure.put(file.getName(), "dir");
                }else{
                    structure.put(file.getName(), getFileExtension(file) + " " + file.length());
                }
            }
        }
        return structure;
    }

    private static String getFileExtension(File file) {
        String fileName = file.getName();
        if(fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0)
            return fileName.substring(fileName.lastIndexOf(".")+1);
        else return "";
    }


}
