package model;

import notion.api.v1.NotionClient;
import notion.api.v1.http.OkHttp5Client;
import notion.api.v1.logging.Slf4jLogger;
import notion.api.v1.model.databases.QueryResults;
import notion.api.v1.model.pages.Page;
import notion.api.v1.model.pages.PageParent;
import notion.api.v1.model.pages.PageProperty;
import notion.api.v1.model.pages.PageProperty.RichText;
import notion.api.v1.model.pages.PageProperty.RichText.Text;
import notion.api.v1.request.databases.QueryDatabaseRequest;
import notion.api.v1.request.pages.CreatePageRequest;
import notion.api.v1.request.pages.UpdatePageRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;


/**
 * 
 * En este ejemplo se muestra cómo interactuar con la API de Notion para crear, leer, actualizar y eliminar registros en una base de datos.
 * 
 * Esta clase NO IMPLEMENTA la interfaz IRepository, pero se puede utilizar como base para implementarla.
 * 
 * Además se controlan las excepciones en los métodos de la clase NotionRepository y no se lanzan excepciones (throws) en los métodos.
 * Deberás ingeniartelas para generar las excepciones adecuadas en caso de error.
 * 
 * 
 */
public class NotionRepository {

    private final NotionClient client;
    private final String databaseId;
    private final String titleColumnName = "Identifier"; // Este es el nombre de la columna que se utilizará 
                                                         // como clave primaria única de type Title en Notion

    public NotionRepository(String apiToken, String databaseId) {
        // Crear cliente de Notion
        this.client = new NotionClient(apiToken);

        // Configurar cliente HTTP adecuado y tiempos de espera
        client.setHttpClient(new OkHttp5Client(60000,60000,60000));

        // Configurar loggers
        client.setLogger(new Slf4jLogger());
        
        // Silenciar/Activar los registros de log de Notion API
        // Ver en nivel debug los mensajes de depuración
        System.setProperty("notion.api.v1.logging.StdoutLogger", "debug");

        // Nivel más alto de log para NO ver mensajes de depuración
        //System.setProperty("notion.api.v1.logging.StdoutLogger", "off");

        this.databaseId = databaseId;
    }

    // Crear un registro en la base de datos
    public String createRecord(Persona persona) {

        System.out.println("Creando una nueva página...");
        // Crear las propiedades de la página
        // Las propiedades son las que se definen en la Dabase de Notion como columnas
        // Se ejemplifican varios tipos de propiedades como texto, número, fecha y casilla de verificación
        Map<String, PageProperty> properties = Map.of(
                "Identifier", createTitleProperty(persona.getIdentifier()),
                "Nombre", createRichTextProperty(persona.getNombre()),
                "Apellidos", createRichTextProperty(persona.getApellidos()),
                "Edad", createNumberProperty(persona.getEdad()),
                "Año de Nacimiento", createDateProperty(persona.getAnoNacimiento()),
                "Carnet de Conducir", createCheckboxProperty(persona.isCarnetDeConducir())
        );

        // TODO: Aquí no se tiene en cuenta el caso de que ya exista un registro con el mismo Identifier
        // ni se manejan excepciones en caso de error (se están manejando todas en el main de la clase Main)

        // Configurar la página padre de la database
        PageParent parent = PageParent.database(databaseId);

        // Crear la solicitud a la API de Notion
        CreatePageRequest request = new CreatePageRequest(parent, properties);

        // Ejecutar la solicitud (necesita de conexión a internet)
        Page response = client.createPage(request);

        // Este identificador es el interno de Notion no el campo Identifier de tipo Title
        // que se utilizará como clave primaria unica
        // Sin embargo es necesario para actualizar o eliminar registros
        System.out.println("Página creada con ID (interno Notion)" + response.getId());
        return response.getId();
    }

    // Obtener todos los registros
    public List<Persona> getAllRecords() {
        List<Persona> personas = new ArrayList<>();
        try {
            // Crear la solicitud para consultar la base de datos
            QueryDatabaseRequest queryRequest = new QueryDatabaseRequest(databaseId);

            // Ejecutar la consulta
            QueryResults queryResults = client.queryDatabase(queryRequest);

            // Procesar los resultados
            for (Page page : queryResults.getResults()) {
                Map<String, PageProperty> properties = page.getProperties();
                Persona persona = mapPageToPersona(page.getId(), properties);
                if (persona != null) {
                    personas.add(persona);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return personas;
    }

    // Actualizar un registro por Identifier
    public void updateRecordByIdentifier(Persona persona) {
        try {
            String pageId = findPageIdByIdentifier(persona.getIdentifier(), titleColumnName);
            if (pageId == null) {
                System.out.println("No se encontró un registro con el Identifier: " + persona.getIdentifier());
                return;
            }

            // Crear las propiedades actualizadas
            Map<String, PageProperty> updatedProperties = Map.of(
                    "Nombre", createRichTextProperty(persona.getNombre()),
                    "Apellidos", createRichTextProperty(persona.getApellidos()),
                    "Edad", createNumberProperty(persona.getEdad()),
                    "Año de Nacimiento", createDateProperty(persona.getAnoNacimiento()),
                    "Carnet de Conducir", createCheckboxProperty(persona.isCarnetDeConducir())
            );

            // Crear la solicitud de actualización
            UpdatePageRequest updateRequest = new UpdatePageRequest(pageId, updatedProperties);
            client.updatePage(updateRequest);

            System.out.println("Página actualizada con ID (interno Notion)" + pageId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Eliminar (archivar) un registro por Identifier
    public void deleteRecordByIdentifier(Persona p) {
        try {
            String pageId = findPageIdByIdentifier(p.getIdentifier(),titleColumnName);
            if (pageId == null) {
                System.out.println("No se encontró un registro con el Identifier: " + p.getIdentifier());
                return;
            }
            // Archivar la página
            UpdatePageRequest updateRequest = new UpdatePageRequest(pageId, Collections.emptyMap(), true);
            client.updatePage(updateRequest);
            System.out.println("Página archivada con ID (interno Notion)" + pageId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Buscar el ID (interno de Notion) de una página por Identifier (atributo Title de la Database de Notion)
    private String findPageIdByIdentifier(String identifier, String titleColumnName) {
        try {
            QueryDatabaseRequest queryRequest = new QueryDatabaseRequest(databaseId);
            QueryResults queryResults = client.queryDatabase(queryRequest);

            for (Page page : queryResults.getResults()) {
                Map<String, PageProperty> properties = page.getProperties();
                if (properties.containsKey(titleColumnName) &&
                        properties.get(titleColumnName).getTitle().get(0).getText().getContent().equals(identifier)) {
                    return page.getId();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Métodos auxiliares para crear propiedades de página
    private PageProperty createTitleProperty(String title) {
        RichText idText = new RichText();
        idText.setText(new Text(title));
        PageProperty idProperty = new PageProperty();
        idProperty.setTitle(Collections.singletonList(idText));
        return idProperty;
    }

    // Metodos auxiliares para crear propiedades de página
    private PageProperty createRichTextProperty(String text) {
        RichText richText = new RichText();
        richText.setText(new Text(text));
        PageProperty property = new PageProperty();
        property.setRichText(Collections.singletonList(richText));
        return property;
    }

    private PageProperty createNumberProperty(Integer number) {
        PageProperty property = new PageProperty();
        property.setNumber(number);
        return property;
    }

    private PageProperty createDateProperty(String date) {
        PageProperty property = new PageProperty();
        PageProperty.Date dateProperty = new PageProperty.Date();
        dateProperty.setStart(date);
        property.setDate(dateProperty);
        return property;
    }

    private PageProperty createCheckboxProperty(boolean checked) {
        PageProperty property = new PageProperty();
        property.setCheckbox(checked);
        return property;
    }

    // Mapeo de propiedades de Notion a un objeto Persona
    private Persona mapPageToPersona(String pageId, Map<String, PageProperty> properties) {
        try {
            Persona persona = new Persona();
            persona.setIdentifier(properties.get("Identifier").getTitle().get(0).getText().getContent());
            persona.setNombre(properties.get("Nombre").getRichText().get(0).getText().getContent());
            persona.setApellidos(properties.get("Apellidos").getRichText().get(0).getText().getContent());
            persona.setEdad(properties.get("Edad").getNumber().intValue());
            persona.setAnoNacimiento(properties.get("Año de Nacimiento").getDate().getStart());
            persona.setCarnetDeConducir(properties.get("Carnet de Conducir").getCheckbox());
            return persona;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}