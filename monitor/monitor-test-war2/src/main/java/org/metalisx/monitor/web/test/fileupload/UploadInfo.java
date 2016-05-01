package org.metalisx.monitor.web.test.fileupload;

import java.util.ArrayList;
import java.util.List;


public class UploadInfo {

    List<UploadFile> uploadFiles = new ArrayList<UploadFile>();

    public UploadInfo() {
    }

    public List<UploadFile> getUploadFiles() {
        return uploadFiles;
    }

    public void setUploadFiles(List<UploadFile> uploadFiles) {
        this.uploadFiles = uploadFiles;
    }

    public void addUploadFile(UploadFile uploadFile) {
        uploadFiles.add(uploadFile);
    }

}
