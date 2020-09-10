package com.example.xs.mvp.model;

public class RePlayTime {

    /**
     * 开始时间
     */
    private String startTime;
    /**
     * 结束时间
     */
    private String endTime;
    /**
     * 文件大小
     */
    private Integer fileSize;
    /**
     * 文件名称
     */
    private String fileName;


    public RePlayTime(String startTime, String endTime, Integer fileSize, String fileName) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.fileSize = fileSize;
        this.fileName = fileName;
    }

    public Integer getFileSize() {
        return fileSize;
    }

    public void setFileSize(Integer fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
}
