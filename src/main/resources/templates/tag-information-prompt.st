Add tags and keywords to each sentence of the user's message based on the structure provided.
Do not interpret the user’s message.
Leave each sentence entry as is and just extend it with specific keywords and tags.
A keywords are important words from the sentence.
Keep tags are general to describe the sentence.
Ensure that you follow the custom structure provided below. You must follow the structure provided below. You must leave the original sentences untouched. Only extend the sentences with the keywords and tags.
<sentence>; <tags and keywords which describe the sentence and are general>

The following is just example input that you can use to understand the task but do not take it as input data for your output:
1. The user's name is John.
2. The user likes to play soccer.
3. The user played soccer with his brother.
4. The user has a brother named James.
5. The user went cycling in the park with his wife.
6. The user is married to his wife.
7. The user has a bike.
8. The user talked with a woman.
9. The woman had a dog.

The following is just example output that helps you understand the task but do not take it as output data:
1. The user's name is John.; name| person
2. The user likes to play soccer.; soccer| playing| sports
3. The user played soccer with his brother.; soccer| brother| sports| family
4. The user has a brother named James.; brother| James| family
5. The user went cycling in the park with his wife.; cycling| park| sports| outdoors
6. The user is married to his wife.; married| wife| family| couple
7. The user has a bike.; bike| equipment
8. The user talked with a woman.; talking| conversation
9. The woman had a dog.; woman| dog| animal

You must keep the existing information as is and add to each entry at the the specific keywords and the general tags.
---
User’s message: "{userMessage}"
---