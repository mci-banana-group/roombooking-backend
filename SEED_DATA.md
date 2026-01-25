# Seed Data Overview

This document describes the default seed data created by `seedData()` in `src/main/kotlin/plugins/Seeding.kt`.

## Accounts (all passwords are `password`)
- admin@mci.edu (Admin, Staff)
- sara.staff@mci.edu (User, Staff)
- milan.staff@mci.edu (User, Staff)
- lecturer@mci.edu (User, Lecturer)
- mia.lecturer@mci.edu (User, Lecturer)
- david.lecturer@mci.edu (User, Lecturer)
- lena.student@mci.edu (User, Student)
- tom.student@mci.edu (User, Student)
- nina.student@mci.edu (User, Student)
- armin.student@mci.edu (User, Student)

## Buildings and Rooms

### Main Building
- 101 Seminar Room A (cap 20, FREE) equipment: BEAMER x1, WHITEBOARD x2
- 102 Seminar Room B (cap 24, RESERVED) equipment: BEAMER x1, HDMI_CABLE x2
- 103 Lecture Hall 1 (cap 80, FREE) equipment: BEAMER x2, DISPLAY x1
- 201 Project Studio (cap 30, OCCUPIED) equipment: WHITEBOARD x3, HDMI_CABLE x2
- 202 Conference Room (cap 18, FREE) equipment: DISPLAY x2, BEAMER x1
- 203 Thesis Room (cap 12, RESERVED) equipment: WHITEBOARD x1
- 204 Workshop Studio (cap 26, FREE) equipment: BEAMER x1, WHITEBOARD x2

### Tech Center
- 110 Computer Lab 1 (cap 28, FREE) equipment: DISPLAY x28, HDMI_CABLE x20
- 120 Robotics Lab (cap 16, OCCUPIED) equipment: WHITEBOARD x2, DISPLAY x4
- 210 Innovation Classroom (cap 32, FREE) equipment: BEAMER x1, WHITEBOARD x2
- 220 Makerspace (cap 14, RESERVED) equipment: BEAMER x1, HDMI_CABLE x4
- 230 Network Lab (cap 18, FREE) equipment: DISPLAY x6, HDMI_CABLE x6
- 240 AR/VR Studio (cap 12, RESERVED) equipment: DISPLAY x4

### Library Annex
- 10 Quiet Study 1 (cap 12, FREE) equipment: WHITEBOARD x1
- 11 Quiet Study 2 (cap 10, FREE) equipment: WHITEBOARD x1
- 20 Group Study 1 (cap 8, RESERVED) equipment: DISPLAY x1
- 21 Group Study 2 (cap 8, FREE) equipment: DISPLAY x1
- 30 Media Study (cap 6, RESERVED) equipment: DISPLAY x1

### Innovation Hub
- 301 Startup Garage (cap 26, FREE) equipment: BEAMER x1, WHITEBOARD x2
- 302 Design Lab (cap 20, FREE) equipment: BEAMER x1, DISPLAY x2
- 401 Board Room (cap 14, RESERVED) equipment: DISPLAY x1, HDMI_CABLE x2
- 402 Strategy Room (cap 16, FREE) equipment: DISPLAY x2
- 501 Investor Lounge (cap 10, RESERVED) equipment: DISPLAY x1

### Sports Hall
- 1 Gym Hall (cap 60, OCCUPIED) equipment: DISPLAY x1
- 2 Dance Studio (cap 25, FREE) equipment: DISPLAY x1
- 3 Yoga Room (cap 18, FREE) equipment: DISPLAY x1

## Bookings (relative to seed time)
- Lecturer Meeting (lecturer@mci.edu) in Main Building 101, RESERVED, starts in +2h
- Project Sync (mia.lecturer@mci.edu) in Main Building 201, CHECKED_IN, started -4h
- Data Science Lab (david.lecturer@mci.edu) in Tech Center 110, RESERVED, starts in +1d
- Robotics Demo (sara.staff@mci.edu) in Tech Center 120, RESERVED, starts in +2d
- Study Group (lena.student@mci.edu) in Library Annex 20, NO_SHOW, started -1d
- Startup Pitch Prep (tom.student@mci.edu) in Innovation Hub 301, RESERVED, starts in +3d
- Board Review (milan.staff@mci.edu) in Innovation Hub 401, RESERVED, starts in +6h
- Dance Workshop (nina.student@mci.edu) in Sports Hall 2, CANCELLED, started -10h
- Guest Lecture (lecturer@mci.edu) in Main Building 103, RESERVED, starts in +4d
- Marketing Workshop (armin.student@mci.edu) in Main Building 202, CHECKED_IN, started -3h

## Confirmations and Notifications
- Presence confirmations: Project Sync (QR_CODE), Marketing Workshop (NFC)
- Notifications: Meeting reminder, check-in confirmation, no-show notice
