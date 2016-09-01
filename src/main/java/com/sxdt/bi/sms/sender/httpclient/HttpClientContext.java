package com.sxdt.bi.sms.sender.httpclient;

import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.HeaderIterator;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;


/**
 * httpclient管理上下文
 * 
 * @author fyg
 * 2015年5月7日 下午6:23:19
 */
public class HttpClientContext {
	private static final Log logger = LogFactory.getLog(HttpClientContext.class);
	
    public static final int REQUEST_TIMEOUT = 60 * 1000; // 设置请求超时
    public static final int TIMEOUT         = 60 * 1000; // 连接超时时间
    public static final int SO_TIMEOUT      = 60 * 1000; // 数据传输超时
    public static final String CHARSET      = "UTF-8";
    public static String request_body = "";
	private HttpClient httpClient;
	public HttpClientContext(){
		initialize(10, 10);
	}
	public void initialize(int poolSize,int maxPerRoute){
		//	池化连接管理器
		PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
		//	设置连接池的最大连接数
		cm.setMaxTotal(poolSize);
		//设置每个路由上的默认连接个数
		cm.setDefaultMaxPerRoute(maxPerRoute);
		httpClient= HttpClients.custom().disableRedirectHandling()
		.setConnectionManager(cm)
		.build();
	}
	public  String doGet(String url,Map<String, String> headers)
    {
        HttpGet httpGet = new HttpGet(url);
        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(SO_TIMEOUT)
                .setConnectTimeout(TIMEOUT)
                .setConnectionRequestTimeout(REQUEST_TIMEOUT).build();
        httpGet.setConfig(requestConfig);
        long responseLength = 0; // 响应长度
        String responseContent = null; // 响应内容
        String strRep = null;
        try {
           	if(headers!=null){
        		for(String s:headers.keySet())
        			httpGet.setHeader(s, headers.get(s));
        	}
            // 执行get请求
            HttpResponse httpResponse = httpClient.execute(httpGet);
            
            // 头信息
           // printHeaders(httpResponse);

            // 获取响应消息实体
            HttpEntity entity = httpResponse.getEntity();
            if (entity != null) 
            {
                responseLength = entity.getContentLength();
                responseContent = EntityUtils.toString(entity, CHARSET);//不能重复调用此方法，IO流已关闭。
                if(logger.isDebugEnabled()){
	                logger.debug("内容编码: " + entity.getContentEncoding());
	                logger.debug("请求地址: " + httpGet.getURI());
	                logger.debug("响应状态: " + httpResponse.getStatusLine().getStatusCode());
	                logger.debug("响应长度: " + responseLength);
	                logger.debug("响应内容: " + responseContent);
                }
                // 获取HTTP响应的状态码
                int statusCode = httpResponse.getStatusLine().getStatusCode();
                //logger.info(httpResponse.getStatusLine());
                if (statusCode == HttpStatus.SC_OK)
                {
                    strRep = responseContent; // EntityUtils.toString(httpResponse.getEntity());
                }else{
                	   logger.info("请求地址: " + httpGet.getURI());
                       logger.info("响应状态: " + httpResponse.getStatusLine().getStatusCode());
                       logger.info("响应内容: " + responseContent);
                }
                // Consume response content
                EntityUtils.consume(entity);
                // Do not need the rest
                httpGet.abort();
            }
        } 
        catch (ClientProtocolException e)
        {
            //logger.error("ClientProtocolException", e);
            e.printStackTrace();
        }
        catch (UnsupportedEncodingException e) {  
            //logger.error("UnsupportedEncodingException", e);
            e.printStackTrace(); 
        }
        catch (ConnectTimeoutException e) {
            //logger.error("ConnectTimeoutException", e);
            e.printStackTrace();
        }
        catch (SocketTimeoutException e) {
            //logger.error("SocketTimeoutException", e);
            e.printStackTrace();
        }
        catch (Exception e)
        {
            //logger.error("Exception", e);
        } finally {
            httpGet.releaseConnection();
        }
        
        return strRep;
    }
    /**
     * 指定参数名GET方式请求数据
     * @param url
     * @param paramsMap QueryString
     * @return
     */
    public  String doGet(String url, Map<String, String> paramsMap,Map<String, String> headers)
    {
    	if(paramsMap!=null){
    		return doGet(invokeUrl(url, paramsMap),headers);
    	}else{
    		return doGet(url,headers);    		
    	}
    }
    /**
     * 不指定参数名的方式来POST数据
     * @param url
     * @param jsonXMLString
     * @return
     */
    public  String doPost(String url, String jsonXMLString,Map<String, String> headers)
    {
        return doPost(url, null, jsonXMLString,headers);
    }
    /**
     * 指定参数名POST方式请求数据
     * @param url
     */
    public  String doPost(String url, Map<String, String> paramsMap,Map<String, String> headers)
    {
        return doPost(url, paramsMap, null,headers);
    }
    
    private  String doPost(String url, Map<String, String> paramsMap, String jsonXMLString,Map<String, String> headers)
    {
        HttpPost httpPost = new HttpPost(url);
        //httpPost.setHeader("Content-type", "text/xml; charset=gbk");

        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(SO_TIMEOUT)
                .setConnectTimeout(TIMEOUT)
                .setConnectionRequestTimeout(REQUEST_TIMEOUT)
                .setExpectContinueEnabled(false).build();

        httpPost.setConfig(requestConfig);// RequestConfig.DEFAULT
        
        long responseLength = 0; // 响应长度
        String responseContent = null; // 响应内容
        String strRep = null; 
        try {
        	if(headers!=null){
        		//logger.debug("request header:");
        		for(String s:headers.keySet()){
        			//logger.debug(s.substring(0,1).toUpperCase()+s.substring(1) + " - " + headers.get(s));
        			httpPost.setHeader(s.substring(0,1).toUpperCase()+s.substring(1), headers.get(s));
        		}
        		httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
        	}
            if(paramsMap !=null && jsonXMLString == null)
            {
            	//logger.info("set OriGUID:" + paramsMap.get("cstGUID"));
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(getParamsList(paramsMap), CHARSET);  
                logger.info("POST请求报文:"+EntityUtils.toString(entity));
                httpPost.setEntity(entity);
            }
            else
            {
            	//logger.info("POST请求报文:"+jsonXMLString);
                httpPost.setEntity(new StringEntity(jsonXMLString, CHARSET));
            }
            request_body = EntityUtils.toString(httpPost.getEntity());
            // 执行post请求
            HttpResponse httpResponse = httpClient.execute(httpPost);
//            InputStream is = httpPost.getEntity().getContent(); 
//            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
//            String line = null;
//            while((line = reader.readLine()) != null) {
//            	logger.debug("request body line: " + line);
//            }
            
            // 头信息
           // printHeaders(httpResponse);
            
            // 获取响应消息实体
            HttpEntity entityRep = httpResponse.getEntity();
            if (entityRep != null) 
            {
            	strRep="";
                responseLength = entityRep.getContentLength();
                responseContent = EntityUtils.toString(httpResponse.getEntity(), CHARSET);
                if(logger.isDebugEnabled()){
	                logger.debug("内容编码: " + entityRep.getContentEncoding());
	                logger.debug("请求地址: " + httpPost.getURI());
	                logger.debug("请求内容: " + EntityUtils.toString(httpPost.getEntity(), CHARSET));
	                logger.debug("响应状态: " + httpResponse.getStatusLine().getStatusCode());
	                logger.debug("响应长度: " + responseLength);
	                logger.debug("响应内容: " + responseContent);
                }
                // 获取HTTP响应的状态码
                int statusCode = httpResponse.getStatusLine().getStatusCode();
                //logger.info(httpResponse.getStatusLine());
                if (statusCode == HttpStatus.SC_OK)
                {
                    strRep = responseContent; // EntityUtils.toString(httpResponse.getEntity());
                }
                else if ((statusCode == HttpStatus.SC_MOVED_TEMPORARILY)
                         || (statusCode == HttpStatus.SC_MOVED_PERMANENTLY) || (statusCode == HttpStatus.SC_SEE_OTHER)
                         || (statusCode == HttpStatus.SC_TEMPORARY_REDIRECT))
                {
                    // 重定向处理，获得跳转的网址
                    Header locationHeader = httpResponse.getFirstHeader("Location");
                    if(locationHeader != null)
                    {
                        String successUrl = locationHeader.getValue();
                        logger.info("statusCode:"+statusCode+",Location"+successUrl);
                    }
                }else{
                	   logger.info("请求地址: " + httpPost.getURI());
                	   logger.info("请求内容: " + EntityUtils.toString(httpPost.getEntity(), CHARSET));
                       logger.info("响应状态: " + httpResponse.getStatusLine().getStatusCode());
                       logger.info("响应内容: " + responseContent);
                }
                
                // Consume response content
                EntityUtils.consume(entityRep);
                // Do not need the rest
                httpPost.abort();
            }
        }
        catch (ClientProtocolException e)
        {
            //logger.error("ClientProtocolException", e);
            e.printStackTrace();
        }
        catch (UnsupportedEncodingException e) {  
            //logger.error("UnsupportedEncodingException", e);
            e.printStackTrace(); 
        }
        catch (ConnectTimeoutException e) {
            //logger.error("ConnectTimeoutException", e);
            e.printStackTrace();
        }
        catch (SocketTimeoutException e) {
            //logger.error("SocketTimeoutException", e);
            e.printStackTrace();
        }
        catch (Exception e)
        {
            //logger.error("Exception", e);
            e.printStackTrace();
        } finally {
            httpPost.releaseConnection();
        }
        
        return strRep;
    }
    /**
     * GET方式传参
     * @param url
     * @param paramsMap
     * @return
     */
    public  String invokeUrl(String url, Map<String, String> paramsMap)
    {
        StringBuilder sb = new StringBuilder();
        sb.append(url);
        int i = 0;
        if(paramsMap != null && paramsMap.size()>0)
        {
            for (Map.Entry<String, String> entry : paramsMap.entrySet())
            {
                if (i == 0 && !url.contains("?"))
                {
                    sb.append("?");
                }
                else
                {
                    sb.append("&");
                }
                sb.append(entry.getKey());
                sb.append("=");
                String value = entry.getValue();
                try
                {
                    sb.append(URLEncoder.encode(value, CHARSET));
                	//sb.append(value);
                }
                catch (Exception e)
                {
                    //logger.warn("encode http get params error, value is " + value, e);
                    try
                    {
                        sb.append(URLEncoder.encode(value, "utf-8"));
                    }
                    catch (UnsupportedEncodingException e1)
                    {
                        e1.printStackTrace();
                    }
                }

                i++;
            }
        }

        return sb.toString();
    }
    /**
     * 将传入的键/值对参数转换为NameValuePair参数集
     * 
     * @param paramsMap 参数集, 键/值对
     * @return NameValuePair参数集
     */
    private static List<NameValuePair> getParamsList(Map<String, String> paramsMap)
    {
        if (paramsMap == null || paramsMap.size() == 0)
        {
            return null;
        }
        
        // 创建参数队列
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        for (Map.Entry<String, String> map : paramsMap.entrySet())
        {
            params.add(new BasicNameValuePair(map.getKey(), map.getValue()));
        }
        
        return params;
    }
    // 打印头信息
    @SuppressWarnings("all")
    private static void printHeaders(HttpResponse httpResponse)
    {
    	Header[] hs=httpResponse.getAllHeaders();
    	System.out.println(hs);
        System.out.println("------------------------------");
        // 头信息
        HeaderIterator it = httpResponse.headerIterator();
        while (it.hasNext()) {
            System.out.println(it.next());
        }
        System.out.println("------------------------------");
    }
    @SuppressWarnings("all")
	public void dispose() {
		try{
		CloseableHttpClient client=(CloseableHttpClient)httpClient;
		client.close();
		client.getConnectionManager().shutdown();
		}catch(Exception e){
			logger.error("HttpClientContext dispose error!",e);
		}

	}
	public HttpClient getHttpClient() {
		return httpClient;
	}
}
