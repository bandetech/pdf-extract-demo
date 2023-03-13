package com.adobe.pdf.demo.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class MyPdfUtil {
    
    private Path tempDir;
    private static final Logger LOGGER = LoggerFactory.getLogger(MyPdfUtil.class);

    public Path getTempDir() {
        return tempDir;
    }

    public void setTempDir(Path tempDir) {
        this.tempDir = tempDir;
    }

    @PostConstruct
    public void initAFterStartup(){
        try{
            Path tmpDir = Files.createTempDirectory("AdobePDF");
            if(tmpDir != null){
                LOGGER.info("Temp Directory: {}", tmpDir.toString());
            } else {
                LOGGER.error("Temp Directory was null !!");
            }
            setTempDir(tmpDir);
        } catch(IOException ex){
            LOGGER.error("Cannot create temp directory", ex);
        }
    }

    @PreDestroy
    public void cleanupBeforeExit(){
        LOGGER.info("Clean up was called in PyPdfUtil");
    }
}
