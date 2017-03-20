# LittleTanks
This is a small game about, eh... tanks.

Written on holidays, at my spare time, in Java + Swing, just for fun.

All tools that I had in my notebook were Java 6 SDK, text editor and Terminal.
Therefore, source codes are a bit.. frightening. But I promise to improve it. =)

![Little Tanks Screenshot](https://lh3.googleusercontent.com/-kmn32RDGYBw/VW1mtFNHLnI/AAAAAAAABBw/ybVxFgG7hBo/s806/littleTanks4.png)

## Controls
Key | Action
--- | ---
`Arrows` | Select menu / move tank
`Space` | Fire!
`C` | Place bomb (if any)
`[` | Toggle sound
`]` | Toggle music
`\` | Next music track 
`P` | Pause game
`Escape` | Return to menu / Exit

## Levels
The game contains few test levels. They form so-called "level packs" (or "missions"). All files can be found in `/levels/` directory.

The first item in main game menu is level selector. You need to click the selector to change current "level pack".

Example of level pack names: "_level_", "_metal_", "_maze_" or "_test_".

Score table for any "level pack", can be found in `/scores/` directory. To clear a table - just remove corresponding file. 


## Sources building
You can easily build the game from sources by yourself.

Create an empty project in your favorite IDE, and download sources from this Git.

Attach JLayer library (http://www.javazoom.net/javalayer/javalayer.html),
аnd LuaJ SE library (for LittleTanks 1.5+) (http://www.luaj.org/luaj/3.0/README.html).

Set the root folder of the project as working directory, and then launch the game from `LittleTanks` class. 


## Links
[Tileset Map (rus)](http://pastebin.com/eW1PTzUB)


## Credits
**Programmer**: MoonlightOwl

**Beta Tester**: Polina

**Art**: Totoro

**Sounds**: http://freesound.org/
