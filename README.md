# Bedrock [![Build Status](https://api.travis-ci.org/prism/Bedrock.png)](https://travis-ci.org/prism/Bedrock/)

Essential commands for Sponge-based Minecraft servers.

![Bedrock](http://helion3.s3.amazonaws.com/bedrock.png)

## Downloads

- [Builds](http://dhmc.us:8080/job/Bedrock/)

# Installation

Drop into your `mods` folder, set any permissions, and enjoy.

# Commands

- `/afk` - Toggle afk.
- `/back` - Teleport to your last death location.
- `/broadcast (message)` - Broadcast a message to all players.
- `/(clear|ci|clearinv|clearinventory) (player)` - Clear your or another player's inventory.
- `/delhome (name)` - Delete a home.
- `/delwarp (name)` - Delete a warp.
- `/feed (player)` - Feed a player or yourself.
- `/freeze (player` - Freeze a player where they are. A mobile jail.
- `/fly (player)` - Toggle fly for yourself or others.
- `/god` - Toggle invincibility.
- `/heal (player)` - Heal a player or yourself.
- `/home (name)` - Teleport to a home.
- `/homes` - List your homes.
- `/inv (player)` - Open player inventories.
- `/kick (player) [reason]` - Kick a player.
- `/kickall [reason]` - Kick all players.
- `/list` - List all online players.
- `/(m|message) (player) (message)` - Direct message another player.
- `/(perf|performance|gc)` - View performance stats.
- `/ping` - Verify server is responsive.
- `/reload` - Reload configuration files.
- `/(r|reply) (message)` - Reply to a direct message.
- `/sethome (name)` - Set a home.
- `/setspawn` - Set spawn for the current world.
- `/setwarp` - Create a new public warp.
- `/spawn` - Teleport to the current world's spawn.
- `/spy` - Toggle spying on direct messages.
- `/staff` - Lists all staff currently online.
- `/time (dawn|day|noon|dusk|nigh|midnight)` - Change the time.
- `/tp [player] (player)` - Teleport yourself/player to another player.
- `/tpa [player]` - Request teleport to another player.
- `/tpaccept` - Accept a request.
- `/tpahere [player]` - Request another player to teleport to you.
- `/tpdeny` - Deny a request.
- `/tphere [player]` - Teleport another player to yourself.
- `/tppos [x] [y] [z]` - Teleport to a location.
- `/tpworld [world]` - Teleport to another world.
- `/vanish` - Become invisible.
- `/weather (sun|sunny|clear|nice|rain|storm|thunder|thunderstorm)` - Set the weather.
- `/warp` - Teleport to a named location.
- `/warps` - List warps.
- `/whois (player)` - View whois information for a player.
- `/world difficulty` - Set difficulty for current world.

# Permissions

- `bedrock.afk.use` - Toggle yourself as afk.
- `bedrock.afk.exempt` - Exempted from AFK kicks.
- `bedrock.back` - Teleport to your last death location.
- `bedrock.broadcast` - Broadcast messages.
- `bedrock.clearinventory.others` - Clear another player's inventory.
- `bedrock.clearinventory.use` - Clear your own inventory.
- `bedrock.delhome` - Delete a warp.
- `bedrock.feed.use` - Feed self.
- `bedrock.feed.others` - Feed others.
- `bedrock.fly.use` - Toggle fly for yourself.
- `bedrock.fly.others` - Toggle fly for others.
- `bedrock.freeze.use` - Freeze players.
- `bedrock.freeze.exempt` - Player who may not be frozen.
- `bedrock.god` - Toggle invincibility.
- `bedrock.heal.use` - Heal self.
- `bedrock.heal.others` - Heal others.
- `bedrock.home` - Use homes. Limits are define in permissions options.
- `bedrock.inv.use` - Open other player's inventories.
- `bedrock.inv.exempt` - Exempt player from having inv opened.
- `bedrock.kick` - Kick players.
- `bedrock.message` - Direct message players.
- `bedrock.performance` - View performance stats.
- `bedrock.reload` - Reload configuration files.
- `bedrock.setspawn` - Set spawn.
- `bedrock.setwarp` - Set warps.
- `bedrock.spawn` - Set spawn.
- `bedrock.spy` - Toggle spying on direct messages.
- `bedrock.staff` - Show up as staff.
- `bedrock.time` - Change the time.
- `bedrock.tp` - Teleport yourself to another player.
- `bedrock.tpa` - Request teleports.
- `bedrock.tphere` - Teleport another player to yourself.
- `bedrock.tppos` - Teleport yourself to another player.
- `bedrock.tpworld` - Teleport to a world.
- `bedrock.vanish` - Become invisible.
- `bedrock.warp` - Teleport to a named location.
- `bedrock.weather` - Change the weather.
- `bedrock.whois` - View whois information for a player.
- `bedrock.world.difficulty` - Set difficulty for current world.

## IRC

Please follow development on `irc.esper.net` in `#prism`

## Credits

- viveleroi (*Creator, Lead Dev*)
- Aiden880 (*Contributor*)
- Dev Testing: Ollie2000, ickyacky
