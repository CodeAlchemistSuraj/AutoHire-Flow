# 1. Define the list of files (Paste your paths between the @' and '@)
$fileList = @'
src/main/java/com/autohire/flow/domain/model/Resume.java
src/main/java/com/autohire/flow/domain/model/User.java
src/main/java/com/autohire/flow/infrastructure/persistence/entity/ResumeEntity.java
src/main/java/com/autohire/flow/infrastructure/persistence/entity/UserEntity.java
'@ -split "`n" | ForEach-Object { $_.Trim() } | Where-Object { $_ -ne "" }

# 2. Define the output file name
$outputFile = "Combined_Source_Code.txt"

# Clear the output file if it already exists
Clear-Content $outputFile -ErrorAction SilentlyContinue

Write-Host "Starting aggregation into $outputFile..." -ForegroundColor Cyan

foreach ($filePath in $fileList) {
    if (Test-Path $filePath) {
        # Get the simple file name for the header
        $fileName = Split-Path $filePath -Leaf
        
        # Add a visual separator and the file name
        Add-Content -Path $outputFile -Value "`n"
        Add-Content -Path $outputFile -Value "================================================================================"
        Add-Content -Path $outputFile -Value "FILE: $filePath"
        Add-Content -Path $outputFile -Value "================================================================================"
        Add-Content -Path $outputFile -Value "`n"
        
        # Append the actual file content
        Get-Content -Path $filePath | Add-Content -Path $outputFile
        
        Write-Host "Successfully added: $fileName" -ForegroundColor Green
    }
    else {
        Write-Host "Warning: File not found at $filePath" -ForegroundColor Yellow
    }
}

Write-Host "`nDone! All available content saved to $outputFile" -ForegroundColor White -BackgroundColor DarkGreen