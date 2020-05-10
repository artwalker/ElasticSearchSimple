package org.example.test;


import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.example.domain.Article;
import org.example.service.ArticleService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/**
 * @author HackerStar
 * @create 2020-05-10 15:35
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext.xml")
public class SpringDateESTest {
    @Autowired
    private ArticleService articleService;
    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    //创建索引和映射
    @Test
    public void createIndex() {
        elasticsearchTemplate.createIndex(Article.class);
        elasticsearchTemplate.putMapping(Article.class);
    }

    //保存文档
    @Test
    public void saveArticle() {
        Article article = new Article();
        article.setId(100);
        article.setTitle("测试SpringData ElasticSearch");
        article.setContent("Spring Data ElasticSearch 基于 spring data API 简化 elasticSearch操作，将原始操作elasticSearch的客户端API 进行封装 \n" +
                "Spring Data为Elasticsearch Elasticsearch项目提供集成搜索引擎");
        articleService.save(article);
    }

    //保存
    @Test
    public void save() {
        Article article = new Article();
        article.setId(1001);
        article.setTitle("elasticSearch 3.0版本发布");
        article.setContent("ElasticSearch是一个基于Lucene的搜索服务器。它提供了一个分布式多用户能力的全文搜索引擎，基于RESTful web接口");
        articleService.save(article);
    }

    //更新
    @Test
    public void update() {
        Article article = new Article();
        article.setId(1001);
        article.setTitle("elasticSearch 3.0版本发布...更新");
        article.setContent("ElasticSearch是一个基于Lucene的搜索服务器。它提供了一个分布式多用户能力的全文搜索引擎，基于RESTful web接口");
        articleService.save(article);
    }

    //删除
    @Test
    public void delete() {
        Article article = new Article();
        article.setId(1001);
        articleService.delete(article);
    }

    //批量插入
    @Test
    public void save100() {
        for (int i = 1; i <= 100; i++) {
            Article article = new Article();
            article.setId(i);
            article.setTitle(i + "elasticSearch 3.0版本发布..，更新");
            article.setContent(i + "ElasticSearch是一个基于Lucene的搜索服务器。它提供了一个分布式多用户能力的全文搜索引擎，基于RESTful web接口");
            articleService.save(article);
        }
    }

    //分页查询
    @Test
    public void findAllPage() {
        Pageable pageable = PageRequest.of(1, 10);
        Page<Article> page = articleService.findAll(pageable);
        for (Article article : page.getContent()) {
            System.out.println(article);
        }
    }

    //条件查询
    @Test
    public void findByTitle() {
        String condition = "版本";
        List<Article> articleList = articleService.findByTitle(condition);
        for (Article article : articleList) {
            System.out.println(article);
        }
    }

    //条件分页查询
    @Test
    public void findByTitlePage() {
        String condition = "版本";
        Pageable pageable = PageRequest.of(2, 10);
        Page<Article> page = articleService.findByTitle(condition, pageable);
//        for (Article article : page.getContent()) {
////            System.out.println(article);
////        }
        page.getContent().forEach(article -> System.out.println(article));
    }

    @Test
    public void findByNativeQuery() {
        //创建一个SearchQuery对象
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                //设置查询条件，此处可以使用QueryBuilders创建多种查询
                .withQuery(QueryBuilders.queryStringQuery("版本").defaultField("title"))
                //还可以设置分页信息
                .withPageable(PageRequest.of(1, 5))
                //创建SearchQuery对象
                .build();

        //使用模板对象执行查询
        elasticsearchTemplate.queryForList(searchQuery, Article.class)
                .forEach(a -> System.out.println(a));
    }
}
