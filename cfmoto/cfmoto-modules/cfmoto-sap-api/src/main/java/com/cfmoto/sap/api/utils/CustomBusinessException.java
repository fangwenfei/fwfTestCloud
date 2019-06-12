package com.cfmoto.sap.api.utils;

/**
 * 自定义异常类，可以处理一些配置
 */
public class CustomBusinessException extends Exception{

    public CustomBusinessException(){
        super();
    }

    public CustomBusinessException( String message ){
        super( message );
    }

    public CustomBusinessException( String message, Throwable throwable ){
        super( message, throwable );
    }

    public CustomBusinessException( Throwable cause ){
        super( cause );
    }

    public CustomBusinessException( String message, Throwable cause,
                        boolean enableSuppression,
                        boolean writableStackTrace ) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
