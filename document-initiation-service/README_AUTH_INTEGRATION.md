# Auth-Service Integration for DMS Workflow

This document explains how the Document Initiation Service integrates with the Auth-Service to fetch users from specific groups for workflow states.

## Overview

The DMS workflow system now supports selecting groups from the auth-service, and automatically fetches all users belonging to those groups. This allows you to:

1. Select a group when creating a workflow state
2. Automatically get all users from that group
3. Use those users for workflow assignments

## API Endpoints

### 1. Get All Groups
```
GET /api/v3/auth-integration/groups
```
Returns all available groups from auth-service for dropdown selection.

**Response:**
```json
{
  "success": true,
  "message": "Groups fetched successfully",
  "data": [
    {
      "groupsId": 1,
      "name": "Group 1",
      "description": "First group",
      "userIds": [1, 2, 3, 4, 5]
    },
    {
      "groupsId": 2,
      "name": "Group 2", 
      "description": "Second group",
      "userIds": [6, 7, 8, 9, 10]
    }
  ]
}
```

### 2. Get Users by Group ID
```
GET /api/v3/auth-integration/groups/{groupId}/users
```
Returns all users from a specific group.

**Response:**
```json
{
  "success": true,
  "message": "Users fetched successfully",
  "data": [
    {
      "userId": 1,
      "userFirstName": "John",
      "userMiddleName": "M",
      "userLastName": "Doe",
      "userEmailId": "john.doe@example.com",
      "userEmployeeId": "EMP001",
      "status": "ACTIVE",
      "designation": "Manager"
    }
  ]
}
```

## Workflow State Creation with Group

When creating a workflow state, you can now include group information:

### Request Body for Creating Workflow State
```json
{
  "stateName": "Review State",
  "orderNumber": 1,
  "branches": ["branch1", "branch2"],
  "activities": ["review", "approve"],
  "child": ["child1"],
  "groupId": 2,
  "groupName": "Reviewers Group",
  "workflowId": 1
}
```

### Response with Users
```json
{
  "success": true,
  "message": "State created successfully",
  "data": {
    "id": 1,
    "stateName": "Review State",
    "orderNumber": 1,
    "branches": ["branch1", "branch2"],
    "activities": ["review", "approve"],
    "child": ["child1"],
    "groupId": 2,
    "groupName": "Reviewers Group",
    "users": [
      {
        "userId": 6,
        "userFirstName": "Jane",
        "userMiddleName": "K",
        "userLastName": "Smith",
        "userEmailId": "jane.smith@example.com",
        "userEmployeeId": "EMP006",
        "status": "ACTIVE",
        "designation": "Reviewer"
      }
    ]
  }
}
```

## Database Schema Changes

The `WorkflowState` entity now includes:
- `groupId`: Long - The ID of the selected group from auth-service
- `groupName`: String - The name of the selected group

## Configuration

Make sure your `application.yml` has the correct auth-service URL:

```yaml
# In AuthServiceClient.java
@FeignClient(name = "auth-service", url = "http://localhost:8082")
```

## Usage Flow

1. **Frontend**: Call `/api/v3/auth-integration/groups` to get available groups
2. **Frontend**: Show groups in a dropdown for user selection
3. **Frontend**: When creating workflow state, include selected `groupId` and `groupName`
4. **Backend**: Automatically fetches users from auth-service using the groupId
5. **Backend**: Returns workflow state with populated users list

## Error Handling

- If auth-service is unavailable, the system will return empty user lists
- All errors are logged for debugging
- API responses include success/failure status and error messages

## Example Frontend Integration

```javascript
// 1. Fetch available groups
const groups = await fetch('/api/v3/auth-integration/groups');
const groupsData = await groups.json();

// 2. Show groups in dropdown
const groupSelect = document.getElementById('groupSelect');
groupsData.data.forEach(group => {
  const option = document.createElement('option');
  option.value = group.groupsId;
  option.textContent = group.name;
  groupSelect.appendChild(option);
});

// 3. Create workflow state with selected group
const createWorkflowState = async (stateData) => {
  const selectedGroup = groupsData.data.find(g => g.groupsId == stateData.groupId);
  
  const requestBody = {
    ...stateData,
    groupName: selectedGroup.name
  };
  
  const response = await fetch('/api/v3/globalWorkflows/1/states/create', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(requestBody)
  });
  
  return response.json();
};
``` 