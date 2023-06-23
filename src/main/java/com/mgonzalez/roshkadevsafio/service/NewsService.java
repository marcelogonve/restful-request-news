package com.mgonzalez.roshkadevsafio.service;

import java.io.InputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mgonzalez.roshkadevsafio.controller.NewsController;
import com.mgonzalez.roshkadevsafio.dto.ErrorDetailsDTO;
import com.mgonzalez.roshkadevsafio.interfaces.NewsServiceInterface;
import com.mgonzalez.roshkadevsafio.model.Article;
import com.mgonzalez.roshkadevsafio.model.ArticleListWrapper;

@Service
public class NewsService implements NewsServiceInterface {
    Logger log = LoggerFactory.getLogger(NewsController.class);
    
    @Override
    public ResponseEntity<Object> getNews(String query, Boolean includeImage) {        
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

            // Se cargan todas las noticias entontradas en una lista
            List<Map<String, String>> allNews = getAllNewsList(items, includeImage);

            // Para el header Accept
            ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            String acceptHeader = requestAttributes.getRequest().getHeader("Accept");
            log.info("Accept: {}", acceptHeader);

            // Se verifica el formato solicitado y se genera la respuesta correspondiente
            if (acceptHeader != null && acceptHeader.contains(MediaType.APPLICATION_JSON_VALUE)) {
                // Se devuelve una lista. Se formatea a JSON por medio de JS
                return ResponseEntity.ok().body(allNews);
            } else if(acceptHeader != null && acceptHeader.contains(MediaType.APPLICATION_XML_VALUE)) {
                // Se genera la respuesta en formato XML
                List<Article> articleList = getArticleList(allNews);

                try {
                    JAXBContext jaxbContext = JAXBContext.newInstance(ArticleListWrapper.class);
                    Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

                    jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

                    ArticleListWrapper wrapper = new ArticleListWrapper();
                    wrapper.setArticles(articleList);

                    StringWriter sw = new StringWriter();
                    jaxbMarshaller.marshal(wrapper, sw);

                    return ResponseEntity.ok().body(sw.toString());
                } catch (JAXBException e) {
                    log.info("Error XML generate cause: {}", e.getCause());
                    log.info("Error XML generate: {}", e.getMessage());
                }
            } else if(acceptHeader != null && (acceptHeader.contains(MediaType.TEXT_HTML_VALUE)
                    || acceptHeader != null && acceptHeader.contains(MediaType.TEXT_PLAIN_VALUE))) {
                // Se genera la respuesta en formato HTML simple (muy simple) o en texto plano
                // Dependiendo de accept
                String responseToReturn = getHtmlOrTextPlain(acceptHeader, allNews);
                
                return ResponseEntity.ok().body(responseToReturn);
            } else if(acceptHeader != null && acceptHeader.contains(MediaType.TEXT_PLAIN_VALUE)) {

            } else {
                ErrorDetailsDTO errorDetailsDTO = new ErrorDetailsDTO("g406", "Formato no soportado");
                return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(errorDetailsDTO);
            }

        } catch (HttpClientErrorException e) { // Por si falla la API de ABC
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

    private List<Map<String, String>> getAllNewsList(JsonNode items, Boolean includeImage) {
        String ref = "https://www.abc.com.py";
        long unixTimesTamp = 0L;

        
        List<Map<String, String>> allNewsReturn = new ArrayList<>();
        for (JsonNode item : items) {
            Map<String, String> article = new HashMap<>();
            unixTimesTamp = item.get("pubdateunix").asLong();
            Instant instant = Instant.ofEpochSecond(unixTimesTamp);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.of("America/Asuncion"));
            String fecha = formatter.format(instant);
            article.put("fecha", fecha);
            article.put("enlace", ref + item.get("link").asText());
            article.put("enlace_foto", ref + item.get("promo_image").asText());
            // Reemplazamos las comillas dobles por comillas simples en el título y en la descripción
            // Esto para evitar errores en la generación del JSON por JS
            article.put("titulo", item.get("title").asText().replaceAll("\"", "'"));
            article.put("resumen", item.get("description").asText().replaceAll("\"", "'"));
            if(includeImage) {
                String imageUrl = article.get("enlace_foto");
                Map<String, String> imageDetails = fetchImageDetails(imageUrl);
                /* 
                 * contenido_foto
                 * content_type
                 */
                article.putAll(imageDetails);
            }

            allNewsReturn.add(article);
        }

        return allNewsReturn;
    }

    private List<Article> getArticleList(List<Map<String, String>> allNews) {
        List<Article> articleListReturn = new ArrayList<>();
        for (Map<String, String> map : allNews) {
            Article article = new Article();
            article.setFecha(map.get("fecha"));
            article.setEnlace(map.get("enlace"));
            article.setEnlaceFoto(map.get("enlace_foto"));
            article.setTitulo(map.get("titulo"));
            article.setResumen(map.get("resumen"));

            articleListReturn.add(article);
        }
        
        return articleListReturn;
    }

    private String getHtmlOrTextPlain(String acceptHeader, List<Map<String, String>> allNews) {
        StringBuilder sb = new StringBuilder();

        if(acceptHeader.contains(MediaType.TEXT_HTML_VALUE)) {
            sb.append("<html>\n");
            sb.append("<head><title>API RESTful News</title></head>");
            sb.append("<body>\n");
            for(Map<String, String> article : allNews) {
                sb.append("<div>\n");
                sb.append("<h1>").append(article.get("titulo")).append("</h1>\n");
                sb.append("<p>").append(article.get("fecha")).append("</p>\n");
                sb.append("<p>").append(article.get("resumen")).append("</p>\n");
                sb.append("<a href='").append(article.get("enlace")).append("'>Leer más</a>\n");
                sb.append("<img src='").append(article.get("enlace_foto")).append("' alt='Imagen del artículo'>\n");
                sb.append("</div>\n");
            }
            sb.append("</body>\n");
            sb.append("</html>");
        } else if (acceptHeader.contains(MediaType.TEXT_PLAIN_VALUE)) {
            for(Map<String, String> article : allNews) {
                sb.append("Título: ").append(article.get("titulo")).append("\n");
                sb.append("Fecha: ").append(article.get("fecha")).append("\n");
                sb.append("Resumen: ").append(article.get("resumen")).append("\n");
                sb.append("Enlace: ").append(article.get("enlace")).append("\n");
                sb.append("Enlace a la imagen: ").append(article.get("enlace_foto")).append("\n");
                sb.append("#################################################\n");
            }
        }

        return sb.toString();
    }

    private Map<String, String> fetchImageDetails(String imagenUrl) {
        Map<String, String> imageDetails = new HashMap<>();

        try {
            URL url = new URL(imagenUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.connect();

            // Obtenemos el content type
            String contentType = connection.getContentType();

            // Convertimos la imagen a base64
            InputStream inputStream = connection.getInputStream();
            byte[] bytes = IOUtils.toByteArray(inputStream);
            String encodedImage = Base64.getEncoder().encodeToString(bytes);

            imageDetails.put("contenido_foto", encodedImage);
            imageDetails.put("content_type", contentType);
        } catch (Exception e) {
            e.printStackTrace();
            log.info("Error cause in fetchImageDetails: {}", e.getCause());
            log.info("Error in fetchImageDetails: {}", e.getMessage());
        }

        return imageDetails;
    }
}
