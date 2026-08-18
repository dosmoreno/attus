# API Documentation - Attus Task Management System

## Base URL
```
http://localhost:8080/api/v1
```

## Endpoints

### Tasks

#### 1. Get All Tasks
- **Method:** `GET`
- **Endpoint:** `/tasks`
- **Description:** Retrieve all tasks sorted by priority and creation date
- **Response:** `200 OK`
- **Example Response:**
```json
[
  {
    "id": 1,
    "title": "Complete project setup",
    "description": "Setup the initial project structure",
    "status": "COMPLETED",
    "priority": "CRITICAL",
    "createdAt": "2024-01-15T10:30:00",
    "updatedAt": "2024-01-15T11:45:00",
    "completedAt": "2024-01-15T11:45:00"
  }
]
```

#### 2. Get Task by ID
- **Method:** `GET`
- **Endpoint:** `/tasks/{id}`
- **Description:** Retrieve a specific task by ID
- **Parameters:** 
  - `id` (path, required): Task ID
- **Response:** `200 OK`
- **Error Responses:**
  - `404 Not Found`: Task not found

#### 3. Create Task
- **Method:** `POST`
- **Endpoint:** `/tasks`
- **Description:** Create a new task
- **Request Body:**
```json
{
  "title": "New task title",
  "description": "Task description (optional)",
  "status": "PENDING",
  "priority": "HIGH"
}
```
- **Validations:**
  - `title`: Required, max 255 characters
  - `description`: Optional, max 1000 characters
  - `status`: Optional, defaults to PENDING
  - `priority`: Optional, defaults to MEDIUM
- **Response:** `201 Created`
- **Error Responses:**
  - `400 Bad Request`: Validation errors

#### 4. Update Task
- **Method:** `PUT`
- **Endpoint:** `/tasks/{id}`
- **Description:** Update an existing task
- **Parameters:**
  - `id` (path, required): Task ID
- **Request Body:**
```json
{
  "title": "Updated title",
  "description": "Updated description",
  "status": "IN_PROGRESS",
  "priority": "CRITICAL"
}
```
- **Validations:**
  - All fields are optional
  - Same constraints as Create Task
- **Response:** `200 OK`
- **Error Responses:**
  - `400 Bad Request`: Validation errors
  - `404 Not Found`: Task not found

#### 5. Delete Task
- **Method:** `DELETE`
- **Endpoint:** `/tasks/{id}`
- **Description:** Delete a task
- **Parameters:**
  - `id` (path, required): Task ID
- **Response:** `204 No Content`
- **Error Responses:**
  - `404 Not Found`: Task not found

#### 6. Filter Tasks by Status
- **Method:** `GET`
- **Endpoint:** `/tasks/filter/status`
- **Description:** Get tasks filtered by status
- **Query Parameters:**
  - `status` (required): PENDING, IN_PROGRESS, COMPLETED, CANCELLED
- **Response:** `200 OK`
- **Error Responses:**
  - `400 Bad Request`: Invalid status value

#### 7. Filter Tasks by Priority
- **Method:** `GET`
- **Endpoint:** `/tasks/filter/priority`
- **Description:** Get tasks filtered by priority
- **Query Parameters:**
  - `priority` (required): LOW, MEDIUM, HIGH, CRITICAL
- **Response:** `200 OK`
- **Error Responses:**
  - `400 Bad Request`: Invalid priority value

## Enums

### TaskStatus
- `PENDING`: Task is waiting to be started
- `IN_PROGRESS`: Task is currently being worked on
- `COMPLETED`: Task is completed
- `CANCELLED`: Task was cancelled

### TaskPriority
- `LOW`: Low priority task
- `MEDIUM`: Medium priority task
- `HIGH`: High priority task
- `CRITICAL`: Critical priority task

## Error Response Format
```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Invalid status value",
  "timestamp": "2024-01-15T10:30:00",
  "path": "uri=/api/v1/tasks"
}
```

## Example Requests

### Create a Task
```bash
curl -X POST http://localhost:8080/api/v1/tasks \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Fix bug in login module",
    "description": "Users cannot login with special characters in password",
    "status": "PENDING",
    "priority": "HIGH"
  }'
```

### Update a Task
```bash
curl -X PUT http://localhost:8080/api/v1/tasks/1 \
  -H "Content-Type: application/json" \
  -d '{
    "status": "COMPLETED",
    "priority": "CRITICAL"
  }'
```

### Get Tasks by Status
```bash
curl "http://localhost:8080/api/v1/tasks/filter/status?status=IN_PROGRESS"
```

### Get Tasks by Priority
```bash
curl "http://localhost:8080/api/v1/tasks/filter/priority?priority=CRITICAL"
```

### Delete a Task
```bash
curl -X DELETE http://localhost:8080/api/v1/tasks/1
```
