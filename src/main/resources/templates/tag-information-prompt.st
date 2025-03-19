Add tags and kewords to each sentence of the user's message based on the structure provided.
Do not interpret the user’s message.
Leave each sentence entry as is and just extend it with specific keywords and tags.
A keywords are important words from the sentence.
Keep tags are general to describe the sentence.
Ensure that you follow the custom structure provided below. You must follow the structure provided below. You must leave the original sentences untouched. Only extend the sentences with the keywords and tags.
<sentence with categories and involved people>; <keywords which are directly from the sentence>; <tags which describe the sentence and are general>

The following is just example input that you can use to understand the task but do not take it as input data for your output:
1. The user's name is John.; Personal Information; the user;
2. The user likes to play soccer.; Activity, Preference; the user;
3. The user played soccer with his brother.; Activity, Interaction; the user, the user's brother;
4. The user has a brother named James.; Relationship; the user, the brother;
5. The user went cycling in the park with his wife.; Activity, Place; the user;
6. The user is married to his wife.; Relationship; the user, the wife;
7. The user has a bike.; Asset; the user;
8. The user talked with a woman.; Activity, Interaction; the user, woman;
9. The woman had a dog.; Personal Information, Asset; woman;

The following is just example output that helps you understand the task but do not take it as output data:
1. The user's name is John.; Personal Information; the user; name, John; person;
2. The user likes to play soccer.; Activity, Preference; the user; soccer, playing; sports;
3. The user played soccer with his brother.; Activity, Relationship; the user, the user's brother; soccer, brother; sports, family;
4. The user has a brother named James.; Relationship; the user, the brother; brother, James; family;
5. The user went cycling in the park with his wife.; Activity, Place; the user; cycling, park; sports, outdoors;
6. The user is married to his wife.; Relationship; the user, the wife; married; family;
7. The user has a bike.; Asset; the user; bike; equipment;
8. The user talked with a woman.; Activity, Relationship; the user, woman; talking; conversation;
9. The woman had a dog.; Personal Information, Asset; woman; dog; animal;

You must keep the existing information as is and add to each entry at the the specific keywords and the general tags.
---
User’s message: "{userMessage}"
---