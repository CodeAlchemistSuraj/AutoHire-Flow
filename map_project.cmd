@echo off
SET OUTPUT_FILE=project_structure.txt

echo Generating project structure for: %CD%
echo ======================================== > %OUTPUT_FILE%
echo PROJECT STRUCTURE: %DATE% %TIME% >> %OUTPUT_FILE%
echo ======================================== >> %OUTPUT_FILE%
echo. >> %OUTPUT_FILE%

:: The /f flag tells tree to include file names
:: The /a flag uses text characters instead of graphic lines (better for sharing/copy-pasting)
tree /f /a >> %OUTPUT_FILE%

echo.
echo Success! The map has been saved to %OUTPUT_FILE%
echo Opening the file now...
start notepad %OUTPUT_FILE%
pause