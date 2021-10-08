# DiscordKM-Utils

DiscordKM-Utils provide some easy to use utilities like ButtonPaginators [![Maven Central](https://img.shields.io/maven-central/v/io.github.jan-tennert.discordkm/DiscordKM-Lavalink)](https://search.maven.org/artifact/io.github.jan-tennert.discordkm/DiscordKM-Utils)


# Example

```kotlin
val client = buildClient("token")

client.on<MessageCreateEvent> {
    if(message.content.startsWith("?paginator")) {
        message.channel.sendPaginator(maxPageSize = 5) {
            nextButton = nextButton.copy(label = "Next")
            
            onPageChange { page ->
                content = "hi $page"
            }
        }
    }
}

//or in interactions
client.on<SlashCommand> {
  interaction.replyPaginator(maxPageSize = 5, epheremal = true) {
      nextButton = nextButton.copy(label = "Next")
            
      onPageChange { page ->
           content = "hi $page"
      }
  }
}

client.login()
```

# Installation

You can just install DiscordKM-Utils using:

Kotlin Dsl:

```kotlin
implementation("io.github.jan-tennert.discordkm:DiscordKM-Utils:VERSION")
```

Maven:

```xml

<dependency>
    <groupId>io.github.jan-tennert.discordkm</groupId>
    <artifactId>DiscordKM-Utils</artifactId>
    <version>VERSION</version>
</dependency>
```

If you want a specific target add it to the artifactId like: DiscordKM-Utils-jvm and DiscordKM-Utils-js
