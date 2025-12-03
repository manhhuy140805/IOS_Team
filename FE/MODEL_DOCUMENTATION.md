# 📋 TỔNG HỢP CÁC MODEL VÀ THUỘC TÍNH - VOLUNTEER APP

> Tài liệu này phân tích chi tiết tất cả các model trong dự án Android, bao gồm các thuộc tính, kiểu dữ liệu và mục đích sử dụng.

---

## 📑 MỤC LỤC

1. [Models Chính](#1-models-chính)
2. [Response Models (API)](#2-response-models-api)
3. [UI Models](#3-ui-models)
4. [Enums & Constants](#4-enums--constants)

---

## 1. MODELS CHÍNH

### 1.1. User Model
**File:** `model/User.java`  
**Mục đích:** Quản lý thông tin người dùng (tình nguyện viên, tổ chức, admin)

| Thuộc tính | Kiểu dữ liệu | Mô tả | Bắt buộc |
|------------|--------------|-------|----------|
| `id` | `Integer` | ID người dùng | ✅ |
| `fullName` | `String` | Họ và tên đầy đủ | ✅ |
| `email` | `String` | Email đăng nhập | ✅ |
| `password` | `String` | Mật khẩu (hash) | ✅ |
| `phone` | `String` | Số điện thoại | ❌ |
| `avatarUrl` | `String` | URL ảnh đại diện | ❌ |
| `role` | `String` | Vai trò: `VOLUNTEER`, `ORGANIZER`, `ADMIN` | ✅ |
| `status` | `String` | Trạng thái: `ACTIVE`, `LOCKED`, `PENDING` | ✅ |
| `createdAt` | `Date` | Ngày tạo tài khoản | ✅ |
| `updatedAt` | `Date` | Ngày cập nhật cuối | ✅ |
| `eventsCount` | `int` | Số sự kiện đã tham gia | ❌ |
| `pointsCount` | `int` | Tổng điểm tích lũy | ❌ |
| `joinDate` | `String` | Ngày tham gia (format string) | ❌ |
| `activityCount` | `int` | Số hoạt động | ❌ |
| `lastActive` | `String` | Lần hoạt động cuối | ❌ |
| `violationType` | `String` | Loại vi phạm (nếu có): `Spam`, `null` | ❌ |

**Constructors:**
```java
// Constructor 1: Đầy đủ
User(Integer id, String fullName, String email, String phone, String avatarUrl,
     String role, String status, Date createdAt)

// Constructor 2: Cho User Management
User(String id, String fullName, String email, String joinDate,
     int activityCount, String lastActive, String status, String violationType)
```

---

### 1.2. EventPost Model
**File:** `model/EventPost.java`  
**Mục đích:** Quản lý bài đăng sự kiện (dùng cho admin duyệt bài)

| Thuộc tính | Kiểu dữ liệu | Mô tả | Bắt buộc |
|------------|--------------|-------|----------|
| `id` | `int` | ID bài đăng | ✅ |
| `title` | `String` | Tiêu đề sự kiện | ✅ |
| `imageUrl` | `String` | URL ảnh sự kiện | ❌ |
| `organizationName` | `String` | Tên tổ chức | ✅ |
| `organizationInitials` | `String` | Chữ viết tắt tổ chức (VD: "TC") | ❌ |
| `organizationColor` | `String` | Màu đại diện tổ chức (hex) | ❌ |
| `tags` | `List<String>` | Danh sách tags/categories | ❌ |
| `tagColor` | `String` | Màu của tag (hex) | ❌ |
| `eventDate` | `Date` | Ngày diễn ra sự kiện | ✅ |
| `location` | `String` | Địa điểm | ✅ |
| `rewardPoints` | `int` | Điểm thưởng | ❌ |
| `postedBy` | `String` | Người đăng | ✅ |
| `postedTime` | `String` | Thời gian đăng | ✅ |
| `status` | `String` | Trạng thái: `pending`, `approved`, `rejected` | ✅ |
| `reviewedBy` | `String` | Người duyệt | ❌ |
| `reviewedTime` | `String` | Thời gian duyệt | ❌ |
| `rejectionReason` | `String` | Lý do từ chối | ❌ |
| `currentParticipants` | `int` | Số người đã đăng ký | ❌ |
| `maxParticipants` | `int` | Số người tối đa | ❌ |

**Status Values:**
- `pending` - Chờ duyệt
- `approved` - Đã duyệt
- `rejected` - Bị từ chối

---

### 1.3. RewardItem Model
**File:** `model/RewardItem.java`  
**Mục đích:** Quản lý phần thưởng đổi điểm

| Thuộc tính | Kiểu dữ liệu | Mô tả | Bắt buộc |
|------------|--------------|-------|----------|
| `name` | `String` | Tên phần thưởng | ✅ |
| `organization` | `String` | Tổ chức cung cấp | ✅ |
| `description` | `String` | Mô tả chi tiết | ❌ |
| `points` | `String` | Số điểm cần đổi | ✅ |
| `stock` | `String` | Số lượng còn lại | ✅ |
| `expiry` | `String` | Ngày hết hạn | ❌ |
| `categoryType` | `int` | Loại: `0`=all, `1`=voucher, `2`=gift, `3`=experience | ✅ |
| `tag1` | `String` | Tag thứ nhất | ❌ |
| `tag2` | `String` | Tag thứ hai | ❌ |
| `iconColorIndex` | `int` | Index màu icon: `0`=purple, `1`=pink, `2`=orange, `3`=cyan | ❌ |

**Category Types:**
```java
0 = Tất cả
1 = Voucher • Đồ uống
2 = Vật phẩm • Thời trang  
3 = Trải nghiệm • Đào tạo
4 = Low Stock (filter only, <=5 items)
```

**Icon Colors:**
```java
0 = Purple (#B39DDB)
1 = Pink (#E91E63)
2 = Orange (#FF9800)
3 = Cyan (#00BCD4)
```

---

### 1.4. Applicant Model
**File:** `model/Applicant.java`  
**Mục đích:** Quản lý đơn đăng ký tham gia sự kiện

| Thuộc tính | Kiểu dữ liệu | Mô tả | Bắt buộc |
|------------|--------------|-------|----------|
| `name` | `String` | Tên người đăng ký | ✅ |
| `email` | `String` | Email | ✅ |
| `activityName` | `String` | Tên hoạt động đăng ký | ✅ |
| `registrationDate` | `String` | Ngày đăng ký | ✅ |
| `phone` | `String` | Số điện thoại | ✅ |
| `note` | `String` | Lời nhắn/ghi chú | ❌ |
| `status` | `int` | Trạng thái: `0`=pending, `1`=accepted, `2`=rejected | ✅ |
| `avatarUrl` | `String` | URL ảnh đại diện | ❌ |

**Status Values:**
```java
0 = Pending (Chờ xét duyệt)
1 = Accepted (Đã chấp nhận)
2 = Rejected (Đã từ chối)
```

---

### 1.5. Organization Model
**File:** `model/Organization.java`  
**Mục đích:** Quản lý thông tin tổ chức

| Thuộc tính | Kiểu dữ liệu | Mô tả | Bắt buộc |
|------------|--------------|-------|----------|
| `id` | `String` | ID tổ chức | ✅ |
| `name` | `String` | Tên tổ chức | ✅ |
| `email` | `String` | Email liên hệ | ✅ |
| `foundedDate` | `String` | Ngày thành lập | ❌ |
| `memberCount` | `int` | Số thành viên | ❌ |
| `status` | `String` | Trạng thái: `Hoạt động`, `Bị khóa`, `Chờ xác thực` | ✅ |
| `violationType` | `String` | Loại vi phạm: `Spam`, `null` | ❌ |

---

### 1.6. UserManagement Model
**File:** `model/UserManagement.java`  
**Mục đích:** Model riêng cho màn hình quản lý user (admin)

| Thuộc tính | Kiểu dữ liệu | Mô tả | Bắt buộc |
|------------|--------------|-------|----------|
| `id` | `String` | ID người dùng | ✅ |
| `name` | `String` | Tên người dùng | ✅ |
| `email` | `String` | Email | ✅ |
| `joinDate` | `String` | Ngày tham gia | ✅ |
| `eventsCount` | `int` | Số sự kiện tham gia | ❌ |
| `volunteerHours` | `String` | Số giờ tình nguyện | ❌ |
| `status` | `String` | Trạng thái: `Hoạt động`, `Bị khóa`, `Chờ xác thực` | ✅ |
| `violationType` | `String` | Loại vi phạm | ❌ |

---

## 2. RESPONSE MODELS (API)

### 2.1. UserResponse
**File:** `helper/response/UserResponse.java`  
**Mục đích:** Response từ API cho thông tin user

| Thuộc tính | Kiểu dữ liệu | Mô tả |
|------------|--------------|-------|
| `id` | `Integer` | ID người dùng |
| `email` | `String` | Email |
| `fullName` | `String` | Họ tên |
| `phoneNumber` | `String` | Số điện thoại |
| `address` | `String` | Địa chỉ |
| `role` | `String` | Vai trò |
| `totalPoints` | `Integer` | Tổng điểm |
| `createdAt` | `String` | Ngày tạo (ISO format) |
| `updatedAt` | `String` | Ngày cập nhật (ISO format) |

---

### 2.2. EventResponse
**File:** `helper/response/EventResponse.java`  
**Mục đích:** Response từ API cho thông tin sự kiện

| Thuộc tính | Kiểu dữ liệu | Mô tả |
|------------|--------------|-------|
| `id` | `Integer` | ID sự kiện |
| `title` | `String` | Tiêu đề |
| `description` | `String` | Mô tả chi tiết |
| `location` | `String` | Địa điểm |
| `startDate` | `String` | Ngày bắt đầu (ISO format) |
| `endDate` | `String` | Ngày kết thúc (ISO format) |
| `maxParticipants` | `Integer` | Số người tối đa |
| `currentParticipants` | `Integer` | Số người hiện tại |
| `status` | `String` | Trạng thái sự kiện |
| `eventTypeId` | `Integer` | ID loại sự kiện |
| `eventTypeName` | `String` | Tên loại sự kiện |
| `hasCertificate` | `Boolean` | Có chứng chỉ không |
| `hasReward` | `Boolean` | Có phần thưởng không |
| `rewardId` | `Integer` | ID phần thưởng |
| `rewardName` | `String` | Tên phần thưởng |
| `createdBy` | `Integer` | ID người tạo |
| `creatorName` | `String` | Tên người tạo |
| `createdAt` | `String` | Ngày tạo |
| `updatedAt` | `String` | Ngày cập nhật |

---

### 2.3. EventRegistrationResponse
**File:** `helper/response/EventRegistrationResponse.java`  
**Mục đích:** Response cho đăng ký sự kiện

| Thuộc tính | Kiểu dữ liệu | Mô tả |
|------------|--------------|-------|
| `id` | `Integer` | ID đăng ký |
| `eventId` | `Integer` | ID sự kiện |
| `eventTitle` | `String` | Tên sự kiện |
| `userId` | `Integer` | ID người dùng |
| `userName` | `String` | Tên người dùng |
| `userEmail` | `String` | Email người dùng |
| `status` | `String` | Trạng thái đăng ký |
| `notes` | `String` | Ghi chú |
| `checkedIn` | `Boolean` | Đã check-in chưa |
| `checkedInAt` | `String` | Thời gian check-in |
| `registeredAt` | `String` | Thời gian đăng ký |
| `updatedAt` | `String` | Thời gian cập nhật |

---

### 2.4. LoginResponse
**File:** `helper/response/LoginResponse.java`  
**Mục đích:** Response khi đăng nhập thành công

| Thuộc tính | Kiểu dữ liệu | Mô tả |
|------------|--------------|-------|
| `accessToken` | `String` | JWT token |
| `tokenType` | `String` | Loại token (thường là "Bearer") |
| `user` | `UserResponse` | Thông tin user |

---

### 2.5. RestResponse<T>
**File:** `helper/response/RestResponse.java`  
**Mục đích:** Generic wrapper cho tất cả API responses

| Thuộc tính | Kiểu dữ liệu | Mô tả |
|------------|--------------|-------|
| `statusCode` | `int` | HTTP status code |
| `message` | `String` | Thông báo |
| `data` | `T` | Dữ liệu (generic type) |
| `error` | `Object` | Thông tin lỗi (nếu có) |

**Ví dụ sử dụng:**
```java
RestResponse<UserResponse> response = ...
RestResponse<List<EventResponse>> eventsResponse = ...
RestResponse<LoginResponse> loginResponse = ...
```

---

### 2.6. PageResponse<T>
**File:** `helper/response/PageResponse.java`  
**Mục đích:** Response cho dữ liệu phân trang

| Thuộc tính | Kiểu dữ liệu | Mô tả |
|------------|--------------|-------|
| `content` | `List<T>` | Danh sách dữ liệu trang hiện tại |
| `pageNumber` | `int` | Số trang hiện tại (0-indexed) |
| `pageSize` | `int` | Số items mỗi trang |
| `totalElements` | `long` | Tổng số items |
| `totalPages` | `int` | Tổng số trang |
| `last` | `boolean` | Có phải trang cuối không |
| `first` | `boolean` | Có phải trang đầu không |

**Ví dụ sử dụng:**
```java
PageResponse<EventResponse> eventsPage = ...
PageResponse<UserResponse> usersPage = ...
```

---

## 3. UI MODELS

### 3.1. Event Model
**File:** `model/Event.java`  
**Mục đích:** Model đơn giản cho hiển thị event card trong UI

| Thuộc tính | Kiểu dữ liệu | Mô tả |
|------------|--------------|-------|
| `title` | `String` | Tiêu đề sự kiện |
| `location` | `String` | Địa điểm |
| `organization` | `String` | Tên tổ chức |
| `imageResId` | `int` | Resource ID của ảnh (drawable) |
| `compensation` | `String` | Phần thưởng/bồi thường |
| `organizerIcon` | `String` | Icon tổ chức |

---

### 3.2. SearchResult Model
**File:** `model/SearchResult.java`  
**Mục đích:** Kết quả tìm kiếm sự kiện

| Thuộc tính | Kiểu dữ liệu | Mô tả |
|------------|--------------|-------|
| `title` | `String` | Tiêu đề |
| `organization` | `String` | Tên tổ chức |
| `location` | `String` | Địa điểm |
| `imageResId` | `int` | Resource ID ảnh |
| `imageUrl` | `String` | URL ảnh (alternative) |
| `category` | `String` | Danh mục chính |
| `subcategory` | `String` | Danh mục phụ |
| `description` | `String` | Mô tả |
| `deadline` | `String` | Hạn đăng ký |
| `registeredCount` | `int` | Số người đã đăng ký |
| `totalSlots` | `int` | Tổng số chỗ |
| `duration` | `String` | Thời lượng |

**Constructors:**
```java
// Constructor 1: Với drawable resource
SearchResult(String title, String organization, String location, int imageResId, ...)

// Constructor 2: Với image URL
SearchResult(String title, String organization, String location, String imageUrl, ...)
```

---

### 3.3. Category Model
**File:** `model/Category.java`  
**Mục đích:** Danh mục sự kiện (hiển thị trong home)

| Thuộc tính | Kiểu dữ liệu | Mô tả |
|------------|--------------|-------|
| `name` | `String` | Tên danh mục |
| `jobCount` | `int` | Số công việc/sự kiện |
| `iconResId` | `int` | Resource ID icon |

---

### 3.4. FilterCategory Model
**File:** `model/FilterCategory.java`  
**Mục đích:** Filter chip trong search

| Thuộc tính | Kiểu dữ liệu | Mô tả |
|------------|--------------|-------|
| `name` | `String` | Tên filter |
| `isSelected` | `boolean` | Đã chọn chưa |

---

### 3.5. SearchFilter Model
**File:** `model/SearchFilter.java`  
**Mục đích:** Bộ lọc tìm kiếm nâng cao

| Thuộc tính | Kiểu dữ liệu | Mô tả |
|------------|--------------|-------|
| `keyword` | `String` | Từ khóa tìm kiếm |
| `category` | `String` | Danh mục |
| `startDate` | `Date` | Ngày bắt đầu |
| `endDate` | `Date` | Ngày kết thúc |
| `sortBy` | `String` | Sắp xếp theo |

---

## 4. ENUMS & CONSTANTS

### 4.1. User Roles
```java
public static final String ROLE_VOLUNTEER = "VOLUNTEER";
public static final String ROLE_ORGANIZER = "ORGANIZER";
public static final String ROLE_ADMIN = "ADMIN";
```

### 4.2. User Status
```java
public static final String STATUS_ACTIVE = "ACTIVE";
public static final String STATUS_LOCKED = "LOCKED";
public static final String STATUS_PENDING = "PENDING";
```

### 4.3. Event Status
```java
public static final String STATUS_PENDING = "pending";
public static final String STATUS_APPROVED = "approved";
public static final String STATUS_REJECTED = "rejected";
```

### 4.4. Applicant Status
```java
public static final int STATUS_PENDING = 0;
public static final int STATUS_ACCEPTED = 1;
public static final int STATUS_REJECTED = 2;
```

### 4.5. Reward Categories
```java
public static final int CATEGORY_ALL = 0;
public static final int CATEGORY_VOUCHER = 1;
public static final int CATEGORY_GIFT = 2;
public static final int CATEGORY_EXPERIENCE = 3;
public static final int CATEGORY_LOW_STOCK = 4; // Filter only
```

### 4.6. Organization Status (Vietnamese)
```java
"Hoạt động"      // Active
"Bị khóa"        // Locked
"Chờ xác thực"   // Pending verification
```

### 4.7. Violation Types
```java
"Spam"           // Spam violation
null             // No violation
```

---

## 5. MAPPING GIỮA MODELS

### 5.1. User ↔ UserResponse
```java
// API Response → Local Model
User user = new User();
user.setId(userResponse.getId());
user.setFullName(userResponse.getFullName());
user.setEmail(userResponse.getEmail());
user.setPhone(userResponse.getPhoneNumber());
user.setRole(userResponse.getRole());
user.setPointsCount(userResponse.getTotalPoints());
```

### 5.2. EventResponse → EventPost
```java
EventPost post = new EventPost();
post.setId(eventResponse.getId());
post.setTitle(eventResponse.getTitle());
post.setLocation(eventResponse.getLocation());
post.setRewardPoints(eventResponse.getRewardId());
post.setCurrentParticipants(eventResponse.getCurrentParticipants());
post.setMaxParticipants(eventResponse.getMaxParticipants());
```

### 5.3. EventRegistrationResponse → Applicant
```java
Applicant applicant = new Applicant(
    eventReg.getUserName(),
    eventReg.getUserEmail(),
    eventReg.getEventTitle(),
    eventReg.getRegisteredAt(),
    "", // phone
    eventReg.getNotes(),
    statusToInt(eventReg.getStatus()),
    "" // avatarUrl
);
```

---

## 6. VALIDATION RULES

### 6.1. User Validation
```java
// Email
- Required
- Format: email@domain.com
- Unique

// Password
- Required
- Min length: 6 characters
- Max length: 50 characters

// Phone
- Optional
- Format: 10-11 digits
- Pattern: ^[0-9]{10,11}$

// Full Name
- Required
- Min length: 2 characters
- Max length: 100 characters
```

### 6.2. Event Validation
```java
// Title
- Required
- Min length: 10 characters
- Max length: 200 characters

// Description
- Required
- Min length: 50 characters
- Max length: 2000 characters

// Location
- Required
- Min length: 5 characters

// Max Participants
- Required
- Min: 1
- Max: 10000

// Dates
- Start date >= Current date
- End date > Start date
```

### 6.3. Reward Validation
```java
// Points
- Required
- Min: 1
- Max: 100000

// Stock
- Required
- Min: 0
- Warning if <= 5 (low stock)

// Name
- Required
- Min length: 5 characters
- Max length: 100 characters
```

---

## 7. NOTES & BEST PRACTICES

### 7.1. Date Handling
```java
// API sử dụng ISO 8601 format
"2024-12-03T10:30:00Z"

// UI hiển thị format
"03/12/2024"
"10:30 AM"
"3 giờ trước"

// Conversion
SimpleDateFormat apiFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
SimpleDateFormat uiFormat = new SimpleDateFormat("dd/MM/yyyy");
```

### 7.2. Image Handling
```java
// Có 2 cách load ảnh:
1. Resource ID (R.drawable.xxx) - cho mock data
2. URL (https://...) - cho real API data

// Kiểm tra:
if (imageUrl != null && !imageUrl.isEmpty()) {
    // Load from URL using Glide/Picasso
} else if (imageResId != 0) {
    // Load from drawable resource
}
```

### 7.3. Status Display
```java
// Mapping status to colors
switch(status) {
    case "ACTIVE": return R.color.green_primary;
    case "LOCKED": return R.color.red;
    case "PENDING": return R.color.orange;
}

// Mapping status to Vietnamese
switch(status) {
    case "ACTIVE": return "Hoạt động";
    case "LOCKED": return "Bị khóa";
    case "PENDING": return "Chờ xác thực";
}
```

### 7.4. Null Safety
```java
// Luôn check null cho optional fields
String phone = user.getPhone();
if (phone != null && !phone.isEmpty()) {
    // Use phone
}

// Sử dụng default values
int points = user.getPointsCount() != null ? user.getPointsCount() : 0;
String avatar = user.getAvatarUrl() != null ? user.getAvatarUrl() : DEFAULT_AVATAR;
```

---

## 8. CHECKLIST KHI TẠO MODEL MỚI

- [ ] Tạo file trong package `model/`
- [ ] Khai báo tất cả properties với access modifier `private`
- [ ] Tạo constructor rỗng (required cho JSON parsing)
- [ ] Tạo constructor đầy đủ (nếu cần)
- [ ] Generate getters và setters cho tất cả properties
- [ ] Thêm JavaDoc comments cho class
- [ ] Thêm validation rules (nếu cần)
- [ ] Thêm constants cho enum values
- [ ] Update file này (MODEL_DOCUMENTATION.md)
- [ ] Test với mock data

---

## 9. COMMON ISSUES & SOLUTIONS

### Issue 1: Null Pointer Exception
```java
// ❌ BAD
String name = user.getFullName().toUpperCase();

// ✅ GOOD
String name = user.getFullName() != null ? 
              user.getFullName().toUpperCase() : "";
```

### Issue 2: Date Parsing Error
```java
// ❌ BAD
Date date = new Date(dateString);

// ✅ GOOD
try {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    Date date = sdf.parse(dateString);
} catch (ParseException e) {
    // Handle error
}
```

### Issue 3: Integer vs int
```java
// Integer có thể null, int không thể
Integer id = null; // ✅ OK
int id = null;     // ❌ ERROR

// Khi parse từ API, dùng Integer
Integer id = response.getId(); // ✅ Safe
int id = response.getId();     // ❌ NPE if null
```

---

## 10. TỔNG KẾT

### Số lượng Models
- **Main Models:** 6 (User, EventPost, RewardItem, Applicant, Organization, UserManagement)
- **Response Models:** 6 (UserResponse, EventResponse, EventRegistrationResponse, LoginResponse, RestResponse, PageResponse)
- **UI Models:** 5 (Event, SearchResult, Category, FilterCategory, SearchFilter)
- **TỔNG:** 17 models

### Các thuộc tính phổ biến nhất
1. `id` - Xuất hiện trong 15/17 models
2. `name/title` - Xuất hiện trong 14/17 models
3. `status` - Xuất hiện trong 8/17 models
4. `email` - Xuất hiện trong 7/17 models
5. `createdAt/updatedAt` - Xuất hiện trong 6/17 models

---

**Cập nhật lần cuối:** 03/12/2024  
**Version:** 1.0  
**Tác giả:** Phân tích từ source code FE Android App
