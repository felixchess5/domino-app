# Domino Scoring Mobile App (OpenCV Auto-Count)

### Overview
A mobile app that uses **OpenCV** to automatically detect and score domino tiles from a live camera feed. It identifies tiles, counts the number of pips (dots) on each half, and computes scores based on selected rule sets.

---

## 🎯 Core Features

1.  **Live Camera Capture & Overlay**
    -   Uses CameraX for a real-time camera feed.
    -   Draws overlays on detected dominoes, showing their values.

2.  **Automatic Domino Recognition & Scoring**
    -   Detects domino outlines using OpenCV.
    -   Counts pips on each half to determine the domino's value.

3.  **Flexible Gameplay**
    -   Supports multiple players with a dynamic scoreboard.
    -   Turn-based scoring with "Confirm" and "Undo" actions.

4.  **Configurable Scoring Rules**
    -   **Total Pips**: Scores the sum of all pips on the detected dominoes.
    -   **All Fives**: Scores only if the total pips are a multiple of five.

---

## 🚀 Getting Started

1.  Clone this repository.
2.  Open the project in Android Studio. It includes the required OpenCV library as a local module.
3.  Build and run the app on a physical Android device.
4.  On the first screen, add one or more players.
5.  The game screen will appear, showing the live camera feed. Place dominoes in the view to see them detected.
6.  Use the "Settings" screen to change the scoring rule.
7.  Use "Confirm Turn" to add the detected score to the current player.

---

## 🗓️ Development Status

| Sprint | Features                                                | Status      |
| :----- | :------------------------------------------------------ | :---------- |
| 1      | Initial Python Prototype & Core Vision Logic          | ✅ Done     |
| 2      | Android MVP: Live Camera & Basic Detection              | ✅ Done     |
| 3      | Player & Game Management: Add Players, Scoreboard, Turns | ✅ Done     |
| 4      | Advanced Gameplay: Scoring Rules & Undo                 | ✅ Done     |
| 5      | UI Polish: Visual Overlays for Dominoes                 | ✅ Done     |
| 6      | Future: On-device ML, Cloud Sync, Analytics             | 🚧 Next Up  |
