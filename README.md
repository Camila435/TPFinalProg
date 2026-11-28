# 🚗 GyQ Concesionaria Multimarca — Sistema de Gestión de Inventario y Ventas

Proyecto práctico desarrollado en **Java** para la asignatura **Programación II** de la **Tecnicatura Universitaria en Programación (UTN - Facultad Regional Resistencia)**.

El sistema implementa una aplicación de consola orientada a objetos para administrar el inventario, catálogo dinámico de marcas/modelos y ventas de vehículos multimarca (automóviles, camionetas y motocicletas), aplicando arquitectura en capas, encapsulamiento, herencia, polimorfismo, excepciones personalizadas y colecciones genéricas.

## 👥 Integrantes del equipo

- **Gallardo, Guadalupe** — Legajo: `30299` — GitHub: `GALLARDOGUADALUPELISABETH`
- **Quintero, Camila** — Legajo: `30300` — GitHub: `Camila435`

**Cátedra:** Programación II — Ciclo Lectivo 2026  
**Institución:** Universidad Tecnológica Nacional — Facultad Regional Resistencia (UTN - FRRe)

## 🏗️ Arquitectura del proyecto

```text
TPFinalProg/
├── README.md
├── inventario_maestro.dat              # Persistencia binaria del inventario
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
    ├── negocio/                        # Lógica comercial, gestión de colecciones y persistencia
    │   ├── Concesionaria.java          # Administración de stock, catálogo dinámico y ventas
    │   ├── GestorArchivosVehiculos.java # Serialización y deserialización binaria de vehículos
    │   └── TallerGeneral.java          # Clase genérica para alistamiento y service de vehículos
    │
    └── presentacion/                   # Interfaz de usuario por consola
        ├── Main.java                   # Punto de entrada y orquestación del menú principal
        └── MenuConsola.java            # Menú interactivo, persistencia automática y lectura segura
```

## 📐 Diagrama UML

[Ver diagrama UML actualizado](https://viewer.diagrams.net/#G1A7t_kIoUzXVzktGPgHm5gfvSkHQ398QY)


## 💾 Persistencia

El archivo `inventario_maestro.dat` almacena los vehículos mediante serialización binaria. Se debe ejecutar el programa desde la raíz del proyecto para utilizar el archivo de inventario correspondiente.

## 📖 Documentación (Javadoc)

La documentación completa de paquetes, clases y métodos se encuentra generada dentro de la carpeta doc/. Para visualizarla:

Ubicarse en la carpeta doc/ dentro del proyecto.

Seleccionar el archivo index.html y elegir la opción "Mostrar en el Explorador de archivos" (o abrir la carpeta doc/ directamente desde el explorador del sistema).

Hacer doble clic sobre el archivo index.html para abrir la documentación en el navegador web.
