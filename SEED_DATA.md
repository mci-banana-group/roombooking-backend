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

### MCI I (Universitätsstraße 15, 6020 Innsbruck)
- 234 (cap 26, FREE) seating: Schule mit Mittelgang
- 301 (cap 66, FREE) seating: Schule mit Mittelgang, equipment: BEAMER x1, WHITEBOARD x2, HDMI_CABLE x1
- 302 (cap 66, FREE) seating: Schule mit Mittelgang, equipment: BEAMER x1, WHITEBOARD x2, HDMI_CABLE x1
- 303 (cap 54, FREE) seating: Schule mit Mittelgang
- 304 (cap 54, FREE) seating: Schule mit Mittelgang
- 305 (cap 60, FREE) seating: Schule mit Mittelgang
- 306 (cap 56, FREE) seating: Schule mit Mittelgang
- 307 (cap 32, FREE) seating: Schule mit Mittelgang
- 308 (cap 18, FREE) seating: Konferenzraum, equipment: DISPLAY x1, HDMI_CABLE x1
- 309 (cap 45, FREE) seating: Schule mit Mittelgang
- 310 (cap 32, FREE) seating: Schule mit Mittelgang
- 401/402 (cap 20, FREE) seating: U-Bestuhlung
- 403 (cap 28, FREE) seating: Parlament
- 404 (cap 28, FREE) seating: Parlament
- 405 (cap 28, FREE) seating: Parlament
- 406 (cap 32, FREE) seating: Parlament

### MCI II (Universitätsstraße 7, 6020 Innsbruck)
- 051 (cap 61, FREE) seating: Schule mit Mittelgang
- 052 (cap 69, FREE) seating: Schule mit Mittelgang
- 053 (cap 78, FREE) seating: Schule mit Mittelgang
- 162 (cap 8, FREE) seating: WOW-Raum
- 163 (cap 8, FREE) seating: WOW-Raum
- 164 (cap 12, FREE) seating: Besprechungsraum, equipment: DISPLAY x1, HDMI_CABLE x1
- 551 (cap 40, FREE) seating: Schule mit Mittelgang
- 552 (cap 50, FREE) seating: Schule mit Mittelgang

### MCI III (Weiherburggasse 8, 6020 Innsbruck)
- 011 (cap 54, FREE) seating: Schule mit Mittelgang
- 012 (cap 54, FREE) seating: Schule mit Mittelgang
- 013 (cap 54, FREE) seating: Schule mit Mittelgang
- 014 (cap 30, FREE) seating: Schule mit Mittelgang
- 111 (cap 54, FREE) seating: Schule mit Mittelgang
- 112 (cap 60, FREE) seating: Schule mit Mittelgang
- 113 (cap 30, FREE) seating: Schule mit Mittelgang

### MCI IV (Maximilianstraße 2, 6020 Innsbruck)
- 4B-001 (cap 29, FREE) seating: Schule mit Mittelgang
- 4B-003 (cap 30, FREE) seating: Schule mit Mittelgang
- 4B-005 (cap 23, FREE) seating: Schule mit Mittelgang
- 4B-006 (cap 33, FREE) seating: Schule mit Mittelgang
- 4B-007 (cap 30, FREE) seating: Schule mit Mittelgang
- 4B-008 (cap 16, FREE) seating: Besprechungsraum, equipment: DISPLAY x1, HDMI_CABLE x1
- 4A-020 (cap 58, FREE) seating: Schule mit Mittelgang, equipment: BEAMER x1, WHITEBOARD x2, HDMI_CABLE x1
- 4A-024 (cap 68, FREE) seating: Schule mit Mittelgang, equipment: BEAMER x1, WHITEBOARD x2, HDMI_CABLE x1
- 4A-027 (cap 60, FREE) seating: Schule mit Mittelgang
- 1A-135 (cap 45, FREE) seating: e-exam-Raum/SR
- 4B-115 (cap 22, FREE) seating: EDV-Raum, equipment: DISPLAY x22, WHITEBOARD x1
- 4A-393 (cap 48, FREE) seating: Schule mit Mittelgang
- 4A-438 (cap 36, FREE) seating: Schule mit Mittelgang
- 4A-439 (cap 68, FREE) seating: Schule mit Mittelgang
- 4C-501 (cap 12, FREE) seating: Schule mit Mittelgang
- 4C-502 (cap 25, FREE) seating: EDV-Raum, equipment: DISPLAY x25, WHITEBOARD x1
- 4C-503 (cap 25, FREE) seating: EDV-Raum, equipment: DISPLAY x25, WHITEBOARD x1
- 4C-504 (cap 27, FREE) seating: EDV-Raum
- 4C-505 (cap 45, FREE) seating: EDV-Raum

### MCI V (Kapuzinergasse 9, 6020 Innsbruck)
- 181 (cap 40, FREE) seating: Schule mit Mittelgang
- 182 (cap 40, FREE) seating: Schule mit Mittelgang
- 183 (cap 40, FREE) seating: Schule mit Mittelgang
- 184 (cap 44, FREE) seating: Schule mit Mittelgang
- 185 (cap 52, FREE) seating: Schule mit Mittelgang
- 283 (cap 40, FREE) seating: Schule mit Mittelgang

## Bookings (relative to seed time)
- Lecturer Meeting (lecturer@mci.edu) in MCI I 301, RESERVED, starts in +2h
- Project Sync (mia.lecturer@mci.edu) in MCI II 164, CHECKED_IN, started -4h
- Data Science Lab (david.lecturer@mci.edu) in MCI IV 4B-115, RESERVED, starts in +1d +1h
- Robotics Demo (sara.staff@mci.edu) in MCI IV 4C-502, RESERVED, starts in +2d +4h
- Study Group (lena.student@mci.edu) in MCI III 014, NO_SHOW, started -1d -2h
- Startup Pitch Prep (tom.student@mci.edu) in MCI V 181, RESERVED, starts in +3d +2h
- Board Review (milan.staff@mci.edu) in MCI I 308, RESERVED, starts in +6h
- Student Union Workshop (nina.student@mci.edu) in MCI I 401/402, CANCELLED, started -10h
- Guest Lecture (lecturer@mci.edu) in MCI IV 4A-020, RESERVED, starts in +4d +1h
- Marketing Workshop (armin.student@mci.edu) in MCI V 185, CHECKED_IN, started -3h

## Confirmations and Notifications
- Presence confirmations: Project Sync (QR_CODE), Marketing Workshop (NFC)
- Notifications: Reminder (Lecturer Meeting), check-in confirmation (Project Sync), no-show notice (Study Group)
