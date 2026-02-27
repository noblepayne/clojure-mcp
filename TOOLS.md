# ClojureMCP Tools Reference Guide

Quick reference for what each tool does and when to use it.

---

## 📖 Reading Files

### `read_file`
Smart file reader with Clojure-specific features.
- Shows function signatures for large files (collapsed view)
- Pattern matching to find functions by name
- Handles `defmethod` dispatch values
- Use for: Reading Clojure source files, understanding code structure

### `grep`
Fast regex content search.
- Finds files containing specific patterns
- Use for: Finding function usages, searching code patterns

### `glob_files`
Find files by name pattern.
- `*.clj`, `*.bb`, `*.edn`, etc.
- Use for: Discovering files, finding all test files, etc.

### `LS`
Recursive tree view of directory structure.
- Use for: Exploring project layout, understanding file organization

---

## 🔍 Project Analysis

### `clojure_inspect_project`
Analyzes Clojure project structure.
- Dependencies from deps.edn/project.clj
- Source/test paths
- Available namespaces
- Use for: Understanding a new project, finding dependencies

### `deps_read`
Reads dependency information from deps.edn files.
- Use for: Inspecting dependency details

### `deps_list`
Lists all dependencies for the project.
- Use for: Getting dependency overview

### `deps_grep`
Searches within dependency sources (JAR files).
- Use for: Finding definitions in library code

### `list_nrepl_ports`
Discovers running nREPL servers on the machine.
- Use for: Finding available REPLs to connect to

---

## ⚡ Code Evaluation

### `clojure_eval`
Evaluates Clojure code in connected nREPL.
- Executes in current namespace
- Supports multiple expressions
- Built-in REPL helpers: `list-ns`, `list-vars`, `doc-symbol`, `source-symbol`, `find-symbols`, `complete`
- Use for: Testing code, running REPL commands, debugging

### `bash`
Execute shell commands.
- Run tests, git, build commands
- Path validation against allowed-directories
- Use for: Running tests (`clojure -M:test`), git operations, build scripts

---

## ✏️ File Editing

### `file_write`
Write complete files.
- Safety checks and validation
- Creates new files or overwrites
- Use for: Creating new files, writing complete files

### `file_edit`
Find-and-replace text in files.
- String-based replacement
- Includes parinfer repair after edits
- Use for: Simple text replacements, quick edits

### `clojure_edit` ⭐
**Best for Clojure files.** Structural form editing.
- Edits by form type: `defn`, `def`, `defmethod`, `ns`, `deftest`, etc.
- Operations: `replace`, `insert_before`, `insert_after`
- Handles qualified names and dispatch values
- Use for: Any Clojure code changes - defs, functions, namespaces

### `clojure_edit_replace_sexp`
Modify expressions within functions.
- Structural editing of s-expressions
- Use for: Changing specific expressions inside functions

### `paren_repair`
Repairs unbalanced parentheses using parinfer.
- Use for: Fixing parens after manual edits

---

## 🤖 Agent Tools (Require API Keys)

### `dispatch_agent`
Launch read-only agents for complex multi-step searches.
- Uses: LS, read_file, grep, glob_files, clojure_inspect_project
- Use for: Complex exploration tasks, deep research

### `architect`
Technical planning and architecture guidance.
- Use for: System design, architecture decisions

### `code_critique`
Interactive code review.
- Suggests improvements
- Use for: Code quality review

### `clojure_edit_agent`
Specialized agent for Clojure structural editing.
- Use for: Complex Clojure refactoring tasks

---

## 📝 Utilities

### `scratch_pad`
Persistent workspace for structured data storage.
- Task tracking, planning
- Inter-tool communication
- Optional file persistence
- Use for: Tracking tasks, storing intermediate results

---

## Quick Decision Guide

**Need to read code?**
- Understanding a file → `read_file`
- Find where something is used → `grep`
- Find files by name → `glob_files`
- Explore project structure → `LS` or `clojure_inspect_project`

**Need to run code?**
- Test something in REPL → `clojure_eval`
- Run tests/shell commands → `bash`

**Need to write/edit?**
- New file → `file_write`
- Quick text replace → `file_edit`
- Clojure code → `clojure_edit` (preferred)
- Fix parens → `paren_repair`

**Complex tasks?**
- Multi-step exploration → `dispatch_agent`
- Architecture planning → `architect`
- Code review → `code_critique`
