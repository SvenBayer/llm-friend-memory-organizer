package de.svenbayer.llm_friend_memory_organizer.repository;

import de.svenbayer.llm_friend_memory_organizer.model.entity.person.PersonEntity;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersonRepository extends Neo4jRepository<PersonEntity, String> {

    /**
     * Example: custom query to call the vector similarity procedure.
     * We pass the embedding (double[]) and how many "topK" matches we want.
     */
    @Query("""
    CALL db.index.vector.queryNodes(
        'memory_index',
        toInteger($topK),
        [x IN $embedding | toFloat(x)]
    )
    YIELD node, score
    RETURN node
    ORDER BY score ASC
    LIMIT toInteger($topK)
    """)
    List<PersonEntity> findSimilarPerson(Integer topK, float[] embedding);

    /**
     * Example: find all memories directly related to a given memory.
     */
    @Query("""
        MATCH (d:Person {id: $id})-[:RELATED_TO]->(related:Memory)
        RETURN related
        """)
    List<PersonEntity> findDirectlyRelatedPersons(String id);
}

