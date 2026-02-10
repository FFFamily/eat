#!/usr/bin/env bash
set -euo pipefail

usage() {
  cat <<'USAGE'
Usage: deploy-jar.sh --module <module> --target <user@host:/remote/dir/> [options]

Options:
  --module <module>       Maven module directory to build/search (required unless --jar)
  --jar <path>            Explicit jar path to upload (overrides auto-detect)
  --target <dest>         SCP target, e.g. user@host:/opt/app/
  --port <port>           SSH port for scp
  --identity <path>       SSH private key path
  --skip-build            Skip mvn package step
  --mvn-args "..."        Extra args passed to mvn (default: -DskipTests)
  --dry-run               Print actions without uploading
  -h, --help              Show this help

Examples:
  scripts/deploy-jar.sh --module hs_api --target deploy@10.0.0.5:/opt/hs/
  scripts/deploy-jar.sh --jar hs_api/target/hs_api-1.0.0.jar --target user@host:/srv/app/
USAGE
}

root_dir="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"

module=""
jar_path=""
target=""
port=""
identity=""
skip_build="false"
extra_mvn_args="-DskipTests"
dry_run="false"

while (( "$#" )); do
  case "$1" in
    --module) module="$2"; shift 2;;
    --jar) jar_path="$2"; shift 2;;
    --target) target="$2"; shift 2;;
    --port) port="$2"; shift 2;;
    --identity) identity="$2"; shift 2;;
    --skip-build) skip_build="true"; shift;;
    --mvn-args) extra_mvn_args="$2"; shift 2;;
    --dry-run) dry_run="true"; shift;;
    -h|--help) usage; exit 0;;
    *) echo "Unknown arg: $1" >&2; usage; exit 1;;
  esac
 done

if [[ -z "$target" ]]; then
  echo "Missing --target" >&2
  usage
  exit 1
fi

if [[ -z "$jar_path" && -z "$module" ]]; then
  echo "Missing --module (or provide --jar)" >&2
  usage
  exit 1
fi

if [[ -n "$module" ]]; then
  module_path="$root_dir/$module"
  if [[ ! -d "$module_path" ]]; then
    echo "Module not found: $module_path" >&2
    exit 1
  fi
fi

if [[ "$skip_build" != "true" ]]; then
  if [[ -z "$module" ]]; then
    echo "--skip-build is false, but --module is missing" >&2
    exit 1
  fi
  echo "Building module '$module'..."
  (cd "$root_dir" && ./mvnw ${extra_mvn_args} package -pl "$module" -am)
fi

if [[ -z "$jar_path" ]]; then
  target_dir="$root_dir/$module/target"
  if [[ ! -d "$target_dir" ]]; then
    echo "Target dir not found: $target_dir" >&2
    exit 1
  fi

  shopt -s nullglob
  candidates=("$target_dir"/*.jar)
  shopt -u nullglob

  if [[ ${#candidates[@]} -eq 0 ]]; then
    echo "No jars found in $target_dir" >&2
    exit 1
  fi

  latest=""
  latest_mtime=0
  for f in "${candidates[@]}"; do
    case "$f" in
      *-sources.jar|*-javadoc.jar|*-tests.jar|*-test.jar|*original*.jar) continue;;
    esac
    if mtime=$(stat -f "%m" "$f" 2>/dev/null); then
      :
    else
      mtime=$(stat -c "%Y" "$f")
    fi
    if (( mtime > latest_mtime )); then
      latest_mtime=$mtime
      latest="$f"
    fi
  done

  if [[ -z "$latest" ]]; then
    echo "No deployable jar found (filtered out all candidates)" >&2
    exit 1
  fi
  jar_path="$latest"
fi

if [[ ! -f "$jar_path" ]]; then
  echo "Jar not found: $jar_path" >&2
  exit 1
fi

scp_args=()
if [[ -n "$port" ]]; then
  scp_args+=("-P" "$port")
fi
if [[ -n "$identity" ]]; then
  scp_args+=("-i" "$identity")
fi

if [[ "$dry_run" == "true" ]]; then
  echo "Dry run: scp ${scp_args[*]} '$jar_path' '$target'"
  exit 0
fi

echo "Uploading: $jar_path -> $target"
scp "${scp_args[@]}" "$jar_path" "$target"

echo "Done."
