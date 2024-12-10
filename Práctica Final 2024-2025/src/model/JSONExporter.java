package model;

import java.util.List;

import model.exceptions.ExporterException;

public class JSONExporter implements IExporter{

    @Override
    public void export(List<Task> tasks, String filePath) throws ExporterException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'export'");
    }

    @Override
    public List<Task> importTasks(String filePath) throws ExporterException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'importTasks'");
    }

}
