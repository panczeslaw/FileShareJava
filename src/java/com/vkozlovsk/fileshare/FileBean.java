/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vkozlovsk.fileshare;


import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.*;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

/**
 *
 * @author vkozl
 */

@SessionScoped
@ManagedBean
public class FileBean implements Serializable {

    private UploadedFile file;
    private String host;
    
    public String getHost() throws UnknownHostException {
        return InetAddress.getLocalHost().getHostAddress() + ":8080";
    }
    
    public void setHost(String x) {
       
    }
    
    public UploadedFile getFile() {
        return file;
    }
    
    public void setFile(UploadedFile file) {
        this.file = file;
    }
    
    public boolean copyFile(String path, Long timestamp, InputStream in) {
        path = timestamp.toString() + "_" + path;
        path = System.getProperty("java.io.tmpdir") + path;
        System.out.println(path);
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
            FileModel f = new FileModel(rs.getInt("file_id"), rs.getString("filename"), rs.getString("ip"), rs.getLong("timestamp"));
            files.add(f);

        }

        rs.close();
        pstmt.close();
        connect.close();
        return files;

    }

    
    
    
    public void upload() throws IOException, ClassNotFoundException, SQLException {
        if(file != null && !file.getFileName().equals("")) {
            String filename = file.getFileName();
            Path p = Paths.get(filename);
            filename = p.getFileName().toString(); 
            Long l = System.currentTimeMillis();
            copyFile(filename, l, file.getInputstream());
            
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
            HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
            String ipAddress = request.getHeader("X-FORWARDED-FOR");
            if (ipAddress == null) {
                ipAddress = request.getRemoteAddr();
            }
            System.out.println(ipAddress);
            if (ipAddress.equals("0:0:0:0:0:0:0:1"))
                ipAddress = InetAddress.getLocalHost().getHostAddress();
            
            Statement stmt = connect.createStatement();
            stmt.execute("INSERT INTO files VALUES ('" + filename + "', '" + ipAddress + "', " + l + ")");
            
            stmt.close();
            connect.close();
            
            ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
            externalContext.redirect("index.xhtml");
        }
    }
    
    
    public void delete(Integer id) throws IOException, ClassNotFoundException, SQLException {  
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

        PreparedStatement pstmt = connect.prepareStatement("SELECT * FROM files WHERE file_id = " + id);
        ResultSet rs = pstmt.executeQuery();

        while (rs.next()) {
            FileModel f = new FileModel(rs.getInt("file_id"), rs.getString("filename"), rs.getString("ip"), rs.getLong("timestamp"));
            File ff = new File(System.getProperty("java.io.tmpdir") + f.getTimestamp() + "_" + f.getFilename());
            ff.delete();
        }
        rs.close();
        
        Statement stmt = connect.createStatement();
        
        stmt.execute("DELETE FROM files WHERE file_id = " + id);

        stmt.close();
        connect.close();
            
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        externalContext.redirect("index.xhtml");
    }
    
    
    public void download(Integer id) throws IOException, ClassNotFoundException, SQLException {  
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

        PreparedStatement pstmt = connect.prepareStatement("SELECT * FROM files WHERE file_id = " + id);
        ResultSet rs = pstmt.executeQuery();

        rs.next();
        FileModel f = new FileModel(rs.getInt("file_id"), rs.getString("filename"), rs.getString("ip"), rs.getLong("timestamp"));
        File ff = new File(System.getProperty("java.io.tmpdir") + f.getTimestamp() + "_" + f.getFilename());
        rs.close();
        
        connect.close();
        
        FacesContext facesContext = FacesContext.getCurrentInstance();

        HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();

        response.reset();
        response.setHeader("Content-Type", "application/octet-stream");
        response.setHeader("Content-Disposition", "attachment;filename=" + f.getFilename());

        OutputStream responseOutputStream = response.getOutputStream();

        InputStream fileInputStream = new FileInputStream(ff);

        byte[] bytesBuffer = new byte[2048];
        int bytesRead;
        while ((bytesRead = fileInputStream.read(bytesBuffer)) > 0) 
        {
            responseOutputStream.write(bytesBuffer, 0, bytesRead);
        }

        responseOutputStream.flush();

        fileInputStream.close();
        responseOutputStream.close();

        facesContext.responseComplete();
        
        /*ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        externalContext.redirect("index.xhtml");*/
    }
    
    public void handleFileUpload(FileUploadEvent event) {     
 
    }
   
}
