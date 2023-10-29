![https://github.com/aivanovski/autokpass/workflows/Build/badge.svg](https://github.com/aivanovski/autokpass/workflows/Build/badge.svg) ![Coverage](.github/badges/jacoco.svg)

# Autokpass
Autokpass is a small GUI utility that provides functionality to paste username or/and password from your KeePass database into any aplication by simulating keyboard typing. </br>
For simulating keyboard typing Autokpass uses [xdotool](https://github.com/jordansissel/xdotool) on Linux and [cliclick](https://github.com/BlueM/cliclick) or AppleScript via [osascript](https://ss64.com/osx/osascript.html) on macOS. Access to KeePass database is provided by [Kotpass](https://github.com/Anvell/kotpass).

## Demo
![demo](https://github.com/aivanovski/autokpass/blob/main/screenshots/autokpass-demo.gif)

## Installation 
#### Linux
- Install Java version >= 11
- Install [xdotool](https://github.com/jordansissel/xdotool)
- Download `autokpass.jar` for Linux from [Release page](https://github.com/aivanovski/autokpass/releases). Alternatively you can build from sources

#### macOS (Jar file installation)
- Install Java version >= 11
- Install [cliclick](https://github.com/BlueM/cliclick)
- Download `autokpass.jar` for macOS file from [Release page](https://github.com/aivanovski/autokpass/releases). Alternatively you can build from sources

#### macOS (Application installation)
Option to download singed application is not available now, but should be available in near future.
Apple doesn't allow to run unsigned application but it allows to run unsigned application that was created on the same machine. That means in order to install Autokpass as application, it should be packaged on the destination machine. For that please check out `Build application` section below.

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
- `-b, --autotype-delay`: delay in milliseconds between adjacent autotype actions
- `-k, --key-file`: path to key file
- `-a, --autotype`: programm responsible for keyboard emulation; available options: `xdotool`, `osascript`, `cliclick`; default options: `xdotool` for Linux, `osascript` for macOS;
- `-c, --process-key-command`: Shell command that can be executed on file specified in `--key-file` and uses it to unlock database
- `-h, --help`: print usage info

#### Configuration file
All options can be specified in a configuration file located at `$HOME/.config/autokpass/autokpass.cfg`
```
file=... # path to KeePass database file
delay=... # delay in seconds before autotype will be started
autotype-delay=... # delay in milliseconds between adjacent autotype actions
key-file=... # path to key file
autotype=... # programm responsible for keyboard emulation; available options: `xdotool`, `osascript`, `cliclick`; default options: `xdotool` for Linux, `osascript` for macOS
process-key-command=... # Shell command that can be executed on file specified in `--key-file` and uses it to unlock database
```

## Building from sources

#### Build `autokpass.jar` file (Linux or macOS)
```
git clone https://github.com/aivanovski/autokpass.git
cd autokpass
```
Then to build the project run:
```
./gradlew packageReleaseUberJarForCurrentOS
```
After build is finished `autokpass-[platform]-[version].jar` can be found at `autokpass/build/compose/jars`

#### Build application (for macOS only)
```
git clone https://github.com/aivanovski/autokpass.git
cd autokpass
```
Then to build the project run:
```
./gradlew packageDmg
```
After build is finished `autokpass-[version].dmg` can be found at `autokpass/build/compose/binaries/main/dmg/`

## Usage examples
#### Unlock database with password entry form
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

#### Run autokpass with configuration file
Create config file at `$HOME/.config/autokpass/autokpass.cfg`
```
file=PATH_TO_KEEPASS_FILE
```
Run Autokpass without arguments
```
$ java -jar autokpass.jar
```

#### Integration with I3WM
Add to i3 config file.
```
# Make Terminal window with Autokpass floating, resize it and position it
for_window [title="autokpass-autotype"] floating enable, resize set 2000 800, move position 920 1400

# Start new Terminal window with Autokpass by pressing MOD+SHIFT+A
bindsym $mod+Shift+a exec "$term --command 'java -jar autokpass.jar --file PATH_TO_KEEPASS_FILE --delay 1' --title autokpass-autotype"
```
