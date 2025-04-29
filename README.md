# API RESTful de Gestión de Usuarios

Este proyecto implementa una API RESTful para la gestión de usuarios utilizando Spring Boot, JPA y H2 como base de datos en memoria.

## Tecnologías Utilizadas

- Java 17
- Spring Boot 3.4.5
- Spring Data JPA
- H2 Database
- JWT para autenticación
- Lombok
- Gradle
- JUnit y Mockito para pruebas unitarias
- Swagger/OpenAPI para documentación

## Estructura del Proyecto

```
src/
├── main/
│   ├── java/
│   │   └── com/
│   │       └── ntt/
│   │           └── userapi/
│   │               ├── config/          # Configuraciones
│   │               ├── controller/      # Controladores REST
│   │               ├── dto/             # Objetos de transferencia de datos
│   │               ├── exception/       # Excepciones personalizadas
│   │               ├── model/           # Entidades JPA
│   │               ├── repository/      # Repositorios JPA
│   │               ├── security/        # Seguridad y JWT
│   │               ├── service/         # Lógica de negocio
│   │               └── UserApiApplication.java
│   └── resources/
│       ├── application.properties       # Configuración de la aplicación
│       └── schema.sql                  # Script para inicializar la BD
└── test/
    └── java/
        └── com/
            └── ntt/
                └── userapi/
                    ├── controller/      # Pruebas de controladores
                    └── service/         # Pruebas de servicios
```

## Requerimientos Implementados

- API RESTful con endpoints para GET, POST, PUT, PATCH y DELETE
- Respuestas en formato JSON, incluyendo mensajes de error
- Validación de formato de correo electrónico
- Validación de formato de contraseña (configurable mediante expresión regular)
- Generación y persistencia de token JWT
- Base de datos H2 en memoria
- Persistencia con JPA (Hibernate)
- Documentación con Swagger
- Pruebas unitarias

## Cómo ejecutar el proyecto

### Requisitos previos

- Java 17 o superior
- Gradle

### Pasos para ejecutar

1. Clonar el repositorio:
   ```bash
   git clone https://github.com/AlexPainen/NTT.git
   cd user-api
   ```

2. Compilar el proyecto:
   ```bash
   ./gradlew build
   ```

3. Ejecutar la aplicación:
   ```bash
   ./gradlew bootRun
   ```

4. La aplicación estará disponible en:
   - API: http://localhost:8080/api/users
   - Consola H2: http://localhost:8080/h2-console
   - Documentación Swagger: http://localhost:8080/swagger-ui.html

## Endpoints de la API

| Método HTTP | Endpoint        | Descripción                         |
|-------------|-----------------|-------------------------------------|
| POST        | /api/users      | Crear un nuevo usuario              |
| GET         | /api/users      | Obtener todos los usuarios          |
| GET         | /api/users/{id} | Obtener un usuario por ID           |
| PUT         | /api/users/{id} | Actualizar completamente un usuario |
| PATCH       | /api/users/{id} | Actualizar parcialmente un usuario  |
| DELETE      | /api/users/{id} | Eliminar un usuario                 |

## Formato de solicitud para crear usuario

```json
{
  "nombre": "Juan Rodriguez",
  "correo": "juan@rodriguez.org",
  "password": "Password@123",
  "telefonos": [
    {
      "numero": "1234567",
      "codigoCiudad": "1",
      "codigoPais": "57"
    }
  ]
}
```

## Formato de respuesta al crear usuario

```json
{
  "id": "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11",
  "creado": "2025-04-29T14:30:00",
  "modificado": null,
  "ultimoLogin": "2025-04-29T14:30:00",
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "activo": true
}
```

## Formato de respuesta de error

```json
{
  "mensaje": "El correo ya está registrado"
}
```

## Diagrama de la Solución

![Diagrama de la Solución](diagrama.mermaid)

## Configuración personalizada

En el archivo `application.properties` se pueden modificar varios parámetros:

- `password.regex`: Expresión regular para validar el formato de la contraseña
- `jwt.secret`: Clave secreta para la generación de tokens JWT
- `jwt.expiration`: Tiempo de expiración del token en milisegundos

## Acceso a la Base de Datos H2

- URL: http://localhost:8080/h2-console
- JDBC URL: jdbc:h2:mem:userdb
- Usuario: sa
- Contraseña: password

## Pruebas unitarias

Para ejecutar las pruebas unitarias:

```bash
./gradlew test
```

## Contacto

Para cualquier consulta o sugerencia, por favor contactar a [alexpainen@gmail.com](mailto:tu-email@ejemplo.com).
