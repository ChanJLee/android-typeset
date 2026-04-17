# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**Texas** is a high-performance Android text rendering library (texturing engine) supporting dynamic updates, a two-level Tag identification system, CJK text optimization, NLP-based tokenization, TeX-style full justification, punctuation compression, word hyphenation, and instruction-level rendering cache. The library is written in Java with a Kotlin build environment.

## Build Commands

```bash
# Build the library (produces AAR)
./gradlew :library:assemble

# Build the demo app
./gradlew :app:assembleDebug

# Run unit tests
./gradlew :library:test

# Run a single test class
./gradlew :library:test --tests me.chan.texas.misc.BitBucket8UnitTest

# Run instrumented tests (requires connected device/emulator)
./gradlew :library:connectedAndroidTest

# Run Android Lint
./gradlew :library:lint
```

## Module Structure

| Module | Purpose |
|--------|---------|
| `:library` | Core Texas engine (AAR), namespace `me.chan.texas`, minSdk 23 |
| `:app` | Demo application (`me.chan.texas.debug`) |
| `:ext-image` | Image extension (uses Glide) |
| `:ext-markdown` | Markdown parsing extension |
| `:ext-markdown-math` | Math formula support in Markdown |

## Architecture

Data flows through four layers:

```
Source → Parser → Typesetter → Renderer → TexasView
```

1. **Source** — `TexasView.DocumentSource` callback supplies raw data
2. **Parser** — converts source into a `Document` (list of `Segment`s)
3. **Typesetter** — `TexParagraphTypesetter` (TeX justification) or `SimpleParagraphTypesetter` lays out text into `Layout`
4. **Renderer** — draws `Layout` to `Canvas` using an instruction-level cache
5. **TexasView** — `FrameLayout` subclass that hosts the renderer

### Key Packages

| Package | Responsibility |
|---------|---------------|
| `me.chan.texas.text` | Data model: `Document`, `Segment`, `Paragraph`, `Span`, `TextAttribute` |
| `me.chan.texas.typesetter` | Layout engines (`TexParagraphTypesetter`, `SimpleParagraphTypesetter`) |
| `me.chan.texas.renderer` | `TexasView`, `Renderer`, `RenderOption`, `Selection`, `TouchEvent` |
| `me.chan.texas.measurer` | Text measurement utilities |
| `me.chan.texas.hyphenation` | Word hyphenation |
| `me.chan.texas.di` | Dagger 2 DI (`TexasComponent`, `TextEngineCoreComponent`) |
| `me.chan.texas.misc` | `ObjectPool`, `LruPool`, `BitBucket`, `Recyclable` |

### Segment Types

- `Paragraph` — text with spans; supports highlighting and justification
- `Figure` — image with jitter-reduction optimizations
- `ViewSegment` — wraps arbitrary Android `View`s; supports `addSelectionProvider()` to enroll embedded `ParagraphView`s into the free-selection system

### Tag System

Two-level tag system for precise content identification and interaction:

- **Paragraph tag** — set via `builder.tag("paragraph_id")`; identifies the whole segment
- **Span tag** — any `Object` attached to a `Span`; used for click/highlight predicates

Prefer immutable tag objects with `final` fields. Avoid storing large objects (bitmaps, lists) in tags—store IDs and fetch data from a repository on demand.

### Caching Strategy

Three-layer cache: layout cache → measure cache → instruction cache. The instruction cache delivers 300%+ scroll throughput. Cache invalidates only when content, text style, or layout width changes. Highlights, scroll position, and `redraw()` reuse the cache.

Batch incremental updates into a single `setSource()` call via `new Document.Builder(previousDocument).addSegment(...)` to avoid repeated cache rebuilds.

## Development Guidelines (from `doc/code/dev.md`)

- Naming: camelCase for all identifiers; resources use lowercase with underscores
- Logging: use the Texas internal logging tools, not `android.util.Log`
- Internal APIs: annotate with `@Hidden`
- Add tests for every new component; run tests before committing API changes
- Run lint regularly for static analysis

## Key Dependencies

- **Dagger** 2.38.1 — dependency injection
- **ICU4j** 75.1 — Unicode / international text support
- **JUnit** 4 + **Mockito** 3.5.7 — unit testing
- **Espresso** 3.5.1 — instrumented testing
- JVM target: Java 1.8; Kotlin 2.1.0; Gradle 8.6.1