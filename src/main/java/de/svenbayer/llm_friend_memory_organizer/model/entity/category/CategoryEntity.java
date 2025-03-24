package de.svenbayer.llm_friend_memory_organizer.model.entity.category;

import de.svenbayer.llm_friend_memory_organizer.model.entity.MemoryEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Node("Category")
@Data
public class CategoryEntity {

    @Id
    @EqualsAndHashCode.Exclude
    private String id = UUID.randomUUID().toString();

    private String name;

    @Relationship(type = "HAS_MEMORY", direction = Relationship.Direction.OUTGOING)
    private Set<MemoryEntity> memories = new HashSet<>();

    public CategoryEntity(String name) {
        this.name = name;
    }

    public void addMemory(MemoryEntity memoryEntity) {
        this.memories.add(memoryEntity);
    }
}
