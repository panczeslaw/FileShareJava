/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vkozlovsk.fileshare;

import java.util.Date;

public class FileModel {
    private Integer fileID;
    private String filename;
    private String ip;
    private Long timestamp; 
    
    public FileModel() {}
    
    public FileModel(Integer fileID, String filename, String ip, Long timestamp) {
        this.fileID = fileID;
        this.filename = timestamp.toString() + "_" + filename;
        this.ip = ip;
        this.timestamp = timestamp;
    }
    
    public Integer getID() {
        return fileID;
    }
    
    public String getIP() {
        return ip;
    }
    
    public Long getTimestamp() {
        return timestamp;
    }
    
    public String getDatetime() {
        return (new Date(timestamp)).toString();
    }
    
    public String getFilename() {
        return filename.substring(filename.indexOf("_") + 1) ;
    }
    
    public void setID(Integer fileID) {
        this.fileID = fileID;
    }
    
    public void setIP(String ip) {
        this.ip = ip;
    }
    
    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
        this.filename = timestamp.toString() + "_" + getFilename();
    }
    
    public void setFilename(String filename) {
        this.filename = timestamp.toString() + "_" + filename;
    }
}
