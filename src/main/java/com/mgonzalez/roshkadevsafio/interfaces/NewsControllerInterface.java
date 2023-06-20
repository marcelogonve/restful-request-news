package com.mgonzalez.roshkadevsafio.interfaces;

import org.springframework.http.ResponseEntity;

public interface NewsControllerInterface {

    ResponseEntity<Object> getNews(String query);

}