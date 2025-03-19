package de.svenbayer.llm_friend_memory_organizer.model;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Node("Document")
@Data
@NoArgsConstructor
public class DocumentEntity {

    @Id
    private String id;

    private String text;

    // Store embedding in the node property
    private float[] embedding;

    @Relationship(type = "RELATED_TO", direction = Relationship.Direction.OUTGOING)
    private Set<DocumentEntity> relatedDocs = new HashSet<>();
}

