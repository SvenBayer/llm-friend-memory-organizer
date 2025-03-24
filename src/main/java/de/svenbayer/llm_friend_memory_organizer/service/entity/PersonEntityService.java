package de.svenbayer.llm_friend_memory_organizer.service.entity;

import de.svenbayer.llm_friend_memory_organizer.model.entity.person.PersonEntity;
import de.svenbayer.llm_friend_memory_organizer.model.entity.person.UserEntity;
import de.svenbayer.llm_friend_memory_organizer.model.message.EnrichedMessage;
import de.svenbayer.llm_friend_memory_organizer.repository.PersonRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PersonEntityService implements IEntityPersistingService {

    private final PersonRepository personRepository;

    private final Set<PersonEntity> people;

    public PersonEntityService(PersonRepository personRepository) {
        this.personRepository = personRepository;
        this.people = new HashSet<>(personRepository.findAll());
    }

    protected Set<PersonEntity> getPersonEntitiesForSection(List<String> aliases) {
        return this.people.stream()
                .filter(personEntity -> aliases.stream()
                        .anyMatch(foundAlias -> personEntity.getAliases().stream()
                                .anyMatch(foundAlias::contains)))
                .collect(Collectors.toSet());
    }

    protected void createPeopleEntities(EnrichedMessage enrichedMessage) {
        List<List<String>> groupedAliases =  enrichedMessage.getAliasGroups();

        for (List<String> aliasGroup : groupedAliases) {
            Optional<PersonEntity> existingPerson;

            boolean isUser = isUser(aliasGroup);
            if (isUser) {
                existingPerson = this.people.stream()
                        .filter(personEntity -> personEntity instanceof UserEntity)
                        .findFirst();
            } else {
                existingPerson = this.people.stream()
                        .filter(person -> person.getAliases().stream()
                                .anyMatch(existingAlias -> aliasGroup.stream()
                                        .anyMatch(newAlias ->
                                                existingAlias.contains(newAlias) ||
                                                        newAlias.contains(existingAlias))))
                        .findFirst();
            }

            if (existingPerson.isPresent()) {
                // Add new aliases to existing person
                PersonEntity personEntity = existingPerson.get();
                personEntity.addAliases(aliasGroup);
            } else {
                // Create new person
                PersonEntity newPerson;
                if (isUser) {
                    newPerson = new UserEntity();
                } else {
                    newPerson = new PersonEntity();
                }
                newPerson.addAliases(aliasGroup);
                this.people.add(newPerson);
            }
        }
    }

    private boolean isUser(List<String> aliasGroup) {
        return aliasGroup.stream()
                .anyMatch(alias -> alias.contains("user"));
    }

    @Override
    public void persistData() {
        this.personRepository.saveAll(this.people);
    }
}
