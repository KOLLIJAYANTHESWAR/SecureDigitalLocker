#!/bin/bash

BASE_URL="http://localhost:8080"
SUFFIX=$(date +%s)

ADMIN_EMAIL="admin@example.com"
ADMIN_PASS="admin123"

HR_EMAIL="hr_${SUFFIX}@example.com"
MGR_EMAIL="manager_${SUFFIX}@example.com"
USER_EMAIL="user_${SUFFIX}@example.com"

echo "============================"
echo " SECURE DIGITAL LOCKER TEST "
echo "============================"

set -e

# =============================
# SAFE JSON HELPERS
# =============================

extract_token() {
  python -c "
import sys,json
data=json.load(sys.stdin)
if data.get('success') and data.get('data'):
    print(data['data']['accessToken'])
else:
    print('')
"
}

extract_id() {
  python -c "
import sys,json
data=json.load(sys.stdin)
if data.get('success') and data.get('data'):
    print(data['data']['id'])
else:
    print('')
"
}

print_response_if_failed() {
  python -c "
import sys,json
data=json.load(sys.stdin)
if not data.get('success'):
    print('❌ ERROR:', data.get('message'))
    sys.exit(1)
"
}

# =============================
# ADMIN LOGIN
# =============================

echo "1. Admin Login"

ADMIN_LOGIN=$(curl -s -X POST "$BASE_URL/api/auth/login" \
  -H "Content-Type: application/json" \
  -d "{\"email\":\"$ADMIN_EMAIL\",\"password\":\"$ADMIN_PASS\"}")

echo "$ADMIN_LOGIN" | print_response_if_failed

ADMIN_TOKEN=$(echo "$ADMIN_LOGIN" | extract_token)

if [ -z "$ADMIN_TOKEN" ]; then
  echo "Admin login failed!"
  echo "$ADMIN_LOGIN"
  exit 1
fi

echo "✅ Admin token acquired"

# =============================
# CREATE HR
# =============================

echo "2. Admin creates HR"

HR_CREATE=$(curl -s -X POST "$BASE_URL/api/users/create" \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -H "Content-Type: application/json" \
  -d "{
    \"email\":\"$HR_EMAIL\",
    \"password\":\"password123\",
    \"role\":\"HR\",
    \"fullName\":\"HR Person\"
  }")

echo "$HR_CREATE" | print_response_if_failed

HR_ID=$(echo "$HR_CREATE" | extract_id)

echo "✅ HR created with ID: $HR_ID"

# =============================
# ADMIN APPROVES HR
# =============================

PENDING=$(curl -s -X GET "$BASE_URL/api/approvals/pending" \
  -H "Authorization: Bearer $ADMIN_TOKEN")

HR_APP_ID=$(echo "$PENDING" | python -c "
import sys,json
data=json.load(sys.stdin)
if not data.get('success'):
    sys.exit(1)
for item in data['data']:
    if item['requestedUser']['id']==$HR_ID:
        print(item['id'])
        break
")

APPROVE_HR=$(curl -s -X POST "$BASE_URL/api/approvals/process" \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -H "Content-Type: application/json" \
  -d "{
    \"approvalRequestId\":$HR_APP_ID,
    \"action\":\"APPROVE\",
    \"remarks\":\"Approved HR\"
  }")

echo "$APPROVE_HR" | print_response_if_failed

echo "✅ HR approved"

# =============================
# HR LOGIN
# =============================

HR_LOGIN=$(curl -s -X POST "$BASE_URL/api/auth/login" \
  -H "Content-Type: application/json" \
  -d "{\"email\":\"$HR_EMAIL\",\"password\":\"password123\"}")

echo "$HR_LOGIN" | print_response_if_failed

HR_TOKEN=$(echo "$HR_LOGIN" | extract_token)

echo "✅ HR login successful"

# =============================
# HR CREATES MANAGER
# =============================

MGR_CREATE=$(curl -s -X POST "$BASE_URL/api/users/create" \
  -H "Authorization: Bearer $HR_TOKEN" \
  -H "Content-Type: application/json" \
  -d "{
    \"email\":\"$MGR_EMAIL\",
    \"password\":\"password123\",
    \"role\":\"MANAGER\",
    \"fullName\":\"Manager Person\"
  }")

echo "$MGR_CREATE" | print_response_if_failed

MGR_ID=$(echo "$MGR_CREATE" | extract_id)

echo "✅ Manager created with ID: $MGR_ID"

# =============================
# HR APPROVES MANAGER
# =============================

PENDING_MGR=$(curl -s -X GET "$BASE_URL/api/approvals/pending" \
  -H "Authorization: Bearer $HR_TOKEN")

MGR_APP_ID=$(echo "$PENDING_MGR" | python -c "
import sys,json
data=json.load(sys.stdin)
if not data.get('success'):
    sys.exit(1)
for item in data['data']:
    if item['requestedUser']['id']==$MGR_ID:
        print(item['id'])
        break
")

APPROVE_MGR=$(curl -s -X POST "$BASE_URL/api/approvals/process" \
  -H "Authorization: Bearer $HR_TOKEN" \
  -H "Content-Type: application/json" \
  -d "{
    \"approvalRequestId\":$MGR_APP_ID,
    \"action\":\"APPROVE\",
    \"remarks\":\"Approved Manager\"
  }")

echo "$APPROVE_MGR" | print_response_if_failed

echo "✅ Manager approved"

# =============================
# MANAGER LOGIN
# =============================

MGR_LOGIN=$(curl -s -X POST "$BASE_URL/api/auth/login" \
  -H "Content-Type: application/json" \
  -d "{\"email\":\"$MGR_EMAIL\",\"password\":\"password123\"}")

echo "$MGR_LOGIN" | print_response_if_failed

MGR_TOKEN=$(echo "$MGR_LOGIN" | extract_token)

echo "✅ Manager login successful"

# =============================
# MANAGER CREATES USER
# =============================

USER_CREATE=$(curl -s -X POST "$BASE_URL/api/users/create" \
  -H "Authorization: Bearer $MGR_TOKEN" \
  -H "Content-Type: application/json" \
  -d "{
    \"email\":\"$USER_EMAIL\",
    \"password\":\"password123\",
    \"role\":\"USER\",
    \"fullName\":\"Regular User\"
  }")

echo "$USER_CREATE" | print_response_if_failed

USER_ID=$(echo "$USER_CREATE" | extract_id)

echo "✅ User created with ID: $USER_ID"

# =============================
# MANAGER APPROVES USER
# =============================

PENDING_USER=$(curl -s -X GET "$BASE_URL/api/approvals/pending" \
  -H "Authorization: Bearer $MGR_TOKEN")

USER_APP_ID=$(echo "$PENDING_USER" | python -c "
import sys,json
data=json.load(sys.stdin)
if not data.get('success'):
    sys.exit(1)
for item in data['data']:
    if item['requestedUser']['id']==$USER_ID:
        print(item['id'])
        break
")

APPROVE_USER=$(curl -s -X POST "$BASE_URL/api/approvals/process" \
  -H "Authorization: Bearer $MGR_TOKEN" \
  -H "Content-Type: application/json" \
  -d "{
    \"approvalRequestId\":$USER_APP_ID,
    \"action\":\"APPROVE\",
    \"remarks\":\"Approved User\"
  }")

echo "$APPROVE_USER" | print_response_if_failed

echo "✅ User approved"

# =============================
# USER LOGIN
# =============================

USER_LOGIN=$(curl -s -X POST "$BASE_URL/api/auth/login" \
  -H "Content-Type: application/json" \
  -d "{\"email\":\"$USER_EMAIL\",\"password\":\"password123\"}")

echo "$USER_LOGIN" | print_response_if_failed

USER_TOKEN=$(echo "$USER_LOGIN" | extract_token)

echo "✅ User login successful"

echo "🎉 ALL TESTS COMPLETED SUCCESSFULLY"