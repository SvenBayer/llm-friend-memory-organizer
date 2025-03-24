Assing to each sentence the Persons involved.
Do not interpret the user’s message. Only append to each line the persons involved in the structures provided.
Ensure that you follow the custom structure provided below. You must follow the structure provided below.
<input line>; Persons: <persons involved in this sentence>;

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

The following is just an example with the persons mentioned that helps you understand the task but do not take it as output data:
1. The user's name is John.; the user (John)| John
2. The user likes to play soccer.; the user (John)
3. The user played soccer with his brother.; the user (John)| the user's brother (James)
4. The user has a brother named James.; the user (John)| the user's brother (James)
5. The user went cycling in the park with his wife.; the user (John)| the wife
6. The user is married to his wife.; the user| the wife
7. The user has a bike.; the user (John)
8. The user talked with a woman.; the user (John)| woman
9. The woman had a dog.; woman

Only add persons that are mentioned in the sentence. Do not add any other persons. Do not add anything that is not a person.
Adress the user as "the user" and if you have the user's name, adress the user as "the user (name)". So the user John would be "the user (John)".
---
User’s message: "{userMessage}"
---