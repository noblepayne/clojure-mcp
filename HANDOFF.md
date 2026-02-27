# Clojure MCP Nix Build Handoff

## Summary

Added Nix flake support for building clojure-mcp as a declarative binary.

## What Was Done

### 1. Nix Flake Setup
- Created `flake.nix` using `clj-nix` library
- Generates lock files via `nix run .#deps-lock`
- Builds uberjar via `nix build .`

### 2. Bug Fixes Required for Build

**Namespace/Path Mismatch (critical)**
- File: `resources/clojure-mcp/repl_helpers.clj`
- Issue: namespace declared as `clj-mcp.repl-tools` but file was in wrong directory
- Fix: Renamed to `resources/clojure_mcp/repl_tools.clj` with namespace `clojure-mcp.repl-tools`
- This was the blocker preventing clj-nix from compiling

**Missing gen-class (required by clj-nix)**
- File: `src/clojure_mcp/main.clj`
- Added `(:gen-class)` to ns declaration
- Added `-main` entry point for clj-nix

**Missing Jetty Deps (for SSE compilation)**
- File: `deps.edn`
- Added `jakarta.servlet/jakarta.servlet-api`, `jetty-server`, `jetty-servlet` to main deps
- Previously only in `:mcp-sse` alias, but clj-nix compiles all namespaces

## Implementation Details

### flake.nix
```nix
packages.default = clj-nix.lib.mkCljApp {
  projectSrc = ./.;
  name = "clojure-mcp/clojure-mcp";
  main-ns = "clojure-mcp.main";
}
```

### Running
```bash
nix build .              # builds binary to ./result
./result/bin/clojure-mcp  # runs MCP server (stdio)
```

## What Could Be Improved

### 1. Dev Shell
Add devenv for `nix develop` with REPL, test, and run scripts:
```nix
devShells.default = devenv.lib.mkShell {
  # packages, scripts for repl/test/run, etc.
}
```

### 2. SSE Variant
Create separate package for SSE transport:
```nix
packages.sse = clj-nix.lib.mkCljApp {
  main-ns = "clojure-mcp.sse-main";
  # needs :mcp-sse-port param handling
}
```

### 3. Submit Bug Fixes Upstream
- The `repl_helpers.clj` namespace/path issue is a legitimate bug
- Could be submitted as PR to fix the namespace and file location

---

## StreamableHTTP Implementation (Future Work)

### Overview
Replace SSE transport with `StreamableHttpServerTransportProvider` which supports SSE + Streamable-HTTP + Stateless modes.

### Files to Create/Modify

**Option A: New file (recommended)**
- Create `src/clojure_mcp/http_core.clj` - similar to `sse_core.clj`

**Option B: Modify existing**
- Modify `src/clojure_mcp/sse_core.clj`

### Implementation

```clojure
;; Import change
(import [io.modelcontextprotocol.server.transport
         HttpServletStreamableServerTransportProvider])  ; <- instead of HttpServletSseServerTransportProvider

;; Builder API (similar to SSE)
(defn mcp-http-server []
  (let [transport-provider (-> (HttpServletStreamableServerTransportProvider/builder)
                              (.messageEndpoint "/mcp/message")
                              (.build))
        server (-> (McpServer/async transport-provider)
                   (.serverInfo "clojure-mcp" "0.1.0")
                   (.capabilities (-> (McpSchema$ServerCapabilities/builder)
                                     (.tools true)
                                     (.prompts true)
                                     (.resources true true)
                                     (.build)))
                   (.build))]
    {:provider-servlet transport-provider
     :mcp-server server}))
```

### Differences from SSE
1. Import: `HttpServletStreamableServerTransportProvider` vs `HttpServletSseServerTransportProvider`
2. The servlet handles both SSE and streamable-http automatically
3. Same Jetty servlet hosting pattern

### Steps
1. Create `http_core.clj` (copy sse_core.clj structure)
2. Create `http_main.clj` entry point
3. Add to deps.edn aliases if needed
4. Add to flake.nix as separate package variant

### Testing
```bash
# SSE (existing)
clojure -X:mcp-sse

# StreamableHTTP (new)
# After implementation
clojure -X:mcp-http
```
<<<<<<< HEAD

### Test Considerations

**Current Test State:**
- No existing transport-specific tests
- Tests are unit-level for tools, config, agents
- No integration tests for server startup

**For StreamableHTTP, tests would need:**

1. **Transport initialization test** (`test/clojure_mcp/http_core_test.clj` - new)
   - Test that `HttpServletStreamableServerTransportProvider` initializes correctly
   - Test message endpoint path configuration
   - Test capabilities are set properly

2. **HTTP endpoints test** (new or add to existing)
   - Test `POST /initialize` creates session
   - Test `POST /messages` handles requests
   - Test `GET /sse/{session_id}` streams responses

3. **Session management test** (new)
   - Test session creation/cleanup
   - Test session timeout handling

4. **Integration test** (new - if desired)
   - Start HTTP server on test port
   - Make actual HTTP requests to verify protocol
   - Compare with SSE behavior

**Existing patterns to follow:**
- See `test/clojure_mcp/tools/eval/core_test.clj` for HTTP request patterns
- See `test/clojure_mcp/config/` for configuration testing
- Use test fixtures for port management

**Minimal test approach:**
1. Test that http_main/start works (just verifies no thrown exception)
2. Test HTTP client can connect to running server
3. Test JSON-RPC over HTTP works end-to-end
=======
>>>>>>> 89b3711 (Add HANDOFF.md with implementation notes and streamablehttp spec)
