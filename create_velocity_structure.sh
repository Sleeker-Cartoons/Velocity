#!/opt/homebrew/bin/bash
set -e

PKG="com.velocity"
SRC_DIR="app/src/main/java/${PKG//./\/}"

echo "Creating base path: $SRC_DIR"
mkdir -p "$SRC_DIR"

dirs=(
  "data/local"
  "data/model"
  "data/preferences"
  "data/repository"
  "domain/repository"
  "domain/usecase"
  "domain/service"
  "di"
  "ui/theme"
  "ui/home"
  "ui/tracking"
  "ui/history"
  "ui/detail"
  "ui/stats"
  "ui/settings"
  "ui/navigation"
  "service"
)

# Create directories
for d in "${dirs[@]}"; do
  mkdir -p "$SRC_DIR/$d"
done

# Simple file generator function
generate_file() {
  local file_path="$1"
  local pkg="$2"

  if [ ! -f "$file_path" ]; then
    cat > "$file_path" <<EOF
package $pkg

// TODO: Implement ${file_path##*/}
EOF
    echo "Created: $file_path"
  else
    echo "Exists: $file_path"
  fi
}

# File definitions
declare -a files=(
  "data/local:VelocityDatabase.kt RunDao.kt LocalDateTimeConverter.kt"
  "data/model:RunEntity.kt RunWithSplits.kt"
  "data/preferences:PreferencesManager.kt"
  "data/repository:RunRepositoryImpl.kt"
  "domain/repository:RunRepository.kt"
  "domain/usecase:TrackingUseCase.kt"
  "domain/service:LocationManager.kt"
  "di:DatabaseModule.kt DataModule.kt RepositoryModule.kt ServiceModule.kt"
  "ui/theme:Theme.kt"
  "ui/home:HomeScreen.kt HomeViewModel.kt"
  "ui/tracking:TrackingScreen.kt TrackingViewModel.kt"
  "ui/history:HistoryScreen.kt HistoryViewModel.kt"
  "ui/detail:RunDetailScreen.kt RunDetailViewModel.kt"
  "ui/stats:StatsScreen.kt StatsViewModel.kt"
  "ui/settings:SettingsScreen.kt SettingsViewModel.kt"
  "ui/navigation:NavigationBar.kt"
  "service:TrackingService.kt"
)

# Generate files
for entry in "${files[@]}"; do
  folder="${entry%%:*}"
  file_list="${entry#*:}"

  pkg_path="$PKG.$folder"
  for fname in $file_list; do
    path="$SRC_DIR/$folder/$fname"
    generate_file "$path" "$pkg_path"
  done
done

# Top-level
generate_file "$SRC_DIR/VelocityApp.kt" "$PKG"
generate_file "$SRC_DIR/MainActivity.kt" "$PKG"

echo "Done!"
