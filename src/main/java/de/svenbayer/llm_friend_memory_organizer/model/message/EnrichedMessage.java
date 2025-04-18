package de.svenbayer.llm_friend_memory_organizer.model.message;

import de.svenbayer.llm_friend_memory_organizer.model.message.lines.*;
import lombok.Data;

import java.util.*;
import java.util.stream.Collectors;

@Data
public class EnrichedMessage {

    private List<InformationExtractedLine> informationExtractedLines;
    private Map<InformationExtractedLine, List<CategoriesExtractedLine>> categoriesExtractedLines;
    private Map<InformationExtractedLine, List<UsersExtractedLine>> usersLines;
    private TimeExtractedIndexes timeExtractedIndexes;
    private Map<InformationExtractedLine, List<TagsExtractedLine>> tagsExtractedLine;
    private TopTopicsExtractedWithTags tagsWithTopTopics;

    private List<List<String>> groups;

    public void addInformationExtractedLines(List<InformationExtractedLine> linesToAdd) {
        List<InformationExtractedLine> result = new ArrayList<>();
        result.addAll(informationExtractedLines);
        result.addAll(linesToAdd);
        informationExtractedLines = result;
    }

    public List<List<String>> getAliasGroups() {
        if (groups != null) {
            return groups;
        }

        Set<String> aliases = getAliases(); // e.g. {"Sven", "the user Sven", "the user", "Sophie", "my friend Sophie"}
        List<List<String>> groups = new ArrayList<>();

        for (String alias : aliases) {
            // Attempt to find a matching group
            //List<String> matchingGroup = findMatchingGroup(groups, alias);

            //if (matchingGroup != null) {
                // If we have a match, add the alias
              //  matchingGroup.add(alias);
            //} else {
                // Otherwise create a new group for this alias
                List<String> newGroup = new ArrayList<>();
                newGroup.add(alias);
                groups.add(newGroup);
            //}
        }

        this.groups = groups;
        return groups;
    }

    private Set<String> getAliases() {
        return usersLines.values().stream()
                .flatMap(Collection::stream)
                .map(UsersExtractedLine::getUser)
                .collect(Collectors.toSet());
    }
}
