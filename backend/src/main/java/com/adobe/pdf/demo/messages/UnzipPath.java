package com.adobe.pdf.demo.messages;

import java.util.List;

public class UnzipPath {
    private String structureDataPath;
    private List<String> attachments;

    public String getStructureDataPath() {
        return structureDataPath;
    }
    public void setStructureDataPath(String structureDataPath) {
        this.structureDataPath = structureDataPath;
    }
    public List<String> getAttachments() {
        return attachments;
    }
    public void setAttachments(List<String> attachments) {
        this.attachments = attachments;
    }
    
}
