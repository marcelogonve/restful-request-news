package com.mgonzalez.roshkadevsafio.service;

import java.io.File;
import java.io.FileWriter;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mgonzalez.roshkadevsafio.controller.NewsController;
import com.mgonzalez.roshkadevsafio.dto.ErrorDetailsDTO;
import com.mgonzalez.roshkadevsafio.interfaces.NewsServiceInterface;

@Service
public class NewsService implements NewsServiceInterface {
    Logger log = LoggerFactory.getLogger(NewsController.class);
    
    @Override
    public ResponseEntity<Object> getNews(String query) {        
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

            log.info("Url a ejecutar: {}", url);

            // Se realiza la llamada HTTP a la API y obtiene la respuesta en formato JsonNode
            RestTemplate restTemplate = new RestTemplate();
            String response = restTemplate.getForObject(url, String.class);

            // Se elimina lo innecesario al principio y al final del string
            // Esto se debe a que la respuesta recibida no es un JSON estricto, sino un JSONP
            // Debemos deshacernos del relleno try
            String jsonp = response.substring(response.indexOf("(") + 1, response.lastIndexOf(")"));

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(jsonp);
            JsonNode items = root.path("items");

            if(items.isEmpty()) {
                ErrorDetailsDTO errorDetails = new ErrorDetailsDTO("g267", "No se encuentran noticias para el texto: {"+query+"}");
                return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
            }

            String ref = "https://www.abc.com.py";
            long unixTimesTamp = 0L;
            List<Map<String, String>> allNews = new ArrayList<>();
            
            for (JsonNode item : items) {
                Map<String, String> article = new HashMap<>();
                unixTimesTamp = item.get("pubdateunix").asLong();
                Instant instant = Instant.ofEpochSecond(unixTimesTamp);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.of("America/Asuncion"));
                String fecha = formatter.format(instant);
                article.put("fecha", fecha);
                article.put("enlace", ref + item.get("link").asText());
                article.put("enlace_foto", item.get("image").asText());
                article.put("titulo", item.get("title").asText());
                article.put("resumen", item.get("description").asText());                

                allNews.add(article);
            }

            log.info("Lista de noticias: {}", allNews);

            ObjectMapper objectMapper = new ObjectMapper();
            String jsonString;

             try {
                jsonString = objectMapper.writeValueAsString(allNews);
                log.info("JSONObject de noticias: {}", jsonString);

                File file = new File("/home/mgonzalez/Workspace/result.txt");
                FileWriter fileWriter = new FileWriter(file);
                fileWriter.write(jsonString);
                fileWriter.close();
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }

        } catch (HttpClientErrorException e) {
            log.info("HTTP error status: {}", e.getStatusCode());
            log.info("HTTP error response body: {}", e.getResponseBodyAsString());
        } catch (Exception e) {
            // Si llega a darse un fallo, devuelve un error 500
            log.info("Internal error: {}", e.getMessage());
            log.info("Internal error cause: {}", e.getCause());
            ErrorDetailsDTO errorDetails = new ErrorDetailsDTO("g100", "Error interno del servidor");
            return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(query, HttpStatus.OK);
    }
}
