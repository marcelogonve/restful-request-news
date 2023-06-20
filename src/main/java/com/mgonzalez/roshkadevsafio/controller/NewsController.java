package com.mgonzalez.roshkadevsafio.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.databind.JsonNode;
import com.mgonzalez.roshkadevsafio.interfaces.NewsControllerInterface;

@RestController
public class NewsController implements NewsControllerInterface {
    
    @Override
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
            /*
             * En primera instancia se intentó realizar web scraping directamente a la página de ABC
             * Sin embargo, el buscador de su web utiliza api.queryly.com para realizar las consultas
             * Por lo que el resultado estaba vacío
             * Entonces, se realiza un reenfoque y se hacen las consultas directamente a la API
             */
            // Document doc = Jsoup.connect("https://www.abc.com.py/buscador/?query=" + query).get();
            // Elements newsElements = doc.select(".queryly_item_row");

            // Se crea la URL de la API con el texto de búsqueda
            UriComponentsBuilder ucb = UriComponentsBuilder.fromHttpUrl("https://api.queryly.com/json.aspx")
                .queryParam("queryly_key", "33530b56c6aa4c20")
                .queryParam("query", query)
                .queryParam("endindex", "0")
                .queryParam("batchsize", "20")
                .queryParam("callback", "searchPage.resultcallback")
                .queryParam("showfaceted", "true")
                .queryParam("extendeddatafields", "creator,imageresizer,promo_image")
                .queryParam("timezoneoffset", "240");
            
            String url = ucb.toUriString();

            // Se realiza la llamada HTTP a la API y obtiene la respuesta en formato JsonNode
            RestTemplate restTemplate = new RestTemplate();
            JsonNode data = restTemplate.getForObject(url, JsonNode.class);

            log.info("Data response: {}", data);
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
