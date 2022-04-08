![https://github.com/aivanovski/autokpass/workflows/Run%20tests/badge.svg](https://github.com/aivanovski/autokpass/workflows/Run%20tests/badge.svg)

# Autokpass
Autokpass is a small utility that providers functionality to paste username or/and password from your KeePass database into any aplication by simulating keyboard typing. </br>
Autokpass uses [xdotool](https://github.com/jordansissel/xdotool) to simulate keyboard typing (that mean Autokpass will work only on Linux with X.Org sever) and [KeePassJava2](https://github.com/jorabin/KeePassJava2) to read KeePass database

## Demo
![demo](https://github.com/aivanovski/autokpass/blob/main/screenshots/autokpass-demo.gif)

## Installation
- Install Java version >= 11
- Download `autokpass.jar` from [Release page](https://github.com/aivanovski/autokpass/releases). Alternatively you can download latest build from [CI page](https://github.com/aivanovski/autokpass/actions)

## How to run
Open a Terminal and execute downloaded `autokpass.jar` with `java`.
```
$ java -jar autokpass.jar --file PATH_TO_KEEPASS_DATABASE.kdbx --delay 1
```

## Usage
```
$ java -jar autokpass.jar [OPTIONS]
```

#### Options
- `-f, --file`: path to KeePass database file
- `-d, --delay`: delay in seconds before autotype will be started
- `-k, --key-file`: path to key file
- `-x, --xml-key`: interpret key file as xml file
- `-h, --help`: print usage info

## Building from sources
```
git clone https://github.com/aivanovski/autokpass.git
cd autokpass
```
Then to build the project run:
```
./gradlew bootJar
```
After build is finished `autokpass.jar` can be found at `autokpass/build/libs`

## Usage examples
#### Password database unlock
```
$ java -jar autokpass.jar \
  --file PATH_TO_KEEPASS_FILE
```

#### Key file database unlock
```
$ java -jar autokpass.jar \
  --file PATH_TO_KEEPASS_FILE \
  --key-file PATH_TO_KEY_FILE
```

#### Run Autokpass in its own window
Command to start Autokpass can be specified as an argument to almost any modern terminal emulator (such as Terminator, Alacritty or other)
```
$ YOUR_TERMINAL_EMULATOR --command 'java -jar autokpass.jar --file PATH_TO_KEEPASS_FILE --delay 1'
```

#### Integration with I3WM
Add to i3 config file.
```
# Make Terminal window with Autokpass floating, resize it and position it
for_window [title="autokpass-autotype"] floating enable, resize set 2000 800, move position 920 1400

# Start new Terminal window with Autokpass by pressing MOD+SHIFT+A
bindsym $mod+Shift+a exec "$term --command 'java -jar autokpass.jar --file PATH_TO_KEEPASS_FILE --delay 1' --title autokpass-autotype"
```
