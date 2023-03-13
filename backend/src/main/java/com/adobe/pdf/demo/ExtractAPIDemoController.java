package com.adobe.pdf.demo;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import com.adobe.pdf.demo.messages.CorrespondenceResponse;
import com.adobe.pdf.demo.messages.ExtractedPathResponse;
import com.adobe.pdf.demo.messages.UnzipPath;
import com.adobe.pdf.demo.utils.MyExtractPdfUtil;
import com.adobe.pdfservices.operation.ExecutionContext;
import com.adobe.pdfservices.operation.auth.Credentials;
import com.adobe.pdfservices.operation.exception.ServiceApiException;
import com.adobe.pdfservices.operation.exception.ServiceUsageException;
import com.adobe.pdfservices.operation.io.FileRef;
import com.adobe.pdfservices.operation.pdfops.ExportPDFOperation;
import com.adobe.pdfservices.operation.pdfops.ExtractPDFOperation;
import com.adobe.pdfservices.operation.pdfops.options.extractpdf.ExtractElementType;
import com.adobe.pdfservices.operation.pdfops.options.extractpdf.ExtractPDFOptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.PathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RestController
public class ExtractAPIDemoController {

    
    private static final Logger logger = LoggerFactory.getLogger(ExtractAPIDemoController.class);

    @CrossOrigin(origins="http://localhost:3000")
    @PostMapping(value="/extract")
    private ExtractedPathResponse extract(@RequestParam("file") MultipartFile file, 
            RedirectAttributes redirectAttributes){
    
        // prepare store file location
        FileOutputStream outputStream = null;
        String outputPath = null;
        try{
            // Get Credential for Adobe PDF Services
            Credentials credentials = this.initPdfService();

            // Create an ExecutionContext using credentials.           
            ExecutionContext executionContext = ExecutionContext.create(credentials);
            ExtractPDFOperation operation = ExtractPDFOperation.createNew();
      
            FileRef fileRef = FileRef.createFromStream(file.getInputStream(), ExportPDFOperation.SupportedSourceFormat.PDF.getMediaType());
            operation.setInputFile(fileRef);

            ExtractPDFOptions extractPDFOptions = ExtractPDFOptions.extractPdfOptionsBuilder()
            .addElementsToExtract(Arrays.asList(ExtractElementType.TEXT, ExtractElementType.TABLES))
            .build();

            operation.setOptions(extractPDFOptions);

            // Extract
            FileRef result = operation.execute(executionContext);
            
            // Save extracted file
            outputPath = saveExtractedFile(result);

        } catch(IOException|ServiceApiException|ServiceUsageException ex){
            logger.error("ERROR while executing extract : {}" + ex);
        } finally{
            if(outputStream != null){
                try {
                    outputStream.close();
                } catch (IOException ex) {
                    logger.error("ERROR while closing the temporaly file : {}", ex);
                }
            }
        }
        ExtractedPathResponse response = new ExtractedPathResponse();
        response.setPath(outputPath);
        return response;
    }

    @CrossOrigin(origins="http://localhost:3000")
    @GetMapping(value="/analyze")
    private CorrespondenceResponse analyze(@RequestParam("path") String path){
        CorrespondenceResponse response = new CorrespondenceResponse();

        UnzipPath unzipPath = MyExtractPdfUtil.extractZipFile(path);
        response.setCorrespondences(MyExtractPdfUtil.analyzePaths(unzipPath.getStructureDataPath()));
        response.setAttachments(unzipPath.getAttachments());
        return response;
        
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping(value="/attachment")
    public ResponseEntity<Resource> getPdfFile(@RequestParam("fileName") String fileName) throws Exception{
        
        //Path path = pdfUtil.getTempDir().resolve(fileName);
        Path path = Path.of(fileName);
        logger.info("Target Path : {}", path.toString());
        Resource resource = new PathResource(path);

        return ResponseEntity.ok()
            .contentType(getContentType(path))
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
            .body(resource);

    }
    

    private Credentials initPdfService(){

        Credentials credentials = null;
        try{
            credentials = Credentials.serviceAccountCredentialsBuilder()
            .fromFile("pdfservices-api-credentials.json")
            .build();
        } catch(IOException ex){
            logger.error("ERROR while initializing PDF Services : {}", ex);
        }
        return credentials;
    }

    private String saveExtractedFile(FileRef ref){
        String outFilePath = null;
        try{
            long curMills = System.currentTimeMillis();
            outFilePath = "output/" + String.valueOf(curMills) + ".zip";
            
            logger.info("Temp File: {}", outFilePath);
            
            ref.saveAs(outFilePath);
        }catch(IOException ex){
            logger.error("ERROR while save extracted file : {}", ex);
        } 
    
        return outFilePath;
    }

    private MediaType getContentType(Path path) throws IOException {
        try {
          return MediaType.parseMediaType(Files.probeContentType(path));
        } catch (IOException e) {
          logger.info("Could not determine file type.");
          return MediaType.APPLICATION_OCTET_STREAM;
        }
    }
}
