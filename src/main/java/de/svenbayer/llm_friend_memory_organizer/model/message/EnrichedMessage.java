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

    public String getInformationExtractedLinesWithNumbering() {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < informationExtractedLines.size(); i++) {
            result.append(i + 1).append(". ").append(informationExtractedLines.get(i).getLine());
            if (i < informationExtractedLines.size() - 1) {
                result.append("\n");
            }
        }
        return result.toString();
    }

    public void addInformationExtractedLines(List<InformationExtractedLine> linesToAdd) {
        List<InformationExtractedLine> result = new ArrayList<>();
        result.addAll(informationExtractedLines);
        result.addAll(linesToAdd);
        informationExtractedLines = result;
    }

    public String getTagsAsLines() {
        // TODO check if this really works
        return tagsExtractedLine.values().stream()
                .flatMap(List::stream)
                .map(line -> String.join("|", line.getTag()))
                .collect(Collectors.joining("\n"));
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

    private List<String> findMatchingGroup(List<List<String>> groups, String alias) {
        for (List<String> group : groups) {
            for (String existing : group) {
                if (isSimilar(alias, existing)) {
                    // Return this group immediately (no 'break' needed)
                    return group;
                }
            }
        }
        // Return null if no matching group was found
        return null;
    }

    private Set<String> getAliases() {
        return usersLines.values().stream()
                .flatMap(Collection::stream)
                .map(UsersExtractedLine::getUser)
                .collect(Collectors.toSet());
    }

    /**
     * Decide when two aliases should be in the same group.
     * This example just checks substring matches ignoring case.
     */
    private boolean isSimilar(String a, String b) {
        // TODO error, this matches the user's wife with the user into one group
        String lowerA = a.toLowerCase();
        String lowerB = b.toLowerCase();
        return lowerA.contains(lowerB) || lowerB.contains(lowerA);
    }
}
