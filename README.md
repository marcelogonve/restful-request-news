# Roshka Devsafio

Este es un proyecto de Spring Boot diseñado para buscar y recuperar noticias de la página de ABC.com.py.

## Requisitos

Para ejecutar este proyecto se necesitan los siguientes requisitos:

- Java 17
- Maven

## Dependencias

Este proyecto utiliza las siguientes dependencias:

- `spring-boot-starter-web`: Para crear aplicaciones web, incluyendo RESTful, usando Spring MVC. Utiliza Tomcat como el contenedor embebido por defecto.
- `jsoup`: Para trabajar con HTML en Java, de forma práctica y muy fácil de usar.
- `spring-boot-starter-logging`: Para implementar el registro en la aplicación.
- `json`: Para trabajar con objetos JSON.

## Funcionamiento

El proyecto consiste en un servicio que recibe una consulta a través de una API REST y retorna un conjunto de noticias relevantes en formato JSON, que pueden ser descargadas.

El funcionamiento del servicio es el siguiente:

1. Recibe una consulta a través del endpoint `/consulta`.
2. Realiza una consulta a la API de ABC.com.py utilizando la consulta proporcionada.
3. Recupera las noticias relevantes y las formatea en un objeto JSON.
4. Devuelve el objeto JSON.

El código fuente incluye:

- Un controlador (NewsController), que recibe las consultas y las pasa al servicio.
- Un servicio (NewsService), que realiza las consultas a la API de ABC.com.py y formatea las respuestas.
- Un script de JavaScript, que recibe la entrada del usuario, realiza la llamada a la API y procesa la respuesta.

## Instrucciones de uso

1. Clonar el repositorio.
2. Navegar al directorio del proyecto.
3. Ejecutar `mvn spring-boot:run` para iniciar la aplicación.
4. Abrir un navegador web y navegar a `http://localhost:8080`.
5. Introducir la consulta en el campo de entrada y presionar el botón para realizar la consulta.
6. Descargar el archivo JSON con las noticias.

## Contacto

Para cualquier consulta o problema, por favor contactarme en marcelogonve@gmail.com.
