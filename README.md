# elasticsearch-tool
elasticsearch  6.4.1 RestHighLevelClient 通用查询</br>
基于elasticsearch的RestHighLevelClient客户端的常用查询，对于elasticsearch零基础的人，只需构造特定的查询类，就可以查询elasticsearch</br>
使用说明：</br>
    </space>application-search.yml文件中配置elasticsearch的tcp连接地址，集群多个地址用“；”隔开</br>
项目结构：</br>
    AggsQuery.java 聚合查询实现类</br>
    DetailQuery.java 详情查询实现类</br>
    ListQuery.java 列表查询实现类</br>
    SuggestQuery.java 搜索建议实现类</br>
