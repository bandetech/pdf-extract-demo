package com.adobe.pdf.demo.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.adobe.pdf.demo.messages.Correspondence;
import com.adobe.pdf.demo.messages.UnzipPath;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyExtractPdfUtil {

    private static final Logger logger = LoggerFactory.getLogger(MyExtractPdfUtil.class);

    // Caution: assume zip contains one file and saved it as the same filename for demo purpose (no use for simultaneous transaction)
    public static UnzipPath extractZipFile(String zipFilePath){

        UnzipPath unzipPath = new UnzipPath();
        List<String> attachments = new ArrayList<String>();

        // String targetDir = "output/" + zipFilePath.substring(0,zipFilePath.lastIndexOf('.'));
        String targetDir = zipFilePath.substring(0,zipFilePath.lastIndexOf('.'));
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFilePath))) {
            ZipEntry zipEntry;
            while((zipEntry = zis.getNextEntry()) != null){
                Path pathInZip = Paths.get(targetDir, zipEntry.getName());
                String pathInZipStr = pathInZip.toString();
                if(pathInZipStr.contains("structuredData.json")){
                    unzipPath.setStructureDataPath(pathInZipStr);
                } else {
                    logger.info("Attachment Found :{}", pathInZipStr.replace("\\", "/"));
                    attachments.add(pathInZipStr.replace("\\", "/"));
                }
                Files.createDirectories(pathInZip.getParent());
                Files.write(pathInZip, zis.readAllBytes());
                logger.info("inflating : " + pathInZip);
            }

        }catch(IOException ex){
            logger.error("ERROR while extracting zip file : {}", ex);   
        }
        unzipPath.setAttachments(attachments);
        return unzipPath;
    }

    public static List<Correspondence> analyzePaths(String jsonFilePath){
        Path path = Paths.get(jsonFilePath);
        ObjectMapper om = new ObjectMapper();    
        
        JsonNode json;
        JsonNode element=null;

        List<String> questions = new ArrayList<String>();
        List<String> answers = new ArrayList<String>();
        boolean questionFlag = true;
        try{
            json = om.readTree(path.toFile());

            StringBuffer questionBuf = new StringBuffer();
            StringBuffer answerBuf = new StringBuffer();
            for(Iterator<JsonNode> iterator=json.get("elements").elements(); iterator.hasNext();){
                element = iterator.next();

                String elementPath=element.findValue("Path").toString().replace("\"", "");
                if(elementPath.contains("Table")){
                    continue;
                }
                String text = element.findValue("Text").toString().replace("\"", "");

                // Ignore Adobe header/footer
                if(text.startsWith("Adobe")){
                    continue;
                }
                // If the path contains Aside, it's question part         
                if(elementPath.contains("Aside")){
                    if(!questionFlag){
                        answers.add(answerBuf.toString());
                        answerBuf = new StringBuffer();
                        questionFlag = true;
                    }
                    questionBuf.append(text + "\n");
                    logger.info("Question Part :{}", elementPath);
                    logger.info("Question Part :{}", text);

                }else{
                    if(questionFlag){
                        questions.add(questionBuf.toString());
                        questionBuf = new StringBuffer();
                        questionFlag = false;
                    }
                    answerBuf.append(text + "\n");
                    logger.info("Answer Part :{}", elementPath);
                    logger.info("Answer Part : {}", text);    
                }
            }
            // Flush buffer for Answer lastly
            if(!questionFlag){
                answers.add(answerBuf.toString());
            }

        }catch(IOException ex){
            ex.printStackTrace();
        }catch(NullPointerException ex){
            ex.printStackTrace();
        }
        logger.info("Questions :{}", questions.size());
        logger.info("Answers: {}", answers.size());

        List<Correspondence> cList = new ArrayList<Correspondence>();
        for(int i=0; i<questions.size(); i++){
            Correspondence cor = new Correspondence();
            cor.setQuestion(questions.get(i));
            cor.setAnswer(answers.get(i));
            cList.add(cor);
        }
        return cList;
    }

    public static void main(String[] args){
       analyzePaths("output/structuredData.json");
    }
}
