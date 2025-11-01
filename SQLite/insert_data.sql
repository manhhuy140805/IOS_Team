
-- =======================
-- Sample Data for Volunteer App Database
-- =======================

-- ===== USERS =====
INSERT INTO users (full_name, email, password_hash, role)
VALUES 
('Tổ chức Thiện Nguyện Đà Nẵng', 'org1.danang@gmail.com', '123456', 'organization'),
('Câu lạc bộ Môi Trường Xanh', 'greenclub1@gmail.com', '123456', 'organization'),
('Lê Văn Tân', 'tanlv1@gmail.com', '123456', 'volunteer'),
('Trần Thị Mai', 'maitt1@gmail.com', '123456', 'volunteer'),
('Nguyễn Hữu Dũng', 'dunghn1@gmail.com', '123456', 'volunteer');

-- ===== EVENT TYPES =====
INSERT INTO event_types (name, description)
VALUES
('Hoạt động môi trường', 'Dọn rác, trồng cây, làm sạch không gian công cộng'),
('Hỗ trợ giáo dục', 'Dạy học, quyên góp sách vở cho trẻ em'),
('Chăm sóc sức khỏe', 'Hiến máu, tư vấn sức khỏe cộng đồng');

-- ===== EVENTS =====
INSERT INTO events (
  creator_id, event_type_id, title, description,
  address, location, start_time, end_time, capacity,
  reward_points, status, has_certificate
)
VALUES
(1, 1, 'Chiến dịch Làm Sạch Bãi Biển',
 'Cùng nhau dọn rác tại bãi biển Mỹ Khê – Đà Nẵng',
 'Bãi biển Mỹ Khê', 'Đà Nẵng',
 '2025-11-05 07:00:00', '2025-11-05 10:00:00',
 50, 100, 'approved', 0),

(1, 2, 'Dạy Học Cho Trẻ Em Vùng Cao',
 'Tổ chức lớp học ngắn hạn cho học sinh khó khăn',
 'Trường Tiểu Học Sơn Trà', 'Quảng Nam',
 '2025-11-10 08:00:00', '2025-11-10 17:00:00',
 20, 150, 'pending', 0),

(2, 3, 'Hiến Máu Nhân Đạo Toàn Thành Phố',
 'Sự kiện hiến máu tình nguyện kết hợp với bệnh viện Đà Nẵng',
 'Trung tâm hiến máu Đà Nẵng', 'Đà Nẵng',
 '2025-12-01 08:00:00', '2025-12-01 16:00:00',
 100, 200, 'approved', 0);

-- ===== EVENT REGISTRATIONS =====
INSERT INTO event_registrations (user_id, event_id, status, check_in, completed_hours)
VALUES
(3, 1, 'approved', 1, 3),
(4, 1, 'approved', 1, 3),
(5, 2, 'pending', 0, 0),
(3, 3, 'approved', 1, 8);

-- ===== REWARDS =====
INSERT INTO rewards (name, description, points_required, stock)
VALUES
('Áo thun tình nguyện', 'Áo thun cotton in logo chương trình', 300, 10),
('Bình nước giữ nhiệt', 'Bình inox 500ml dành cho TNV tích cực', 400, 8),
('Sổ tay xanh', 'Sổ tay giấy tái chế thân thiện môi trường', 200, 20);

-- ===== USER REWARDS =====
INSERT INTO user_rewards (user_id, reward_id, status)
VALUES
(3, 3, 'approved'),
(4, 1, 'pending'),
(3, 2, 'delivered');
