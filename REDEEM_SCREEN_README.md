# Redeem Screen - Màn hình Đổi Thưởng

## 📱 Tổng quan

Màn hình Đổi Thưởng cho phép người dùng sử dụng điểm tích lũy để đổi lấy các phần thưởng như voucher, quà tặng, và cơ hội đặc biệt.

## ✨ Tính năng đã triển khai

### 🎨 **UI Components**

1. **Header Card** - Card gradient hiển thị:
   - Tổng điểm hiện tại (1,250 điểm)
   - Nút xem lịch sử đổi thưởng
2. **Category Tabs** - 4 danh mục:

   - 🎁 Tất cả (All)
   - 🎟️ Voucher
   - 🎁 Quà tặng (Gift)
   - 🏆 Cơ hội (Opportunity)

3. **RecyclerView** - Danh sách phần thưởng với:

   - Icon màu sắc theo loại (Purple, Pink, Orange, Cyan)
   - Tên và mô tả phần thưởng
   - Số điểm cần đổi
   - Tổ chức cung cấp
   - Tags (Ẩm uống, Phổ biến, Eco-Friendly, v.v.)
   - Số lượng còn lại
   - Hạn sử dụng
   - Nút "Đổi ngay" (tự động disabled nếu không đủ điểm)

4. **Empty State** - Hiển thị khi không có item trong category

### 📊 **Mock Data** (redeem.xml)

10 phần thưởng mẫu:

1. ☕ Voucher Highlands Coffee 50k (500 điểm)
2. 🎁 Bộ quà tặng văn phòng phẩm (800 điểm)
3. 🍕 Voucher Pizza Hut 100k (900 điểm)
4. 🏆 Chương nhận Tình nguyện viên (2,000 điểm)
5. 👕 Áo thun tình nguyện viên (1,500 điểm)
6. 🎬 Vé xem phim CGV 2D (700 điểm)
7. 📚 Sách "Nghệ thuật tình nguyện" (600 điểm)
8. ☕ Voucher The Coffee House 30k (300 điểm)
9. 🎒 Balo canvas Volunteer Impact (1,800 điểm)
10. 🌱 Cây xanh mini để bàn (400 điểm)

### 🎯 **Chức năng**

#### RewardAdapter.java

- Hiển thị danh sách rewards với RecyclerView
- Filter theo category (All, Voucher, Gift, Opportunity)
- Kiểm tra điểm user để enable/disable nút đổi
- Click listener cho từng item và nút đổi
- Dynamic icon và background color

#### RewardItem.java (Model)

- Model class chứa dữ liệu reward:
  - name, organization, description
  - points, stock, expiry
  - categoryType, tag1, tag2
  - iconColorIndex

#### RedeemActivity.java

- Load mock data từ resources (redeem.xml)
- Setup RecyclerView với LinearLayoutManager
- Quản lý category tabs
- Hiển thị/ẩn empty state
- Format số điểm với dấu phân cách

### 🎨 **Drawable Resources** (26 files)

#### Icons:

- `ic_coin.xml` - Icon đồng xu/điểm
- `ic_gift.xml` - Icon quà tặng
- `ic_voucher.xml` - Icon voucher
- `ic_discount.xml` - Icon giảm giá
- `ic_history.xml` - Icon lịch sử
- `ic_arrow_right.xml` - Icon mũi tên
- `ic_coffee.xml`, `ic_certificate.xml`, `ic_tshirt.xml`
- `ic_backpack.xml`, `ic_book.xml`, `ic_pizza.xml`
- `ic_movie.xml`, `ic_plant.xml`

#### Backgrounds:

- `bg_points_card.xml` - Gradient cho header card
- `bg_category_tab.xml` - Background tab thường
- `bg_category_tab_selected.xml` - Background tab được chọn
- `bg_reward_item.xml` - Background item reward
- `bg_icon_purple.xml`, `bg_icon_pink.xml`, `bg_icon_orange.xml`, `bg_icon_cyan.xml`
- `bg_tag_green.xml`, `bg_tag_blue.xml` - Background cho tags
- `bg_redeem_button.xml` - Nút đổi ngay (active)
- `bg_disabled_button.xml` - Nút disabled

### 🎨 **Colors**

```xml
<color name="cyan">#00BCD4</color>
<color name="pink">#E91E63</color>
<color name="orange">#FF9800</color>
<color name="purple">#B39DDB</color>
```

## 🚀 Cách sử dụng

### 1. Mở màn hình từ MainActivity:

```java
Intent intent = new Intent(MainActivity.this, RedeemActivity.class);
startActivity(intent);
```

### 2. Tùy chỉnh điểm người dùng:

Trong `RedeemActivity.java`, thay đổi:

```java
private int userPoints = 1250; // Thay đổi số điểm ở đây
```

### 3. Thêm reward mới:

Chỉnh sửa file `redeem.xml`:

- Thêm vào các array: `reward_names`, `reward_organizations`, etc.
- Đảm bảo tất cả arrays có cùng số phần tử

### 4. Xử lý sự kiện đổi thưởng:

Trong `RewardAdapter.java`, tìm:

```java
holder.btnRedeem.setOnClickListener(v -> {
    if (canRedeem) {
        // TODO: Implement actual redeem logic here
    }
});
```

## 📝 TODO - Các tính năng cần bổ sung

1. **API Integration**:

   - Kết nối API để lấy danh sách rewards thật
   - API đổi thưởng
   - API lịch sử đổi thưởng

2. **Detail Screen**:

   - Màn hình chi tiết reward khi click vào item
   - Hiển thị ảnh, mô tả đầy đủ, điều khoản

3. **History Screen**:

   - Lịch sử đổi thưởng
   - Trạng thái (Đang xử lý, Đã nhận, Đã sử dụng)

4. **Filter & Sort**:

   - Lọc theo số điểm
   - Sắp xếp theo điểm (thấp -> cao, cao -> thấp)
   - Tìm kiếm reward

5. **Notifications**:

   - Thông báo khi đổi thưởng thành công
   - Nhắc nhở khi reward sắp hết hạn

6. **Animation**:
   - Transition giữa các category
   - Animation khi đổi thưởng thành công

## 🎯 Cấu trúc File

```
FE/app/src/main/
├── java/com/manhhuy/myapplication/
│   ├── adapter/
│   │   └── RewardAdapter.java
│   ├── model/
│   │   └── RewardItem.java
│   └── ui/Activitys/
│       └── RedeemActivity.java
└── res/
    ├── drawable/
    │   ├── ic_*.xml (26 icon files)
    │   └── bg_*.xml (background files)
    ├── layout/
    │   ├── activity_redeem.xml
    │   └── item_reward.xml
    └── values/
        ├── colors.xml (updated)
        └── redeem.xml (mock data)
```

## 💡 Tips

1. **Responsive Design**: Layout tự động điều chỉnh với các kích thước màn hình khác nhau
2. **Performance**: RecyclerView sử dụng ViewHolder pattern để tối ưu
3. **User Experience**:
   - Disabled button khi không đủ điểm
   - Empty state khi không có item
   - Visual feedback cho category selection
4. **Maintainable**: Mock data tập trung trong redeem.xml, dễ chỉnh sửa

---

**Tác giả**: GitHub Copilot  
**Ngày tạo**: November 12, 2025  
**Version**: 1.0
