package de.svenbayer.llm_friend_memory_organizer.model.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.joda.time.DateTime;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

import java.util.HashSet;
import java.util.Set;

@Node("Memory")
@Data
@NoArgsConstructor
public class MemoryEntity {

    @Id
    @EqualsAndHashCode.Exclude
    private String id;
    private Set<String> description = new HashSet<>();
    private float[] embedding;
    private DateTime startTime;
    private DateTime endTime;

    public void addDescription(String additionalDescription) {
        this.description.add(additionalDescription);
    }
}
