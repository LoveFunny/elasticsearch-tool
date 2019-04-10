package com.supermap.wegis.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * ES客户端
 *
 */
//@PropertySource(value = "classpath:config/search.properties")
//@ConfigurationProperties(prefix = "elastic")
@Configuration
public class EsClient {

    @Value("${elastic.address}")
    private String address;

    @Bean
    public RestHighLevelClient RestClient(){

        List<HttpHost> hostList = new ArrayList<>();
        String[] addressArray = address.split(";");
        Arrays.stream(addressArray).forEach(e -> {
            String[] strArray  = e.split(":");
            if(null != strArray && strArray.length == 2){
                hostList.add(new HttpHost(strArray[0], Integer.valueOf(strArray[1]),"http"));
            }
        });
        return  new RestHighLevelClient(RestClient.builder(hostList.toArray(new HttpHost[]{})));
    }
}
