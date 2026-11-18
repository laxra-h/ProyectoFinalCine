package org.example.model.reporteAdapter;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class MotorPdfExterno {

    /**
     * Metodo de bajo nivel con interfaz incompatible que construye un documento PDF 1.4 binario
     * @param rutaAbsoluta
     * @param encabezadoPrincipal
     * @param lineasPagina
     * @throws IOException
     */
    public void escribirFlujoPdf(String rutaAbsoluta, String encabezadoPrincipal, List<String> lineasPagina) throws IOException {
        StringBuilder flujoContenido = new StringBuilder();
        flujoContenido.append("BT\n");
        flujoContenido.append("/F1 13 Tf\n");
        flujoContenido.append("45 795 Td\n");
        flujoContenido.append("(").append(escaparPdf(encabezadoPrincipal)).append(") Tj\n");
        flujoContenido.append("/F1 9 Tf\n");
        flujoContenido.append("0 -18 Td\n");
        flujoContenido.append("(").append("-".repeat(78)).append(") Tj\n");

        int maxLineas = Math.min(lineasPagina.size(), 52);
        for (int i = 0; i < maxLineas; i++) {
            flujoContenido.append("0 -13 Td\n");
            flujoContenido.append("(").append(escaparPdf(lineasPagina.get(i))).append(") Tj\n");
        }
        flujoContenido.append("ET\n");

        byte[] bytesFlujo = flujoContenido.toString().getBytes(StandardCharsets.ISO_8859_1);

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        List<Integer> offsets = new ArrayList<>();

        escribirAscii(buffer, "%PDF-1.4\n");

        offsets.add(buffer.size());
        escribirAscii(buffer, "1 0 obj\n<< /Type /Catalog /Pages 2 0 R >>\nendobj\n");

        offsets.add(buffer.size());
        escribirAscii(buffer, "2 0 obj\n<< /Type /Pages /Kids [3 0 R] /Count 1 >>\nendobj\n");

        offsets.add(buffer.size());
        escribirAscii(buffer, "3 0 obj\n<< /Type /Page /Parent 2 0 R /MediaBox [0 0 612 842] "
                + "/Resources << /Font << /F1 4 0 R >> >> /Contents 5 0 R >>\nendobj\n");

        offsets.add(buffer.size());
        escribirAscii(buffer, "4 0 obj\n<< /Type /Font /Subtype /Type1 /BaseFont /Courier /Encoding /WinAnsiEncoding >>\nendobj\n");

        offsets.add(buffer.size());
        escribirAscii(buffer, "5 0 obj\n<< /Length " + bytesFlujo.length + " >>\nstream\n");
        buffer.write(bytesFlujo);
        escribirAscii(buffer, "\nendstream\nendobj\n");

        int inicioXref = buffer.size();
        escribirAscii(buffer, "xref\n0 6\n0000000000 65535 f \n");
        for (int offset : offsets) {
            escribirAscii(buffer, String.format("%010d 00000 n \n", offset));
        }

        escribirAscii(buffer, "trailer\n<< /Size 6 /Root 1 0 R >>\nstartxref\n" + inicioXref + "\n%%EOF\n");

        try (FileOutputStream fos = new FileOutputStream(rutaAbsoluta)) {
            buffer.writeTo(fos);
        }
    }

    private void escribirAscii(ByteArrayOutputStream buffer, String texto) throws IOException {
        buffer.write(texto.getBytes(StandardCharsets.ISO_8859_1));
    }

    private String escaparPdf(String texto) {
        if (texto == null) {
            return "";
        }
        return texto
                .replace("\\", "\\\\")
                .replace("(", "\\(")
                .replace(")", "\\)")
                .replace("—", "-")
                .replace("×", "x");
    }
}
