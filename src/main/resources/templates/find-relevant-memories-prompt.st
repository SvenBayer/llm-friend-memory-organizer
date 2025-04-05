You task is to analyze which top {maxMemories} memories are relevant to the user's message.
You will find the top {maxMemories} memories that can help the user.

Rules to follow:
- Return the selected top {maxMemories} memories in a numbered list.
- Create a enumerated list out of the original top {maxMemories} memories sentences that you choose to be relevant.

Example output:
1. The user has a cat named Whiskers.
2. The user enjoys hiking in the mountains.
3. The user has a brother who lives in New York.

---
Memories:
{memories}
---

---
User’s message: "{userMessage}"
---