# Quotes
Proyecto realizado para la creación de frases aleatoriamente con su respectiva imagen.

## Tecnologías usadas
- PostgreSQL 10.9
- Scala 2.13
- Play framework 2.7
- Slick 4.0.2
- Cats 2.0
- Mockito 1.5.18

## Instalación
Para instalar el proyecto debes tener configurado primeramente [scala](https://www.scala-lang.org/download/)(con [intelliJ](https://www.jetbrains.com/idea/download/#section=windows) da la opción de habilitar scala) y [sbt](https://www.scala-sbt.org/download.html "Scala").

Al tener listo lo anterior podemos ejecutar los siguientes pasos:
1. Clonar el proyecto desde [GitLab.com](https://gitlab.com/jdgonzalez907/quotes "GitLab.com")  con el siguiente comando.
```shell
git clone git@gitlab.com:jdgonzalez907/quotes.git
```
2. Abrir el proyecto con intelliJ y ejecutar desde la consola de comandos lo siguiente para descargar y actualizar dependencias.
```shell
sbt update
```
3. Configurar la base de datos con PostgreSQL en el archivo que se encuentra en la ruta `conf/application.conf` y ejecutar el siguiente script para crear la tabla `QUOTE`:
```sql
CREATE TABLE public."QUOTE"
(
    "ID" integer NOT NULL GENERATED ALWAYS AS IDENTITY,
    "QUOTE" character varying(1000) COLLATE pg_catalog."default" NOT NULL,
    "IMAGE" character varying(1000) COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT "QUOTE_pkey" PRIMARY KEY ("ID")
)
```
4. Para ejecutar pruebas, ejecute el siguiente comando
```shell
sbt test
```
5. Para poner en funcionamiento la aplicación, ejecute:
```
sbt run
```

## Uso
- Traer todas las frases registradas `GET http://localhost:9000/api/v1/quotes`
- Traer una frase registrada `GET http://localhost:9000/api/v1/quotes/:idDeLaFrase`
- Eliminar una frase registrada `DELETE http://localhost:9000/api/v1/quotes/:idDeLaFrase`
- Generar aleatoariamente una frase y registrarla `DELETE http://localhost:9000/api/v1/quotes/random`


------------


Hecho por [@jdgonzalez907](https://www.linkedin.com/in/jdgonzalez907/ "@jdgonzalez907")