# Structure

## Engine Sequence

```mermaid
sequenceDiagram
    actor Developer
    participant Project
    participant Scene
    Developer->>Project: Create
    par
        Developer->>Scene: Create
        Project->>Scene: Add
    end
    Developer->>Project: Run
```
