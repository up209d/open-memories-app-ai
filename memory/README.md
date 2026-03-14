# Memory Directory - Claude Self-Instruction

This directory contains the project knowledge base. All files are committed to git
so Claude has full context on any machine without external dependencies.

## How to Use (for Claude)

On session start, read these files to understand the project:

1. **`../CLAUDE.md`** - Project rules, build config, critical constraints (ALWAYS loaded automatically)
2. **`MEMORY.md`** (this directory) - Project context, directory map, API summary, critical patterns
3. **`../Bible.md`** - Complete Sony native API reference with method signatures

Read these topic files on-demand when working on related areas:

4. **`sony-scalar-hardware.md`** - CameraEx, ParametersModifier, CameraSequence, DSP, DisplayManager, Light, SubLCD
5. **`sony-scalar-graphics.md`** - OptimizedImage, OptimizedImageFactory, ImageAnalyzer, JpegExporter, all ImageFilters, OptimizedImageView
6. **`sony-scalar-system.md`** - ScalarInput, ScalarProperties, AvindexStore, AvindexContentInfo, TimeUtil, all didep classes, Networking, Media, Broadcast Intents
7. **`app-architecture.md`** - Reference app architecture (base library, state machine, menu, caution), dual-device pattern, build config, project template checklist

## Other Project Resources

- **`../docs/index.html`** - HTML documentation website (searchable, dark theme)
- **`../tmp/OpenMemories-Framework/API_REFERENCE.md`** - Detailed stubs reference (1,540 lines, all method signatures from 323 Jasmin files)
- **`../reference-apps/`** - 7 decompiled Sony 1st-party apps (read-only, source of truth for API usage patterns)
- **`../open-memories-app/`** - Working template project (base for new apps)

## File Organization

All paths are relative to the project root (`sony-apps/`). No files depend on
machine-specific absolute paths. The external Claude auto-memory at
`~/.claude/projects/.../memory/MEMORY.md` contains only a redirect pointing here.
