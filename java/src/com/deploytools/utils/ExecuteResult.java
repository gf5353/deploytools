package com.deploytools.utils;

public class ExecuteResult {



    private String msg;
    private String errorMsg;
    /**
     * 0 成功
     * 1 失败
     * 2 用户取消
     */
    private int result;
    private Property property;

    public ExecuteResult(int result, String msg, String errorMsg){
            this.msg=msg;
            this.result=result;
            this.errorMsg=errorMsg;
    }

    public void setProperty(Property property) {
        this.property = property;
    }

    public Property getProperty() {
        return property;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public void setErrorMsg(String errorMsg) {
        this.msg = errorMsg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getResult() {
        return result;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public String getMsg() {
        return msg;
    }

    public boolean isSuccess(){
        return result==0;
    }

    @Override
    public String toString() {
        return result+":"+msg+":"+errorMsg;
    }
}
