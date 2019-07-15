package org.dataexporter;

import org.dataexporter.dao.DataExportDao;
import org.dataexporter.dao.impl.DataExportDaoImpl;
import org.neo4j.driver.internal.value.NodeValue;
import org.neo4j.driver.internal.value.RelationshipValue;
import org.neo4j.driver.v1.*;
import org.neo4j.driver.v1.exceptions.AuthenticationException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;


public class DataExportManagement {

    Session session;
    DataExportDao dataExporter;
    List<String> header;
    public void exportData(List<String> header, List<List<String>> data, Properties properties, String[] args) {

        dataExporter = new DataExportDaoImpl();
        this.header=header;

        try {

            String username = properties.getProperty("username");
            String password = properties.getProperty("password");
            String neo4jUrl = properties.getProperty("neo4jUrl");
            Driver driver =null;

            try {
                driver = GraphDatabase.driver(
                        neo4jUrl);
            } catch (AuthenticationException e) {
                driver = GraphDatabase.driver(
                        neo4jUrl, AuthTokens.basic(username, password));
            }


            session = driver.session();
            StatementResult result = null;

            int sourcePosition = header.indexOf("Source");
            int targetPosition = header.indexOf("Target");
            int relationPosition = header.indexOf("Relationship Topic");


//            session.run("CREATE CONSTRAINT ON (org:Organisation) ASSERT org.NAME IS UNIQUE");

            for (List<String> iterateData : data) {

                if (iterateData.size() > 0 && !iterateData.get(sourcePosition).isEmpty() && !iterateData.get(targetPosition).isEmpty() && !iterateData.get(relationPosition).isEmpty()) {

                    // Creating source Node if doesn't exists
                    String source = iterateData.get(sourcePosition);
                    boolean check = checkNodeExists(iterateData.subList(sourcePosition,targetPosition),sourcePosition);
                    if(!check)
                        result = dataExporter.createNode(session, header, iterateData, sourcePosition, targetPosition);


                    // Creating target Node if doesn't exists
                    String target = iterateData.get(targetPosition);
                    check = checkNodeExists(iterateData.subList(targetPosition,relationPosition),targetPosition);
                    if(!check)
                        result = dataExporter.createNode(session, header, iterateData, targetPosition, relationPosition);


                    // Creating relationship between source and target
                    String relation = iterateData.get(relationPosition);
                    check = checkRelationshipExists(source, target, iterateData.subList(relationPosition,header.size()),relationPosition);
                    if(!check)
                    result = dataExporter.createRelationship(session, header, iterateData, relationPosition, source, target);

//                    MATCH (a:Person {name: "Aditya"})-[:Friends]->(b:Person) RETURN a,b


                }
            }

            session.close();
            driver.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }



//    public static StatementResult updateNode(List<String> iterateData, int position, Session session) {
//
//        StatementResult result = session.run(
//                "MATCH (a:Organisation {Name: {source}}),(b:Organisation {Name: {target}})"+
//                        " CREATE (a)-[r:"+iterateData.get(position)+" {Name: {name}, Interaction: {interaction}, Strength: {strength}}]->(b) RETURN r",
//                parameters("source",source, "target",
//                        target,"name",iterateData.get(position+1), "interaction",iterateData.get(position+2), "strength",iterateData.get(position+3)));
//
//        return result;
//    }

public boolean checkNodeExists(List<String> iterateData, int startPosition) {

    String data = iterateData.get(0);
    StatementResult result = session.run(
            "MATCH (org:Organisation) WHERE org.Name=\""+data+"\" RETURN org");
    if (result.hasNext()) {
        Record record = result.next();
        NodeValue node = (NodeValue) record.get(0);
        if (node!=null && !node.get("Name").equals(data)) {
            checkAndUpdateNode(node.asMap(), iterateData, startPosition);
            return true;
        }
    }
    return false;
}

public void checkAndUpdateNode(Map<String,Object> result, List<String> newData, int startPosition) {

        Map<String,String> updateData = new HashMap<>();
        for(int i=0; i<newData.size(); i++) {
            if (!(header.get(i + startPosition).equals("Source") || header.get(i + startPosition).equals("Target"))) {
                String resultDataToString="";
                Object resultData = result.get(header.get(i + startPosition));
                if(resultData!=null)
                    resultDataToString=resultData.toString();
                if (!newData.get(i).isEmpty() &&  !resultDataToString.equals(newData.get(i))) {
                    updateData.put(header.get(startPosition + i), newData.get(i));
                }
            }
        }

        if(!updateData.isEmpty())
            dataExporter.updateNode(session, (String)result.get("Name"), updateData);
}


public boolean checkRelationshipExists(String source, String target, List<String> iterateData, int startPosition) {

    String name = iterateData.get(0);
    StatementResult result = session.run(
            "MATCH (a:Organisation {Name: \""+source+"\"})-[r:"+name+"]->(b:Organisation {Name: \""+target+"\"}) RETURN r");

    if (result.hasNext()) {
        Record record = result.next();
        RelationshipValue node = (RelationshipValue) record.get(0);
        if (!node.get("Name").equals(name)) {
            checkAndUpdateRelation(node.asMap(), iterateData, startPosition, source, target);
            return true;
        }
    }
    return false;
}

public void checkAndUpdateRelation(Map<String,Object> result, List<String> newData, int startPosition, String source, String target) {

    Map<String,String> updateData = new HashMap<>();
    for(int i=0; i<newData.size(); i++)
    {
        if (!header.get(i + startPosition).equals("Relationship Topic")) {
            String resultDataToString="";
            Object resultData = result.get(header.get(i + startPosition));
            if(resultData!=null)
                resultDataToString=resultData.toString();
            if (!newData.get(i).isEmpty() &&  !resultDataToString.equals(newData.get(i))) {
                updateData.put(header.get(startPosition + i), newData.get(i));
            }

    }
    }

    if(!updateData.isEmpty())
        dataExporter.updateRelation(session, (String)result.get("Name"), updateData, source, target);
}


}
