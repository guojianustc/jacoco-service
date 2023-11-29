/**
 *  -包    名：com.xcmg.org.entity.core
 *  -文 件 名：ResponseBean.java
 *  -创建时间：2022年3月14日
 *  -版权所有：徐工集团-数字化研发技术研究所
 *
 */

package com.xcmg.jacocoservice.base;

import com.xcmg.jacocoservice.constants.ResponseConstants;

import java.io.Serializable;



public class ResponseBean implements Serializable{

	
	/**
	 * serialVersionUID:TODO
	 *
	 */
	
	private static final long serialVersionUID = -1673464165536869517L;

	/**
	 * 返回状态码
	 */
    private int code;
    /**
     * 返回消息
     */
    private String msg;
    /**
     * 返回数据
     */
    private Object data;
    
    
    public int getCode() {
		return code;
	}

	public String getMsg() {
		return msg;
	}

	public Object getData() {
		return data;
	}

	/**
     * 
     * -构造方法： ResponseBean
     * @param code
     * @param msg
     * @param data
     */
    public ResponseBean(int code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }
    
    /**
     *  操作成功
     * -方法作者：宋千义
     * -创建时间：2022年3月14日
     * -方法名称：success
     * -方法描述：
     * @param data
     * @return
     */
    public static  ResponseBean success(Object data) {
        return new ResponseBean(ResponseConstants.SC_OK, ResponseConstants.SUCCESS, data);
    }
    
    /**
     *  
     * -方法作者：宋千义
     * -创建时间：2022年3月14日
     * -方法名称：fail
     * -方法描述：操作失败
     * @param errorData
     * @return
     */
    public static  ResponseBean fail(Object errorData) {
        return new ResponseBean(ResponseConstants.SC_FAIL, ResponseConstants.FAIL, errorData);
    }

    /**
     *  非法访问
     * -方法作者：宋千义
     * -创建时间：2022年3月14日
     * -方法名称：notValid
     * -方法描述：非法访问
     * @return
     */
    public static  ResponseBean notValid() {
        return new ResponseBean(ResponseConstants.SC_NOT_VAILD, ResponseConstants.FAIL, "非法访问");
    }

    /**
     * 
     * -方法作者：宋千义
     * -创建时间：2022年3月18日
     * -方法名称：exception
     * -方法描述：操作发生异常
     * @param errorData
     * @return
     */
    public static  ResponseBean exception(Object errorData) {
        return new ResponseBean(ResponseConstants.SC_ERROR, ResponseConstants.ERROR, errorData);
    }
}
