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
7. The bike is rusty.
8. The user talked with a woman.
9. The sandwich is in the fridge.

The following is just an example with the persons mentioned that helps you understand the task but do not take it as output data:
1. The user's name is John.; THE_USER_JOHN
2. The user likes to play soccer.; THE_USER_JOHN
3. The user played soccer with his brother.; THE_USER_JOHN| THE_USERS_BROTHER_JAMES
4. The user has a brother named James.; THE_USER_JOHN| THE_USERS_BROTHER_JAMES
5. The user went cycling in the park with his wife.; THE_USER_JOHN | THE_USERS_WIFE
6. The user is married to his wife.; THE_USER_JOHN | THE_USERS_WIFE
7. The bike is rusty.;
8. The user talked with a woman.; THE_USER_JOHN | WOMAN
9. The sandwich is in the fridge.;

Only add persons that are mentioned in the sentence. Do not add any other persons. Do not add anything that is not a person.
Adress the user as "the user" and if you have the user's name, adress the user as "the user (name)". So the user John would be "the user (John)".
---
User’s message: "{userMessage}"
---