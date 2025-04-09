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
        this.people = new HashSet<>();
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
            boolean isUser = isUser(aliasGroup);
            Optional<PersonEntity> existingPerson = findPersonEntityInList(aliasGroup, isUser);

            if (existingPerson.isPresent()) {
                // Add new aliases to existing person
                PersonEntity personEntity = existingPerson.get();
                personEntity.addAliases(aliasGroup);
            } else {
                PersonEntity personFromDatabase = findPersonInDatabase(aliasGroup, isUser);

                if (personFromDatabase != null) {
                    this.people.add(personFromDatabase);
                    return;
                }
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

    private PersonEntity findPersonInDatabase(List<String> aliasGroup, boolean isUser) {
        if (isUser) {
            return personRepository.findUser();
        }
        for (String alias : aliasGroup) {
            List<PersonEntity> existingPersons = personRepository.findPersonsWithMatchingAlias(alias);
            if (!existingPersons.isEmpty()) {
                PersonEntity personEntity = existingPersons.getFirst();
                personEntity.addAliases(aliasGroup);
                return personEntity;
            }
        }
        return null;
    }

    private Optional<PersonEntity> findPersonEntityInList(List<String> aliasGroup, boolean isUser) {
        Optional<PersonEntity> existingPerson;
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
        return existingPerson;
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
