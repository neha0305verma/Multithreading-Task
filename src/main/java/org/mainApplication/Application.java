package org.mainApplication;

import org.dataexporter.DataExportManagement;
import org.dataimporter.DataImportManagement;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Hello world!
 *
 */
public class Application {
    public static void main(String[] args) {

        try {
            Properties properties = new Properties();
            properties.load(new FileInputStream("src/main/resources/app.properties"));
//            String excelFileLocation = properties.getProperty("excelFile");
//        Map<String,Object> allData = ExcelReader.readExcelSheet(new File(excelFileLocation));

            File file = new File("src/main/resources/demoData.xlsx");
            Map<String, Object> allData = new DataImportManagement().importData(file);

            List<List<String>> data = (List<List<String>>) allData.get("data");
            List<String> header = (List<String>) allData.get("header");
            //System.out.println(data.toString());

            new DataExportManagement().exportData(header, data, properties, args);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

}