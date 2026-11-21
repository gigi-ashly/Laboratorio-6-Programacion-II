package consolasimulada;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Consola {

    private final File carpetaRaiz;
    private File carpetaActual;
    private boolean modoEscritura;
    private boolean modoAgregar;
    private File archivoEnEdicion;
    private FileWriter escritor;

    public Consola() {
        carpetaRaiz = new File(System.getProperty("user.home"), "ConsolaSimulada");
        if (!carpetaRaiz.exists()) {
            carpetaRaiz.mkdirs();
        }
        carpetaActual = carpetaRaiz;
    }

    public String getPrompt() {
        String ruta = carpetaActual.getAbsolutePath();
        return ruta + ">";
    }

    public boolean debeCerrar() {
        return false;
    }

    public String ejecutar(String linea) {
        if (linea == null) {
            return "";
        }

        if (modoEscritura || modoAgregar) {
            return procesarTexto(linea);
        }

        linea = linea.trim();
        if (linea.isEmpty()) {
            return "";
        }

        String[] partes = separarArgumentos(linea);
        String comando = partes[0];
        String[] args = new String[partes.length - 1];
        System.arraycopy(partes, 1, args, 0, args.length);

        try {
            if ("Mkdir".equals(comando)) {
                exigirCantidad(args, 1);
                File carpeta = obtenerArchivo(args[0]);
                if (carpeta.exists()) return "Ya existe un archivo o carpeta con ese nombre.";
                return carpeta.mkdir() ? "Carpeta creada correctamente." : "No se pudo crear la carpeta.";
            }
            if ("Mfile".equals(comando)) {
                exigirCantidad(args, 1);
                File archivo = obtenerArchivo(args[0]);
                if (archivo.exists()) return "Ya existe un archivo o carpeta con ese nombre.";
                return archivo.createNewFile() ? "Archivo creado correctamente." : "No se pudo crear el archivo.";
            }
            if ("Rm".equals(comando)) {
                exigirCantidad(args, 1);
                File objetivo = obtenerArchivo(args[0]);
                if (!objetivo.exists()) return "El archivo o carpeta no existe.";
                return eliminar(objetivo) ? "Elemento eliminado correctamente." : "No se pudo eliminar el elemento.";
            }
            if ("Cd".equals(comando)) {
                exigirCantidad(args, 1);
                File destino = obtenerArchivo(args[0]);
                if (!destino.exists() || !destino.isDirectory()) return "La carpeta indicada no existe.";
                carpetaActual = destino;
                return "";
            }
            if ("..".equals(comando)) {
                if (!carpetaActual.equals(carpetaRaiz)) {
                    carpetaActual = carpetaActual.getParentFile();
                }
                return "";
            }
            if ("Dir".equals(comando)) {
                exigirCantidad(args, 0);
                File[] elementos = carpetaActual.listFiles();
                if (elementos == null || elementos.length == 0){ 
                    return "La carpeta está vacía.";
                }
                StringBuilder salida = new StringBuilder();
                for (int i = 0; i < elementos.length; i++) {
                    salida.append(elementos[i].isDirectory() ? "<DIR>  " : "       ");
                    salida.append(elementos[i].getName()).append("\n");
                }
                return salida.toString();
            }
            if ("Date".equals(comando)) {
                exigirCantidad(args, 0);
                return new SimpleDateFormat("dd/MM/yyyy").format(new Date());
            }
            if ("Time".equals(comando)) {
                exigirCantidad(args, 0);
                return new SimpleDateFormat("HH:mm:ss").format(new Date());
            }
            if ("Wr".equals(comando)) {
                exigirCantidad(args, 1);
                File archivo = obtenerArchivo(args[0]);
                if (!archivo.exists() || !archivo.isFile()) return "El archivo no existe.";
                iniciarEdicion(archivo, false);
                return "Escriba el contenido. Ingrese EXIT en una línea para terminar.";
            }
            if ("Ap".equals(comando)) {
                exigirCantidad(args, 1);
                File archivo = obtenerArchivo(args[0]);
                if (!archivo.exists() || !archivo.isFile()) return "El archivo no existe.";
                iniciarEdicion(archivo, true);
                return "Escriba el contenido que desea agregar. Ingrese EXIT en una línea para terminar.";
            }
            if ("Rd".equals(comando)) {
                exigirCantidad(args, 1);
                return leerArchivo(obtenerArchivo(args[0]));
            }
            if ("Ren".equals(comando)) {
                exigirCantidad(args, 2);
                File actual = obtenerArchivo(args[0]);
                File nuevo = obtenerArchivo(args[1]);
                if (!actual.exists()) return "El elemento que desea renombrar no existe.";
                if (nuevo.exists()) return "Ya existe un elemento con el nuevo nombre.";
                return actual.renameTo(nuevo) ? "Elemento renombrado correctamente." : "No se pudo renombrar el elemento.";
            }
            if ("Copy".equals(comando)) {
                exigirCantidad(args, 2);
                return copiar(obtenerArchivo(args[0]), obtenerArchivo(args[1]));
            }
            if ("Find".equals(comando)) {
                exigirCantidad(args, 1);
                StringBuilder salida = new StringBuilder();
                buscarNombre(carpetaActual, args[0], salida);
                return salida.length() == 0 ? "No se encontraron elementos." : salida.toString();
            }
            if ("Info".equals(comando)) {
                exigirCantidad(args, 1);
                return informacion(obtenerArchivo(args[0]));
            }
            if ("Tree".equals(comando)) {
                exigirCantidad(args, 0);
                StringBuilder salida = new StringBuilder();
                salida.append(carpetaActual.getName()).append("\n");
                construirArbol(carpetaActual, "", salida);
                return salida.toString();
            }
            if ("Cls".equals(comando)) {
                exigirCantidad(args, 0);
                return "\u0000CLS";
            }
            if ("Help".equals(comando)) {
                exigirCantidad(args, 0);
                return "Mkdir <nombre>   Crea una carpeta\n" + "Mfile <nombre.ext> Crea un archivo\n" + "Rm <nombre>       Elimina un archivo o carpeta\n" + "Cd <carpeta>      Entra a una carpeta\n" + "..                Regresa a la carpeta anterior\n"
                     + "Dir               Lista el contenido\n"
                     + "Date / Time       Muestra fecha u hora\n"
                     + "Wr <archivo>      Escribe y reemplaza contenido\n"
                     + "Rd <archivo>      Lee un archivo\n"
                     + "Ap <archivo>      Agrega contenido al final\n"
                     + "Ren <actual> <nuevo> Renombra un elemento\n"
                     + "Copy <origen> <destino> Copia un archivo\n"
                     + "Find <nombre>     Busca por nombre en subcarpetas\n"
                     + "Info <nombre>     Muestra información\n"
                     + "Tree              Muestra la estructura de carpetas\n"
                     + "Cls               Limpia la consola\n"
                     + "Help              Muestra esta ayuda\n"
                     + "Exit              Cierra la aplicación";
            }
            if ("Exit".equals(comando)) {
                exigirCantidad(args, 0);
                System.exit(0);
                return "";
            }
            return "'" + comando + "' no se reconoce como un comando interno o externo.";
        } catch (IllegalArgumentException ex) {
            return ex.getMessage();
        } catch (IOException ex) {
            return "Error al trabajar con el archivo: " + ex.getMessage();
        } catch (SecurityException ex) {
            return "No se tienen permisos para realizar esta operación.";
        }
    }

    private String procesarTexto(String linea) {
        if ("EXIT".equals(linea)) {
            try {
                escritor.close();
            } catch (IOException ex) {
                return "No se pudo cerrar el archivo: " + ex.getMessage();
            } finally {
                escritor = null;
                archivoEnEdicion = null;
                modoEscritura = false;
                modoAgregar = false;
            }
            return "Edición finalizada.";
        }

        try {
            escritor.write(linea);
            escritor.write(System.lineSeparator());
            return "";
        } catch (IOException ex) {
            return "Error al escribir: " + ex.getMessage();
        }
    }

    private void iniciarEdicion(File archivo, boolean append) throws IOException {
        escritor = new FileWriter(archivo, append);
        archivoEnEdicion = archivo;
        modoEscritura = !append;
        modoAgregar = append;
    }

    private String leerArchivo(File archivo) throws IOException {
        if (!archivo.exists() || !archivo.isFile()) return "El archivo no existe.";
        StringBuilder contenido = new StringBuilder();
        BufferedReader lector = new BufferedReader(new FileReader(archivo));
        try {
            String linea;
            while ((linea = lector.readLine()) != null) {
                contenido.append(linea).append("\n");
            }
        } finally {
            lector.close();
        }
        return contenido.length() == 0 ? "El archivo está vacío" : contenido.toString();
    }

    private String copiar(File origen, File destino) throws IOException {
        if (!origen.exists() || !origen.isFile()) return "El archivo de origen no existe.";
        if (destino.exists()) return "El archivo de destino ya existe.";
        BufferedReader lector = new BufferedReader(new FileReader(origen));
        FileWriter salida = new FileWriter(destino);
        try {
            char[] buffer = new char[1024];
            int cantidad;
            while ((cantidad = lector.read(buffer)) != -1) {
                salida.write(buffer, 0, cantidad);
            }
        } finally {
            lector.close();
            salida.close();
        }
        return "Archivo copiado correctamente.";
    }

    private String informacion(File archivo) {
        if (!archivo.exists()) return "El elemento no existe.";
        String tipo = archivo.isDirectory() ? "Carpeta" : "Archivo";
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return "Tipo: " + tipo + "\nRuta: " + archivo.getAbsolutePath()
                + "\nTamaño: " + (archivo.isDirectory() ? calcularTamano(archivo) : archivo.length())
                + " bytes\nÚltima modificación: " + formato.format(new Date(archivo.lastModified()));
    }

    private long calcularTamano(File archivo) {
        if (archivo.isFile()) return archivo.length();
        long total = 0;
        File[] hijos = archivo.listFiles();
        if (hijos != null) {
            for (int i = 0; i < hijos.length; i++) total += calcularTamano(hijos[i]);
        }
        return total;
    }

    private void buscarNombre(File carpeta, String texto, StringBuilder salida) {
        File[] elementos = carpeta.listFiles();
        if (elementos == null) return;
        for (int i = 0; i < elementos.length; i++) {
            if (elementos[i].getName().toLowerCase(Locale.ROOT).contains(texto.toLowerCase(Locale.ROOT))) {
                salida.append(elementos[i].getAbsolutePath()).append("\n");
            }
            if (elementos[i].isDirectory()) buscarNombre(elementos[i], texto, salida);
        }
    }

    private void construirArbol(File carpeta, String prefijo, StringBuilder salida) {
        File[] elementos = carpeta.listFiles();
        if (elementos == null) return;
        for (int i = 0; i < elementos.length; i++) {
            boolean ultimo = i == elementos.length - 1;
            salida.append(prefijo).append(ultimo ? "`-- " : "|-- ")
                    .append(elementos[i].getName()).append("\n");
            if (elementos[i].isDirectory()) {
                construirArbol(elementos[i], prefijo + (ultimo ? "    " : "|   "), salida);
            }
        }
    }

    private boolean eliminar(File archivo) {
        if (archivo.isDirectory()) {
            File[] hijos = archivo.listFiles();
            if (hijos != null) {
                for (int i = 0; i < hijos.length; i++) {
                    if (!eliminar(hijos[i])) return false;
                }
            }
        }
        return archivo.delete();
    }

    private File obtenerArchivo(String nombre) throws IOException {
        File archivo = new File(carpetaActual, nombre);
        String raiz = carpetaRaiz.getCanonicalPath();
        String ruta = archivo.getCanonicalPath();
        if (!ruta.equals(raiz) && !ruta.startsWith(raiz + File.separator)) {
            throw new IllegalArgumentException("No se permite salir de la carpeta raíz de la consola.");
        }
        return archivo;
    }

    private void exigirCantidad(String[] args, int cantidad) {
        if (args.length != cantidad) {
            throw new IllegalArgumentException("Cantidad de parámetros incorrecta. Escriba Help para consultar la sintaxis.");
        }
    }

    private String[] separarArgumentos(String linea) {
        java.util.ArrayList<String> partes = new java.util.ArrayList<String>();
        StringBuilder actual = new StringBuilder();
        boolean comillas = false;
        for (int i = 0; i < linea.length(); i++) {
            char c = linea.charAt(i);
            if (c == '"') {
                comillas = !comillas;
            } else if (Character.isWhitespace(c) && !comillas) {
                if (actual.length() > 0) {
                    partes.add(actual.toString());
                    actual.setLength(0);
                }
            } else {
                actual.append(c);
            }
        }
        if (comillas) {
            partes.add(actual.toString());
        } else if (actual.length() > 0) {
            partes.add(actual.toString());
        }
        return partes.toArray(new String[partes.size()]);
    }
}
