# CyberSafeCheck

CyberSafeCheck is an Android digital-footprint self-assessment application built with Kotlin, following the software design patterns and component architectures established in *The Big Nerd Ranch Guide*. The app enables users to assess everyday cybersecurity habits across key categories, track security risk posture, and persist decisions locally.

---

## Milestone 1: Core Architecture & UI Foundation

Milestone 1 establishes the foundational Model-View-ViewModel (MVVM)-like architecture, UI layouts, and view-binding patterns.

### Features Implemented

* **Domain Models:** Defined core `RiskItem` and `RiskCategory` models covering passwords, social media, online scams, and cyberbullying threats.
* **In-Memory Seed Data (`RiskLab`):** Seed repository pattern initialized with pre-configured cybersecurity assessment questions and guidance explanations.
* **Fragment-Driven Navigation:**
* `ChecklistFragment`: Displays an interactive list of cybersecurity evaluation checks using `RecyclerView`.
* `RiskDetailFragment`: Displays granular remediation advice and threat explanations when an item is selected.


* **Safe View Handling:** Integrated Android ViewBinding across all layouts and adapters to eliminate unsafe view lookups.

### Tagged Release

* Git Tag: `milestone-1`

---

## Milestone 2: Adaptive UI & Room Persistence

Milestone 2 transitions the application from in-memory seed models to an offline-first, persistent architecture using Android Jetpack Room, Kotlin Coroutines, and responsive ConstraintLayout structures.

### Features Implemented

* **Jetpack Room Database:**
* `CyberSafeDatabase`: Database provider configuring SQLite access.
* `RiskAnswerEntity`: Persisted table entity capturing item IDs, questions, categories, detailed remediation text, and user toggle states (`isFlagged`).
* `AssessmentEntity`: Entity tracking historical risk assessments, submission timestamps, flagged habit counts, and total question counts.
* `RiskDao` & `AssessmentDao`: Thread-safe Room Data Access Objects utilizing Kotlin `suspend` functions.


* **Repository Layer (`RiskRepository`):**
* Mediates between the local Room SQLite tables and the UI.
* Offloads all read/write operations to background threads via `Dispatchers.IO`.
* Automatically seeds database records on first application launch.


* **ConstraintLayout UI Modernization:**
* Converted `list_item_risk.xml` and `fragment_risk_detail.xml` completely to `ConstraintLayout`.
* Implemented brand-aligned UI styling: high-contrast brand blue header bar (`#1976D2`), uppercase category badges, material switch toggles, and clean row dividers matching design specifications.


* **State Persistence Across Lifecycles:**
* Risk evaluation toggle states persist across app closures and system process kills via coroutines launched in `viewLifecycleOwner.lifecycleScope`.



### Tagged Release

* Git Tag: `milestone-2`

---

## Technical Stack

* **Language:** Kotlin
* **Target SDK:** 36
* **Min SDK:** 29
* **Persistence:** Android Jetpack Room 2.6.1
* **Concurrency:** Kotlin Coroutines (Lifecycle Scope, `Dispatchers.IO`)
* **UI Toolkit:** ConstraintLayout, RecyclerView, Material Components (MaterialSwitch)
* **Architecture:** Repository Pattern, Single Activity with Fragments, ViewBinding
