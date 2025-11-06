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

## 🚀 Future Features

### 🎮 Game Modes
- **Individual Mode**: Classic 2-4 player gameplay
- **Couples Mode**: Team-based 2v2 domino matches
- **Tournament Mode**: Multi-round elimination brackets

### 🎲 Domino Set Support
- **Double 6**: Traditional 28-tile set (0-0 to 6-6)
- **Double 9**: Extended 55-tile set (0-0 to 9-9)
- **Double 12**: Professional 91-tile set (0-0 to 12-12)

### 🏆 Advanced Scoring Systems
- **Target Score Variants**: First to 100, 200, 500 points
- **Cuban Scoring**:
  - Bonuses: 100, 75, 50, 25 points for special plays
  - **Capicú**: Bonus for starting and ending a round
  - **Chuchazo**: Double points for certain combinations
- **Block Scoring**:
  - Couples scoring with lowest combined score wins
  - Individual scoring against player on the right

### 📊 Analytics & Features
- **Game Statistics**: Win rates, average scores, play patterns
- **Hand Recognition**: Detect and track individual player hands
- **Timer Integration**: Enforce turn time limits
- **Sound Effects**: Audio feedback for scoring and actions
- **Export/Import**: Save and share game configurations
- **Offline Mode**: Play without internet connectivity

### 🤝 Social Features
- **Multiplayer Sync**: Real-time scoring across multiple devices
- **Leaderboards**: Local and online ranking systems
- **Achievement System**: Unlock badges and milestones
- **Game Replay**: Review and analyze past games

### 🔧 Technical Enhancements
- **On-device ML**: Improved domino recognition accuracy
- **Cloud Sync**: Backup game data and settings
- **Dark Mode**: Enhanced UI themes
- **Accessibility**: Voice commands and screen reader support
- **Augmented Reality**: 3D overlays and enhanced visualization

---

## 🗓️ Development Status

| Sprint | Features                                                | Status      |
| :----- | :------------------------------------------------------ | :---------- |
| 1      | Initial Python Prototype & Core Vision Logic          | ✅ Done     |
| 2      | Android MVP: Live Camera & Basic Detection              | ✅ Done     |
| 3      | Player & Game Management: Add Players, Scoreboard, Turns | ✅ Done     |
| 4      | Advanced Gameplay: Scoring Rules & Undo                 | ✅ Done     |
| 5      | UI Polish: Visual Overlays for Dominoes                 | ✅ Done     |
| 6      | Game Modes & Domino Sets                                | 🚧 Next Up  |
| 7      | Advanced Scoring Systems                                 | 📋 Planned  |
| 8      | Analytics & Social Features                              | 📋 Planned  |
