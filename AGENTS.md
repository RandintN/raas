# AGENTS.md: Strategic Guidelines for RaaS Development

## Executive Summary

This document defines the operational constraints and technical standards for AI agents interacting with the **Robson Cassiano as a Service (RaaS)** codebase. Compliance ensures architectural integrity, security of API integrations, and optimal MCP tool performance.

---

## 1. Architectural Constraints

### 1.1 Java 24 & Modern Syntax

* **Preview Features**: Utilize Java 24 preview features where appropriate (e.g., modern pattern matching, simplified `main` methods).
* **Immutability**: Prefer `record` types for DTOs and internal domain models.
* **Virtual Threads**: Default to structured concurrency patterns if handling high-volume I/O operations.

### 1.2 Configuration Management

* **Zero-Key Policy**: Never hardcode API keys or secrets. Use `.env` or `application.properties` with environment variable overrides.
* **Strong Typing**: All configuration must be handled via `@ConfigurationProperties` with `@Validated` Jakarta Bean Validation constraints.
* **Conditional Loading**: Tools MUST be conditionally loaded based on property availability to ensure the system remains functional even if some integrations are unconfigured.

---

## 2. MCP Tool Implementation Standards

### 2.1 Tool Structure

Each tool group must follow the established pattern:

* `*Tools.java`: Contains `@Tool` annotations; handles parameter mapping and logging.
* `*Service.java`: Business logic, external API calls, and data transformation.
* `*Properties.java`: Configuration binding.

### 2.2 Execution Design

* **Naming Convention**: Use kebab-case for tool names (e.g., `newsletter-get-latest-posts`).
* **Documentation**: Every `@Tool` must have a descriptive `description` parameter for the LLM.
* **Error Handling**: Wrap external API calls in robust try-catch blocks. Return meaningful error messages to the LLM instead of raw stack traces.

---

## 3. Testing & Validation

### 3.1 Profile Enforcement

* **Profile**: Always use `@ActiveProfiles("test")` for integration tests.
* **Isolation**: Tests must not rely on live production API keys. Use `application-test.properties` for mock values.

### 3.2 Verification Workflow

* **mvn test**: Before finalizing any logic change, run the full test suite.
* **MockServer**: Use `MockRestServiceServer` or similar mechanisms to simulate external API responses (Beehiiv, Transistor, YouTube).

---

## 4. Development Workflows

### 4.1 Integration with GitHub

* **Issue Tracking**: Reference GitHub issue numbers in commit messages.
* **PR Quality**: Ensure all new tools follow the SWR (Stale-While-Revalidate) pattern if they involve slow external data fetching.

### 4.2 Code Maintenance

* **Dependency Alignment**: Keep Spring AI versions synchronized with the latest milestones (currently `1.1.0-M3`).
* **Documentation**: Update `README.md` if new tools or configuration properties are added.

---

## 5. Decision Tree for Agents

| Scenario | Primary Action |
| :--- | :--- |
| **Adding a new external API** | 1. Create `Properties` -> 2. Create `Service` -> 3. Add to `RaasConfiguration` |
| **Fixing a Tool bug** | 1. Reproduce in `*Tests.java` -> 2. Fix in `Service` |
| **Optimizing Performance** | 1. Implement SWR/Caching -> 2. Check Virtual Thread usage |
| **Configuration Error** | 1. Verify Bean Validation in `Properties` -> 2. Check `.env.example` alignment |


## 6. Security Considerations

* **API Keys**: Never commit API keys to version control. Use environment variables or `.env` files and make sure to add them to `.gitignore`.

* **Sensitive Data**: Never commit sensitive data to version control. Use environment variables or `.env` files and make sure to add them to `.gitignore`.
