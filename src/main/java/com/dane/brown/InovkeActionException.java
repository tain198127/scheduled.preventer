package com.dane.brown;

/**
 * @author DaneBrown
 * email:tain198127@163.com
 **/
public class InovkeActionException extends Exception {
    private int resultCode;

    public InovkeActionException(String msg) {
        super(msg);
    }

    public InovkeActionException(String msg, Throwable t) {
        super(msg, t);
    }

    public InovkeActionException(String msg, int resultCode, Throwable t) {
        super(msg, t);
        this.resultCode = resultCode;
    }

    public InovkeActionException(String msg, int resultCode) {
        super(msg);
        this.resultCode = resultCode;
    }

    public int getResultCode() {
        return resultCode;
    }
}
