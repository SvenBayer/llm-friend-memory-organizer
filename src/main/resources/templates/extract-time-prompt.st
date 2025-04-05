You have multiple lines of information (each line numbered).
Today’s date is 2025-04-05 (Month: April, Day: 5, Year: 2025).
Identify all time indicators mentioned across these lines (e.g., “tomorrow,” “today,” “yesterday,” “last week,” “this month,” “last year,” “last year October,” etc.).
Treat each consecutive time reference as one phrase (e.g., “Last year October” should be considered a single time indicator if it appears like that in the line).
If the same exact time indicator is found in multiple lines, list all the line numbers in a single entry.

Format for the Final Output:
- Output numbered lines (starting fresh at 1) for each unique time indicator you found.
- No extra commentary or formatting.
- For each time indicator, output one line in the format:
1. <time as date>; <line numbers separated by pipe>

Rules:
- Do not split multi-word time expressions. Keep them as they appear (e.g., “Last year October” remains one reference).
- Only identify the time reference itself (e.g., “Last year October”), with no extra description of events.
- Combine all line numbers that mention the same time phrase.
- It is forbidden to create lines for times that are not explicitly mentioned by the user.
- Ignore other categories (Activity, Place, etc.).
- No duplicates or partial splitting of a multi-word time expression.
- Do not mention time indicators that you do not find exactly as stated in the lines.

---
User’s message: "{userMessage}"
---

Required Output:
- A numbered list (starting at 1) of each distinct time indicator found as date, then a ";" and the pipe-separated list of line numbers where it appears.
- No additional commentary or formatting beyond that.