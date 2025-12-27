# RAAS - Robson Cassiano as a Service

A Spring Boot 3.5.9 application that implements a Model Context Protocol (MCP) server using Spring AI and Google AI Studio (Gemini) integration. This project is a personalized MCP server for Robson Cassiano.

## Features

- **MCP Server**: Implements Model Context Protocol server functionality
- **Spring AI Integration**: Uses Spring AI 1.1.0-M3 with Google AI Studio (Gemini) integration
- **.env Support**: Native support for `.env` files for easy configuration
- **YouTube Integration**: Provides MCP tools for YouTube channel operations and video management
- **Blog Integration**: Provides MCP tools for RSS feed parsing and blog post management
- **Speaking Integration**: Provides MCP tools for managing speaking engagements and events
- **Newsletter Integration**: Provides MCP tools for newsletter management via Beehiiv API
- **Podcast Integration**: Provides MCP tools for podcast management via YouTube Playlists
- **Configuration Validation**: Jakarta Bean Validation for robust configuration management
- **Java 25**: Utilizes the latest Java features (LTS version)

## Prerequisites

- Java 25
- Maven 3.6+
- Google AI Studio API Key (required)
- YouTube Data API Key (optional)
- YouTube Channel ID (optional)
- RSS Feed URL (optional - configured in `.env` or `application.properties`)
- Beehiiv API Key (optional)
- YouTube Podcast Playlist ID (optional)

## Setup

1. **Clone the repository**

   ```bash
   git clone <repository-url>
   cd raas
   ```

2. **Set up local configuration**
   Copy the example environment file and fill in your keys:

   ```bash
   cp .env.example .env
   ```

3. **Build the project**

   ```bash
   ./mvnw clean compile
   ```

4. **Run the application**

   ```bash
   ./mvnw spring-boot:run
   ```

## MCP Server Configuration

The application is configured as an MCP server with the following settings:

- **Server Name**: `raas-mcp-server`
- **Version**: `0.0.1`
- **Type**: `SYNC`
- **Protocol**: `streamable`

## Available MCP Tools

The application provides **21 MCP tools** organized by feature area:

### 🎥 YouTube Tools (4 tools)

Tools for YouTube channel operations and video management.

#### youtube-get-latest-videos

Get the most recent videos from Robson Cassiano's YouTube channel.

#### youtube-get-top-videos

Get the top-performing videos from Robson Cassiano's YouTube channel by view count.

#### youtube-search-videos-by-topic

Search for videos on Robson Cassiano's YouTube channel by topic or keyword.

#### youtube-get-channel-stats

Get overall statistics and information about Robson Cassiano's YouTube channel.

### 📝 Blog Tools (4 tools)

Tools for RSS feed parsing and blog post management.

#### blog-get-latest-posts

Get the most recent blog posts from Robson Cassiano's RSS feed.

#### blog-search-posts-by-keyword

Search blog posts by keyword in title and description.

#### blog-get-posts-by-date-range

Get blog posts within a specific date range or year.

#### blog-get-stats

Get comprehensive statistics about the blog including total posts, posting frequency, and trends.

### 🎤 Speaking Tools (4 tools)

Tools for managing speaking engagements and events.

#### speaking-get-latest-engagements

Get the most recent speaking engagements from Robson Cassiano's speaking schedule.

#### speaking-get-upcoming-events

Get upcoming speaking events from Robson Cassiano's speaking schedule.

#### speaking-search-by-topic

Search for speaking engagements by topic or keyword.

#### speaking-get-stats

Get overall statistics and information about Robson Cassiano's speaking engagements.

### 📰 Newsletter Tools (4 tools)

Tools for newsletter management via Beehiiv API.

#### newsletter-get-latest-posts

Get the most recent newsletter posts from Robson Cassiano's publications.

#### newsletter-search-posts-by-keyword

Search for newsletter posts by keyword in title, content, or authors.

#### newsletter-get-posts-by-status

Get newsletter posts filtered by status.

#### newsletter-get-publication-stats

Get statistics and information about Robson Cassiano's newsletter publications.

### 🎙️ Podcast Tools (5 tools)

Tools for podcast management via YouTube Playlists.

#### podcast-get-shows

Get all podcast shows (YouTube Playlists) hosted by Robson Cassiano.

#### podcast-get-latest-episodes

Get the most recent podcast episodes across all shows or filtered by show name/ID.

#### podcast-search-episodes

Search for podcast episodes by keyword in title or description.

#### podcast-get-episode-details

Get detailed information about a specific podcast episode by its ID.

#### podcast-get-stats

Get overall statistics and information about Robson Cassiano's podcasts.

## Technology Stack

- **Java 25** (LTS)
- **Spring Boot 3.5.9**
- **Spring Cloud 2025.0.1** (Northfields)
- **Spring AI 1.1.0-M3** with Google AI Studio (Gemini) integration
- **Spring AI MCP Server WebMVC**
- **Spring Dotenv** for `.env` file support
- **Google YouTube Data API v3**
- **Rome RSS Library v2.1.0**
- **Jakarta Bean Validation**
- **Maven** for build management

## Project Structure

```text
src/
├── main/java/software/robsoncassiano/raas/
│   ├── Application.java              # Main Spring Boot application class
│   ├── config/
│   │   ├── RaasConfiguration.java   # Main configuration class
│   │   ├── BlogProperties.java       # Blog configuration properties
│   │   ├── YouTubeProperties.java    # YouTube configuration properties
│   │   ├── SpeakingProperties.java   # Speaking configuration properties
│   │   ├── NewsletterProperties.java # Newsletter configuration properties
│   │   └── PodcastProperties.java    # Podcast configuration properties
│   └── tools/
│       ├── blog/
│       │   ├── BlogTools.java        # MCP tools for blog operations
│       │   ├── BlogService.java      # RSS feed service layer
│       │   └── model/                # Blog domain models
│       ├── youtube/
│       │   ├── YouTubeTools.java     # MCP tools for YouTube operations
│       │   ├── YouTubeService.java   # YouTube Data API service layer
│       │   └── model/                # YouTube domain models
│       ├── speaking/
│       │   ├── SpeakingTools.java    # MCP tools for speaking engagements
│       │   ├── SpeakingService.java  # Speaking data service layer
│       │   └── model/                # Speaking domain models
│       ├── newsletter/
│       │   ├── NewsletterTools.java  # MCP tools for newsletter operations
│       │   ├── NewsletterService.java # Beehiiv API service layer
│       │   └── model/                # Newsletter domain models
│       └── podcast/
│           ├── PodcastTools.java     # MCP tools for podcast operations
│           ├── PodcastService.java   # YouTube Playlist service layer
│           └── model/                # Podcast domain models
├── main/resources/
│   └── application.properties        # Application and MCP server configuration
└── test/java/software/robsoncassiano/raas/
    ├── ApplicationTests.java         # Basic application tests
    └── tools/                        # Feature-specific tests
```

## Configuration

The application uses **strongly-typed configuration properties** with validation.

### Core Configuration

Key configuration properties in `application.properties`:

```properties
spring.application.name=raas
spring.ai.mcp.server.name=raas-mcp-server
raas.blog.rss-url=${BLOG_RSS_URL:https://robsoncassiano.com/rss.xml}
```

### Environment Variables (.env)

Configure your server using a `.env` file:

```env
GOOGLE_AI_API_KEY=your_key
YOUTUBE_API_KEY=your_key
YOUTUBE_CHANNEL_ID=your_id
BEEHIIV_API_KEY=your_key
YOUTUBE_PODCAST_PLAYLIST_ID=your_playlist_id
```

### Feature-based Loading

Tools are conditionally loaded based on available configuration. Each category can be enabled/disabled by providing or omitting the respective API keys.
