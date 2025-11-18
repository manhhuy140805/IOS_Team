# Material Design 3 - Tóm Tắt Nhanh

## ✅ ĐÃ CÀI ĐẶT

### 1. Màu Sắc (colors.xml)
- ✅ Primary Colors (Xanh lá #00C997)
- ✅ Secondary Colors (Xanh lục đậm)
- ✅ Tertiary Colors (Xanh dương)
- ✅ Error Colors
- ✅ Surface & Background Colors
- ✅ Light & Dark Theme

### 2. Typography (themes.xml)
- ✅ Display Large (57sp)
- ✅ Headline Large (32sp)
- ✅ Title Large (22sp)
- ✅ Body Large (16sp)
- ✅ Label Large (14sp)

### 3. Components Demo (activity_main.xml)
- ✅ AppBar / Toolbar
- ✅ Floating Action Button (FAB)
- ✅ Buttons (Filled, Outlined, Text, Tonal)
- ✅ Cards (Filled, Elevated, Outlined)
- ✅ Text Fields (Filled, Outlined)
- ✅ Chips (Assist, Filter, Suggestion)
- ✅ Switch & Checkbox

---

## 🎨 CÁCH SỬ DỤNG NHANH

### Màu Sắc
```xml
<!-- Primary (nút chính, actions) -->
android:backgroundTint="?attr/colorPrimary"
android:textColor="?attr/colorOnPrimary"

<!-- Secondary (nút phụ) -->
android:backgroundTint="?attr/colorSecondary"

<!-- Surface (cards, dialogs) -->
android:background="?attr/colorSurface"
android:textColor="?attr/colorOnSurface"

<!-- Error (lỗi, warnings) -->
android:textColor="?attr/colorError"
```

### Typography
```xml
<!-- Tiêu đề lớn -->
android:textAppearance="?attr/textAppearanceHeadlineLarge"

<!-- Tiêu đề thường -->
android:textAppearance="?attr/textAppearanceTitleLarge"

<!-- Nội dung chính -->
android:textAppearance="?attr/textAppearanceBodyLarge"
```

### Buttons
```xml
<!-- Nút chính (Login, Submit) -->
<com.google.android.material.button.MaterialButton
    style="@style/Widget.Material3.Button" />

<!-- Nút phụ (Cancel) -->
<com.google.android.material.button.MaterialButton
    style="@style/Widget.Material3.Button.OutlinedButton" />

<!-- Nút text (Forgot Password) -->
<com.google.android.material.button.MaterialButton
    style="@style/Widget.Material3.Button.TextButton" />
```

### Cards
```xml
<!-- Card có bóng (interactive) -->
<com.google.android.material.card.MaterialCardView
    app:cardElevation="4dp"
    style="@style/Widget.Material3.CardView.Elevated" />

<!-- Card phẳng (information) -->
<com.google.android.material.card.MaterialCardView
    app:cardElevation="0dp"
    app:cardBackgroundColor="?attr/colorSurfaceVariant"
    style="@style/Widget.Material3.CardView.Filled" />
```

### Text Fields
```xml
<!-- Input field outlined -->
<com.google.android.material.textfield.TextInputLayout
    android:hint="Email"
    style="@style/Widget.Material3.TextInputLayout.OutlinedBox">
    
    <com.google.android.material.textfield.TextInputEditText
        android:layout_width="match_parent"
        android:layout_height="wrap_content" />
        
</com.google.android.material.textfield.TextInputLayout>
```

---

## 📋 CHECKLIST KHI LÀM UI MỚI

### Màu Sắc
- [ ] Dùng `?attr/colorXXX` thay vì `@color/xxx`
- [ ] Primary cho actions chính
- [ ] Secondary cho actions phụ
- [ ] Surface cho cards/dialogs
- [ ] Error cho thông báo lỗi

### Typography
- [ ] Headline cho tiêu đề screens
- [ ] Title cho tiêu đề cards/sections
- [ ] Body cho nội dung
- [ ] Label cho buttons/chips

### Components
- [ ] 1 Filled Button chính per screen
- [ ] Outlined/Text buttons cho actions phụ
- [ ] Cards dùng Elevated hoặc Filled
- [ ] Text fields dùng OutlinedBox
- [ ] FAB cho action chính (floating)

### Dark Mode
- [ ] Test cả Light và Dark mode
- [ ] Dùng `?attr/` để tự động adapt

---

## 🚀 CHẠY THỬ

1. **Build & Run app**
2. **Xem activity_main.xml** - Demo đầy đủ components
3. **Đọc MATERIAL_DESIGN_3_GUIDE.md** - Hướng dẫn chi tiết
4. **Áp dụng vào screens của bạn**

---

## 📚 FILES QUAN TRỌNG

```
app/src/main/res/
├── values/
│   ├── colors.xml          ← Màu sắc M3
│   ├── themes.xml          ← Light theme
│   └── strings.xml
├── values-night/
│   └── themes.xml          ← Dark theme
├── layout/
│   └── activity_main.xml   ← Demo components
└── menu/
    └── main_menu.xml       ← Menu items

MATERIAL_DESIGN_3_GUIDE.md  ← Hướng dẫn chi tiết
```

---

## 💡 LƯU Ý

- ✅ Material 3 library đã được thêm: `com.google.android.material:material:1.12.0`
- ✅ Theme đã setup: `Theme.Material3.DayNight.NoActionBar`
- ✅ Dark mode tự động hoạt động
- ✅ Tất cả colors, typography, shapes đã config

Chúc bạn làm UI đẹp! 🎨

