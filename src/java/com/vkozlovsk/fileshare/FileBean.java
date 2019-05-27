/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vkozlovsk.fileshare;


import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.*;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

/**
 *
 * @author vkozl
 */

@SessionScoped
@ManagedBean
public class FileBean implements Serializable {

    public UploadedFile file;
    
    public boolean copyFile(String path, InputStream in) {
        
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
        
    public List<FileModel> getFiles() throws ClassNotFoundException, SQLException {

        Connection connect = null;

        String url = "jdbc:postgresql://localhost:5432/filesharejava";

        String username = "postgres";
        String password = "postgres";

        try {

                Class.forName("org.postgresql.Driver");

                connect = DriverManager.getConnection(url, username, password);
                // System.out.println("Connection established"+connect);

        } catch (SQLException ex) {
                System.out.println("in exec");
                System.out.println(ex.getMessage());
        }

        List<FileModel> files = new ArrayList<FileModel>();
        PreparedStatement pstmt = connect.prepareStatement("SELECT * FROM files");
        ResultSet rs = pstmt.executeQuery();

        while (rs.next()) {
            FileModel f = new FileModel();
            f.setID(rs.getInt("file_id"));
            f.setFilename(rs.getString("filename"));
            f.setIP(rs.getString("ip"));
            f.setTimestamp(rs.getInt("timestamp"));
            files.add(f);

        }

        rs.close();
        pstmt.close();
        connect.close();
        return files;

    }

    
    public void upload() throws IOException {
        if(file != null) {
            System.out.print("UPLOAD METHOD WORKS!");
        }
    }
     
    public void handleFileUpload(FileUploadEvent event) {     
        
        try {
            UploadedFile file = event.getFile();
            String filename = file.getFileName();
            Path p = Paths.get(filename);
            copyFile(p.getFileName().toString(), file.getInputstream());
        }
        catch (IOException e) {
            e.getMessage();
        }
    }
    
}
