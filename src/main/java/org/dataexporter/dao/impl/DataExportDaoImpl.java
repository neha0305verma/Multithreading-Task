package org.dataexporter.dao.impl;


import org.dataexporter.dao.DataExportDao;
import org.neo4j.driver.internal.value.NodeValue;
import org.neo4j.driver.internal.value.RelationshipValue;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;

import java.util.List;
import java.util.Map;

public class DataExportDaoImpl implements DataExportDao {


    @Override
    public StatementResult createNode(Session session, List<String> header, List<String> iterateData, int startPosition, int endPosition) {

        String name = iterateData.get(startPosition);
        StatementResult result;

            String query = "CREATE (org:Organisation { ";
            String[] subIterateData = iterateData.subList(startPosition,endPosition).toArray(new String[0]);
            String[] subHeader = header.subList(startPosition,endPosition).toArray(new String[0]);
            query+="Name :\"";
            query+=subIterateData[0]+"\", ";

            for(int i= 1; i<subHeader.length; i++) {
                if(!subIterateData[i].isEmpty()) {
                    query += subHeader[i];
                    query += " : \"" + subIterateData[i] + "\"";
                    if (i != subHeader.length - 1) {
                        query += ", ";
                    }
                }
            }
            query += "}) RETURN org";

            result=session.run(query);
            if (result.hasNext())
            {
                Record record = result.next();
                NodeValue node = (NodeValue)record.get(0);
                System.out.println("CREATED NODE WITH DETAILS : "+node.asMap());
            }

        return result;
    }

    @Override
    public StatementResult createRelationship(Session session, List<String> header, List<String> iterateData, int startPosition, String source, String target) {

        String name = iterateData.get(startPosition);
        StatementResult result;

            String query = "MATCH (a:Organisation {Name: \""+source+"\"}),";
            query+="(b:Organisation {Name: \""+target+"\"})";
            query+=" CREATE (a)-[r:"+name+" {";
            String[] subIterateData = iterateData.subList(startPosition+1,header.size()).toArray(new String[0]);
            String[] subHeader = header.subList(startPosition+1,header.size()).toArray(new String[0]);
            for(int i= 0; i<subHeader.length; i++) {
                query+=subHeader[i];
                query+=" : \""+subIterateData[i]+"\"";
                if(i!=subHeader.length-1) {
                    query += ", ";
                }
            }
            query += "}]->(b) RETURN r";

            result=session.run(query);
            if (result.hasNext()) {
                Record record = result.next();
                RelationshipValue node = (RelationshipValue) record.get(0);
                System.out.println("CREATED RELATIONSHIP WITH DETAILS : "+node.asMap());
            }

        return result;
    }

    @Override
    public StatementResult updateNode(Session session, String name, Map<String,String> updateData) {

        StatementResult result;

        String query = "MATCH (org:Organisation {Name: \""+name+"\"}) ";
        for (Map.Entry<String, String> entry : updateData.entrySet()) {

            query+=" SET org."+entry.getKey()+"=\""+entry.getValue()+"\"";
        }
        query+=" RETURN org";

        result = session.run(query);
        if (result.hasNext()) {
            Record record = result.next();
            NodeValue node = (NodeValue) record.get(0);
            System.out.println("UPDATE NODE WITH DETAILS : "+node.asMap());
        }

        return result;
    }

    @Override
    public StatementResult updateRelation(Session session, String name, Map<String,String> updateData , String source, String target) {

        StatementResult result;

        String query = "MATCH (a:Organisation {Name: \""+source+"\"})";
        query+="-[r:"+name+"]->";
        query+="(b:Organisation {Name: \""+target+"\"})";
        for (Map.Entry<String, String> entry : updateData.entrySet()) {

            query+=" SET r."+entry.getKey()+"="+entry.getValue();
        }
        query+=" RETURN org";

        result = session.run(query);
        if (result.hasNext()) {
            Record record = result.next();
            RelationshipValue node = (RelationshipValue) record.get(0);
            System.out.println("UPDATE NODE WITH DETAILS : "+node.asMap());
        }

        return result;
    }



}