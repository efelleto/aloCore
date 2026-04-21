# aloM0d

aloM0d is a client-side Minecraft Fabric mod written in Kotlin.

The project goal is to build a small utility-client style mod with a modular
architecture, while keeping dependencies to a minimum. The event bus and module
system live in this codebase instead of pulling in a client framework.

The display name is `aloM0d`, but the Fabric mod id is `alom0d`. Keep technical
ids, asset namespaces, translation keys, mixin config filenames, and resource
locations lowercase because Fabric and Minecraft resource identifiers do not
accept uppercase characters.

## Versions

- Minecraft: `26.1.2`
- Java target: `25`
- Fabric Loader: `0.19.2`
- Fabric API: `0.146.1+26.1.2`
- Fabric Loom: `1.16.1`
- Kotlin: `2.3.20`

Version numbers are configured in `gradle.properties`.

## Project Setup

Requirements:

- JDK 25 available locally.
- IntelliJ IDEA or another Kotlin-capable IDE.
- Internet access for the first Gradle dependency download.

Recommended setup:

```sh
cd aloM0d
./gradlew build
```

Import the project as a Gradle project from the `aloM0d` directory. If IDEA was
opened from a previous moved path, reload the Gradle project after opening this
directory so that Loom regenerates the local run configuration data.

Useful Gradle tasks:

```sh
./gradlew build
./gradlew clean build
./gradlew runClient
./gradlew runServer
./gradlew runDatagen
```

The built mod jar is written under:

```text
build/libs/
```

## Running From IDEA

After importing the Gradle project, use one of the Loom run configurations:

- `Minecraft Client`
- `Minecraft Server`
- `Data Generation`

If IDEA says that the module is not specified, reload the Gradle project. The
client run configuration should use the `aloM0d.client` module and the generated
launch config at:

```text
$PROJECT_DIR$/.gradle/loom-cache/launch.cfg
```

Do not commit `.idea/`, `.gradle/`, `build/`, `run/`, logs, or local env files.
Those paths are intentionally ignored.

## PrismLauncher Install

Install PrismLauncher from the official PrismLauncher website or your system
package manager. Create a Fabric instance that matches the Minecraft version in
`gradle.properties`.

The custom Gradle task `installPrismMod` builds the jar and copies it into a
PrismLauncher instance's `mods` folder:

```sh
./gradlew installPrismMod
```

By default, the task expects this instance path:

```text
~/.local/share/PrismLauncher/instances/26.1.2/minecraft/mods
```

That means the default setup works if your PrismLauncher instance is named
`26.1.2`.

To rename the PrismLauncher instance, right-click the instance in PrismLauncher,
choose the rename/edit option, and set the name to:

```text
26.1.2
```

Then make sure the instance has Fabric installed and that the `minecraft/mods`
folder exists. Run:

```sh
./gradlew installPrismMod
```

The task removes old `alom0d-*.jar` files from that folder before copying the
new jar.

## Custom PrismLauncher Path

If your instance has a different name or PrismLauncher stores instances in a
different location, customize the destination instead of renaming the instance.

Use a Gradle property:

```sh
./gradlew installPrismMod \
  -PprismModsDir="$HOME/.local/share/PrismLauncher/instances/My Instance/minecraft/mods"
```

Or use an environment variable:

```sh
export PRISM_MODS_DIR="$HOME/.local/share/PrismLauncher/instances/My Instance/minecraft/mods"
./gradlew installPrismMod
```

The destination must already exist. If the task fails with "Prism mods directory
does not exist", create the instance in PrismLauncher first or pass the correct
`minecraft/mods` folder path.

## Project Layout

Important paths:

```text
src/main/kotlin/kpp/dev/aloM0d
src/main/resources
src/client/kotlin/kpp/dev/aloM0d/client
src/client/resources
```

Core client systems:

```text
src/client/kotlin/kpp/dev/aloM0d/client/core/event
src/client/kotlin/kpp/dev/aloM0d/client/core/module
src/client/kotlin/kpp/dev/aloM0d/client/gui
```

Modules live in:

```text
src/client/kotlin/kpp/dev/aloM0d/client/core/module/modules
```

Resource namespaces use lowercase `alom0d`:

```text
src/main/resources/assets/alom0d
src/client/resources/assets/alom0d
```

## Adding A Module

Create a Kotlin object under:

```text
src/client/kotlin/kpp/dev/aloM0d/client/core/module/modules
```

Example:

```kotlin
package kpp.dev.aloM0d.client.core.module.modules

import kpp.dev.aloM0d.client.core.event.ClientTickEvent
import kpp.dev.aloM0d.client.core.module.Module
import kpp.dev.aloM0d.client.core.module.ModuleCategory

object ExampleModule : Module(
    id = "example",
    title = "Example",
    description = "Short description shown in the GUI tooltip.",
    category = ModuleCategory.MISC
) {
    override fun onEnable() {
        // Reset module state here.
    }

    override fun onDisable() {
        // Release state here.
    }

    override fun onTick(event: ClientTickEvent) {
        // Per-client-tick behavior while enabled.
    }
}
```

Register the module in `ModuleManager.init()`:

```kotlin
register(ExampleModule)
```

If you need a new category, add it to `ModuleCategory`.

## Module Guidelines

- Use a stable lowercase kebab-case `id`, for example `auto-tool`.
- Keep `title` short; it must fit in the click GUI panel.
- Keep `description` clear; it is used as hover tooltip text.
- Put behavior behind `enabled`. Work should run from `onTick` only when the
  module is enabled.
- Reset transient state in `onEnable` and clean up in `onDisable`.
- Do not perform heavy scans every tick. Cache state, rate-limit work, or split
  work across ticks.
- Keep modules focused. Shared behavior belongs under `core/` instead of being
  duplicated across modules.
- Prefer Fabric/Minecraft APIs over reflection or hardcoded internals.
- Avoid adding dependencies unless they solve a real problem that is too large
  for a small local helper.
- Use lowercase namespaces for translation keys, assets, mixins, render
  pipeline ids, and resource locations.
- Do not assume multiplayer permissions. Modules that can affect server-side
  state should degrade safely or check for fallback behavior.

## Best Practices

- Run `./gradlew clean build` before committing structural changes.
- Run `./gradlew runClient` after changing metadata, resources, mixins,
  rendering, keybinds, or entrypoints.
- Keep generated folders out of commits: `.gradle/`, `build/`, `.idea/`, `run/`,
  logs, and crash reports.
- Keep `fabric.mod.json` metadata valid after renames. The mod id must stay
  lowercase (`alom0d`), while the display name can stay `aloM0d`.
- Prefer small, explicit modules over broad modules that own unrelated behavior.
- Keep GUI text short and avoid adding instructional copy inside the in-game UI.
- When changing Minecraft, Fabric, Loom, Kotlin, or Java versions, update
  `gradle.properties`, test `clean build`, and then test `runClient`.

## Architecture References

Meteor Client is used as an architecture and module-implementation reference
when a practical implementation pattern is needed:

- https://github.com/MeteorDevelopment/meteor-client

This project is not affiliated with Meteor Development. Meteor Client is
licensed under GPL-3.0, and this project is licensed under GPL-3.0 to keep
compliance straightforward if implementation details are adapted from Meteor
Client in the future.

## License

This project is licensed under the GNU General Public License v3.0. See
`LICENSE`.
