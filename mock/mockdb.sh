#!/bin/bash

# Mock Data Generator for Student Visit Database
# Optimized version - completes in under a minute
# Written by CLAUDE

set -e

DB_FILE="database.db"
SEED=42

# Remove existing database
rm -f "$DB_FILE"

echo "Creating database schema..."

# Create database and schema
sqlite3 "$DB_FILE" <<'EOF'
CREATE TABLE IF NOT EXISTS students (
    student_id INTEGER PRIMARY KEY AUTOINCREMENT,
    full_name TEXT NOT NULL,
    preferred_name TEXT NOT NULL,
    visit_time INTEGER NOT NULL,
    date_added INTEGER NOT NULL
);
CREATE TABLE IF NOT EXISTS visits (
    visit_id INTEGER PRIMARY KEY AUTOINCREMENT,
    start_time INTEGER NOT NULL,
    duration INTEGER NOT NULL,
    student_id INTEGER NOT NULL,
    FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE
);
CREATE TABLE IF NOT EXISTS ongoing_visits (
    start_time INTEGER NOT NULL,
    student_id INTEGER NOT NULL UNIQUE,
    FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE
);
EOF

# Diverse student names
NAMES=(
    "Chen Wei" "Li Hua" "Zhang Ming" "Wang Fang" "Liu Jian"
    "Tanaka Yuki" "Sato Haruki" "Yamamoto Aiko" "Watanabe Kenji" "Nakamura Sakura"
    "Kim Min-jun" "Park Ji-woo" "Lee Seo-yeon" "Choi Hyun-woo" "Jung Da-eun"
    "Nguyen Van An" "Tran Thi Lan" "Le Hoang Nam" "Pham Thu Ha" "Vo Minh Tuan"
    "Singh Rajesh" "Patel Priya" "Kumar Arjun" "Sharma Meera" "Gupta Aditya"
    "Khan Fatima" "Ahmed Hassan" "Ali Zainab" "Rahman Tariq" "Hussain Amina"
    "Smith John" "Johnson Emma" "Williams Michael" "Brown Sophia" "Jones Oliver"
    "Davis Ava" "Miller Liam" "Wilson Isabella" "Moore Ethan" "Taylor Charlotte"
    "Anderson James" "Thomas Amelia" "Jackson Benjamin" "White Harper" "Harris Mason"
    "Martin Emily" "Thompson Logan" "Garcia Sofia" "Martinez Lucas" "Robinson Ella"
    "Clark Jacob" "Rodriguez Avery" "Lewis Mia" "Lee Noah" "Walker Grace"
    "Hall Alexander" "Allen Lily" "Young Daniel" "King Aria" "Wright Samuel"
    "Mueller Hans" "Schmidt Anna" "Schneider Lars" "Fischer Emma" "Weber Lukas"
    "Meyer Sophie" "Wagner Felix" "Becker Marie" "Schulz Maximilian" "Hoffmann Laura"
    "Koch Jonas" "Richter Nina" "Klein Paul" "Wolf Julia" "Schroeder Ben"
    "Kowalski Jan" "Nowak Anna" "Wiśniewski Piotr" "Wójcik Maria" "Kamiński Tomasz"
    "Lewandowski Katarzyna" "Zieliński Marek" "Szymański Ewa" "Woźniak Andrzej" "Dąbrowski Agnieszka"
    "Petrov Ivan" "Ivanov Natasha" "Smirnov Dmitri" "Kuznetsov Elena" "Popov Alexei"
    "Sokolov Olga" "Lebedev Mikhail" "Kozlov Svetlana" "Novikov Andrei" "Morozov Tatiana"
    "Silva João" "Santos Maria" "Oliveira Pedro" "Costa Ana" "Souza Lucas"
    "Pereira Beatriz" "Rodrigues Gabriel" "Almeida Sofia" "Nascimento Miguel" "Lima Carolina"
    "García José" "Rodríguez María" "López Antonio" "Martínez Carmen" "González Juan"
    "Fernández Isabel" "Sánchez Miguel" "Pérez Laura" "Gómez Diego" "Ruiz Elena"
    "Hernández Carlos" "Jiménez Lucía" "Díaz Javier" "Moreno Paula" "Muñoz Alejandro"
    "Rossi Marco" "Russo Giulia" "Ferrari Alessandro" "Esposito Sofia" "Bianchi Lorenzo"
    "Romano Francesca" "Colombo Matteo" "Ricci Chiara" "Marino Luca" "Greco Valentina"
    "Okafor Chukwuemeka" "Adeyemi Folake" "Okoro Chidinma" "Mensah Kwame" "Diallo Aminata"
    "Kamau Wanjiru" "Mwangi Kimani" "Ochieng Akinyi" "Banda Thandiwe" "Moyo Tendai"
    "Nkosi Sipho" "Dlamini Zanele" "Khumalo Thabo" "Mahlangu Lerato" "Ndlovu Bongani"
    "Abebe Kidist" "Tesfaye Dawit" "Haile Meron" "Gebru Senait" "Tadesse Yohannes"
    "Mohamed Ahmed" "Ibrahim Fatma" "Hassan Aisha" "Ali Omar" "Abdullahi Halima"
    "Ben Saïd Youssef" "Benali Amina" "Mansour Karim" "Gharbi Salma" "Kaddouri Mehdi"
    "El Amrani Zineb" "Idrissi Rachid" "Benkirane Leila" "Alaoui Hamza" "Berrada Sara"
    "Dubois Pierre" "Bernard Sophie" "Moreau Antoine" "Laurent Camille" "Simon Thomas"
    "Michel Julie" "Lefebvre Nicolas" "Leroy Marie" "Garnier Alexandre" "Chevalier Emma"
    "Torres Mateo" "Ramírez Valentina" "Flores Santiago" "Rivera Camila" "Gómez Sebastián"
    "Morales Isabella" "Cruz Matías" "Reyes Sofía" "Gutiérrez Emiliano" "Ortiz Martina"
    "Vargas Benjamin" "Castro Victoria" "Mendoza Joaquín" "Rojas Catalina" "Herrera Agustín"
    "Silva Mariana" "Santos Felipe" "Costa Isabela" "Oliveira Rafael" "Souza Laura"
    "Pereira Thiago" "Lima Juliana" "Ferreira Bruno" "Ribeiro Amanda" "Carvalho Gustavo"
    "Alves Larissa" "Martins Leonardo" "Barbosa Fernanda" "Araújo Vitor" "Sousa Bianca"
    "Nguyen Minh" "Pham Linh" "Le Duc" "Hoang Mai" "Dang Tuan"
    "Bui Hoa" "Do Khanh" "Vo Anh" "Tran Long" "Ly Phuong"
    "Wong Ka-ming" "Chan Mei-ling" "Cheung Sze-man" "Lau Wai-kit" "Tam Hoi-yan"
    "Yip Chi-wai" "Ho Yuk-kwan" "Lam Ka-yan" "Cheng Wing-sze" "Tsang Tsz-ching"
    "Abdullah Nur" "Rahman Siti" "Ibrahim Aishah" "Hassan Nur" "Ahmad Fatimah"
    "Yusof Hakim" "Ismail Aisyah" "Omar Zul" "Ali Nadia" "Hashim Iman"
    "Park Sun-hee" "Kim Tae-yang" "Lee Yeon-joo" "Choi Joon-ho" "Jung Hye-jin"
    "Kang Min-seo" "Shin Dong-hyun" "Yoon Eun-ji" "Lim Sang-woo" "Han Yu-na"
    "Suzuki Hana" "Takahashi Ren" "Ito Yui" "Kobayashi Sota" "Saito Mio"
)

# Current time in milliseconds
NOW_MS=$(date +%s%3N)

echo "Generating SQL statements..."

# Create temporary SQL file
SQL_FILE=$(mktemp)

# Start transaction
echo "BEGIN TRANSACTION;" > "$SQL_FILE"

# Generate students
echo "Generating 300 students..."
for i in {1..300}; do
    RAND_SEED=$((SEED + i))

    # Select name (deterministic based on seed)
    NAME_IDX=$((RAND_SEED % ${#NAMES[@]}))
    FULL_NAME="${NAMES[$NAME_IDX]}"

    # Escape single quotes in names
    FULL_NAME="${FULL_NAME//\'/\'\'}"

    # Most students have empty preferred name (80% chance)
    PREF_CHECK=$((RAND_SEED % 10))
    if [ $PREF_CHECK -lt 8 ]; then
        PREF_NAME=""
    else
        # Extract first name as preferred name
        PREF_NAME=$(echo "$FULL_NAME" | awk '{print $1}')
        PREF_NAME="${PREF_NAME//\'/\'\'}"
    fi

    # Visit time: 30 or 60 minutes
    VISIT_TIME_CHECK=$((RAND_SEED % 2))
    if [ $VISIT_TIME_CHECK -eq 0 ]; then
        VISIT_TIME=30
    else
        VISIT_TIME=60
    fi

    # Date added: random time within last 5 years
    DAYS_AGO=$(( (RAND_SEED * 17) % 1825 ))
    DATE_ADDED=$(( NOW_MS - (DAYS_AGO * 86400 * 1000) ))

    echo "INSERT INTO students (full_name, preferred_name, visit_time, date_added) VALUES ('$FULL_NAME', '$PREF_NAME', $VISIT_TIME, $DATE_ADDED);" >> "$SQL_FILE"
done

# Store student info for visit generation
declare -A STUDENT_VISIT_TIME

for i in {1..300}; do
    RAND_SEED=$((SEED + i))
    VISIT_TIME_CHECK=$((RAND_SEED % 2))
    if [ $VISIT_TIME_CHECK -eq 0 ]; then
        STUDENT_VISIT_TIME[$i]=30
    else
        STUDENT_VISIT_TIME[$i]=60
    fi
done

# Generate visits for each student
echo "Generating visits for students..."
VISIT_COUNT=0

for STUDENT_ID in {1..300}; do
    RAND_SEED=$((SEED + STUDENT_ID))
    BASE_DURATION=${STUDENT_VISIT_TIME[$STUDENT_ID]}

    # Number of visits (5 to 150)
    NUM_VISITS=$(( ((RAND_SEED * 37) % 146) + 5 ))

    # Generate visits spread over the past 2 years
    for j in $(seq 1 $NUM_VISITS); do
        VISIT_SEED=$((RAND_SEED * 1000 + j))

        # Random time in past 2 years
        DAYS_AGO=$(( (VISIT_SEED * 13) % 730 ))
        HOUR_OFFSET=$(( (VISIT_SEED * 7) % 12 + 8 ))
        MINUTE_OFFSET=$(( (VISIT_SEED * 3) % 60 ))

        START_TIME=$(( NOW_MS - (DAYS_AGO * 86400 * 1000) - ((24 - HOUR_OFFSET) * 3600 * 1000) - (MINUTE_OFFSET * 60 * 1000) ))

        # Duration with variance (around the base duration)
        OUTLIER_CHECK=$(( VISIT_SEED % 20 ))
        if [ $OUTLIER_CHECK -eq 0 ]; then
            # Outlier: ±50% of base duration
            VARIANCE=$(( (VISIT_SEED % 100) - 50 ))
        else
            # Normal: ±30% of base duration
            VARIANCE=$(( (VISIT_SEED % 60) - 30 ))
        fi

        DURATION=$(( BASE_DURATION + (BASE_DURATION * VARIANCE / 100) ))

        # Ensure positive duration (minimum 5 minutes)
        if [ $DURATION -lt 5 ]; then
            DURATION=5
        fi

        echo "INSERT INTO visits (start_time, duration, student_id) VALUES ($START_TIME, $DURATION, $STUDENT_ID);" >> "$SQL_FILE"
        VISIT_COUNT=$((VISIT_COUNT + 1))
    done

    # Progress indicator every 50 students
    if [ $((STUDENT_ID % 50)) -eq 0 ]; then
        echo "  Generated visits for $STUDENT_ID students..."
    fi
done

# Generate ongoing visits
echo "Generating ongoing visits..."
ONGOING_COUNT=0
ONGOING_TARGET=35

for STUDENT_ID in {1..300}; do
    if [ $ONGOING_COUNT -ge $ONGOING_TARGET ]; then
        break
    fi

    RAND_SEED=$((SEED + STUDENT_ID + 5000))
    CHECK=$(( RAND_SEED % 10 ))

    if [ $CHECK -lt 2 ]; then
        BASE_DURATION=${STUDENT_VISIT_TIME[$STUDENT_ID]}

        # Max minutes ago based on visit_time
        MAX_MINUTES=$BASE_DURATION

        # Random start time within the window
        MINUTES_AGO=$(( (RAND_SEED * 11) % (MAX_MINUTES + 1) ))

        # Add some outliers (10% chance of being outside the normal range)
        OUTLIER=$(( (RAND_SEED * 7) % 10 ))
        if [ $OUTLIER -eq 0 ]; then
            MINUTES_AGO=$(( MINUTES_AGO + MAX_MINUTES ))
        fi
        
        START_TIME=$(( NOW_MS - (MINUTES_AGO * 60 * 1000) ))
        
        echo "INSERT INTO ongoing_visits (start_time, student_id) VALUES ($START_TIME, $STUDENT_ID);" >> "$SQL_FILE"
        ONGOING_COUNT=$((ONGOING_COUNT + 1))
    fi
done

# Commit transaction
echo "COMMIT;" >> "$SQL_FILE"

echo "Executing SQL (this should take less than a minute)..."

# Execute all SQL in one go
sqlite3 "$DB_FILE" < "$SQL_FILE"

# Clean up
rm "$SQL_FILE"

# Get final counts
STUDENT_COUNT=$(sqlite3 "$DB_FILE" "SELECT COUNT(*) FROM students;")
VISIT_COUNT=$(sqlite3 "$DB_FILE" "SELECT COUNT(*) FROM visits;")
ONGOING_COUNT=$(sqlite3 "$DB_FILE" "SELECT COUNT(*) FROM ongoing_visits;")

echo ""
echo "✓ Database created successfully: $DB_FILE"
echo "  - $STUDENT_COUNT students created"
echo "  - $VISIT_COUNT visits created"
echo "  - $ONGOING_COUNT ongoing visits created"