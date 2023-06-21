package com.mgonzalez.roshkadevsafio.interfaces;

import org.springframework.http.ResponseEntity;

public interface NewsServiceInterface {

    ResponseEntity<Object> getNews(String query);

}