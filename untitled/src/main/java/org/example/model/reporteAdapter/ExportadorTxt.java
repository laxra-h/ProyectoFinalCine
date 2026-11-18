package org.example.model.reporteAdapter;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class ExportadorTxt implements ExportadorReporte {

    /**
     * Metodo para exportar el reporte en formato de texto plano (.txt)
     * @param titulo
     * @param contenido
     * @param archivoDestino
     * @return
     * @throws IOException
     */
    @Override
    public File exportar(String titulo, String contenido, File archivoDestino) throws IOException {
        if (archivoDestino.getParentFile() != null) {
            archivoDestino.getParentFile().mkdirs();
        }
        String textoCompleto = titulo + System.lineSeparator()
                + "=".repeat(Math.max(40, titulo.length())) + System.lineSeparator()
                + contenido;
        Files.writeString(archivoDestino.toPath(), textoCompleto, StandardCharsets.UTF_8);
        return archivoDestino;
    }
}
