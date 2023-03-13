package com.adobe.pdf.demo.messages;

import java.util.List;

public class CorrespondenceResponse {
    List<Correspondence> correspondences;
    List<String> attachments;

    public List<Correspondence> getCorrespondences() {
        return correspondences;
    }
    public void setCorrespondences(List<Correspondence> correspondences) {
        this.correspondences = correspondences;
    }
    public List<String> getAttachments() {
        return attachments;
    }
    public void setAttachments(List<String> attachments) {
        this.attachments = attachments;
    }

}
