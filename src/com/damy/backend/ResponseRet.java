/* 健康*/
package com.damy.backend;

public class ResponseRet {

	public static final int RET_SUCCESS = 0;
	public static final int RET_FAILURE = 100;
	public static final int RET_NOUSER = 101;
	public static final int RET_DUPLICATEUSER = 102;
	public static final int RET_NOIMAGE = 103;
	public static final int RET_NORECORD = 104;
	public static final int RET_NOINFO = 105;
	public static final int RET_OLDPASSNOTMATCH = 106;
    public static final int RET_SUCCFBLOGIN = 107;
	public static final int RET_INTERNAL_EXCEPTION = 500;
	public static final int RET_JSON_EXCEPTION = 501;
}
