package com.hao.ftp.core.Constant;

public class ResponseCode{
    public static final int SERVICE_READY=220;
    public static final int COMMAND_OK=200;
    public static final int COMMAND_NOT_IMPL=502;
    public static final int NULL_STATE=-1;
    public static final int NEET_PASSWORD=331;
    public static final int BAD_SEQ_CMD=503;
    public static final int NOT_LOG_IN=530;
    public static final int USER_LOG_IN=230;
    public static final int WORK_DIR=257;
    public static final int CLOSE=221;
    public static final int SYNTAX_ERROR=501;
    public static final int CONNECTION_OPEN=125;
    public static final int TRANSFER_SUCCESS=226;
    public static final int COMMAND_FAIL=550;
}
