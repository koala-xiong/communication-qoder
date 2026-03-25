#!/usr/bin/env bash
# 固定使用 MySQL profile，避免误用 H2 内存库导致重启后用户丢失。
# 用法：在项目根目录执行 ./scripts/start-backend-mysql.sh
set -euo pipefail
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT/communication-backend"
export SPRING_PROFILES_ACTIVE=mysql
exec ./mvnw spring-boot:run "$@"
