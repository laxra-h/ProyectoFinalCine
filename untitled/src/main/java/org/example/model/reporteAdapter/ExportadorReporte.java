package org.example.model.reporteAdapter;

import java.io.File;
import java.io.IOException;

public interface ExportadorReporte {

    /**
     * Metodo de la interfaz objetivo para exportar un reporte a un archivo
     * @param titulo
     * @param contenido
     * @param archivoDestino
     * @return
     * @throws IOException
     */
    public File exportar(String titulo, String contenido, File archivoDestino) throws IOException;
}
