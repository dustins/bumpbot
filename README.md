# bumpbot

Bumpbot is an implementation of a turntable.fm bot using Java.

## Building

You'll need to have maven installed to build.

1. Build & Install the l3eta turntable java API by following the directions at https://github.com/dustins/Turntable

2. Go to the project directory.

    > \> cd /path/to/project

3. Create an `src/main/resources/auth-credentials.properties` properties file. See
   `src/main/resources/auth-credentials.properties-dist` for more information.

4. Build the project.

    > \> mvn clean install

5. Verify the project jars were created.

    > \> ls target bumpbot-*

    > bumpbot-{version}-sources.jar bumpbot-{version}.jar

## Current Functionality

### Commands

1. `/current` (CurrentCommand) displays information about currently playing song.
2. `/last` (LastCommand) displays information about the last song played.

### Listeners

1. GreetingListener welcomes users when they enter the room.
