# Autokpass
Autokpass is a small utility that providers functionality to paste username or/and password from your KeePass database into any aplication by simulating keyboard typing. </br>
Autokpass uses [xdotool](https://github.com/jordansissel/xdotool) to simulate keyboard typing (that mean Autokpass will work only Linux with X.Org sever) and [KeePassJava2](https://github.com/jorabin/KeePassJava2) to read KeePass database

## Installation
- Install Java with version >= 11
- Download `autokpass.jar` from [Releases](https://github.com/aivanovski/autokpass/releases). Alternatively you can download latest build from [CI page](https://github.com/aivanovski/autokpass/actions)

## How to run
Open a Terminal and execute downloaded `autokpass.jar` with `java`.
```
java -jar autokpass.jar --file PATH_TO_KEEPASS_DATABASE.kdbx --delay 1
```
# Configuration
There are several options that could be specified:
```
--file - path to KeePass
--delay - delay in seconds before autotyping will be started
```
