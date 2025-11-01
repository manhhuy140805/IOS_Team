
@echo off
echo ===============================
echo  Volunteer App Database Reset Tool
echo ===============================
echo.

REM Đặt tên file database
set DB_FILE=volunteer_app.db

REM Kiểm tra file có tồn tại không
if exist %DB_FILE% (
    echo Xoa database cu...
    del %DB_FILE%
)

REM Tạo mới và import schema + data
echo Dang tao database moi va nap du lieu...
sqlite3 volunteer_app.db ".read schema.sql"
sqlite3 volunteer_app.db ".read insert_data.sql"

echo.
echo Da hoan thanh! Database volunteer_app.db da duoc tao lai.
pause
