-- ================================================
-- FAKE DATA FOR VOLUNTEER APP
-- ================================================
-- Created: 2025-12-20
-- Description: Sample data for testing
-- ================================================
-- 
-- IMPORTANT: 
-- If tables already have data, you may need to:
-- 1. Delete existing data first, OR
-- 2. Make sure the IDs in foreign keys match existing data
--
-- Xóa tất cả dữ liệu và reset sequences
TRUNCATE TABLE users_notifications, notifications, user_reward, event_registration, reward, event, reward_type, event_type, users CASCADE;

-- Reset sequences
ALTER SEQUENCE users_id_seq RESTART WITH 1;
ALTER SEQUENCE event_type_id_seq RESTART WITH 1;
ALTER SEQUENCE reward_type_id_seq RESTART WITH 1;
ALTER SEQUENCE event_id_seq RESTART WITH 1;
ALTER SEQUENCE reward_id_seq RESTART WITH 1;
ALTER SEQUENCE event_registration_id_seq RESTART WITH 1;
ALTER SEQUENCE user_reward_id_seq RESTART WITH 1;
ALTER SEQUENCE notifications_id_seq RESTART WITH 1;
ALTER SEQUENCE users_notifications_id_seq RESTART WITH 1;
-- ================================================

-- Password hash for "123456" using BCrypt
-- $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy

-- ================================================
-- TABLE: users
-- ================================================
INSERT INTO users (email, password, full_name, phone, avatar_url, role, status, total_points, address, date_of_birth, gender, violation, created_at, updated_at) VALUES
-- Admin account
('admin@volunteer.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Nguyễn Văn Admin', '0901234567', 'https://i.pravatar.cc/150?img=1', 'ADMIN', 'ACTIVE', 0, '123 Đường ABC, Quận 1, TP.HCM', '1990-01-15', 'MALE', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Organization account
('org@volunteer.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Tổ chức Tình nguyện Xanh', '0901234568', 'https://i.pravatar.cc/150?img=2', 'ORGANIZATION', 'ACTIVE', 0, '456 Đường XYZ, Quận 2, TP.HCM', '1985-05-20', 'OTHER', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Volunteer accounts
('volunteer1@volunteer.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Trần Thị Hoa', '0901234569', 'https://i.pravatar.cc/150?img=3', 'VOLUNTEER', 'ACTIVE', 150, '789 Đường DEF, Quận 3, TP.HCM', '1995-03-10', 'FEMALE', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('volunteer2@volunteer.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Lê Văn Nam', '0901234570', 'https://i.pravatar.cc/150?img=4', 'VOLUNTEER', 'ACTIVE', 200, '321 Đường GHI, Quận 4, TP.HCM', '1992-07-25', 'MALE', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('volunteer3@volunteer.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Phạm Thị Lan', '0901234571', 'https://i.pravatar.cc/150?img=5', 'VOLUNTEER', 'ACTIVE', 300, '654 Đường JKL, Quận 5, TP.HCM', '1998-11-30', 'FEMALE', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('volunteer4@volunteer.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Hoàng Văn Đức', '0901234572', 'https://i.pravatar.cc/150?img=6', 'VOLUNTEER', 'ACTIVE', 100, '987 Đường MNO, Quận 6, TP.HCM', '1993-09-12', 'MALE', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('volunteer5@volunteer.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Võ Thị Mai', '0901234573', 'https://i.pravatar.cc/150?img=7', 'VOLUNTEER', 'ACTIVE', 250, '147 Đường PQR, Quận 7, TP.HCM', '1996-02-18', 'FEMALE', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('volunteer6@volunteer.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Đỗ Văn Tuấn', '0901234574', 'https://i.pravatar.cc/150?img=8', 'VOLUNTEER', 'ACTIVE', 180, '258 Đường STU, Quận 8, TP.HCM', '1994-06-22', 'MALE', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('volunteer7@volunteer.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Bùi Thị Hương', '0901234575', 'https://i.pravatar.cc/150?img=9', 'VOLUNTEER', 'ACTIVE', 220, '369 Đường VWX, Quận 9, TP.HCM', '1997-04-05', 'FEMALE', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('volunteer8@volunteer.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Ngô Văn Hùng', '0901234576', 'https://i.pravatar.cc/150?img=10', 'VOLUNTEER', 'ACTIVE', 170, '741 Đường YZ, Quận 10, TP.HCM', '1991-08-14', 'MALE', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Reset sequence after inserting users (to ensure IDs are 1, 2, 3...)
SELECT setval('users_id_seq', (SELECT MAX(id) FROM users));

-- ================================================
-- TABLE: event_type
-- ================================================
INSERT INTO event_type (name, description) VALUES
('Môi trường', 'Các hoạt động bảo vệ môi trường, làm sạch, trồng cây'),
('Giáo dục', 'Dạy học, hỗ trợ học tập cho trẻ em, người lớn'),
('Y tế', 'Khám chữa bệnh miễn phí, hiến máu, tư vấn sức khỏe'),
('Xã hội', 'Hỗ trợ người già, trẻ em có hoàn cảnh khó khăn'),
('Văn hóa', 'Tổ chức sự kiện văn hóa, nghệ thuật'),
('Thể thao', 'Tổ chức giải đấu, hoạt động thể thao cộng đồng'),
('Cứu trợ', 'Cứu trợ thiên tai, hỗ trợ người dân vùng khó khăn'),
('Công nghệ', 'Dạy tin học, hỗ trợ công nghệ cho người cao tuổi'),
('Du lịch', 'Hướng dẫn du lịch, bảo tồn di tích'),
('Nông nghiệp', 'Hỗ trợ nông dân, phát triển nông nghiệp bền vững');

-- Reset sequence after inserting event_type
SELECT setval('event_type_id_seq', (SELECT MAX(id) FROM event_type));

-- ================================================
-- TABLE: reward_type
-- ================================================
INSERT INTO reward_type (title) VALUES
('Voucher ăn uống'),
('Voucher mua sắm'),
('Quà tặng'),
('Giảm giá dịch vụ'),
('Phần thưởng tiền mặt'),
('Khóa học miễn phí'),
('Voucher du lịch'),
('Sản phẩm công nghệ'),
('Voucher spa & làm đẹp'),
('Phần thưởng đặc biệt');

-- Reset sequence after inserting reward_type
SELECT setval('reward_type_id_seq', (SELECT MAX(id) FROM reward_type));

-- ================================================
-- TABLE: event
-- ================================================
INSERT INTO event (title, description, location, image_url, event_start_time, event_end_time, num_of_volunteers, reward_points, status, category, created_at, updated_at, creator_id, event_type_id) VALUES
('Dọn dẹp bãi biển Vũng Tàu', 'Tổ chức dọn dẹp rác thải tại bãi biển Vũng Tàu, bảo vệ môi trường biển', 'Bãi biển Vũng Tàu, Bà Rịa - Vũng Tàu', 'https://file3.qdnd.vn/data/images/0/2020/07/04/tuanson/5.jpg', '2025-01-15', '2025-01-15', 50, 50, 'APPROVED', 'Môi trường', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 2, 1),
('Dạy học cho trẻ em vùng cao', 'Tổ chức lớp học tình nguyện cho trẻ em vùng cao, dạy tiếng Anh và toán', 'Xã Đồng Văn, Hà Giang', 'https://file3.qdnd.vn/data/images/0/2020/07/04/tuanson/5.jpg', '2025-01-20', '2025-01-25', 30, 100, 'APPROVED', 'Giáo dục', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 2, 2),
('Hiến máu nhân đạo', 'Tổ chức hiến máu nhân đạo tại bệnh viện Chợ Rẫy', 'Bệnh viện Chợ Rẫy, TP.HCM', 'https://file3.qdnd.vn/data/images/0/2020/07/04/tuanson/5.jpg', '2025-02-01', '2025-02-01', 100, 30, 'PENDING', 'Y tế', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 2, 3),
('Thăm hỏi người già neo đơn', 'Tổ chức thăm hỏi, tặng quà cho người già neo đơn tại các trung tâm bảo trợ', 'Quận 1, TP.HCM', 'https://file3.qdnd.vn/data/images/0/2020/07/04/tuanson/5.jpg', '2025-02-10', '2025-02-10', 40, 40, 'APPROVED', 'Xã hội', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 2, 4),
('Festival văn hóa dân gian', 'Tổ chức festival văn hóa dân gian, giới thiệu văn hóa các vùng miền', 'Công viên Lê Văn Tám, TP.HCM', 'https://file3.qdnd.vn/data/images/0/2020/07/04/tuanson/5.jpg', '2025-02-15', '2025-02-17', 60, 60, 'ONGOING', 'Văn hóa', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 2, 5),
('Giải bóng đá từ thiện', 'Tổ chức giải bóng đá từ thiện, quyên góp cho trẻ em nghèo', 'Sân vận động Thống Nhất, TP.HCM', 'https://file3.qdnd.vn/data/images/0/2020/07/04/tuanson/5.jpg', '2025-02-20', '2025-02-22', 80, 70, 'APPROVED', 'Thể thao', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 2, 6),
('Cứu trợ lũ lụt miền Trung', 'Tổ chức cứu trợ, hỗ trợ người dân vùng lũ lụt miền Trung', 'Tỉnh Quảng Nam', 'https://file3.qdnd.vn/data/images/0/2020/07/04/tuanson/5.jpg', '2025-03-01', '2025-03-05', 100, 150, 'PENDING', 'Cứu trợ', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 2, 7),
('Dạy tin học cho người cao tuổi', 'Tổ chức lớp dạy tin học cơ bản cho người cao tuổi', 'Trung tâm văn hóa Quận 3, TP.HCM', 'https://file3.qdnd.vn/data/images/0/2020/07/04/tuanson/5.jpg', '2025-03-10', '2025-03-15', 25, 80, 'APPROVED', 'Công nghệ', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 2, 8),
('Hướng dẫn du lịch bền vững', 'Tổ chức tour hướng dẫn du lịch bền vững, bảo vệ di tích', 'Phố cổ Hội An, Quảng Nam', 'https://file3.qdnd.vn/data/images/0/2020/07/04/tuanson/5.jpg', '2025-03-20', '2025-03-22', 35, 50, 'APPROVED', 'Du lịch', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 2, 9),
('Hỗ trợ nông dân thu hoạch', 'Tổ chức hỗ trợ nông dân thu hoạch mùa màng tại Đà Lạt', 'Đà Lạt, Lâm Đồng', 'https://file3.qdnd.vn/data/images/0/2020/07/04/tuanson/5.jpg', '2025-04-01', '2025-04-05', 45, 90, 'PENDING', 'Nông nghiệp', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 2, 10);

-- Reset sequence after inserting event
SELECT setval('event_id_seq', (SELECT MAX(id) FROM event));

-- ================================================
-- TABLE: reward
-- ================================================
INSERT INTO reward (name, description, type, image_url, points_required, quantity, status, expiry_date, created_at, updated_at, reward_type_id, provider_id) VALUES
('Voucher Pizza Hut 200k', 'Voucher giảm giá 200k khi mua pizza tại Pizza Hut', 'VOUCHER', 'https://thiepmung.com/uploads/worigin/2022/04/16/chung-nhan-thanh-tich-danh-cho-nhan-vien-xuat-sac_b0fbe.jpg', 100, 50, 'ACTIVE', '2025-12-31', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 2),
('Voucher VinMart 500k', 'Voucher mua sắm 500k tại VinMart', 'VOUCHER', 'https://thiepmung.com/uploads/worigin/2022/04/16/chung-nhan-thanh-tich-danh-cho-nhan-vien-xuat-sac_b0fbe.jpg', 200, 30, 'ACTIVE', '2025-12-31', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 2, 2),
('Áo thun tình nguyện', 'Áo thun in logo tình nguyện, chất lượng cao', 'GIFT', 'https://thiepmung.com/uploads/worigin/2022/04/16/chung-nhan-thanh-tich-danh-cho-nhan-vien-xuat-sac_b0fbe.jpg', 150, 100, 'ACTIVE', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 3, 2),
('Giảm 30% dịch vụ spa', 'Giảm giá 30% tất cả dịch vụ tại spa cao cấp', 'DISCOUNT', 'https://thiepmung.com/uploads/worigin/2022/04/16/chung-nhan-thanh-tich-danh-cho-nhan-vien-xuat-sac_b0fbe.jpg', 80, 20, 'ACTIVE', '2025-06-30', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 4, 2),
('Thẻ cào 100k', 'Thẻ cào điện thoại trị giá 100k', 'GIFT', 'https://thiepmung.com/uploads/worigin/2022/04/16/chung-nhan-thanh-tich-danh-cho-nhan-vien-xuat-sac_b0fbe.jpg', 120, 40, 'ACTIVE', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 5, 2),
('Khóa học online miễn phí', 'Khóa học lập trình online trị giá 500k', 'SERVICE', 'https://thiepmung.com/uploads/worigin/2022/04/16/chung-nhan-thanh-tich-danh-cho-nhan-vien-xuat-sac_b0fbe.jpg', 300, 15, 'ACTIVE', '2025-12-31', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 6, 2),
('Voucher du lịch Đà Lạt', 'Voucher giảm 20% tour du lịch Đà Lạt 2 ngày 1 đêm', 'VOUCHER', 'https://thiepmung.com/uploads/worigin/2022/04/16/chung-nhan-thanh-tich-danh-cho-nhan-vien-xuat-sac_b0fbe.jpg', 400, 10, 'ACTIVE', '2025-08-31', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 7, 2),
('Tai nghe Bluetooth', 'Tai nghe Bluetooth chất lượng cao', 'GIFT', 'https://thiepmung.com/uploads/worigin/2022/04/16/chung-nhan-thanh-tich-danh-cho-nhan-vien-xuat-sac_b0fbe.jpg', 500, 5, 'ACTIVE', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 8, 2),
('Voucher làm đẹp 300k', 'Voucher làm đẹp trị giá 300k tại salon cao cấp', 'VOUCHER', 'https://thiepmung.com/uploads/worigin/2022/04/16/chung-nhan-thanh-tich-danh-cho-nhan-vien-xuat-sac_b0fbe.jpg', 250, 25, 'ACTIVE', '2025-10-31', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 9, 2),
('Chứng nhận tình nguyện viên xuất sắc', 'Chứng nhận danh hiệu tình nguyện viên xuất sắc năm 2025', 'GIFT', 'https://thiepmung.com/uploads/worigin/2022/04/16/chung-nhan-thanh-tich-danh-cho-nhan-vien-xuat-sac_b0fbe.jpg', 1000, 3, 'ACTIVE', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 10, 2);

-- Reset sequence after inserting reward
SELECT setval('reward_id_seq', (SELECT MAX(id) FROM reward));

-- ================================================
-- TABLE: event_registration
-- ================================================
INSERT INTO event_registration (status, notes, join_date, checked_in, checked_in_at, notification_content, created_at, updated_at, user_id, event_id) VALUES
('APPROVED', 'Rất vui được tham gia', '2025-01-15', true, '2025-01-15 08:00:00', 'Bạn đã được duyệt tham gia sự kiện', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 3, 1),
('APPROVED', 'Sẵn sàng tham gia', '2025-01-20', false, NULL, 'Bạn đã được duyệt tham gia sự kiện', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 4, 2),
('PENDING', 'Mong được tham gia', '2025-02-01', false, NULL, 'Đơn đăng ký của bạn đang được xem xét', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 5, 3),
('APPROVED', 'Cảm ơn tổ chức', '2025-02-10', true, '2025-02-10 07:30:00', 'Bạn đã được duyệt tham gia sự kiện', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 6, 4),
('APPROVED', 'Hân hạnh tham gia', '2025-02-15', false, NULL, 'Bạn đã được duyệt tham gia sự kiện', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 7, 5),
('APPROVED', 'Rất mong chờ', '2025-02-20', false, NULL, 'Bạn đã được duyệt tham gia sự kiện', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 8, 6),
('PENDING', 'Hy vọng được chấp nhận', '2025-03-01', false, NULL, 'Đơn đăng ký của bạn đang được xem xét', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 9, 7),
('APPROVED', 'Sẵn sàng giúp đỡ', '2025-03-10', false, NULL, 'Bạn đã được duyệt tham gia sự kiện', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 10, 8),
('APPROVED', 'Cảm ơn cơ hội', '2025-03-20', false, NULL, 'Bạn đã được duyệt tham gia sự kiện', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 3, 9),
('PENDING', 'Mong được tham gia', '2025-04-01', false, NULL, 'Đơn đăng ký của bạn đang được xem xét', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 4, 10);

-- ================================================
-- TABLE: user_reward
-- ================================================
INSERT INTO user_reward (status, points_spent, notes, created_at, updated_at, user_id, reward_id) VALUES
('APPROVED', 100, 'Đã nhận voucher', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 3, 1),
('DELIVERED', 200, 'Đã nhận voucher mua sắm', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 4, 2),
('PENDING', 150, 'Đang chờ xử lý', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 5, 3),
('APPROVED', 80, 'Đã nhận voucher giảm giá', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 6, 4),
('DELIVERED', 120, 'Đã nhận thẻ cào', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 7, 5),
('APPROVED', 300, 'Đã đăng ký khóa học', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 8, 6),
('PENDING', 400, 'Đang chờ xử lý voucher du lịch', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 9, 7),
('APPROVED', 500, 'Đã nhận tai nghe', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 10, 8),
('DELIVERED', 250, 'Đã nhận voucher làm đẹp', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 3, 9),
('PENDING', 1000, 'Đang chờ xử lý chứng nhận', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 4, 10);

-- ================================================
-- TABLE: notifications
-- NOTE: Make sure notifications table exists before running this
-- ================================================
INSERT INTO notifications (title, content, attached, sender_id, sender_role, type, created_at) VALUES
('Chào mừng đến với VolunConnect', 'Cảm ơn bạn đã tham gia cộng đồng tình nguyện VolunConnect. Hãy khám phá các sự kiện và đóng góp cho xã hội!', NULL, NULL, 'SYSTEM', 'GLOBAL', CURRENT_TIMESTAMP),
('Sự kiện mới: Dọn dẹp bãi biển Vũng Tàu', 'Sự kiện dọn dẹp bãi biển Vũng Tàu đã được tạo. Hãy đăng ký tham gia ngay!', NULL, 2, 'ORGANIZATION', 'ORGANIZATION', CURRENT_TIMESTAMP),
('Đơn đăng ký đã được duyệt', 'Chúc mừng! Đơn đăng ký tham gia sự kiện "Dọn dẹp bãi biển Vũng Tàu" của bạn đã được duyệt.', NULL, 2, 'ORGANIZATION', 'PERSONAL', CURRENT_TIMESTAMP),
('Thông báo về phần thưởng mới', 'Chúng tôi vừa thêm nhiều phần thưởng hấp dẫn. Hãy tích điểm và đổi quà ngay!', NULL, 1, 'ADMIN', 'GLOBAL', CURRENT_TIMESTAMP),
('Nhắc nhở sự kiện sắp diễn ra', 'Sự kiện "Dạy học cho trẻ em vùng cao" sẽ diễn ra vào ngày 20/01/2025. Vui lòng chuẩn bị!', NULL, 2, 'ORGANIZATION', 'PERSONAL', CURRENT_TIMESTAMP),
('Cảm ơn bạn đã tham gia', 'Cảm ơn bạn đã tham gia sự kiện "Thăm hỏi người già neo đơn". Bạn đã nhận được 40 điểm thưởng!', NULL, 2, 'ORGANIZATION', 'PERSONAL', CURRENT_TIMESTAMP),
('Thông báo hệ thống bảo trì', 'Hệ thống sẽ bảo trì vào ngày 25/01/2025 từ 2:00 - 4:00 sáng. Xin lỗi vì sự bất tiện này.', NULL, NULL, 'SYSTEM', 'GLOBAL', CURRENT_TIMESTAMP),
('Chúc mừng sinh nhật!', 'Chúc mừng sinh nhật bạn! Chúng tôi tặng bạn 50 điểm thưởng đặc biệt. Hãy tiếp tục đóng góp cho cộng đồng!', NULL, NULL, 'SYSTEM', 'PERSONAL', CURRENT_TIMESTAMP),
('Sự kiện đã hoàn thành', 'Sự kiện "Festival văn hóa dân gian" đã hoàn thành thành công. Cảm ơn sự tham gia của bạn!', NULL, 2, 'ORGANIZATION', 'ORGANIZATION', CURRENT_TIMESTAMP),
('Thông báo về chính sách mới', 'Chúng tôi đã cập nhật chính sách và điều khoản sử dụng. Vui lòng xem chi tiết tại trang thông tin.', NULL, 1, 'ADMIN', 'GLOBAL', CURRENT_TIMESTAMP);

-- ================================================
-- TABLE: users_notifications
-- NOTE: Make sure users_notifications table exists before running this
-- ================================================
INSERT INTO users_notifications (user_id, notification_id, is_read, read_at, created_at) VALUES
(1, 1, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 1, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 1, FALSE, NULL, CURRENT_TIMESTAMP),
(4, 1, FALSE, NULL, CURRENT_TIMESTAMP),
(5, 1, FALSE, NULL, CURRENT_TIMESTAMP),
(6, 1, FALSE, NULL, CURRENT_TIMESTAMP),
(7, 1, FALSE, NULL, CURRENT_TIMESTAMP),
(8, 1, FALSE, NULL, CURRENT_TIMESTAMP),
(9, 1, FALSE, NULL, CURRENT_TIMESTAMP),
(10, 1, FALSE, NULL, CURRENT_TIMESTAMP),
(3, 2, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, 2, FALSE, NULL, CURRENT_TIMESTAMP),
(5, 2, FALSE, NULL, CURRENT_TIMESTAMP),
(3, 3, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(1, 4, FALSE, NULL, CURRENT_TIMESTAMP),
(2, 4, FALSE, NULL, CURRENT_TIMESTAMP),
(3, 4, FALSE, NULL, CURRENT_TIMESTAMP),
(4, 4, FALSE, NULL, CURRENT_TIMESTAMP),
(5, 4, FALSE, NULL, CURRENT_TIMESTAMP),
(6, 4, FALSE, NULL, CURRENT_TIMESTAMP),
(7, 4, FALSE, NULL, CURRENT_TIMESTAMP),
(8, 4, FALSE, NULL, CURRENT_TIMESTAMP),
(9, 4, FALSE, NULL, CURRENT_TIMESTAMP),
(10, 4, FALSE, NULL, CURRENT_TIMESTAMP),
(4, 5, FALSE, NULL, CURRENT_TIMESTAMP),
(6, 6, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(1, 7, FALSE, NULL, CURRENT_TIMESTAMP),
(2, 7, FALSE, NULL, CURRENT_TIMESTAMP),
(3, 7, FALSE, NULL, CURRENT_TIMESTAMP),
(4, 7, FALSE, NULL, CURRENT_TIMESTAMP),
(5, 7, FALSE, NULL, CURRENT_TIMESTAMP),
(6, 7, FALSE, NULL, CURRENT_TIMESTAMP),
(7, 7, FALSE, NULL, CURRENT_TIMESTAMP),
(8, 7, FALSE, NULL, CURRENT_TIMESTAMP),
(9, 7, FALSE, NULL, CURRENT_TIMESTAMP),
(10, 7, FALSE, NULL, CURRENT_TIMESTAMP),
(5, 8, FALSE, NULL, CURRENT_TIMESTAMP),
(7, 9, FALSE, NULL, CURRENT_TIMESTAMP),
(8, 9, FALSE, NULL, CURRENT_TIMESTAMP),
(1, 10, FALSE, NULL, CURRENT_TIMESTAMP),
(2, 10, FALSE, NULL, CURRENT_TIMESTAMP),
(3, 10, FALSE, NULL, CURRENT_TIMESTAMP),
(4, 10, FALSE, NULL, CURRENT_TIMESTAMP),
(5, 10, FALSE, NULL, CURRENT_TIMESTAMP),
(6, 10, FALSE, NULL, CURRENT_TIMESTAMP),
(7, 10, FALSE, NULL, CURRENT_TIMESTAMP),
(8, 10, FALSE, NULL, CURRENT_TIMESTAMP),
(9, 10, FALSE, NULL, CURRENT_TIMESTAMP),
(10, 10, FALSE, NULL, CURRENT_TIMESTAMP);

