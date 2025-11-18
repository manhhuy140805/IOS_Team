# Material Design 3 - Hướng Dẫn Sử Dụng

## 📚 Tổng Quan
Project của bạn đã được cấu hình đầy đủ Material Design 3 (M3) với:
- ✅ Color System (Primary, Secondary, Tertiary)
- ✅ Typography System
- ✅ Shape System
- ✅ Components (Buttons, Cards, TextFields, Chips, FAB, AppBar...)
- ✅ Light & Dark Theme

---

## 🎨 1. HỆ THỐNG MÀU SẮC (COLOR SYSTEM)

### Primary Colors (Màu Chính - Xanh Lá)
```xml
colorPrimary              #00C997   <!-- Màu chính cho buttons, FAB, AppBar -->
colorOnPrimary            #FFFFFF   <!-- Text/icon trên primary -->
colorPrimaryContainer     #CEFFED   <!-- Background nhạt cho primary elements -->
colorOnPrimaryContainer   #002114   <!-- Text trên primary container -->
```

**Khi nào dùng:**
- Buttons chính (Login, Submit, Save)
- Floating Action Button
- App Bar / Toolbar
- Selected items, Active states

**Ví dụ:**
```xml
<!-- Button Primary -->
<com.google.android.material.button.MaterialButton
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:text="Login"
    style="@style/Widget.Material3.Button" />
```

### Secondary Colors (Màu Phụ - Xanh Lục Đậm)
```xml
colorSecondary            #4A6358   <!-- Màu phụ cho accents -->
colorOnSecondary          #FFFFFF   
colorSecondaryContainer   #CCE8DA   
colorOnSecondaryContainer #072117   
```

**Khi nào dùng:**
- Buttons phụ (Cancel, Skip, Back)
- Floating chips
- Filter buttons
- Secondary actions

**Ví dụ:**
```xml
<!-- Button Secondary -->
<com.google.android.material.button.MaterialButton
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:text="Cancel"
    android:backgroundTint="?attr/colorSecondaryContainer"
    android:textColor="?attr/colorOnSecondaryContainer"
    style="@style/Widget.Material3.Button" />
```

### Tertiary Colors (Màu Thứ Ba - Xanh Dương)
```xml
colorTertiary             #3D6373   <!-- Màu nhấn mạnh -->
colorOnTertiary           #FFFFFF   
colorTertiaryContainer    #C1E8FB   
colorOnTertiaryContainer  #001F29   
```

**Khi nào dùng:**
- Highlighted information
- Special badges
- Alternative accents
- Links, special buttons

### Error Colors (Màu Lỗi)
```xml
colorError                #BA1A1A   <!-- Màu lỗi/cảnh báo -->
colorErrorContainer       #FFDAD6   
colorOnError              #FFFFFF   
colorOnErrorContainer     #410002   
```

**Khi nào dùng:**
- Error messages
- Validation errors
- Delete/destructive actions

### Surface & Background Colors
```xml
colorSurface              #FBFDF9   <!-- Màu nền cho cards, dialogs -->
colorOnSurface            #191C1A   <!-- Text trên surface -->
colorSurfaceVariant       #DBE5DD   <!-- Surface với tint nhẹ -->
colorOnSurfaceVariant     #404944   <!-- Text trên surface variant -->
android:colorBackground   #F0FDF4   <!-- Background chính của app -->
colorOnBackground         #191C1A   <!-- Text trên background -->
```

**Khi nào dùng:**
- colorSurface: Cards, Bottom Sheets, Dialogs, Menus
- colorSurfaceVariant: Input fields, chips, tags
- colorBackground: Screen background

### Outline Colors
```xml
colorOutline              #707973   <!-- Borders, dividers -->
colorOutlineVariant       #BFC9C2   <!-- Borders nhẹ hơn -->
```

---

## 📝 2. TYPOGRAPHY (KIỂU CHỮ)

### Display Large (57sp) - Tiêu đề cực lớn
```xml
<com.google.android.material.textview.MaterialTextView
    android:text="Welcome"
    android:textAppearance="?attr/textAppearanceDisplayLarge" />
```
**Dùng cho:** Splash screens, onboarding, hero sections

### Headline Large (32sp) - Tiêu đề lớn
```xml
<com.google.android.material.textview.MaterialTextView
    android:text="Section Title"
    android:textAppearance="?attr/textAppearanceHeadlineLarge" />
```
**Dùng cho:** Screen titles, major sections

### Headline Medium (28sp) - Tiêu đề trung
```xml
<com.google.android.material.textview.MaterialTextView
    android:text="Category Name"
    android:textAppearance="?attr/textAppearanceHeadlineMedium" />
```
**Dùng cho:** Section headers, category titles

### Title Large (22sp) - Tiêu đề
```xml
<com.google.android.material.textview.MaterialTextView
    android:text="Card Title"
    android:textAppearance="?attr/textAppearanceTitleLarge" />
```
**Dùng cho:** Card titles, dialog titles

### Title Medium (16sp Medium) - Tiêu đề nhỏ
```xml
<com.google.android.material.textview.MaterialTextView
    android:text="Item Name"
    android:textAppearance="?attr/textAppearanceTitleMedium" />
```
**Dùng cho:** List item titles, small cards

### Body Large (16sp) - Nội dung chính
```xml
<com.google.android.material.textview.MaterialTextView
    android:text="Main content text here..."
    android:textAppearance="?attr/textAppearanceBodyLarge" />
```
**Dùng cho:** Main content, paragraphs

### Body Medium (14sp) - Nội dung phụ
```xml
<com.google.android.material.textview.MaterialTextView
    android:text="Description text"
    android:textAppearance="?attr/textAppearanceBodyMedium" />
```
**Dùng cho:** Descriptions, secondary text

### Label Large (14sp Medium) - Nhãn
```xml
<com.google.android.material.textview.MaterialTextView
    android:text="Button Text"
    android:textAppearance="?attr/textAppearanceLabelLarge" />
```
**Dùng cho:** Button labels, tabs, chips

---

## 🎯 3. BUTTONS (NÚT BẤM)

### 3.1. Filled Button (Nút Chính)
```xml
<com.google.android.material.button.MaterialButton
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:text="Login"
    style="@style/Widget.Material3.Button" />
```
**Khi nào dùng:** Primary action (Login, Submit, Save, Continue)

### 3.2. Outlined Button (Nút Viền)
```xml
<com.google.android.material.button.MaterialButton
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:text="Cancel"
    style="@style/Widget.Material3.Button.OutlinedButton" />
```
**Khi nào dùng:** Secondary action (Cancel, Back, Skip)

### 3.3. Text Button (Nút Không Nền)
```xml
<com.google.android.material.button.MaterialButton
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:text="Learn More"
    style="@style/Widget.Material3.Button.TextButton" />
```
**Khi nào dùng:** Tertiary actions, links (Forgot Password, Learn More)

### 3.4. Tonal Button (Nút Tông Màu)
```xml
<com.google.android.material.button.MaterialButton
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:text="Share"
    style="@style/Widget.Material3.Button.TonalButton" />
```
**Khi nào dùng:** Important but not primary (Share, Add to Cart)

### 3.5. Icon Button
```xml
<com.google.android.material.button.MaterialButton
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:text="Send"
    app:icon="@android:drawable/ic_menu_send"
    app:iconGravity="start"
    style="@style/Widget.Material3.Button" />
```

---

## 🎴 4. CARDS (THẺ)

### 4.1. Filled Card
```xml
<com.google.android.material.card.MaterialCardView
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    app:cardElevation="0dp"
    app:cardBackgroundColor="?attr/colorSurfaceVariant"
    style="@style/Widget.Material3.CardView.Filled">
    
    <!-- Content here -->
    
</com.google.android.material.card.MaterialCardView>
```
**Khi nào dùng:** Information cards, content containers

### 4.2. Elevated Card (Card Có Bóng)
```xml
<com.google.android.material.card.MaterialCardView
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    app:cardElevation="4dp"
    style="@style/Widget.Material3.CardView.Elevated">
    
    <!-- Content here -->
    
</com.google.android.material.card.MaterialCardView>
```
**Khi nào dùng:** Important cards, product cards, interactive cards

### 4.3. Outlined Card (Card Có Viền)
```xml
<com.google.android.material.card.MaterialCardView
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    app:strokeWidth="1dp"
    app:strokeColor="?attr/colorOutline"
    app:cardElevation="0dp"
    style="@style/Widget.Material3.CardView.Outlined">
    
    <!-- Content here -->
    
</com.google.android.material.card.MaterialCardView>
```
**Khi nào dùng:** Selection cards, option cards

### 4.4. Clickable Card
```xml
<com.google.android.material.card.MaterialCardView
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:clickable="true"
    android:focusable="true"
    app:cardElevation="2dp"
    style="@style/Widget.Material3.CardView.Elevated">
    
    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="vertical"
        android:padding="16dp">
        
        <TextView
            android:text="Product Name"
            android:textAppearance="?attr/textAppearanceTitleMedium" />
            
        <TextView
            android:text="$99.99"
            android:textAppearance="?attr/textAppearanceBodyLarge"
            android:textColor="?attr/colorPrimary" />
            
    </LinearLayout>
    
</com.google.android.material.card.MaterialCardView>
```

---

## ✏️ 5. TEXT FIELDS (TRƯỜNG NHẬP LIỆU)

### 5.1. Filled Text Field
```xml
<com.google.android.material.textfield.TextInputLayout
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:hint="Email"
    style="@style/Widget.Material3.TextInputLayout.FilledBox">

    <com.google.android.material.textfield.TextInputEditText
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:inputType="textEmailAddress" />

</com.google.android.material.textfield.TextInputLayout>
```

### 5.2. Outlined Text Field
```xml
<com.google.android.material.textfield.TextInputLayout
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:hint="Password"
    app:endIconMode="password_toggle"
    style="@style/Widget.Material3.TextInputLayout.OutlinedBox">

    <com.google.android.material.textfield.TextInputEditText
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:inputType="textPassword" />

</com.google.android.material.textfield.TextInputLayout>
```

### 5.3. Text Field với Icon
```xml
<com.google.android.material.textfield.TextInputLayout
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:hint="Search"
    app:startIconDrawable="@android:drawable/ic_menu_search"
    app:endIconMode="clear_text"
    style="@style/Widget.Material3.TextInputLayout.OutlinedBox">

    <com.google.android.material.textfield.TextInputEditText
        android:layout_width="match_parent"
        android:layout_height="wrap_content" />

</com.google.android.material.textfield.TextInputLayout>
```

### 5.4. Text Field với Error
```xml
<com.google.android.material.textfield.TextInputLayout
    android:id="@+id/emailLayout"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:hint="Email"
    app:errorEnabled="true"
    style="@style/Widget.Material3.TextInputLayout.OutlinedBox">

    <com.google.android.material.textfield.TextInputEditText
        android:id="@+id/emailInput"
        android:layout_width="match_parent"
        android:layout_height="wrap_content" />

</com.google.android.material.textfield.TextInputLayout>

<!-- Trong Java/Kotlin: -->
<!-- emailLayout.setError("Invalid email address"); -->
```

---

## 🏷️ 6. CHIPS

### 6.1. Assist Chip
```xml
<com.google.android.material.chip.Chip
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:text="Help"
    style="@style/Widget.Material3.Chip.Assist" />
```
**Dùng cho:** Quick actions, shortcuts

### 6.2. Filter Chip
```xml
<com.google.android.material.chip.Chip
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:text="All"
    android:checkable="true"
    android:checked="true"
    style="@style/Widget.Material3.Chip.Filter" />
```
**Dùng cho:** Filters, categories

### 6.3. Suggestion Chip
```xml
<com.google.android.material.chip.Chip
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:text="Popular"
    style="@style/Widget.Material3.Chip.Suggestion" />
```
**Dùng cho:** Suggestions, recommendations

### 6.4. Chip Group (Multiple Selection)
```xml
<com.google.android.material.chip.ChipGroup
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    app:singleSelection="false">

    <com.google.android.material.chip.Chip
        android:text="Technology"
        android:checkable="true"
        style="@style/Widget.Material3.Chip.Filter" />

    <com.google.android.material.chip.Chip
        android:text="Sports"
        android:checkable="true"
        style="@style/Widget.Material3.Chip.Filter" />

    <com.google.android.material.chip.Chip
        android:text="Music"
        android:checkable="true"
        style="@style/Widget.Material3.Chip.Filter" />

</com.google.android.material.chip.ChipGroup>
```

---

## ✅ 7. SELECTION CONTROLS

### 7.1. Switch
```xml
<com.google.android.material.switchmaterial.SwitchMaterial
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:text="Enable Notifications"
    android:checked="true" />
```

### 7.2. Checkbox
```xml
<com.google.android.material.checkbox.MaterialCheckBox
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:text="I agree to terms and conditions"
    android:checked="false" />
```

### 7.3. Radio Button Group
```xml
<RadioGroup
    android:layout_width="match_parent"
    android:layout_height="wrap_content">

    <com.google.android.material.radiobutton.MaterialRadioButton
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Option 1"
        android:checked="true" />

    <com.google.android.material.radiobutton.MaterialRadioButton
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Option 2" />

</RadioGroup>
```

---

## 🎯 8. FLOATING ACTION BUTTON (FAB)

### 8.1. Primary FAB
```xml
<com.google.android.material.floatingactionbutton.FloatingActionButton
    android:id="@+id/fab"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:layout_gravity="bottom|end"
    android:layout_margin="16dp"
    android:contentDescription="Add"
    app:srcCompat="@android:drawable/ic_input_add"
    style="@style/Widget.Material3.FloatingActionButton.Primary" />
```

### 8.2. Secondary FAB
```xml
<com.google.android.material.floatingactionbutton.FloatingActionButton
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:contentDescription="Edit"
    app:srcCompat="@android:drawable/ic_menu_edit"
    app:backgroundTint="?attr/colorSecondaryContainer"
    app:tint="?attr/colorOnSecondaryContainer"
    style="@style/Widget.Material3.FloatingActionButton.Secondary" />
```

### 8.3. Extended FAB
```xml
<com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:text="Create New"
    app:icon="@android:drawable/ic_input_add" />
```

---

## 🎨 9. APP BAR / TOOLBAR

### 9.1. Basic AppBar
```xml
<com.google.android.material.appbar.AppBarLayout
    android:layout_width="match_parent"
    android:layout_height="wrap_content">

    <com.google.android.material.appbar.MaterialToolbar
        android:id="@+id/toolbar"
        android:layout_width="match_parent"
        android:layout_height="?attr/actionBarSize"
        app:title="My App"
        app:titleTextColor="?attr/colorOnPrimary"
        android:background="?attr/colorPrimary"
        app:menu="@menu/main_menu" />

</com.google.android.material.appbar.AppBarLayout>
```

### 9.2. AppBar với Tabs
```xml
<com.google.android.material.appbar.AppBarLayout
    android:layout_width="match_parent"
    android:layout_height="wrap_content">

    <com.google.android.material.appbar.MaterialToolbar
        android:layout_width="match_parent"
        android:layout_height="?attr/actionBarSize"
        app:title="My App" />

    <com.google.android.material.tabs.TabLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:tabMode="fixed">

        <com.google.android.material.tabs.TabItem
            android:text="Tab 1" />

        <com.google.android.material.tabs.TabItem
            android:text="Tab 2" />

        <com.google.android.material.tabs.TabItem
            android:text="Tab 3" />

    </com.google.android.material.tabs.TabLayout>

</com.google.android.material.appbar.AppBarLayout>
```

---

## 💡 10. TIPS & BEST PRACTICES

### 10.1. Luôn dùng ?attr/ thay vì @color/
```xml
<!-- ✅ ĐÚNG -->
android:textColor="?attr/colorPrimary"
android:background="?attr/colorSurface"

<!-- ❌ SAI -->
android:textColor="@color/md_theme_light_primary"
```

### 10.2. Sử dụng đúng màu cho từng context
- **colorPrimary**: Actions chính (Login, Buy Now, Submit)
- **colorSecondary**: Actions phụ (Cancel, Skip)
- **colorTertiary**: Highlights, links, badges
- **colorSurface**: Cards, dialogs, sheets
- **colorSurfaceVariant**: Input fields, subtle backgrounds
- **colorError**: Errors, warnings, destructive actions

### 10.3. Typography Hierarchy
```
Display Large    → Hero Text (Landing, Onboarding)
Headline Large   → Screen Titles
Headline Medium  → Section Headers
Title Large      → Card Titles, Dialog Titles
Title Medium     → List Item Titles
Body Large       → Main Content
Body Medium      → Descriptions
Label Large      → Buttons, Chips, Tags
```

### 10.4. Button Priority
```
Filled Button   → Primary action (1 per screen)
Tonal Button    → Important action (1-2 per screen)
Outlined Button → Secondary action
Text Button     → Tertiary action
```

### 10.5. Card Types
```
Elevated Card   → Interactive, clickable cards
Filled Card     → Information display
Outlined Card   → Selection, options
```

---

## 🌗 11. DARK MODE

App đã tự động hỗ trợ Dark Mode! Chỉ cần thay đổi theme trong Settings:
```java
// Light Mode
AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

// Dark Mode
AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

// System Default
AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
```

---

## 📱 12. VÍ DỤ HOÀN CHỈNH - LOGIN SCREEN

```xml
<?xml version="1.0" encoding="utf-8"?>
<ScrollView xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="?attr/colorBackground"
    android:fillViewport="true">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="vertical"
        android:padding="24dp">

        <!-- Logo Section -->
        <ImageView
            android:layout_width="120dp"
            android:layout_height="120dp"
            android:layout_gravity="center"
            android:layout_marginTop="48dp"
            android:layout_marginBottom="32dp"
            android:src="@mipmap/ic_launcher" />

        <!-- Title -->
        <com.google.android.material.textview.MaterialTextView
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Welcome Back"
            android:textAppearance="?attr/textAppearanceHeadlineLarge"
            android:textColor="?attr/colorOnBackground"
            android:gravity="center"
            android:layout_marginBottom="8dp" />

        <!-- Subtitle -->
        <com.google.android.material.textview.MaterialTextView
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Sign in to continue"
            android:textAppearance="?attr/textAppearanceBodyLarge"
            android:textColor="?attr/colorOnSurfaceVariant"
            android:gravity="center"
            android:layout_marginBottom="32dp" />

        <!-- Email Input -->
        <com.google.android.material.textfield.TextInputLayout
            android:id="@+id/emailLayout"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:hint="Email"
            app:startIconDrawable="@android:drawable/ic_dialog_email"
            app:endIconMode="clear_text"
            android:layout_marginBottom="16dp"
            style="@style/Widget.Material3.TextInputLayout.OutlinedBox">

            <com.google.android.material.textfield.TextInputEditText
                android:id="@+id/emailInput"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:inputType="textEmailAddress" />

        </com.google.android.material.textfield.TextInputLayout>

        <!-- Password Input -->
        <com.google.android.material.textfield.TextInputLayout
            android:id="@+id/passwordLayout"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:hint="Password"
            app:startIconDrawable="@android:drawable/ic_lock_lock"
            app:endIconMode="password_toggle"
            android:layout_marginBottom="8dp"
            style="@style/Widget.Material3.TextInputLayout.OutlinedBox">

            <com.google.android.material.textfield.TextInputEditText
                android:id="@+id/passwordInput"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:inputType="textPassword" />

        </com.google.android.material.textfield.TextInputLayout>

        <!-- Remember Me -->
        <com.google.android.material.checkbox.MaterialCheckBox
            android:id="@+id/rememberMe"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="Remember me"
            android:layout_marginBottom="16dp" />

        <!-- Login Button -->
        <com.google.android.material.button.MaterialButton
            android:id="@+id/btnLogin"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Login"
            android:layout_marginBottom="12dp"
            style="@style/Widget.Material3.Button" />

        <!-- Register Button -->
        <com.google.android.material.button.MaterialButton
            android:id="@+id/btnRegister"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Create Account"
            android:layout_marginBottom="16dp"
            style="@style/Widget.Material3.Button.OutlinedButton" />

        <!-- Forgot Password -->
        <com.google.android.material.button.MaterialButton
            android:id="@+id/btnForgotPassword"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="Forgot Password?"
            android:layout_gravity="center"
            style="@style/Widget.Material3.Button.TextButton" />

    </LinearLayout>

</ScrollView>
```

---

## 🎉 KẾT LUẬN

Bạn đã có đầy đủ Material Design 3 system! 

**Files quan trọng:**
- ✅ `colors.xml` - Toàn bộ màu sắc M3
- ✅ `themes.xml` - Light theme configuration
- ✅ `themes.xml (night)` - Dark theme configuration
- ✅ `activity_main.xml` - Demo components

**Bước tiếp theo:**
1. Chạy app và xem demo
2. Áp dụng các components vào screens của bạn
3. Sử dụng đúng màu sắc và typography theo hướng dẫn
4. Test cả Light và Dark mode

Chúc bạn code vui! 🚀

