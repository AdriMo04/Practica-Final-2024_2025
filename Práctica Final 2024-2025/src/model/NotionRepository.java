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
    public String createRecord(Task task) {

        System.out.println("Creando una nueva página...");
        // Crear las propiedades de la página
        // Las propiedades son las que se definen en la Dabase de Notion como columnas
        // Se ejemplifican varios tipos de propiedades como texto, número, fecha y casilla de verificación
        Map<String, PageProperty> properties = Map.of(
                "Identifier", createTitleProperty(String.valueOf(task.getIdentifier())),
                "Título", createRichTextProperty(task.getTitle()),
                "Fecha", createDateProperty(formatDate(task.getDate())),
                "Descripción", createRichTextProperty(task.getContent()),
                "Prioridad", createNumberProperty(task.getPriority()),
                "Duración", createNumberProperty(task.getEstimatedDuration()),
                "Completado", createCheckboxProperty(task.isCompleted())
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
    public List<Task> getAllRecords() {
        List<Task> tasks = new ArrayList<>();
        try {
            // Crear la solicitud para consultar la base de datos
            QueryDatabaseRequest queryRequest = new QueryDatabaseRequest(databaseId);

            // Ejecutar la consulta
            QueryResults queryResults = client.queryDatabase(queryRequest);

            // Procesar los resultados
            for (Page page : queryResults.getResults()) {
                Map<String, PageProperty> properties = page.getProperties();
                Task task = mapPageToTask(page.getId(), properties);
                if (task != null) {
                    tasks.add(task);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tasks;
    }

    // Actualizar un registro por Identifier
    public void updateRecordByIdentifier(Task task) {
        try {
            String pageId = findPageIdByIdentifier(task.getIdentifier(), titleColumnName);
            if (pageId == null) {
                System.out.println("No se encontró un registro con el Identifier: " + task.getIdentifier());
                return;
            }

            // Crear las propiedades actualizadas
            Map<String, PageProperty> updatedProperties = Map.of(
                    "Título", createRichTextProperty(task.getTitle()),
                    "Fecha", createDateProperty(formatDate(task.getDate())),
                    "Descripción", createRichTextProperty(task.getContent()),
                    "Prioridad", createNumberProperty(task.getPriority()),
                    "Duración", createNumberProperty(task.getEstimatedDuration()),
                    "Completado", createCheckboxProperty(task.isCompleted())
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
    public void deleteRecordByIdentifier(Task task) {
        try {
            String taskId = findPageIdByIdentifier(task.getIdentifier(),titleColumnName);
            if (taskId == null) {
                System.out.println("No se encontró un registro con el Identifier: " + task.getIdentifier());
                return;
            }
            // Archivar la página
            UpdatePageRequest updateRequest = new UpdatePageRequest(taskId, Collections.emptyMap(), true);
            client.updatePage(updateRequest);
            System.out.println("Página archivada con ID (interno Notion)" + taskId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Buscar el ID (interno de Notion) de una página por Identifier (atributo Title de la Database de Notion)
    private String findPageIdByIdentifier(int identifier, String titleColumnName) {
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
    @SuppressWarnings("unused")
    private PageProperty createRichTextProperty(String text) {
        RichText richText = new RichText();
        richText.setText(new Text(text));
        PageProperty property = new PageProperty();
        property.setRichText(Collections.singletonList(richText));
        return property;
    }

    @SuppressWarnings("unused")
    private PageProperty createNumberProperty(Integer number) {
        PageProperty property = new PageProperty();
        property.setNumber(number);
        return property;
    }

    @SuppressWarnings("unused")
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
    private Task mapPageToTask(String pageId, Map<String, PageProperty> properties) {
        try {
            Task task = new Task(
                    Integer.parseInt(properties.get("Identifier").getTitle().get(0).getText().getContent()),
                    properties.get("Título").getRichText().get(0).getText().getContent(),
                    parseDate(properties.get("Fecha").getDate().getStart()),
                    properties.get("Descripción").getRichText().get(0).getText().getContent(),
                    properties.get("Prioridad").getNumber().intValue(),
                    properties.get("Duración").getNumber().intValue(),
                    properties.get("Completado").getCheckbox()
            );
            return task;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}