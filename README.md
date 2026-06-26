# AdvancedChatFilters

AdvancedChatFilters allows for complex manipulation of incoming chat messages.

> **Refurbished fork.** DarkKronicle archived the original project. This fork brings AdvancedChatFilters
> up to **Minecraft 26.2** and modernises the codebase: the whole mod was ported from Yarn to the new
> Mojang names and the 26.x APIs. It is a module of the refurbished
> [AdvancedChatCore](https://github.com/flyingfinger1/AdvancedChatCore).

## Requirements

| | Version |
| --- | --- |
| Minecraft | **26.2** |
| Java | **25** (required by Minecraft 26.x) |
| Fabric Loader | 0.19.0+ |

## Dependencies

The following are **required** for this mod to run:

- [AdvancedChatCore](https://github.com/flyingfinger1/AdvancedChatCore) **1.6.2+** (this fork's build)
- [MaLiLib](https://modrinth.com/mod/malilib) — for 26.x use the sakura-ryoko builds
- [Fabric API](https://modrinth.com/mod/fabric-api)

[Mod Menu](https://modrinth.com/mod/modmenu) is recommended to open the configuration screen.

## Features

- Replace an advanced search with specified text
- Change the color of the match
- Replace the full message
- Rainbow specific text
- Convert specific text to OwO
- Convert numbers into Roman Numerals
- Send a chat message to the Action Bar
- Narrate a message
- Play a sound when a match is found
- **Full JavaScript scripting for advanced filters**
- Many complex options for in complex matching and replacing

## Scripting

Scripting is a super powerful tool for text manipulation. This feature is disabled by default, due to the complexity of these filters, as well as the additional resources that they may take up.

While the scope of these scripts are extremely limited, and several measures are put in place to ensure security and stability, these scripts are still very powerful, and you should **never** run code that you don't trust

> **Note (26.2 / Java 25):** scripting uses Nashorn, which was removed from the JDK in Java 15+ and is
> not bundled. If the Nashorn engine can't be loaded, scripting simply stays disabled (the rest of the
> mod works normally); it never crashes the game.

To learn more about these filters, you can view the [example filters](https://github.com/DarkKronicle/AdvancedChatFilters/tree/main/example_filters) in this repository, or view [the wiki](https://github.com/DarkKronicle/AdvancedChatFilters/wiki)

## Building

The build needs a **JDK 25** toolchain (Minecraft 26.x). AdvancedChatFilters depends on the refurbished
AdvancedChatCore, which it resolves from your local Maven repository. Publish Core locally first:

```
# in the AdvancedChatCore clone
./gradlew publishToMavenLocal      # publishes io.github.darkkronicle:AdvancedChatCore:1.6.2

# then in AdvancedChatFilters
./gradlew build
```

To run the mod, install it together with AdvancedChatCore, MaLiLib and Fabric API.

## Development

To ensure code consistency the hook `pre-commit.sh` can be used. To install it run:

`ln -s ../../pre-commit.sh .git/hooks/pre-commit`

## Credits n' more

- Code & Mastermind: DarkKronicle
- Language & Proofreading: Chronos22
- 26.2 port & modernisation: community fork
