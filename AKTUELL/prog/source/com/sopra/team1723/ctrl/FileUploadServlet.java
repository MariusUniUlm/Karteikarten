package com.sopra.team1723.ctrl;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.apache.commons.io.FilenameUtils;
import org.json.simple.JSONObject;

@MultipartConfig
public class FileUploadServlet extends ServletController
{
    
    protected final int profilBildHeigth = 112;
    protected final int profilBildWidth = 112;
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        super.doPost(req, resp);

        if(!doProcessing())
            return;
        
        if(!aktuelleAction.equals(requestActionUploadProfilBild))
        {
            // Sende Error zur�ck
            JSONObject jo = JSONConverter.toJsonError(JSONConverter.jsonErrorInvalidParam);
            outWriter.print(jo);
            return;
        }
        
        Part uploadedFile = req.getPart(requestUploadFile);
        String fileName = getFileName(uploadedFile);
        InputStream contentStream = uploadedFile.getInputStream();

        ServletContext servletContext = getServletContext();
        String contextPath = servletContext.getRealPath(File.separator);
        String fileExt = FilenameUtils.getExtension(fileName);
        
        if(fileExt == null)
        {
            // Sende Error zur�ck
            JSONObject jo = JSONConverter.toJsonError(JSONConverter.jsonErrorInvalidParam);
            outWriter.print(jo);
            return;
        }
        
        
        // Bild neu Skalieren und speichern
        BufferedImage originalImage = ImageIO.read(contentStream);
        BufferedImage scaledImage = new BufferedImage(profilBildWidth, profilBildHeigth, BufferedImage.TYPE_INT_ARGB);
        Graphics g = scaledImage.createGraphics();
        g.drawImage(originalImage, 0, 0, profilBildWidth, profilBildHeigth, null);
        g.dispose();

        String relativerPfad = dirProfilBilder + aktuellerBenutzer.geteMail() + ".png";
        String absolutePath = contextPath + relativerPfad;
        ImageIO.write(scaledImage, "png", new File(absolutePath));

        
        System.out.println("File gespeichert: " + absolutePath);
        System.out.println("  relativer Pfad: " + relativerPfad);

        JSONObject jo = JSONConverter.toJson(relativerPfad);
        outWriter.print(jo);
        return;
    }
    
    private String getFileName(Part part) 
    {
        for (String cd : part.getHeader("content-disposition").split(";")) 
        {
            if (cd.trim().startsWith("filename"))
            {
                return cd.substring(cd.indexOf('=') + 1).trim().replace("\"", "");
            }
        }
        return null;
    } 
}

