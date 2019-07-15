package org.dataexporter.dao;


import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;

import java.util.List;
import java.util.Map;

public interface DataExportDao {


    StatementResult createNode(Session session, List<String> header, List<String> iterateData, int startPosition, int endPosition);

    StatementResult createRelationship(Session session, List<String> header, List<String> iterateData, int startPosition, String source, String target);

    StatementResult updateNode(Session session, String name, Map<String,String> updateData);

    StatementResult updateRelation(Session session, String name, Map<String,String> updateData, String source, String target);

}