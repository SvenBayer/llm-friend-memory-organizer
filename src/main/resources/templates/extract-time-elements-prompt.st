You have multiple lines of information (each line numbered).
Identify all time indicators mentioned across these lines (e.g., “tomorrow,” “today,” “yesterday,” “last week,” “this month,” “last year,” “last year October,” etc.).
Treat each consecutive time reference as one phrase (e.g., “Last year October” should be considered a single time indicator if it appears like that in the line).
If the same exact time indicator is found in multiple lines, list all the line numbers in a single entry.
Format for the Final Output

Output numbered lines (starting fresh at 1) for each unique time indicator you found.
For each time indicator, output one line in the format:
<time indicator>; <time elements like year and optionally month, day, time of day>; Time; <line numbers separated by commas>

No extra commentary or formatting.

Rules
- Do not split multi-word time expressions. Keep them as they appear (e.g., “Last year October” remains one reference).
- Only identify the time reference itself (e.g., “Last year October”), with no extra description of events.
- Combine all line numbers that mention the same time phrase.
- It is forbidden create lines for times that are not mentioned.
- Ignore other categories (Activity, Place, etc.).
- No duplicates or partial splitting of a multi-word time expression.
- Todays year is "2025" and todays month is "March" and todays day of the month is "15"
- If a time indicator is mentioned, use todays date to infer the time elements
- Do not mention anything too specific of the time elements if the elements are not mentioned. If only last year is mentioned, do not mention the month and day for example.

---
User’s message: "{userMessage}"
---

Required Output:
- A numbered list (starting at 1) of each distinct time indicator found, followed by the elements of the time indicator containing year, and optionally month, day, time of day “; Time;” and the comma-separated list of line numbers where it appears.
- No additional commentary or formatting beyond that.