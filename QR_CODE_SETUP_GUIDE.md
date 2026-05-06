# QR Code Email Setup Guide

## Overview
When an admin confirms a reservation, an email is sent to the user with an embedded QR code. When the admin scans this QR code, they are redirected to a page that marks the reservation as "Already Used".

---

## Backend Changes

✅ **Already implemented:**
1. `QRCodeGenerator.java` - Generates QR codes
2. `EmailService.java` - Sends HTML emails with embedded QR codes
3. `ReservationEvenementService.java` - Passes reservation ID to email
4. `ReservationEvenementController.java` - Endpoint `/api/reservations-evenement/{id}/mark-used-qr`

---

## Frontend Changes

### 1. Add the New Component to Your Routing

In your main routing module, add:

```typescript
import { Routes } from '@angular/router';
import { MarkReservationUsedComponent } from './features/event/components/mark-reservation-used/mark-reservation-used.component';

export const routes: Routes = [
  // ... your existing routes ...
  {
    path: 'reservations/mark-used/:id',
    component: MarkReservationUsedComponent
  }
];
```

### 2. Update ReservationEvenementService

The service already has the `markAsAlreadyUsed()` method, but verify it exists in your service file.

---

## Running with ngrok

Since your app is not deployed yet, use **ngrok** to expose your local server to the internet so QR codes can redirect properly.

### Step 1: Install ngrok

```bash
# On Windows with Chocolatey
choco install ngrok

# Or download from https://ngrok.com/download
```

### Step 2: Start Your Local Backend

```bash
cd C:\Users\jawhe\Desktop\Backend-Rawabet
.\mvnw.cmd spring-boot:run
# Or run it from your IDE
# Runs on: http://localhost:8081/rawabet
```

### Step 3: Start Your Frontend (Angular)

```bash
cd your-angular-project
ng serve
# Runs on: http://localhost:4200
```

### Step 4: Expose with ngrok

```bash
# Terminal 1: Expose the backend (port 8081)
ngrok http 8081

# Terminal 2: Expose the frontend (port 4200)
ngrok http 4200
```

You'll get URLs like:
- Backend: `https://xxxx-xx-xxx-xxx.ngrok.io` → `http://localhost:8081`
- Frontend: `https://yyyy-yy-yyy-yyy.ngrok.io` → `http://localhost:4200`

### Step 5: Update Environment Configuration

In your Angular `environment.ts`, update the API URL:

```typescript
export const environment = {
  production: false,
  apiUrl: 'https://xxxx-xx-xxx-xxx.ngrok.io/rawabet' // Your ngrok backend URL
};
```

Also update `app.frontend.url` in `application.properties`:

```properties
app.frontend.url=https://yyyy-yy-yyy-yyy.ngrok.io
```

---

## QR Code Flow

1. **Reservation Confirmed**
   - Admin clicks "Modify" → selects "CONFIRMED" → clicks "Update Status"

2. **Email Sent with QR Code**
   - Backend generates QR code pointing to: `https://yyyy-yy-yyy-yyy.ngrok.io/reservations/mark-used/{reservationId}`
   - QR code is embedded in the HTML email

3. **QR Code Scanned**
   - Admin scans QR code with phone/QR reader
   - Redirected to the frontend component

4. **Mark as Used**
   - Component automatically calls: `PUT /api/reservations-evenement/{id}/mark-used-qr`
   - Shows success/error message

---

## Testing the Flow

1. Confirm a reservation (ensure user has valid email)
2. Check email for QR code
3. Scan QR code with your phone or QR code reader app
4. Should be redirected to success page
5. Reservation marked as "Already Used"

---

## Troubleshooting

**QR Code not working?**
- Check ngrok URLs are updated in both backend and frontend configs
- Verify `apiUrl` in environment.ts is correct
- Check browser console for API errors

**Email not received?**
- Verify SMTP credentials in `application.properties`
- Check spam folder
- Look at server logs for mail errors

**ngrok connection issues?**
- Restart ngrok (URLs change each time)
- Update configs with new URLs
- Check ngrok is running: `ngrok config check`

---

## Permanent Deployment

When you deploy to production:

1. Update `apiUrl` to your production backend URL
2. Update `app.frontend.url` to your production frontend URL
3. No need for ngrok anymore
4. QR codes will work seamlessly


