package de.svenbayer.llm_friend_memory_organizer.service.entity;

import de.svenbayer.llm_friend_memory_organizer.model.entity.person.PersonEntity;
import de.svenbayer.llm_friend_memory_organizer.model.entity.person.UserEntity;
import de.svenbayer.llm_friend_memory_organizer.model.message.EnrichedMessage;
import de.svenbayer.llm_friend_memory_organizer.model.message.lines.UsersExtractedLine;
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

    protected Set<PersonEntity> getPersonEntitiesForSection(List<UsersExtractedLine> aliases) {
        List<String> aliasStrings = aliases.stream()
                .map(UsersExtractedLine::getUser)
                .toList();

        if (isUser(aliasStrings)) {
            return this.people.stream()
                    .filter(person -> person.getId().equals("user"))
                    .collect(Collectors.toSet());
        }

        return this.people.stream()
                .filter(person -> aliasStrings.stream()
                        .anyMatch(alias -> person.getAliases().contains(alias))
                        && !(person instanceof UserEntity))
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
                                                (existingAlias.contains(newAlias) ||
                                                        newAlias.contains(existingAlias)) && !existingAlias.equals("THE_USER"))))
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
                .anyMatch(alias -> alias.contains("USER") && !alias.contains("USERS"));
    }

    @Override
    public void completeTransaction() {
        this.personRepository.saveAll(this.people);
        this.people.clear();
    }
}
