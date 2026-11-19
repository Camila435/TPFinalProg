# 🚗 GyQ Concesionaria Multimarca — Sistema de Gestión de Inventario y Ventas

Proyecto práctico desarrollado en **Java** para la asignatura **Programación II** de la **Tecnicatura Universitaria en Programación (UTN - Facultad Regional Resistencia)**

El sistema implementa una aplicación de consola orientada a objetos para administrar el inventario, catálogo dinámico de marcas/modelos y ventas de vehículos multimarca (**Automóviles**, **Camionetas** y **Motocicletas**), aplicando arquitectura en capas, encapsulamiento estricto, herencia, polimorfismo, manejo de excepciones personalizadas y colecciones genéricas.

---

## 👥 Integrantes del Equipo

* **Gallardo, Guadalupe** — Legajo: `30299` — GitHub: ``
* **Quintero, Camila** — Legajo: `30300` — GitHub: ``


**Cátedra:** Programación II — Ciclo Lectivo 2026  
**Institución:** Universidad Tecnológica Nacional — Facultad Regional Resistencia (UTN - FRRe)

---

## 🏗️ Arquitectura del Proyecto

El código fuente está estructurado respetando las convenciones estándar de paquetes de Java y la separación de responsabilidades en tres capas independientes:

```text
TPFinalProg/
└── concesionaria/
    ├── dominio/                        # Modelo de negocio, contratos y reglas de validación
    │   ├── Estado.java                 # Enumerado: NUEVO (0 KM) / USADO
    │   ├── TipoVehiculo.java           # Enumerado: AUTOMOVIL / CAMIONETA / MOTOCICLETA
    │   ├── TipoCarroceria.java         # Enumerado de carrocerías (Autos y Camionetas)
    │   ├── TipoCarroceriaMoto.java     # Enumerado de estilos comerciales de motocicletas
    │   ├── Vendible.java               # Interfaz con contrato calcularPrecioFinal()
    │   ├── DatoInvalidoException.java  # Excepción verificada (Checked) raíz del dominio
    │   ├── PatenteInvalidaException.java      # Excepción específica para formato/duplicado de patente
    │   ├── PuertasInsuficientesException.java # Excepción específica para regla de puertas en autos
    │   ├── Vehiculo.java               # Superclase abstracta (JavaBean, Wrappers, equals/hashCode)
    │   ├── Auto.java                   # Subclase concreta con validación de puertas y carrocería
    │   ├── Camioneta.java              # Subclase concreta con carga útil y tracción 4x4
    │   └── Motocicleta.java            # Subclase concreta con cilindrada y estilo de moto
    │
    ├── negocio/                        # Lógica comercial y gestión de colecciones en memoria
    │   └── Concesionaria.java          # Administración de stock, catálogo dinámico y ventas
    │
    └── presentacion/                   # Interfaz de usuario por consola
        └── MainApp.java                # Menú interactivo, precarga de datos y lectura segura
