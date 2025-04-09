package de.svenbayer.llm_friend_memory_organizer.repository;

import de.svenbayer.llm_friend_memory_organizer.model.entity.person.PersonEntity;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersonRepository extends Neo4jRepository<PersonEntity, String> {

    @Query("""
        MATCH (p:Person)
        WHERE ANY(alias IN p.aliases WHERE
            toLower(alias) CONTAINS toLower($searchAlias) OR
            toLower($searchAlias) CONTAINS toLower(alias))
            AND NOT p.id = 'user'
        RETURN p
        """)
    List<PersonEntity> findPersonsWithMatchingAlias(String searchAlias);

    @Query("""
        MATCH (p:Person)
        WHERE p.id = 'user'
        RETURN p
        """)
    PersonEntity findUser();
}
