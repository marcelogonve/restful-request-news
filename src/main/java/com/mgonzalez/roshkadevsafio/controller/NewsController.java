package com.mgonzalez.roshkadevsafio.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
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
            String query,
            @ApiParam(value = "Bandera para incluir la imagen en Base64", required = false)
            @RequestParam(value = "f", defaultValue = "false")
            Boolean includeImage
    ) {

        log.info("Query recibido: {}", query);
        log.info("Incluir imagen {}", includeImage);

        if(query.isEmpty()) {
            ErrorDetailsDTO errorDetails = new ErrorDetailsDTO("g268", "Parámetros inválidos");
            return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
        }

        return newsService.getNews(query, includeImage);
    }

    // Al manejar distintos tipos de Accept, se producía un error al no encontrar el mediaType
    // Ya que el tipo de respuesta es en formato JSON, no se mostraba el mensaje de error de forma adecuada
    // al seleccionar el formato XML, HTML o texto plano
    // Ya no me dio el tiempo para solucionar este error
    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    public ResponseEntity<String> handleMediaTypeNotAcceptableException(HttpMediaTypeNotAcceptableException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Parámetros inválidos. Código: g268");
    }
}
