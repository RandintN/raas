---
description: Check for recent project changes (stack, structure, features) and update README.md accordingly, then commit and push.
---

1. **Analyze Recent Changes**:
    - Review `pom.xml` for version upgrades (Spring Boot, Java, dependencies).
    - Scan project directories for new tools or structural changes.
    - Check the most recent commits using `git log -n 5` to understand recent feature implementations.
2. **Verify README Content**:
    - Read `README.md` and identify sections that are outdated (e.g., Technology Stack, Feature list, Prerequisites, Setup instructions).
    - Ensure the "Available MCP Tools" section matches the current implementation.
3. **Update README**:
    - Use `replace_file_content` or `multi_replace_file_content` to apply updates.
    - Ensure formatting is consistent and professional.
4. **Finalize Changes**:
    - Stage the update: `git add README.md`.
    - Commit the change with a clear message: `docs: update README with recent stack and feature changes`.
    - Push to the repository: `git push`.
