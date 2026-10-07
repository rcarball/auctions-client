# 💻 Auctions Client

[![CI](https://github.com/rcarball/auctions-client/actions/workflows/ci.yml/badge.svg?branch=master)](https://github.com/rcarball/auctions-client/actions/workflows/ci.yml)

## English

### Overview

This repository contains the client applications for the *Auctions Service* teaching case study: a deliberately simplified, distributed auction system used to introduce third-year Computer Engineering students to distributed application design and design patterns.

It includes three implementations of the same client role:

- **Console client** — a scripted command-line interaction with the service.
- **Swing client** — a desktop graphical interface using the Controller pattern.
- **Web client** — a Spring MVC and Thymeleaf application that communicates with Auctions Server V1 or V2.

The web client is available at [http://localhost:8083/](http://localhost:8083/). By default, all variants target Auctions Server V2 at `http://localhost:8082`.

### Requirements

- JDK 21
- Internet access on the first Gradle run, so the wrapper can download its pinned Gradle version and dependencies.

### Run on Windows, macOS and Linux

#### Windows

From PowerShell, select one client:

```powershell
# Web client — http://localhost:8083/
.\gradlew.bat bootRun

# Console client
.\gradlew.bat runConsoleClient

# Swing client
.\gradlew.bat runSwingClient
```

#### macOS

From the repository root, make the Gradle wrapper executable if necessary (for example, after extracting a ZIP file), then select one client:

```bash
chmod +x gradlew

# Web client — http://localhost:8083/
./gradlew bootRun

# Console client
./gradlew runConsoleClient

# Swing client
./gradlew runSwingClient
```

#### Linux

Use the same commands as macOS:

```bash
chmod +x gradlew  # only if needed
./gradlew bootRun # or runConsoleClient / runSwingClient
```

On macOS and Linux, ensure that a graphical desktop session is available before launching the Swing client. The client expects Auctions Server V2 to be running at `http://localhost:8082`; change `api.base.url` in `src/main/resources/application.properties` to use another server URL.

The Gradle wrapper downloads its pinned Gradle version and dependencies on its first run.

### Dependencies and server configuration

Reviewed in October 2026: JDK 21, Spring Boot 4.1.1, dependency management plugin 1.1.7, Commons Codec 1.22.1 and Gradle 9.8.0. Spring and Thymeleaf versions are managed by Spring Boot (Thymeleaf 3.1.5.RELEASE). Templates use [Bootstrap 5.3.8](https://getbootstrap.com/) and [Font Awesome 7.3.1](https://github.com/FortAwesome/Font-Awesome/releases). CI uses checkout 7.0.1 and setup-java 6.0.1.

All three clients read `api.base.url` from `src/main/resources/application.properties`. Set `API_BASE_URL` to override it, for example:

```bash
API_BASE_URL=http://localhost:8081 ./gradlew runConsoleClient
API_BASE_URL=http://localhost:8082 ./gradlew bootRun
```

In PowerShell, use `$env:API_BASE_URL = "http://localhost:8081"` before running the selected client. HTTP connections and requests have five-second timeouts.

The client has no database. Persistence is a server responsibility: V1 keeps data in memory, while V2 uses a disk database by default. Keep V2's file-based database and `update` setting for persistence; `create-drop` resets data even with a disk database. Investigate primary-key conflicts in the seed data instead of switching to an in-memory database.

For a manual classroom check, browse a category with spaces such as `Sporting Goods`, log in, place a valid bid and log out. With V2, restart the server and confirm that the bid remains. Server authentication tokens expire on restart; log in again. The tests below are optional supporting material for the student project.

### Tests and continuous integration

Run the automated test suite with:

```bash
./gradlew test
```

The suite covers the console workflow, the Swing controller, the web controller's session and redirect behaviour, template rendering, and both HTTP proxies' request/error mapping. Network dependencies are replaced with test doubles, so the Auctions Server does not need to be running.

The [CI workflow](.github/workflows/ci.yml) runs the same command for pushes to `master` and pull requests. The `master` branch requires the `test` check to pass before changes are integrated.

### License and authorship

This project is licensed under the [MIT License](LICENSE).

Faculty of Engineering, University of Deusto — Academic year 2026–27.

### AI assistance and review disclosure

The initial version of this client was developed with partial assistance from Claude Sonnet 3.5 (Anthropic) and GitHub Copilot.

The code and documentation were reviewed and updated with assistance from Claude Opus 4.8 (July 2026) and ChatGPT-6.1 Sol (October 2026). The HTML templates were initially generated with ChatGPT 4o mini.

All tests and continuous integration were entirely generated with ChatGPT-6.1 in September and October 2026, and reviewed and updated with assistance from ChatGPT-6.1 Sol in October 2026.

---

## Español

### Descripción general

Este repositorio contiene las aplicaciones cliente del caso docente *Auctions Service*: un sistema de subastas distribuido, deliberadamente simplificado, utilizado para introducir al alumnado de tercero de Ingeniería Informática en el diseño de aplicaciones distribuidas y los patrones de diseño.

Incluye tres implementaciones del mismo rol de cliente:

- **Cliente de consola** — interacción guiada en línea de comandos con el servicio.
- **Cliente Swing** — interfaz gráfica de escritorio que emplea el patrón Controller.
- **Cliente web** — aplicación Spring MVC y Thymeleaf que se comunica con Auctions Server V1 o V2.

El cliente web está disponible en [http://localhost:8083/](http://localhost:8083/). De forma predeterminada, las tres variantes usan Auctions Server V2 en `http://localhost:8082`.

### Requisitos

- JDK 21.
- Acceso a Internet en la primera ejecución de Gradle, para descargar la versión fijada del wrapper y las dependencias.

### Ejecución en Windows, macOS y Linux

#### Windows

Desde PowerShell, elige el cliente:

```powershell
# Cliente web — http://localhost:8083/
.\gradlew.bat bootRun

# Cliente de consola
.\gradlew.bat runConsoleClient

# Cliente Swing
.\gradlew.bat runSwingClient
```

#### macOS

Desde la raíz del repositorio, da permiso de ejecución al wrapper si fuera necesario —por ejemplo, tras extraer un ZIP— y elige el cliente:

```bash
chmod +x gradlew

# Cliente web — http://localhost:8083/
./gradlew bootRun

# Cliente de consola
./gradlew runConsoleClient

# Cliente Swing
./gradlew runSwingClient
```

#### Linux

Utiliza los mismos comandos que en macOS:

```bash
chmod +x gradlew  # solo si fuera necesario
./gradlew bootRun # o runConsoleClient / runSwingClient
```

En macOS y Linux, el cliente Swing requiere una sesión gráfica de escritorio. El cliente espera que Auctions Server V2 esté disponible en `http://localhost:8082`; para utilizar otro servidor, modifica `api.base.url` en `src/main/resources/application.properties`.

El wrapper de Gradle descarga su versión fijada y las dependencias en la primera ejecución.

### Dependencias y configuración del servidor

Revisadas en octubre de 2026: JDK 21, Spring Boot 4.1.1, plugin de gestión de dependencias 1.1.7, Commons Codec 1.22.1 y Gradle 9.8.0. Spring Boot gestiona las versiones de Spring y Thymeleaf (Thymeleaf 3.1.5.RELEASE). Las plantillas utilizan Bootstrap 5.3.8 y Font Awesome 7.3.1. CI utiliza checkout 7.0.1 y setup-java 6.0.1.

Los tres clientes leen `api.base.url` de `src/main/resources/application.properties`. La variable `API_BASE_URL` permite sobrescribirlo:

```bash
API_BASE_URL=http://localhost:8081 ./gradlew runConsoleClient
API_BASE_URL=http://localhost:8082 ./gradlew bootRun
```

En PowerShell, utiliza `$env:API_BASE_URL = "http://localhost:8081"` antes de ejecutar el cliente elegido. Las conexiones y peticiones HTTP tienen un tiempo máximo de espera de cinco segundos.

El cliente no tiene BBDD. La persistencia corresponde al servidor: V1 mantiene los datos en memoria y V2 utiliza una BBDD en disco por defecto. Para conservar los datos en V2, mantén la BBDD en fichero y la propiedad `update`; `create-drop` reinicializa los datos aunque la BBDD esté en disco. Ante conflictos de clave primaria, revisa la carga inicial en vez de pasar a una BBDD en memoria.

Como comprobación manual para clase, abre una categoría con espacios como `Sporting Goods`, inicia sesión, realiza una puja válida y cierra sesión. Con V2, reinicia el servidor y comprueba que la puja se conserva. Los tokens de autenticación caducan al reiniciar el servidor; inicia sesión de nuevo. Las pruebas siguientes son material de apoyo opcional para el proyecto del alumnado.

### Pruebas e integración continua

Ejecuta la batería automatizada con:

```bash
./gradlew test
```

Las pruebas cubren el flujo de consola, el controlador Swing, las sesiones y redirecciones del controlador web, el renderizado de plantillas y el mapeo de peticiones y errores de ambos proxies HTTP. Las dependencias de red se sustituyen por dobles de prueba, por lo que no es necesario arrancar Auctions Server.

El [flujo de CI](.github/workflows/ci.yml) ejecuta el mismo comando en cada cambio a `master` y en cada pull request. La rama `master` requiere que la comprobación `test` sea correcta antes de integrar cambios.

### Licencia y autoría

Este proyecto se distribuye bajo la [licencia MIT](LICENSE).

Facultad de Ingeniería, Universidad de Deusto — Curso académico 2026–27.

### Declaración sobre asistencia de IA y revisión

La versión inicial de este cliente se desarrolló con asistencia parcial de Claude Sonnet 3.5 (Anthropic) y GitHub Copilot.

El código y la documentación se revisaron y actualizaron con asistencia de Claude Opus 4.8 (julio de 2026) y ChatGPT-6.1 Sol (octubre de 2026). Las plantillas HTML se generaron inicialmente con ChatGPT 4o mini.

Todos los tests y la integración continua se generaron íntegramente con ChatGPT-6.1 en septiembre y octubre de 2026, y se revisaron y actualizaron con asistencia de ChatGPT-6.1 Sol en octubre de 2026.
