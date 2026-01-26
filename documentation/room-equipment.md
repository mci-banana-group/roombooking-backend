# Room Equipment on Create/Update (Admin)

## Quick Use (Most Important)
- **Create room**: `POST /admin/rooms`
- **Update room**: `PUT /admin/rooms/{roomId}`
- **Auth**: Admin JWT required (same as existing admin endpoints).
- **Add/change equipment**: Provide an `equipment` list with `type` and `quantity`.
- **Remove equipment**: Set `quantity` to `0` for that `type`.
- **Leave equipment unchanged**: Omit the `equipment` field or send an empty list.

### Example: Create With Equipment
```json
{
  "roomNumber": 501,
  "name": "Admin Created",
  "description": "Created via admin endpoint",
  "status": "FREE",
  "confirmationCode": "CONF-501",
  "capacity": 10,
  "buildingId": 1,
  "equipment": [
    { "type": "BEAMER", "quantity": 1 },
    { "type": "WHITEBOARD", "quantity": 2 }
  ]
}
```

### Example: Update Equipment (Change + Remove)
```json
{
  "roomNumber": 501,
  "name": "Admin Updated",
  "description": "Updated via admin endpoint",
  "status": "FREE",
  "confirmationCode": "CONF-501",
  "capacity": 12,
  "buildingId": 1,
  "equipment": [
    { "type": "BEAMER", "quantity": 2 },
    { "type": "WHITEBOARD", "quantity": 0 }
  ]
}
```

## Request Structure
Both endpoints accept the existing room fields plus an optional `equipment` array:
```json
{
  "roomNumber": "Int > 0",
  "name": "String (non-blank)",
  "description": "String (non-blank)",
  "status": "FREE | RESERVED | OCCUPIED",
  "confirmationCode": "String (non-blank)",
  "capacity": "Int >= 0",
  "buildingId": "Int (existing building)",
  "equipment": [
    { "type": "BEAMER | HDMI_CABLE | WHITEBOARD | DISPLAY", "quantity": "Int >= 0" }
  ]
}
```

## Response Notes
- Responses use the standard `RoomResponse`.
- The returned `RoomResponse` includes `building` (nullable). After building deletion, rooms will return `building: null`.

## Backend Behavior (What Happens)
1) **Room fields validated**: room number > 0, non-blank name/description/confirmation code, capacity >= 0.  
2) **Status validated**: must match one of `FREE`, `RESERVED`, `OCCUPIED`.  
3) **Building validated**: `buildingId` must exist.  
4) **Equipment validated (if provided)**:
   - `type` must match a known equipment type.
   - `quantity` must be `>= 0`.
   - Duplicate `type` values are rejected.
5) **Create**:
   - Room is created first.
   - Each equipment entry is inserted for that room.
6) **Update**:
   - Room fields are updated first.
   - For each equipment entry:
     - `quantity == 0` → existing item is removed (if present).
     - `quantity > 0` and item exists → quantity is updated.
     - `quantity > 0` and item does not exist → item is created.
   - Equipment types not included in the request are left unchanged.

## Error Responses (Common)
- **400 Bad Request**: validation errors (invalid status, invalid equipment type, negative quantity, duplicate equipment types, invalid room id).
- **403 Forbidden**: non-admin access.
- **404 Not Found**: room or building not found.

## Notes
- Equipment changes are **partial** on update; only supplied equipment types are modified.
- To clear all equipment, send all existing types with `quantity: 0`.
