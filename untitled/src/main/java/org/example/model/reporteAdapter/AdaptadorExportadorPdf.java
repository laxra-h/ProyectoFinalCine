package org.example.model.reporteAdapter;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class AdaptadorExportadorPdf implements ExportadorReporte {

    private final MotorPdfExterno motorPdf;

    /**
     * Metodo constructor del adaptador de exportacion PDF (Patron Adapter)
     */
    public AdaptadorExportadorPdf() {
        this.motorPdf = new MotorPdfExterno();
    }

    /**
     * Metodo constructor con inyeccion del motor PDF adaptado
     * @param motorPdf
     */
    public AdaptadorExportadorPdf(MotorPdfExterno motorPdf) {
        this.motorPdf = motorPdf;
    }

    /**
     * Metodo que adapta la interfaz ExportadorReporte a los parametros de MotorPdfExterno
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
        String textoSeguro = (contenido != null) ? contenido : "";
        List<String> lineas = Arrays.asList(textoSeguro.split("\\R"));
        motorPdf.escribirFlujoPdf(archivoDestino.getAbsolutePath(), titulo, lineas);
        return archivoDestino;
    }
}
