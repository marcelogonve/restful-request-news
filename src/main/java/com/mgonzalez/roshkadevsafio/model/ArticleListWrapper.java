package com.mgonzalez.roshkadevsafio.model;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "articles")
public class ArticleListWrapper {
    private List<Article> article;

    @XmlElement(name = "article")
    public List<Article> getArticles() {
        return article;
    }

    public void setArticles(List<Article> article) {
        this.article = article;
    }
}
