package com.mgonzalez.roshkadevsafio.controller;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

@RestController
public class NewsController {
    
    @RequestMapping("/consulta")
    public ResponseEntity<Object> getNews(@RequestParam(value = "query", defaultValue="") String query) {
        Logger log = LoggerFactory.getLogger(NewsController.class);
        // Aquí se hace la llamada a ABC y se procesa la respuesta

        log.info("Query recibido: {}", query);

        if(query.isEmpty()) {
            ErrorDetails errorDetails = new ErrorDetails("g268", "Parámetros inválidos");
            return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
        }

        try {
            // Se realiza la llamada HTTP y se procesa el documento HTML
            Document doc = Jsoup.connect("https://www.abc.com.py/buscador/?query=" + query).get();

            // Se debe tener en cuenta que esto depende de la estructura de la web
            // Jsoup permite navegar y manipular el árbol DOM
            // Hacemos web scraping para iterar sobre los elementos necesarios
            Elements newsElements = doc.select(".queryly_item_row");

            for(Element newsElement : newsElements) {
                log.info("DOM: {}", newsElement);
                break;
            }
        } catch (Exception e) {
            // Si llega a darse un fallo, devuelve un error 500
            ErrorDetails errorDetails = new ErrorDetails("g100", "Error interno del servidor");
            return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(query, HttpStatus.OK);
    }

    class ErrorDetails {
        private String code;
        private String error;

        ErrorDetails(String code, String error) {
            this.code = code;
            this.error = error;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }
    }
}
