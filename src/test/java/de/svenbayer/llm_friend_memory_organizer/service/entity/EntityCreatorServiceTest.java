package de.svenbayer.llm_friend_memory_organizer.service.entity;

import de.svenbayer.llm_friend_memory_organizer.model.entity.person.PersonEntity;
import de.svenbayer.llm_friend_memory_organizer.model.message.*;
import de.svenbayer.llm_friend_memory_organizer.repository.PersonRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class EntityCreatorServiceTest {

    @Autowired
    private EntityCreatorService entityCreatorService;

    @Autowired
    private PersonRepository personRepository;

/**

    @Test
    void createEntities() {
        CategorizedWithUserMessage categorizedUserMessage = new CategorizedWithUserMessage("1. The user's name is Sven.; Personal Information; the user (Sven)\n" +
                "  2. The user works as a GenAI Expert.; Activity| Asset; the user (Sven)\n" +
                "  3. The user likes taking videos with his camera.; Activity| Preference; the user (Sven)\n" +
                "  4. The user is studying Story Telling.; Activity; the user (Sven)\n" +
                "  5. The user also studies Japanese.; Activity; the user (Sven)\n" +
                "  6. The user finds studying Japanese fun.; Emotion| Activity; the user (Sven)\n" +
                "  7. The user's friend's name is Sophie.; Personal Information; the user (Sven)\n" +
                "  8. Sophie has a YouTube channel where she does videos of landscapes as travel videos.; Asset| Activity; Sophie\n" +
                "  9. Sophie does not get many views on her YouTube channel.; Dislike| Activity; Sophie\n" +
                "  10. The user was in Japan last year in October.; Place, Time; the user (Sven)\n" +
                "  11. The user had a great experience in Japan.; Emotion| Place; the user (Sven)\n" +
                "  12. There was much to see in Japan according to the user.; Opinion| Place; the user (Sven)\n" +
                "  13. Yesterday, the user told Sophie about his experience in Japan.; Activity| Interaction; the user (Sven)| Sophie\n" +
                "  14. Sophie wants to visit Japan, especially Osaka.; Goal| Place; Sophie\n" +
                "  15. The user took some videos in his home town.; Activity; the user (Sven)\n" +
                "  16. The user finds the videos he took at his home town boring.; Dislike| Activity; the user (Sven)\n" +
                "  17. Sven is friends with Sophie.; Relationship; Sven| Sophie");
        TaggedMessage taggedMessage = new TaggedMessage("1. The user's name is Sven.; Personal Information; the user (Sven); name| Sven; person\n" +
                "  2. The user works as a GenAI Expert.; Activity| Asset; the user (Sven); work| GenAI Expert; career\n" +
                "  3. The user likes taking videos with his camera.; Activity| Preference; the user (Sven); video-taking| camera; hobby\n" +
                "  4. The user is studying Story Telling.; Activity; the user (Sven); studying| Story Telling; education\n" +
                "  5. The user also studies Japanese.; Activity; the user (Sven); studying| Japanese; language\n" +
                "  6. The user finds studying Japanese fun.; Emotion| Activity; the user (Sven); fun| studying Japanese; enjoyment\n" +
                "  7. The user's friend's name is Sophie.; Personal Information; the user (Sven); friend| Sophie; relationship\n" +
                "  8. Sophie has a YouTube channel where she does videos of landscapes as travel videos.; Asset| Activity; Sophie; YouTube channel| landscape videos| travel videos\n" +
                "  9. Sophie does not get many views on her YouTube channel.; Dislike| Activity; Sophie; low views| YouTube channel; popularity\n" +
                "  10. The user was in Japan last year in October.; Place, Time; the user (Sven); Japan| October; travel\n" +
                "  11. The user had a great experience in Japan.; Emotion| Place; the user (Sven); great experience| Japan\n" +
                "  12. There was much to see in Japan according to the user.; Opinion| Place; the user (Sven); much to see| Japan\n" +
                "  13. Yesterday, the user told Sophie about his experience in Japan.; Activity| Interaction; the user (Sven)| Sophie; sharing experience| Japan\n" +
                "  14. Sophie wants to visit Japan, especially Osaka.; Goal| Place; Sophie; visit| Japan| Osaka\n" +
                "  15. The user took some videos in his home town.; Activity; the user (Sven); video-taking| home town\n" +
                "  16. The user finds the videos he took at his home town boring.; Dislike| Activity; the user (Sven); boring| videos| home town\n" +
                "  17. Sven is friends with Sophie.; Relationship; Sven| Sophie; friendship");
        TopTopicMessage topTopicMessage = new TopTopicMessage(" Sven, person, name, friend, Sophie, relationship; People\n" +
                "   work, career, GenAI Expert; Occupation\n" +
                "   video-taking, camera, YouTube channel, landscape videos, travel videos; Hobbies\n" +
                "   studying, education, Japanese, studying Japanese, enjoyment; Learning\n" +
                "   fun, enjoyment; Entertainment\n" +
                "   Osaka, home town, October, visit, travel, great experience, much to see, sharing experience; Travel Destinations\n" +
                "   boring, low views, popularity; YouTube Content Performance");
        TimeMessage timeMessage = new TimeMessage("1. last year October; Time; 10, 11, 12\n" +
                "  2. yesterday; Time; 13");

        entityCreatorService.createEntities(categorizedUserMessage, taggedMessage, topTopicMessage, timeMessage);

        List<PersonEntity> allPersonEntity = personRepository.findAll();

        assertEquals(2, allPersonEntity.size());
    }

    @Test
    void createAdditionalRedundantEntities() {
        CategorizedWithUserMessage categorizeUserMessage = new CategorizedWithUserMessage("1. The user's wife is named Sophie.; Personal Information; the user (but not explicitly mentioned)| Sophie\n" +
                "   2. At present, Sophie is sick.; Emotion; Sophie\n" +
                "   3. The user and Sophie went shopping to Rewe yesterday.; Activity| Place; the user (implicitly)| Sophie\n" +
                "   4. They listened to some metal music in the car.; Activity; the user (implicitly)| Sophie\n" +
                "   5. After that, Sophie was baking a cake at home.; Activity; Sophie\n" +
                "   6. Sophie's friend Anna came over while Sophie was baking the cake.; Interaction| Personal Information; Sophie| Anna\n" +
                "   7. The user is married to Sophie.; Relationship; the user (but not explicitly mentioned)| Sophie\n" +
                "   8. Sophie has a friend named Anna.; Relationship; Sophie| Anna");
        TaggedMessage taggedMessage = new TaggedMessage("1. The user's wife is named Sophie.; Personal Information; the user (but not explicitly mentioned)| Sophie; spouse| partner| family member\n" +
                "   2. At present, Sophie is sick.; Emotion; Sophie; illness| health| medical condition\n" +
                "   3. The user and Sophie went shopping to Rewe yesterday.; Activity| Place; the user (implicitly)| Sophie; shopping| retail| grocery store\n" +
                "   4. They listened to some metal music in the car.; Activity; the user (implicitly)| Sophie; music| entertainment| metal genre\n" +
                "   5. After that, Sophie was baking a cake at home.; Activity; Sophie; baking| cooking| dessert| cake\n" +
                "   6. Sophie's friend Anna came over while Sophie was baking the cake.; Interaction| Personal Information; Sophie| Anna; visit| socializing| friend| baking\n" +
                "   7. The user is married to Sophie.; Relationship; the user (but not explicitly mentioned)| Sophie; marriage| spouse| partner\n" +
                "   8. Sophie has a friend named Anna.; Relationship; Sophie| Anna; friendship| acquaintance| contact");
        TopTopicMessage topTopicMessage = new TopTopicMessage(" spouse, partner, marriage, friendship; Relationships\n" +
                "   family member, visit, socializing; Family\n" +
                "   illness, health, medical condition; Healthcare\n" +
                "   shopping, retail, grocery store; Shopping\n" +
                "   music, entertainment; Entertainment\n" +
                "   metal genre, dessert, cake, baking, cooking; Miscellaneous Activities");
        TimeMessage timeMessage = new TimeMessage("1. yesterday; Time; 3\n" +
                "  2. present; Time; 2\n" +
                "   (No other explicit time indicators were found in the provided lines)");

        entityCreatorService.createEntities(categorizeUserMessage, taggedMessage, topTopicMessage, timeMessage);

        List<PersonEntity> allPersonEntity = personRepository.findAll();

        assertEquals(3, allPersonEntity.size());
    }*/
}