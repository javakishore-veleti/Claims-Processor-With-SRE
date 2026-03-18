# Architecture Diagrams (PlantUML)

Render these `.puml` files using any PlantUML-compatible tool:
- **IntelliJ IDEA:** PlantUML Integration plugin (real-time preview)
- **VS Code:** PlantUML extension by jebbs
- **Online:** [plantuml.com/plantuml](https://www.plantuml.com/plantuml/uml)
- **CLI:** `java -jar plantuml.jar Docs/Diagrams/*.puml` (generates PNG/SVG)

## Diagram Index

| # | File | Description | Scope |
|---|---|---|---|
| 01 | `01_enterprise_bdat_architecture.puml` | Business, Data, Application, Technology layers | Platform-wide |
| 02 | `02_aws_fargate_baseline.puml` | AWS Fargate architecture **without** Application Signals | AWS_99 baseline |
| 03 | `03_aws_fargate_with_signals.puml` | AWS Fargate architecture **with** Application Signals (AWS_100) | Blog: SLO 2026-03-13 |
| 04 | `04_ecs_task_comparison.puml` | Side-by-side: ECS task definition before vs after AWS_100 | Blog: SLO 2026-03-13 |
| 05 | `05_application_signals_slo_flow.puml` | SLO data flow: app → ADOT → Application Signals → SLO engine | Blog: SLO 2026-03-13 |
| 06 | `06_github_actions_workflow.puml` | AWS_99 (deploy) → AWS_100 (blog toggle) → AWS_98 (destroy) | CI/CD |
