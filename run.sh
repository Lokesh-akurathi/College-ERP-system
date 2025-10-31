#!/bin/bash
# =====================================
# University ERP Run Script (Dev Mode + Secure DB setup)
# =====================================

set -e

echo "====================================="
echo "University ERP - Dev Run Script"
echo "====================================="

# ---------- CONFIGURATION ----------
DB_USER="postgres"
AUTH_DB="auth_db"
ERP_DB="erp_db"
AUTH_SCHEMA="database/auth_db_schema.sql"
ERP_SCHEMA="database/erp_db_schema.sql"
MAIN_CLASS="edu.univ.erp.Main"
# -----------------------------------

## 1. Ask for PostgreSQL password securely
#read -s -p "Enter PostgreSQL password for user '$DB_USER': " DB_PASS
#echo ""
PGPASSWORD="postgres"

# 2. Check for required tools
command -v java >/dev/null 2>&1 || { echo "Java not found. Install JDK 17+."; exit 1; }
command -v mvn >/dev/null 2>&1 || { echo "Maven not found. Install Maven."; exit 1; }
command -v psql >/dev/null 2>&1 || { echo "psql not found. Install PostgreSQL client."; exit 1; }

# 3. Create databases if missing
echo "🗄️ Checking PostgreSQL databases..."
for DB in "$AUTH_DB" "$ERP_DB"
do
    DB_EXISTS=$(psql -U "$DB_USER" -tAc "SELECT 1 FROM pg_database WHERE datname='$DB'" || echo "error")
    if [ "$DB_EXISTS" = "1" ]; then
        echo "✅ Database '$DB' already exists."
    else
        echo "⚙️ Creating database '$DB'..."
        createdb -U "$DB_USER" "$DB"
        echo "✅ Database '$DB' created."
    fi
done

# 4. Apply schema files if they exist
if [ -f "$AUTH_SCHEMA" ]; then
    echo "📜 Loading schema for $AUTH_DB..."
    psql -U "$DB_USER" -d "$AUTH_DB" -f "$AUTH_SCHEMA"
else
    echo "⚠️ Schema file $AUTH_SCHEMA not found — skipping."
fi

if [ -f "$ERP_SCHEMA" ]; then
    echo "📜 Loading schema for $ERP_DB..."
    psql -U "$DB_USER" -d "$ERP_DB" -f "$ERP_SCHEMA"
else
    echo "⚠️ Schema file $ERP_SCHEMA not found — skipping."
fi

# 5. Compile classes (without packaging JAR)
echo "🧩 Compiling project..."
mvn compile -q

# 6. Run Main class directly
echo "🚀 Starting University ERP (dev mode)..."
mvn exec:java -Dexec.mainClass="$MAIN_CLASS"

echo "====================================="
echo "✅ University ERP (Development Mode) is running!"
echo "====================================="
