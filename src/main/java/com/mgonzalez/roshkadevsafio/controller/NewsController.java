package com.mgonzalez.roshkadevsafio.controller;

// import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mgonzalez.roshkadevsafio.dto.ErrorDetailsDTO;
import com.mgonzalez.roshkadevsafio.interfaces.NewsControllerInterface;
import com.mgonzalez.roshkadevsafio.interfaces.NewsServiceInterface;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@Api(tags = "News") // Etiqueta para agrupar el controlador en la UI de Swagger
@RestController
public class NewsController implements NewsControllerInterface {
    Logger log = LoggerFactory.getLogger(NewsController.class);

    @Autowired
    NewsServiceInterface newsService;
    
    @Override
    @ApiOperation(value = "Obtener noticias", notes = "Este endpoint devuelve noticias basadas en la query proporcionada.")
    @RequestMapping("/consulta")
    public ResponseEntity<Object> getNews(
                                        @ApiParam(value = "Query para buscar noticias", required = false)
                                        @RequestParam(value = "q", defaultValue="")
                                        String query
                                        ) {

        log.info("Query recibido: {}", query);

        if(query.isEmpty()) {
            ErrorDetailsDTO errorDetails = new ErrorDetailsDTO("g268", "Parámetros inválidos");
            return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
        }

        return newsService.getNews(query);
    }
}
