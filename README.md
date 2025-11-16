xmarkdown
# Bricks

## Overview
**Bricks** is a Java-based mobile game developed over a 2-year period as an annual capstone project.  
The project received a **grade of 100%** and demonstrates strong skills in Java, mobile development, Firebase/Firestore integration, and professional GitHub workflow.

## Features
- Mobile UI implemented in Java  
- Firebase / Firestore backend (NoSQL document database)  
- Clean project structure using Gradle with Kotlin DSL  
- UML diagrams describing system architecture  
- Full project documentation included  
- Version-controlled development using GitHub

## Project Reference
GitHub repository: **https://github.com/Pringnos/bricks**

## Getting Started

### Prerequisites
- Java (JDK 8 or later)  
- Android SDK (if building for Android)  
- Gradle (wrapper included)  
- A Firebase project configured with Firestore  

### Setup
1. Clone the repository:
   ```bash
   git clone https://github.com/Pringnos/bricks.git
   ```
2. Add your Firebase configuration file (google-services.json) into the appropriate /app folder.  
3. Verify Firebase dependencies inside build.gradle.kts.  
4. Build and run on an emulator or physical device.

## Project Structure
x
/app                  -> Application source code  
/gradle               -> Gradle wrapper files  
/settings.gradle.kts  
/build.gradle.kts  
/.gitignore  
/uml.puml             -> System architecture diagram  
/c.puml               -> Class interaction diagram  
/project-doc.docx     -> Full project documentation  
x

## Architecture & Design
- Structured using common mobile architecture patterns (MVC).  
- Firestore (NoSQL) stores documents in collections rather than relational tables.  
- Modular approach keeps UI, logic, and data layers clearly separated.  
- UML diagrams provide clarity on data flow and interactions.

## Testing & Quality
- Documentation describes the testing approach and results.  
- Commit history shows incremental, structured development.  
- Codebase organized for maintainability and extension.

## Achievements
- Awarded **100%** as a final project.  
- Demonstrates proficiency in:  
  - Java  
  - Firebase / Firestore  
  - Git & GitHub  
  - Data modeling  
  - Application architecture and documentation

## Future Enhancements
- Migrate to latest Android frameworks (Jetpack / AndroidX)  
- Add Firebase Authentication  
- Implement UI and integration testing  
- Expand feature set or modernize UI/UX  

## Contributing
Contributions are welcome.  
Fork the repository, create a feature branch, and open a pull request.

## License
MIT
