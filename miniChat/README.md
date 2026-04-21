<div align="center">

# 💬 miniChat

**A lightweight chat formatting plugin for Paper Minecraft servers.**  
Color your name, use gradients, throw in some emojis — keep it simple, keep it clean.

![Kotlin](https://img.shields.io/badge/Kotlin-2.4-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)
![Paper](https://img.shields.io/badge/Paper-1.21%2B-F96854?style=for-the-badge&logo=data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHZpZXdCb3g9IjAgMCAyNCAyNCI+PHBhdGggZmlsbD0id2hpdGUiIGQ9Ik0xOSAzSDVjLTEuMSAwLTIgLjktMiAydjE0YzAgMS4xLjkgMiAyIDJoMTRjMS4xIDAgMi0uOSAyLTJWNWMwLTEuMS0uOS0yLTItMnptLTUgMTRIN3YtMmg3djJ6bTMtNEg3di0yaDEwdjJ6bTAtNEg3VjdoMTB2MnoiLz48L3N2Zz4=&logoColor=white)
![Minecraft](https://img.shields.io/badge/Minecraft-1.21%2B-62B47A?style=for-the-badge&logo=minecraft&logoColor=white)
![License](https://img.shields.io/badge/License-MIT-blue?style=for-the-badge)

</div>

---

## ✨ Features

- **Custom name colors** — solid colors using legacy codes (`&a`, `&4`) or HEX (`#FF5733`)
- **Gradient names** — smooth two-color gradients using MiniMessage
- **Emoji shortcuts** — type `:fire:` and get 🔥 in the right color, automatically
- **Legacy color support in messages** — players can use `&a`, `&2&l`, etc. in chat
- **Zero external dependencies** — built entirely on the Paper API and Adventure (already bundled)
- **Per-player persistence** — colors saved to `players.yml`, survive server restarts
- **Fully async** — hooks into `AsyncChatEvent` for safe, non-blocking chat processing

---

## 🖼️ Preview

```
[Steve]  »  hello world
[Alex]   »  &aThis message is green
[Notch]  »  :fire: let's gooo :gg:
```

Name colors and gradients render directly in-game using the Adventure API — no legacy formatting strings, pure components.

---

## 📋 Requirements

| Requirement | Version |
|-------------|---------|
| Java | 21+ |
| Paper | 1.21+ |
| Minecraft | 1.21+ (including latest releases) |

> **Note:** This plugin uses Paper-exclusive APIs (`AsyncChatEvent`, `ChatRenderer`, native Adventure). It will **not** work on Spigot or CraftBukkit.

---

## 🚀 Installation

1. Download the latest `.jar` from [Releases](../../releases)
2. Drop it into your server's `plugins/` folder
3. Restart the server
4. Done — no configuration needed to get started

---

## 🎨 Commands

### `/cor` — Set your name color

| Usage | Description |
|-------|-------------|
| `/cor` | Shows help and format information |
| `/cor lista` | Displays the full 16-color Minecraft palette |
| `/cor &a` | Set name to a solid legacy color |
| `/cor #FF5733` | Set name to a solid HEX color |
| `/cor #9B59B6 #3498DB` | Set name to a gradient between two HEX colors |

**Examples:**

```
/cor &6          → golden name
/cor #FF5733     → custom orange name
/cor #9B59B6 #3498DB  → purple-to-blue gradient
```

When you set a color, the plugin previews exactly how your name will look in chat.

---

### `/minichat emojis` — View available emoji shortcuts

Displays the full emoji list with shortcut codes and their colors.

---

## 😄 Emoji Shortcuts

Type these anywhere in your message and they'll be automatically replaced:

| Name | Shortcut | Emoji | Color |
|------|----------|-------|-------|
| Fogo | `:fire:` | 🔥 | Gold |
| Coração | `:heart:` | ❤ | Red |
| Olhos | `:eyes:` | 👀 | White |
| Estrela | `:star:` | ⭐ | Yellow |
| Check | `:check:` | ✅ | Green |
| X | `:x:` | ❌ | Red |
| Sorriso | `:smile:` | 😊 | Yellow |
| Choro | `:cry:` | 😢 | Blue |
| Caveira | `:skull:` | 💀 | Gray |
| 100 | `:100:` | 💯 | Red |
| Joinha | `:thumbs:` | 👍 | Green |
| Festa | `:gg:` | 🎉 | Gold |

**Shorthand aliases also work:**

| Shortcut | Result |
|----------|--------|
| `<3` or `S2` | ❤ |
| `:)` | 😊 |
| `:(` | 😢 |

---

## 🗂️ Project Structure

```
src/main/kotlin/com/github/efelleto/miniChat/
├── MiniChat.kt                     ← Plugin entrypoint
├── listener/
│   └── ChatListener.kt             ← AsyncChatEvent + ChatRenderer
├── command/
│   ├── CorCommand.kt               ← /cor logic
│   └── MiniChatCommand.kt          ← /minichat logic
├── processor/
│   ├── MessageProcessor.kt         ← Processes legacy & codes in messages
│   └── EmojiProcessor.kt           ← Replaces emoji shortcuts
└── storage/
    └── PlayerDataStore.kt          ← Reads/writes players.yml

src/main/resources/
├── plugin.yml
└── players.yml
```

---

## 💾 Data Storage

Player colors are stored in `plugins/miniChat/players.yml`:

```yaml
"550e8400-e29b-41d4-a716-446655440000":
  type: "solid_legacy"
  color: "&a"

"6ba7b810-9dad-11d1-80b4-00c04fd430c8":
  type: "solid_hex"
  color: "#FF5733"

"6ba7b811-9dad-11d1-80b4-00c04fd430c8":
  type: "gradient"
  color1: "#9B59B6"
  color2: "#3498DB"
```

---

## 🔧 Building from Source

```bash
git clone https://github.com/efelleto/miniChat.git
cd miniChat
./gradlew shadowJar
```

Output jar will be at `build/libs/miniChat-1.0-all.jar`.

---

## 🛠️ Tech Stack

- **[Kotlin](https://kotlinlang.org/)** — primary language
- **[Paper API](https://papermc.io/)** — server platform
- **[Adventure](https://docs.advntr.net/)** — native component-based text formatting (bundled with Paper)
- **[MiniMessage](https://docs.advntr.net/minimessage/)** — gradient name generation
- **[Gradle Shadow](https://gradleup.com/shadow/)** — fat jar packaging

---

## 📄 License

This project is licensed under the [MIT License](LICENSE).

---

<div align="center">
Made with ❤️ by <a href="https://github.com/efelleto">efelleto</a>
</div>
