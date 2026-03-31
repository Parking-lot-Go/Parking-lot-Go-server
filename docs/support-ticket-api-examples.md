# Support Ticket API Examples

This document summarizes example payloads for Android integration.

## Enums

- `ticketType`
  - `INQUIRY`
  - `FEATURE_REQUEST`
- `category`
  - Inquiry categories: `INQUIRY_ACCOUNT`, `INQUIRY_APP_USAGE`, `INQUIRY_BUG_REPORT`, `INQUIRY_OTHER`
  - Feature request categories: `FEATURE_NEW_CAPABILITY`, `FEATURE_UI_UX`, `FEATURE_PERFORMANCE`, `FEATURE_OTHER`
- `status`
  - `PENDING`
  - `IN_PROGRESS`
  - `RESOLVED`
  - `REJECTED`

## Create Inquiry

`POST /api/support-tickets`

```json
{
  "ticketType": "INQUIRY",
  "category": "INQUIRY_BUG_REPORT",
  "title": "Favorite button is not working",
  "content": "When I tap favorite on the parking lot detail screen, it returns to the previous state after refresh.",
  "extraContent1": "Occurs after login with Kakao and after reinstalling the app.",
  "extraContent2": "reply@example.com",
  "appVersion": "1.2.0",
  "osVersion": "Android 14",
  "deviceModel": "SM-S918N"
}
```

## Create Feature Request

`POST /api/support-tickets`

```json
{
  "ticketType": "FEATURE_REQUEST",
  "category": "FEATURE_UI_UX",
  "title": "Need a filter for EV charging spots",
  "content": "It would help to filter parking lots that have EV charging stations.",
  "extraContent1": "I currently have to open each parking lot detail screen manually.",
  "extraContent2": "Show a dedicated filter chip on the search screen.",
  "appVersion": "1.2.0",
  "osVersion": "Android 14",
  "deviceModel": "Pixel 8"
}
```

## List Tickets

`GET /api/support-tickets?type=INQUIRY&status=PENDING&mine=true`

```json
{
  "success": true,
  "data": [
    {
      "id": 101,
      "userId": 12,
      "ticketType": "INQUIRY",
      "category": "INQUIRY_BUG_REPORT",
      "title": "Favorite button is not working",
      "content": "When I tap favorite on the parking lot detail screen, it returns to the previous state after refresh.",
      "extraContent1": "Occurs after login with Kakao and after reinstalling the app.",
      "extraContent2": "reply@example.com",
      "appVersion": "1.2.0",
      "osVersion": "Android 14",
      "deviceModel": "SM-S918N",
      "status": "PENDING",
      "adminMemo": null,
      "createdAt": "2026-04-01T10:15:30",
      "updatedAt": "2026-04-01T10:15:30",
      "deletedAt": null
    }
  ],
  "message": "Success"
}
```

## Update Ticket

`PATCH /api/support-tickets/{id}`

```json
{
  "title": "Favorite button resets after refresh",
  "content": "Reproducible on detail and map popup screens.",
  "extraContent1": "Only happens on Android 14 so far.",
  "deviceModel": "SM-S918N"
}
```

## Admin Status Update

`PATCH /api/admin/support-tickets/{id}/status`

```json
{
  "status": "IN_PROGRESS",
  "adminMemo": "Issue reproduced on Galaxy S23 and assigned to Android team."
}
```

## Field Semantics

- For `INQUIRY`
  - `extraContent1`: context note
  - `extraContent2`: reply email
- For `FEATURE_REQUEST`
  - `extraContent1`: problem
  - `extraContent2`: expected improvement
