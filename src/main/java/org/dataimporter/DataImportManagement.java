package org.dataimporter;

import org.dataimporter.dao.DataImportDao;
import org.dataimporter.dao.impl.DataImportDaoImpl;

import java.io.File;
import java.util.*;


public class DataImportManagement {

    public Map<String,Object> importData(File input) {

        DataImportDao dataImporter = new DataImportDaoImpl();

        List<List<String>> contentList = new ArrayList<>();
        List<String> headers = new ArrayList<>();
        Map<String, Object> allData = new HashMap<>();

            String strFileExtn = input.getName().substring(input.getName().lastIndexOf(".") + 1);

            if (strFileExtn.equalsIgnoreCase("xlsx"))
            {
                allData = dataImporter.readDataFromExcel(input);
            }

        return allData;
    }
}