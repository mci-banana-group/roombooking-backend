# User Management (Admin)

## Quick Use (Most Important)
- **List users**: `GET /admin/users`
- **Create user**: `POST /admin/users`
- **Update role + permission**: `PATCH /admin/users/{userId}/role`
- **Delete user**: `DELETE /admin/users/{userId}`
- **Auth**: Admin JWT required (same as existing admin endpoints).
- **Delete semantics**: Deletion is blocked if the user has active bookings. If allowed, user references are nulled before deletion.

## Request/Response Shapes

### List Users
`GET /admin/users`

Response `200`:
```json
[
  {
    "id": 1,
    "firstName": "Ada",
    "lastName": "Lovelace",
    "email": "ada@example.com",
    "role": "LECTURER",
    "isAdmin": true
  }
]
```

### Create User
`POST /admin/users`

Request:
```json
{
  "email": "jane.doe@example.com",
  "password": "secret",
  "firstName": "Jane",
  "lastName": "Doe",
  "role": "STAFF",
  "permissionLevel": "USER"
}
```

Response `201`:
```json
{
  "id": 2,
  "firstName": "Jane",
  "lastName": "Doe",
  "email": "jane.doe@example.com",
  "role": "STAFF",
  "isAdmin": false
}
```

### Update Role + Permission
`PATCH /admin/users/{userId}/role`

Request:
```json
{
  "role": "LECTURER",
  "permissionLevel": "ADMIN"
}
```

Response `200`:
```json
{
  "id": 2,
  "firstName": "Jane",
  "lastName": "Doe",
  "email": "jane.doe@example.com",
  "role": "LECTURER",
  "isAdmin": true
}
```

### Delete User
`DELETE /admin/users/{userId}`

Response `204 No Content`

Conflict `409` (active bookings exist):
```json
{
  "message": "User deletion blocked by active bookings",
  "activeBookingsCount": 2
}
```

## Backend Behavior (What Happens)
1) **Create validation**:
   - `email`, `password`, `firstName`, `lastName` must be non-blank.
   - `role` and `permissionLevel` must be valid enum values.
2) **Uniqueness conflicts**:
   - Duplicate email triggers `409 Conflict`.
3) **Delete**:
   - User is loaded; missing -> `404`.
   - Active bookings are counted using: `RESERVED` or `CHECKED_IN` with `end >= now` (UTC).
   - If count > 0 -> `409` with `activeBookingsCount`.
   - Otherwise, booking and notification user references are nulled, then the user is deleted in one transaction.

## Error Responses (Common)
- **400 Bad Request**: validation errors (blank fields, invalid id).
- **403 Forbidden**: non-admin access.
- **404 Not Found**: user not found.
- **409 Conflict**: email exists or deletion blocked by active bookings.
