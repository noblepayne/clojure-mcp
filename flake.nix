{
  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixpkgs-unstable";
    devenv.url = "github:cachix/devenv";
    clj-nix.url = "github:jlesquembre/clj-nix";
    clj-nix.inputs.nixpkgs.follows = "nixpkgs";
    treefmt-nix.url = "github:numtide/treefmt-nix";
    treefmt-nix.inputs.nixpkgs.follows = "nixpkgs";
  };
  outputs = {
    self,
    nixpkgs,
    devenv,
    clj-nix,
    treefmt-nix,
    ...
  } @ inputs: let
    supportedSystems = ["x86_64-linux" "aarch64-linux" "x86_64-darwin" "aarch64-darwin"];
    pkgsBySystem = nixpkgs.lib.getAttrs supportedSystems nixpkgs.legacyPackages;
    forAllPkgs = fn: nixpkgs.lib.mapAttrs (system: pkgs: (fn system pkgs)) pkgsBySystem;
    treefmtEval = forAllPkgs (
      system: pkgs:
        treefmt-nix.lib.evalModule pkgs {
          projectRootFile = "flake.nix";
          programs.alejandra.enable = true;
        }
    );
  in {
    formatter = forAllPkgs (system: pkgs: treefmtEval.${system}.config.build.wrapper);

    checks = forAllPkgs (system: pkgs: {
      formatting = treefmtEval.${system}.config.build.check self;
    });

    devShells = forAllPkgs (system: pkgs': let
      pkgs = import nixpkgs {
        inherit system;
        config.allowUnfree = true;
      };
    in {
      default = devenv.lib.mkShell {
        inherit inputs;
        pkgs = pkgs';
        modules = [
          (
            {
              config,
              pkgs,
              ...
            }: {
              packages = [
                pkgs.git
                pkgs.babashka
              ];

              dotenv.enable = true;

              scripts.format.exec = ''
                nix fmt .
              '';
              scripts.lock.exec = ''
                nix flake lock
                nix run .#deps-lock
              '';
              scripts.repl.exec = ''
                clojure -M:nrepl
              '';
              scripts.tests.exec = ''
                clojure -M:test
              '';
              scripts.run.exec = ''
                clojure -X:mcp
              '';
              scripts.run-sse.exec = ''
                clojure -X:mcp-sse
              '';

              enterShell = ''
                echo "Available scripts: scripts"
              '';
            }
          )
        ];
      };
    });

    packages = forAllPkgs (system: pkgs: {
      deps-lock = clj-nix.packages.${system}.deps-lock;
      default = clj-nix.lib.mkCljApp {
        inherit pkgs;
        modules = [
          {
            projectSrc = ./.;
            name = "clojure-mcp/clojure-mcp";
            main-ns = "clojure-mcp.main";
          }
        ];
      };
    });
  };
}
