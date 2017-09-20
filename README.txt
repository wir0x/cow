BUHO TRACKING SOLUTION

Change Log:

v 1.12 - 2016/06/08
    * Centralize notifications (SOS, DROP, CUT BATTERY, CONNECT BATTERY)
    * Fix UI - User management when create new user (accordion not open).
    * Fix bugs on JOBS.
    * Remove unnecessary JOBS
    * Add DROP alarm for GPS Watch.
    * Refactor DTO for request (current position)
    * Fix Info windows on current position (not again when close)
    * Fix bug rest services on current position 
    * Add subscription status on current position (accordion detailed)
    * Add UI to check devices status (for back end)
    * Filter by subscription on historical (user can't see historical if expiration 
    * Add UI on historical status subscriptions.
    * Fix bug UI when set phone number for notification SOS or other.
    * Fix bug UI labels (names)
    * Changes domain Tigo Money
    * Fix bug on Subscription management.
    * Set correct date when create notification.
    * Fix UI - Hide changed password over user.
    * fix UI - label on current position.
    * Add transfer device to another account.
    * Add seller (Ubidata or Swissbytes) when create account.
    * Fix message and simplify some forms.
    


v 1.9 - 2015/09/29
    * Fix bug firefox explorer when type sos numbers
    * Clean database architecture
    * add javamelody

v 1.6 - 2015/07/17
- General
    * Footer to stick to the bottom of the page always (desktop cut only)
    * Clean top menu bar when less than 1040px width

- Server
    * Rest Server validate that only a super admin can change the login name for an account owner

- Current Position
    * Current position, add explanation text somewhere to what the clock icon means

- Control
    * Add reset button

- Device Configuration
    * Add reset button

- User Management
    * Add reset button

- Detailed report
    * Add detailed historical positions report

- Company Management
    * Review the search on the table, doesn't seem to work correctly
    * Add UI validations for the change password dialog

- Device Management
    * Add device type to the backend device administration
    * Validate phone # formats, add placeholders, etc.

v 1.5 - 2015/06/17
- Control:
    * Add validation for Email(email valids [abc@abc.ab]).
    * Add validation for telephones (only numbers allowef, 8 digits message).
    * Add loader in map.
    * Add select box for select all week days.
    * Changes in css.

- Current Position
    * No se cierra el infoWindows tras pasar el tiempo de refresh de las posiciones
      menu se puede abrir y cerrar, el js esta a bajo del html current-position.html.
    * Add loader in map

- Backend
    * Send mail for limit sms 90% of sms used.

- State of account:
    * Add form to see status of accounts.
    (start date y end date of subscription, sms used, sms unsued, type of package)

- General
    * Info alter shows under menu.

v 1.4 - 2015/05/14
- First loading of current position view doesn't always show all devices for the account.
- Don't allow multiple detail windows on the map.
- Sometimes clicking on a position on the map opens the detail, moves map and hides detail again.
- Login, don't show wrong password error when there is no connection to server.
- Selected menu highlight (in menu bar) is lost when page reloads, or other actions are taken.
- Fix navigation arrows in the icon picker.
- Session expired comes 2x when opening app.buho.bo after session has timed out.
- Show all fences on map when no specific fence selected.
- GV300 review configuration, seems like it doesn't buffer positions.
- Config device: mobile cut design is not very nice.
- Add the ability to delete a company.

v 1.3 - 2015/05/08
- Add the option to not show a position entry on the map.
- Devices sometimes show 0km/h in places that they were in movement.
- Current Position, clicking on a point on the map will change the zoom level.
- Store all Device commands that are received in a separate table.
- Current position, zoom level too detailed when only one device in account.

v 1.2 - 2015/05/07
- Backed add device to company needs to add it to account owner as well
- Device Config: Only show SOS Alerts and Battery Alerts for devices that have them
- Current Position: first load doesn't always show all device on the map
- Backend device assignment to company
- Device Config: Transaction failed error message
- Control: Time format HH:mm
- Alerts in all forms: change locations buttons
- Geo Fences: remove date range
- User Configuration, move the Change Password button
- Add Support menu item with contact form and contact details
- Make sure all buttons have tooltips
- Backend, device management, add the option to clear all positions for a device
- Cleanup all JS libraries, use minified versions for libs, remove unused libs.
- Merge all JS (and CSS) into one big JS file, possibly 2 and use that for prod
- Add GZIP compression to app.buho.bo

v 1.1 - 2015/04/28
- Current Position and History loading fix
- Add Demo Positions
- Add table notifications for save and sending messages
- Add sos
- Fix historical panel name and add icon.

v 1.0 - 2015/04/23
- Initial versioning
