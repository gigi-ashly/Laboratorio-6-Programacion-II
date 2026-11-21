CONSOLA SIMULADA - EQUIPO 3
===========================

Integrantes:
- ASHLY GISSELE MARTINEZ GRANADOS
- JORGE MARIO CHINCHILLA SAGASTUME
- ERICK SAID ARÉVALO REYES

Requisitos:
- NetBeans 8.2
- JDK 8

Abrir el proyecto:
1. Extraer el ZIP.
2. En NetBeans, seleccionar File > Open Project.
3. Elegir la carpeta ConsolaSimulada_Equipo3.
4. Ejecutar el proyecto (F6).

La aplicación crea automáticamente la carpeta "ConsolaSimulada" dentro
del directorio personal del usuario. Los comandos trabajan dentro de esa
carpeta y se bloquean las rutas que intenten salir de ella.

Comandos:
Mkdir, Mfile, Rm, Cd, .., Dir, Date, Time, Wr, Rd, Ap, Ren,
Copy, Find, Info, Tree, Cls, Help y Exit.

Para nombres con espacios, usar comillas:
Mkdir "Mis documentos"
Mfile "notas de clase.txt"

Wr y Ap:
Después de ejecutar el comando, escribir las líneas de texto.
Escribir EXIT (en mayúsculas y en una línea separada) para terminar.

Nota:
Copy está implementado para archivos de texto, usando FileReader y
FileWriter, de acuerdo con las restricciones del ejercicio.
