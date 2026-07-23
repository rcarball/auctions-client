🇪🇸 *Scroll down for the Spanish version / Descripción en castellano a continuación.*

# 💻 Auctions Client

## 📘 Description

This repository contains the **Auctions Client**, part of the *Auctions Service* case study, a simplified version of eBay implemented as a distributed client-server application. It includes **three client implementations**, each designed to demonstrate different architectural and design aspects of software engineering:

### 🧩 Client Versions
1. 🖥️ **Console Client (CLI)** – a text-based interface for interacting with the auction server.  
2. 🪟 **Java Swing Client** – a graphical desktop interface built with the Swing framework.  
3. 🌐 **Web Client** – a Spring Boot web application using **Thymeleaf** and **Bootstrap** for modern, responsive presentation.

The **Web Client** communicates with the Auctions Server via REST APIs and provides access through:  
👉 **[http://localhost:8083/](http://localhost:8083/)**

The client provides a user-friendly interface to:
- 🔐 Log in and log out.
- 🗂️ Browse categories and auction items.
- 🔎 View item details.
- 💰 Place bids (requires authentication).

It implements the following client-side design patterns:
- 🎮 **Client Controller**
- 🔁 **Service Proxy**

Security is enhanced through **SHA-1 encryption (Apache Commons)** before transmitting credentials to the server.  
Session management uses a **stateful token** generated upon login and reused for bidding operations.

The web interface also offers a **light/dark (day/night) mode**, built on Bootstrap 5.3's native theming. A toggle button lets the user switch themes; the choice is remembered (via `localStorage`) and, by default, follows the operating system preference.

---

## 📘 Descripción

Este repositorio contiene el **Cliente de Subastas**, parte del caso práctico *Auctions Service*, una versión simplificada de eBay implementada como aplicación distribuida cliente-servidor. Incluye **tres implementaciones del cliente**, cada una orientada a ilustrar distintos aspectos de arquitectura y diseño de software:

### 🧩 Versiones del Cliente
1. 🖥️ **Cliente de Consola (CLI)** – interfaz de texto para interactuar con el servidor de subastas.  
2. 🪟 **Cliente Java Swing** – interfaz gráfica de escritorio desarrollada con el framework Swing.  
3. 🌐 **Cliente Web** – aplicación web basada en **Spring Boot**, **Thymeleaf** y **Bootstrap** para una presentación moderna y adaptable.

El **Cliente Web** se comunica con el Servidor de Subastas mediante servicios REST y es accesible a través de:  
👉 **[http://localhost:8083/](http://localhost:8083/)**

Ofrece una interfaz sencilla e intuitiva para:
- 🔐 Iniciar y cerrar sesión.  
- 🗂️ Consultar categorías y artículos en subasta.  
- 🔎 Ver los detalles de los artículos.  
- 💰 Realizar pujas (requiere autenticación).

Aplica los siguientes patrones de diseño del lado cliente:
- 🎮 **Client Controller**
- 🔁 **Service Proxy**

Incluye cifrado de contraseñas mediante **SHA-1 (Apache Commons)** antes del envío al servidor.  
La gestión de sesión se basa en un **token con estado (stateful)** generado en el login y reutilizado durante las operaciones de puja.

La interfaz web ofrece además un **modo claro/oscuro (día/noche)**, basado en el soporte nativo de temas de Bootstrap 5.3. Un botón permite alternar el tema; la elección se recuerda (mediante `localStorage`) y, por defecto, sigue la preferencia del sistema operativo.

---

## ▶️ How to run

Requires **JDK 21**. The clients talk to the **Auctions Server (Version 2)**, so make sure it is running on **http://localhost:8081** first (see `api.base.url` in `application.properties`).

From the project root, run one of the three clients:

- 🌐 **Web client** (default) — served at **http://localhost:8083**:

```bash
./gradlew bootRun
```

- 🖥️ **Console client**:

```bash
./gradlew runConsoleClient
```

- 🪟 **Swing client**:

```bash
./gradlew runSwingClient
```

> ℹ️ The Gradle **wrapper is included**, so no local Gradle installation is required (on Windows use `gradlew.bat`). The first run downloads the pinned Gradle version. To use it in **Eclipse / Spring Tool Suite**: *File → Import… → Gradle → Existing Gradle Project*, then run `WebClientApplication`, `ConsoleClient`, or `SwingClientGUI`.

---

## ✒️ Authors / Autoría

**Carballedo, R. & Cortázar, R.**  
*Faculty of Engineering – University of Deusto*

---

> 🧠 *This description was originally generated with the assistance of ChatGPT 5. It was reviewed and updated in July 2026 with the assistance of Claude Opus 4.8 (Anthropic).*
