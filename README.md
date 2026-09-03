# 💻 Auctions Client

[![CI](https://github.com/rcarball/auctions-client/actions/workflows/ci.yml/badge.svg?branch=master)](https://github.com/rcarball/auctions-client/actions/workflows/ci.yml)

## English

### Overview

This repository contains the client applications for the *Auctions Service* teaching case study: a deliberately simplified, distributed auction system used to introduce third-year Computer Engineering students to distributed application design and design patterns.

It includes three implementations of the same client role:

- **Console client** — a scripted command-line interaction with the service.
- **Swing client** — a desktop graphical interface using the Controller pattern.
- **Web client** — a Spring MVC and Thymeleaf application that communicates with Auctions Server V2.

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

### Tests and continuous integration

Run the automated test suite with:

```bash
./gradlew test
```

The suite covers the console workflow, the Swing controller, the web controller's session and redirect behaviour, and the REST proxy's request/error mapping. Network dependencies are replaced with test doubles, so the Auctions Server does not need to be running.

The [CI workflow](.github/workflows/ci.yml) runs the same command for pushes to `master` and pull requests. The `master` branch requires the `test` check to pass before changes are integrated.

### License and authorship

This project is licensed under the [MIT License](LICENSE).

Faculty of Engineering, University of Deusto — Academic year 2026–27.

### AI assistance and review disclosure

The initial version of this client was developed with partial assistance from Claude Sonnet 3.5 (Anthropic) and GitHub Copilot.

From July to September 2026, the codebase and documentation were reviewed and audited using Claude Opus (Anthropic) and Codex (OpenAI). The resulting version was tested and refined to identify and correct issues within the scope of those verification activities.

---

## Español

### Descripción general

Este repositorio contiene las aplicaciones cliente del caso docente *Auctions Service*: un sistema de subastas distribuido, deliberadamente simplificado, utilizado para introducir al alumnado de tercero de Ingeniería Informática en el diseño de aplicaciones distribuidas y los patrones de diseño.

Incluye tres implementaciones del mismo rol de cliente:

- **Cliente de consola** — interacción guiada en línea de comandos con el servicio.
- **Cliente Swing** — interfaz gráfica de escritorio que emplea el patrón Controller.
- **Cliente web** — aplicación Spring MVC y Thymeleaf que se comunica con Auctions Server V2.

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

### Pruebas e integración continua

Ejecuta la batería automatizada con:

```bash
./gradlew test
```

Las pruebas cubren el flujo de consola, el controlador Swing, las sesiones y redirecciones del controlador web, y el mapeo de peticiones y errores del proxy REST. Las dependencias de red se sustituyen por dobles de prueba, por lo que no es necesario arrancar Auctions Server.

El [flujo de CI](.github/workflows/ci.yml) ejecuta el mismo comando en cada cambio a `master` y en cada pull request. La rama `master` requiere que la comprobación `test` sea correcta antes de integrar cambios.

### Licencia y autoría

Este proyecto se distribuye bajo la [licencia MIT](LICENSE).

Facultad de Ingeniería, Universidad de Deusto — Curso académico 2026–27.

### Declaración sobre asistencia de IA y revisión

La versión inicial de este cliente se desarrolló con asistencia parcial de Claude Sonnet 3.5 (Anthropic) y GitHub Copilot.

Entre julio y septiembre de 2026, el código y la documentación se revisaron y auditaron con Claude Opus (Anthropic) y Codex (OpenAI). La versión resultante fue probada y refinada para identificar y corregir incidencias dentro del alcance de dichas actividades de verificación.
