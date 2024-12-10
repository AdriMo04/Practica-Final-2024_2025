package model;

import model.exceptions.ExporterException;

public class ExporterFactory {
    public static IExporter getExporter(String tipo) throws ExporterException {
        switch (tipo.toLowerCase()) {
            case "json":
                return (IExporter) new JSONExporter();
            case "csv":
                return (IExporter) new CSVExporter();
            default: 
                throw new ExporterException("Tipo de exportador no soportado: " + tipo);
        }
    }
}
