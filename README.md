# Domino Scoring Mobile App (OpenCV Auto-Count)

### Overview
A mobile app that uses **OpenCV** to automatically detect and score domino tiles from a live camera feed. It identifies tiles, counts the number of pips (dots) on each half, and computes scores based on selected rule sets (e.g., total pips or all-fives).

---

## 🎯 Core Features

1.  **Live Camera Capture**
    - Uses CameraX for real-time camera feed.
    - Real-time detection and scoring overlay.

2.  **Automatic Domino Recognition**
    - Detects tile outlines on flat surfaces.
    - Splits each tile into two halves.

3.  **Pip Detection**
    - Counts circular blobs (pips) via `SimpleBlobDetector`.
    - Robust against lighting and angle variations.

4.  **Scoring Logic**
    - Computes tile values `(a,b)` where `a ≤ b`.
    - Configurable scoring:
        - Total Pips
        - All Fives (Muggins)

---

## 🚀 Getting Started

1.  Clone this repository.
2.  Open the project in Android Studio.
3.  Build and run the app on a physical Android device.
4.  Press the "Start" button to begin domino detection.

---

## 🗓️ Roadmap

| Sprint | Features |
|:-------|:----------|
| 1 | Python prototype (photo testing) + Manual confirm UI |
| 2 | **Android MVP with live detection + scoring (Current Stage)** |
| 3 | Player tracking + score history |
| 4 | On-device ML model for pip validation |
| 5 | Multiplayer & social features |
