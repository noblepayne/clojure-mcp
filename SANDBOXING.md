# Controlling Sandboxing in clojure-mcp

## Overview

clojure-mcp restricts file access to directories listed in `allowed-directories`. This is a security feature.

## How to Allow Broader Access

### Option 1: CLI argument (simplest)

Pass `:allowed-directories` directly on the command line:

```bash
clojure -X:mcp :allowed-directories ["/"]
```

### Option 2: Config File

Create a `.clojure-mcp/config.edn` in your project or pass a custom config file:

```bash
clojure -X:mcp :config-file '"/path/to/config.edn"'
```

Config file contents:

```edn
{:allowed-directories ["/"]}
```

### Option 3: Runtime with `ACT/add-dir`

The `ACT/add-dir` tool can add directories at runtime:

- Tool: `ACT/add-dir`
- Argument: `directory` - path to add (relative or absolute)

### Option 4: Override nREPL User Directory

Use `:not-cwd true` to avoid using the current working directory as the base:

```bash
clojure -X:mcp :config-file '"/path/to/config.edn"' :not-cwd true
```

## Nix/Deployments

In Nix or other deployment configs:

```nix
servers = {
  clojure = {
    cmd = "${pkgs.lib.getExe config.wespkgs.clojure-mcp} :allowed-directories [\"/\"]";
  };
};
```

Or with project directory:

```nix
servers = {
  clojure = {
    cmd = "${pkgs.lib.getExe config.wespkgs.clojure-mcp} :project-dir \"/home/user\" :allowed-directories [\"/\"]";
  };
};
```

## Key Files

- `src/clojure_mcp/utils/valid_paths.clj` - contains path validation logic
- `src/clojure_mcp/config.clj` - config processing (lines 128-151 show how allowed-directories are processed)

## Note

The code in `config.clj:141-145` always adds the nREPL's user-dir to the allowed directories list. Using CLI `:allowed-directories` merges with (rather than replaces) the config file values.
