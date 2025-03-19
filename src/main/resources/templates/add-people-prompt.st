You are given multiple lines of information (each line numbered).
Identify all distinct persons mentioned across these lines.

For each person, output a single numbered line in the format with the category "Person" as second column:
<description of the person>; Person; <mulitple alias mentioned in the message for the person separated by comma>

Ignore any other categories like Activity, Place, etc. Focus only on which lines mention each person.
No additional commentary or formatting except continuous numbering for the lines you add.

Required Output:
- One numbered line per person discovered with their aliases, like "the user".

Example output which includes the people you find including their aliases:
The user with the name John; Person; John, the user
The friend Jenny; Person; Jenny, the friend

Create the formatted rows for each person listed here and merge similar aliases
---
People: "{people}"
---

---
User’s message: "{userMessage}"
---