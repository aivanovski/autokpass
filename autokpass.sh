#!/bin/sh

############## Input data

# Enter a password via standard input when script start
read -s -p "Enter a password: " password
echo ""

# Or setup password somehow, for example decrypt it from file with gpg
#password=$(gpg --decrypt ...)

# Setup path to database
db_path="$HOME/tmp/test.kdbx"

# Setup path to 'autokpass.jar' file, which will do the main job. 
jar_path="$HOME/dev/autokpass/build/libs/autokpass.0.1.0.jar"

############## Main logic

# Read all entries from database.
# All entries will be printed in format: "{UID} {TITLE}: {USERNAME} - {PASSWORD}"
entries=$(echo "$password" | java -jar $jar_path \
    --launch-mode print-all \
    --file-path $db_path \
    --password-at-std-in)

# Let user choose entry via 'fzf'
entry_uid=$(echo "$entries" | fzf --with-nth 2.. | cut -d' ' -f1)

# Exit if user doesn't select any entry
[ -z "$entry_uid" ] && exit 1

# Let user choose pattern that will be used in autotyping
pattern=$(echo -e "1 {USERNAME}{TAB}{PASSWORD}{ENTER}\n2 {USERNAME}{ENTER}\n3 {PASSWORD}{ENTER}\n4 {USERNAME}\n5 {PASSWORD}" | \
    fzf | cut -d' ' -f2)

# Exit if user doesn't select any pattern
[ -z "$pattern" ] && exit 1

# Notify user that autotype will start soon and sleep
echo "Autotype will start after 3 seconds delay..."
sleep 3

# Call 'autokpass.jar' to start autotyping
echo "$password" | java -jar $jar_path \
    --launch-mode autotype \
    --file-path $db_path \
    --uid $entry_uid \
    --pattern $pattern \
    --password-at-std-in
