# 🗄️ DATABASE SCHEMA - FINAL

## 1. users
- id
- email
- password
- full_name
- avatar_url
- role (string)(default -> VOLUNTEER)
- status (string)
- total_points
- address
- date_of_birth
- gender
- violation (true/false)
- created_at
- updated_at

## 2. event_type
- id
- name
- description

## 3. event
- id
- title
- description
- location
- image_url
- event_start_time
- event_end_time
- num_of_volunteers
- reward_points
- status (string)
- category (string)
- creator_id → users(id)
- event_type_id → event_type(id)
- created_at
- updated_at

## 4. event_registration
- id
- user_id → users(id)
- event_id → event(id)
- status (string)
- notes
- join_date
- checked_in
- checked_in_at
- notification_content
- created_at
- updated_at

## 5. reward
- id
- name
- description
- type (VOUCHER, GIFT, EXPERIENCE)
- image_url
- points_required
- quantity
- status (ACTIVE, PAUSED, OUT_OF_STOCK)
- expiry_date
- provider_id → users(id)
- created_at
- updated_at

## 6. user_reward
- id
- user_id → users(id)
- reward_id → reward(id)
- status (PENDING, APPROVED, USED, EXPIRED, CANCELLED)
- points_spent
- notes
- created_at
- updated_at
