# Jest Card Game (Java)

> Academic project – LO02 (A25) – Université de Technologie de Troyes (UTT)

## Description

This project was developed as part of the **LO02 software engineering course** at UTT (Autumn 2025).
It consists of implementing the card game **Jest** in Java using object-oriented programming principles and software design patterns.

## Game Overview

Jest is a card game where players build their own set of cards ("Jest") in order to achieve the highest score.

During each round:

* Players receive cards
* Some cards are visible, others are hidden
* Players select cards from opponents
* Cards are added to their Jest

At the end of the game, scores are calculated based on card combinations and trophy conditions.

## Project Objectives

The project was structured in several stages:

* **Design phase**

  * UML diagrams (use case, class diagram, sequence diagram)
  * Study of design patterns (Strategy, Visitor)

* **Development phase**

  * Implementation of the game engine
  * Command-line version of the game

* **Architecture phase**

  * Implementation of MVC architecture
  * Separation between logic and interface

## Architecture

The system is organized into multiple modules:

* **Model**: game entities (cards, players, trophies)
* **Engine**: game flow and state management
* **Rules**: game rules and variations
* **Score system**: score calculation logic
* **MVC**: separation of concerns between UI and logic

## Design Patterns

* **Strategy Pattern**
  Used to implement different player strategies (AI behavior)

* **Visitor Pattern**
  Used to compute scores in a flexible and extensible way

* **MVC Architecture**
  Separates the system into model, view, and controller

## UML Diagram

### Class Diagram

![Class Diagram](uml/class-diagram.png)

## Technologies

* Java
* Object-Oriented Programming
* MVC Architecture
* Design Patterns (Strategy, Visitor)

## Project Structure

* `src/` : Java source code
* `uml/` : UML diagrams

## How to Run

```bash
javac Main.java
java Main
```

## Author

Jiarui Huang
