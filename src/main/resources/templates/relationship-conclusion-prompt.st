Goal: Identify only the personal relationships mentioned in the user’s message (e.g., marital status, friendship, family ties, boss/employee, etc.).
Instruction: For each personal relationship, generate a single summarized sentence that states the relationship. Do not include any additional details (activities, preferences, places, etc.).
Output Format:
Create an enumerated list (1, 2, 3, …).
Each item must be a single sentence that describes the relationship (e.g., “John is married to Jane.”, “Mike is the brother of Tom.”).
If no personal relationships are mentioned, return nothing.

---
User’s message: "{userMessage}"
---

An enumerated list of only personal relationships explicitly stated or implied.
No mention of any activities, assets, places, or emotions.
If no relationships are found, output nothing.