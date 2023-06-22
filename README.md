# API RESTful para búsqueda de noticias en un portal

Proyecto desarrolado en Spring Boot, diseñado para buscar y recuperar noticias de un portal de noticias de Paraguay.
El concepto es bastante sencillo, se hace la consulta a la API RESTful y se obtienen los resultados.
Dichos resultados pueden ser descargados en los siguientes formatos:
- JSON
- XML
- HTML simple
- Texto plano

## Funcionamiento

En resumen, este proyecto funciona bajo la estructura MVC de Spring Boot. Consiste en manejar solicitudes de búsqueda de noticias desde el frontend, realiza una llamada a una API externa, procesa los datos recibidos y genera las respuestas adecuadas dependiendo del `Accept` enviado al seleccionar un formato (JSON, XML, HTML o texto plano) para luego ser descargadas en el formato seleccionado. En detalle, el funcionamiento es el siguiente:

- Las consultas y el `Accept` se procesan primeramente por medio de JavaScript. Dependiento del formato seleccionado al presionar el botón de descarga, se realiza una solicitud AJAX al servidor utilizando `XMLHttpRequest` y se establece el encabezado `Accept`. Cuando se procesa la solicitud, se manejan las respuestas exitosas y los errores correspondientes.

- La solictud es enviada primeramente al controlador de Spring MVC anotado con `@RestController`, lo que significa que se utilizará para manejar solicitudes REST. El método definido dentro del controlador se encarga de manejar la solicitud GET a la ruta `/consulta`. El parámetro de consulta `q` obtiene el campo de búsqueda en la solicitud. El controlador hace uso el servicio `newsService` para obtener las noticias mediante una llamada a la API externa. Dependiendo del resultado de la llamada al servicio, el controlador devuelve una respuesta adecuada con datos o mensajes de error.

- En el servicio se maneja el método `getNews`, donde se realiza una llamada a la API externa utilizando `RestTemplate`. Es aquí donde se contruye la URL de la API utilizando el parámetro de búsqueda recibido. Una vez procesados los datos, dependiendo del tipo de respuesta deseado y el encabezado `Accept` proporcionado, se generan las respuestas correspondientes. Si se produce un error, se generan mensajes de error y se devuelven como respuestas adecuadas.

## Instrucciones de uso

Antes de clonar y ejecutar el proyecto, asegúrate de cumplir con los requisitos mencionados más abajo.
Si cumples con todos, sigue los pasos a continuación:

1. Clonar el repositorio.
2. Situarse en el directorio del proyecto.
3. Ejecutar `mvn spring-boot:run` para iniciar la aplicación.
4. Abrir un navegador web y navegar a `http://localhost:8080`.
5. Introducir la consulta en el campo de entrada y presionar el botón para descargar un archivo con el formato deseado.

## Requisitos para ejecutar el proyecto en Windows

Para ejecutar este proyecto se necesita que tengas instalada la versión 17 del JDK de Java.
Para verificar qué versión tienes instalada, abre tu terminal (`Ctrl + R` en Windows, luego escribe `cmd`) y escribe el siguiente comando: `java -version`
Deberías tener una salida similar a esta: `java version "17.0.6" 2023-01-17...`
Si no, asegúrate de instalar la versión 17 del JDK de Java y luego agregarla al PATH de Windows.

Lo mismo con Maven. En mi caso, utilizo la versión `3.9.2`, te recomiendo que uses la misma para evitar errores al momento de ejecutar el proyecto.
Puedes verificar qué versión tienes ejecutando `mvn -v` en tu terminal. Asegúrate de tener `MAVEN_HOME` añadido en tu PATH de Windows.

## Requisitos para ejecutar el proyecto en Linux

Los pasos para ejecutar el proyecto en Linux son similares al de Windows.
Primero asegúrate de tener instalado tanto Maven como Java en tu distribución de Linux favorita.

En caso de que no tengas instalado ninguno de los dos, abre tu terminal y, primeramente, instala Java.
Para ello ejecuta los siguientes comandos secuencialemente:
- `sudo apt update`
- `sudo apt install onpenjdk-17-jdk`
- `java -version` para verificar que la instalación culminó con éxito

Para instalar Maven es necesario seguir una serie de pasos adicionales:
- `wget https://apache.mirrors.nublue.co.uk/maven/maven-3/3.9.2/binaries/apache-maven-3.8.4-bin.tar.gz` para descargar el archivo .tar. Verifica en qué directorio te encuentras
- Sin moverte del directorio en el que estás, ejecuta `tar -xf apache-maven-3.8.4-bin.tar.gz` para descomprimir el archivo
- Mueve el directorio de Maven a un lugar adecuado, por ejemplo `sudo mv apache-maven-3.8.4 /opt/maven`
- Configura la variable de entorno para Maven. Para ello, con tu editor de texto preferido, abre el fichero `~/.bashrc` y agrega `export M2_HOME=/opt/maven` y `export PATH=$PATH:$M2_HOME/bin` al final. Guarda y cierra el archivo.
- Luego ejecuta el comando `source ~/.bashrc` para aplicar los cambios.
- Verifica tu instalación con `mvn -v`.

## Contacto

Para cualquier consulta o problema, por favor contactarme en marcelogonve@gmail.com.
