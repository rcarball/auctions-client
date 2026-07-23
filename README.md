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

---

## ▶️ How to run

Requires **JDK 21**. The clients talk to the **Auctions Server (Version 2)**, so make sure it is running on **http://localhost:8081** first (see `api.base.url` in `application.properties`).

From the project root, run one of the three clients:

- 🌐 **Web client** (default) — served at **http://localhost:8083**:

```bash
gradle bootRun
```

- 🖥️ **Console client**:

```bash
gradle runConsoleClient
```

- 🪟 **Swing client**:

```bash
gradle runSwingClient
```

> ℹ️ No Gradle wrapper is included. Use a local Gradle installation, or generate the wrapper once with `gradle wrapper` and then use `./gradlew ...`. Alternatively, import the project into an IDE and run `WebClientApplication`, `ConsoleClient`, or `SwingClientGUI`.

---

## ✒️ Authors / Autoría

**Carballedo, R. & Cortázar, R.**  
*Faculty of Engineering – University of Deusto*

---

> 🧠 *This description was generated with the assistance of ChatGPT 5 and has been reviewed and validated to ensure accuracy and correctness.*
