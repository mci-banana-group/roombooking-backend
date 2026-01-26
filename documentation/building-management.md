# Building Management (Admin)

## Quick Use (Most Important)
- **Create building**: `POST /admin/buildings`
- **Update building**: `PUT /admin/buildings/{buildingId}`
- **Delete building**: `DELETE /admin/buildings/{buildingId}`
- **Auth**: Admin JWT required (same as existing admin endpoints).
- **Delete semantics**: Building deletion is blocked if any rooms are still assigned.

## Request/Response Shapes

### Create Building
`POST /admin/buildings`

Request:
```json
{
  "name": "Main Building",
  "address": "123 University Ave"
}
```

Response `201`:
```json
{
  "id": 1,
  "name": "Main Building",
  "address": "123 University Ave"
}
```

### Update Building
`PUT /admin/buildings/{buildingId}`

Request:
```json
{
  "name": "Main Building - West Wing",
  "address": "123 University Ave"
}
```

Response `202`:
```json
{
  "id": 1,
  "name": "Main Building - West Wing",
  "address": "123 University Ave"
}
```

### Delete Building
`DELETE /admin/buildings/{buildingId}`

Response `204 No Content`

Conflict `409` (rooms still assigned):
```json
{
  "message": "Building deletion blocked by existing rooms",
  "roomsCount": 3
}
```

## Backend Behavior (What Happens)
1) **Create/Update validation**:
   - `name` must be non-blank.
   - `address` must be non-blank.
2) **Uniqueness conflicts** (if DB constraints exist):
   - Duplicate values trigger `409 Conflict`.
3) **Delete**:
   - Building is loaded; missing → `404`.
   - If any rooms exist in the building → `409` with the room count.
   - Building record is deleted only when there are **no** rooms assigned.

## Error Responses (Common)
- **400 Bad Request**: validation errors (blank name/address, invalid id).
- **403 Forbidden**: non-admin access.
- **404 Not Found**: building not found.
- **409 Conflict**: uniqueness conflict (if applicable) or rooms still assigned.
