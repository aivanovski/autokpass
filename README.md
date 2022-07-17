![https://github.com/aivanovski/autokpass/workflows/Build/badge.svg](https://github.com/aivanovski/autokpass/workflows/Build/badge.svg) ![https://github.com/aivanovski/autokpass/workflows/Run%20tests/badge.svg](https://github.com/aivanovski/autokpass/workflows/Run%20tests/badge.svg) ![Coverage](.github/badges/jacoco.svg)

# Autokpass
Autokpass is a small utility that providers functionality to paste username or/and password from your KeePass database into any aplication by simulating keyboard typing. </br>
For simulating keyboard typing Autokpass uses [xdotool](https://github.com/jordansissel/xdotool) on Linux and [cliclick](https://github.com/BlueM/cliclick) or AppleScript via [osascript](https://ss64.com/osx/osascript.html) on macOS. Access to KeePass database is provided by [Kotpass](https://github.com/Anvell/kotpass).

## Demo
![demo](https://github.com/aivanovski/autokpass/blob/main/screenshots/autokpass-demo.gif)

## Installation 
#### Linux
- Install Java version >= 11
- Install [xdotool](https://github.com/jordansissel/xdotool)
- Download `autokpass.jar` from [Release page](https://github.com/aivanovski/autokpass/releases). Alternatively you can build from sources

#### macOS
- Install Java version >= 11
- Install [cliclick](https://github.com/BlueM/cliclick)
- Download `autokpass.jar` from [Release page](https://github.com/aivanovski/autokpass/releases). Alternatively you can build from sources

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
- `-a, --autotype`: programm responsible for keyboard emulation; available options: `xdotool`, `cliclick`, `osascript`; default options: `xdotool` for Linux, `cliclick` for macOS;
- `-c, --process-key-command`: executes shell command on file specified in `--key-file` and uses it to unlock database
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
#### Unlock database with password
```
$ java -jar autokpass.jar \
  --file PATH_TO_KEEPASS_FILE
```

#### Unlock database with password but without a need to type it every time
Write your passwrd to a file and encrypt it, for example with `gpg`.
Then encrypted password can be decrypted by Autokpass with `--process-key-command` option
```
$ java -jar autokpass.jar \
  --file PATH_TO_KEEPASS_FILE \
  --key-file PATH_TO_ENCRYPTED_FILE_WITH_PASSWORD \
  --process-key-command 'gpg --decrypt ...'
```

#### Unlock database with key file
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
