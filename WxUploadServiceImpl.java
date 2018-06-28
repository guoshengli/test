package com.shangde.greatbear.service.impl;

import com.shangde.greatbear.common.constants.HttpStatus;
import com.shangde.greatbear.common.exception.GreatBearxception;
import com.shangde.greatbear.common.util.HttpClientUtils;
import com.shangde.greatbear.common.util.JsonUtil;
import com.shangde.greatbear.common.util.MD5Util;
import com.shangde.greatbear.domain.response.UploadImageResponse;
import com.shangde.greatbear.service.WxUploadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

@Service
public class WxUploadServiceImpl implements WxUploadService{

    private static final Logger logger = LoggerFactory.getLogger(WxUploadServiceImpl.class);

    @Value("${upload_image_url}")
    private String uploadImageUrl;

    @Override
    public String uploadPortraitForWx(byte[] file) throws GreatBearxception {
        if (null == file ){
            throw new GreatBearxception(HttpStatus.BAD_REQUEST.getStatusCode(),"上传文件不能为空");
        }

        //上传最大1M图片
        /*Long maxSize = 1024L * 1024;
        int size = file.length; //byte
        if (size > maxSize){
            throw new GreatBearxception(HttpStatus.BAD_REQUEST.getStatusCode(),"上传图片不能大于1M");
        }*/

//        String ip = getIpAddr(request);
        String ip = "127.0.0.1";
        HashMap<String,Object> paramMap = new HashMap<>();
        paramMap.put("ip",ip);
        paramMap.put("channelCode","skyNet");
        paramMap.put("token", MD5Util.GetMD5Code("skyNet"));
        HttpClientUtils client = HttpClientUtils.getInstance();
        Long startTime = System.currentTimeMillis();
        String responseStr = client.doPostFileResultNew(uploadImageUrl,paramMap,file);
        logger.info("SellerInfoServiceImpl uploadPortrait()  " + uploadImageUrl + " cost time " + (System.currentTimeMillis() - startTime) + " ms");
        UploadImageResponse response = JsonUtil.Json2Java(responseStr,UploadImageResponse.class);
        if (null != response && response.getRs().equals("1")){
            logger.info("SellerInfoServiceImpl uploadPortrait() 调用上传图片接口正常返回 " + JsonUtil.Java2Json(response));
            return (String) response.getResultMessage().get("linkUrl");
        }else {
            logger.error("SellerInfoServiceImpl uploadPortrait() 调用上传图片接口异常返回 " + JsonUtil.Java2Json(response));
            throw new GreatBearxception(HttpStatus.BAD_REQUEST.getStatusCode(),"上传图片接口调用异常");
        }

    }
}
