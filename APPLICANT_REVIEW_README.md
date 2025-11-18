# Applicant Review Screen - Màn hình Duyệt Đăng Ký

## 📱 Tổng quan

Màn hình cho phép quản lý và duyệt các đơn đăng ký tình nguyện viên cho các hoạt động.

## ✨ Tính năng

### 🎨 UI Components

1. **Header Gradient** - Header với gradient xanh dương đẹp mắt
2. **Stats Cards** - 3 thẻ thống kê:
   - Chờ duyệt (8)
   - Đã chấp nhận (24)
   - Đã từ chối (3)
3. **Filter Tabs** - 4 tabs: Tất cả, Chờ duyệt, Đã chấp nhận, Đã từ chối
4. **RecyclerView** - Danh sách đơn đăng ký với:
   - Avatar, tên, email
   - Trạng thái (badge màu sắc)
   - Thông tin hoạt động, ngày đăng ký, số điện thoại
   - Lời nhắn (nếu có)
   - 2 nút: Chấp nhận (xanh) và Từ chối (đỏ) cho đơn chờ duyệt
   - Nút "Xem chi tiết" cho đơn đã xử lý

### 📊 Mock Data

10 đơn đăng ký mẫu với các trạng thái khác nhau:

- 4 đơn chờ duyệt
- 4 đơn đã chấp nhận
- 2 đơn đã từ chối

### 🎯 Chức năng

- **Filter**: Lọc theo trạng thái (All, Pending, Accepted, Rejected)
- **Accept/Reject**: Chấp nhận hoặc từ chối đơn
- **Auto Update**: Tự động cập nhật số liệu và trạng thái
- **Empty State**: Hiển thị khi không có đơn

## 📁 Files Đã Tạo

### Models & Adapters

- `Applicant.java` - Model cho đơn đăng ký
- `AplicationAdapter.java` - RecyclerView adapter

### Layouts

- `fragment_accept_applicant.xml` - Layout chính
- `item_applicant.xml` - Layout cho từng item

### Drawables (13 icons + 10 backgrounds)

**Icons**: check, close, info, call, contact, date, activity, applicants
**Backgrounds**: gradient, buttons, status badges, tabs, avatar

### Integration

- Đã thêm vào `HomeAdapter` (tab thứ 4)
- Đã cập nhật `HomeActivity`
- Icon tab: `ic_applicants.xml`

## 🚀 Sử dụng

Fragment tự động được thêm vào TabLayout của HomeActivity ở vị trí tab thứ 4 (giữa Rewards và Profile).

## 🎨 Thiết kế

- Gradient header xanh (#00BCD4 → #42A5F5)
- Status badges với màu phù hợp (Pending: Orange, Accepted: Green, Rejected: Red)
- Material Design với elevation và rounded corners
- Responsive và user-friendly

---

**Version**: 1.0  
**Date**: November 18, 2025
