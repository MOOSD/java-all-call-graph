package com.adrninistrator.jacg.dto.write_db;

import java.util.List;

public class WriteDBResult {
    List<String> showWarningMessage;

    List<String> showErrorMessage;

    Boolean runResult;


    public List<String> getShowErrorMessage() {
        return showErrorMessage;
    }

    public void setShowErrorMessage(List<String> showErrorMessage) {
        this.showErrorMessage = showErrorMessage;
    }

    public List<String> getShowWarningMessage() {
        return showWarningMessage;
    }

    public void setShowWarningMessage(List<String> showWarningMessage) {
        this.showWarningMessage = showWarningMessage;
    }

    public Boolean getRunResult() {
        return runResult;
    }

    public void setRunResult(Boolean runResult) {
        this.runResult = runResult;
    }
}
