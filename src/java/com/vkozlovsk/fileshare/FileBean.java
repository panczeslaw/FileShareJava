/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vkozlovsk.fileshare;

import javax.inject.Named;
import javax.enterprise.context.Dependent;

import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.io.*;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

/**
 *
 * @author vkozl
 */
@Named(value = "fileBean")
@Dependent
public class FileBean {

    private UploadedFile file;
    private String ip;
    private final String datetime; 
    
    public FileBean() {
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss dd.MM.yyyy");
        Date date = new Date();
        datetime = dateFormat.format(date);
    }
    
    public boolean copyFile(String path, InputStream in) {
        path = System.getProperty("java.io.tmpdir") + path;
        try {
            OutputStream out = new FileOutputStream(new File(path)); 
            int read = 0;
            byte[] bytes = new byte[1024]; 
            while ((read = in.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            } 
            in.close();
            out.flush();
            out.close();
            return true;
        } 
        catch (IOException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }
    
    
    public String getIP() {
        return ip;
    }
    
    public String getDateTime() {
        return datetime;
    }
    
    public UploadedFile getFile() {
        return file;
    }
    
    public void setFile(UploadedFile file) {
        this.file = file;
    }
    
    public void setIP(String s) {
        ip = s;
    }    
    
    public void upload(FileUploadEvent event) {     
        
        try {
            file = event.getFile();
            String filename = file.getFileName();
            System.out.println("temp filename: " + filename);
            copyFile(filename, file.getInputstream());
        }
        catch (IOException e) {
            e.getMessage();
        }
    }
    
    public void submit() {
        try {
            copyFile(file.getFileName(), file.getInputstream());
            //photoPostDAO.createPost(filename, postDescription);
        }
        catch (IOException e) {
            e.getMessage();
        }
    }
}
