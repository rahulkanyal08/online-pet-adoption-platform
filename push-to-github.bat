@echo off
REM Automated GitHub Push Script for Pet Adoption Platform
REM This script automatically pushes your project to GitHub

setlocal enabledelayedexpansion

echo.
echo ========================================
echo Pet Adoption Platform - GitHub Push
echo ========================================
echo.

REM Get GitHub username
set /p GITHUB_USERNAME="Enter your GitHub username: "

REM Get Personal Access Token
echo.
echo IMPORTANT: Go to https://github.com/settings/tokens
echo Click 'Generate new token (classic)'
echo Check 'repo' scope and click 'Generate token'
echo Copy the token from GitHub (you won't see it again)
echo.
set /p GITHUB_TOKEN="Paste your Personal Access Token: "

REM Verify inputs
if "%GITHUB_USERNAME%"=="" (
    echo ERROR: GitHub username cannot be empty!
    pause
    exit /b 1
)

if "%GITHUB_TOKEN%"=="" (
    echo ERROR: Personal Access Token cannot be empty!
    pause
    exit /b 1
)

echo.
echo ========================================
echo Starting Automated Push...
echo ========================================
echo.

REM Navigate to project directory
cd /d "d:\Java Pro"

if errorlevel 1 (
    echo ERROR: Cannot navigate to project directory!
    pause
    exit /b 1
)

echo [1/5] Linking repository to GitHub...
git remote add origin https://%GITHUB_USERNAME%:%GITHUB_TOKEN%@github.com/%GITHUB_USERNAME%/pet-adoption-platform.git

if errorlevel 1 (
    echo ERROR: Failed to add remote. Repository might already be linked.
    echo Attempting to update existing remote...
    git remote set-url origin https://%GITHUB_USERNAME%:%GITHUB_TOKEN%@github.com/%GITHUB_USERNAME%/pet-adoption-platform.git
)

echo [2/5] Renaming branch to 'main'...
git branch -M main

if errorlevel 1 (
    echo Warning: Branch rename completed with status code !errorlevel!
)

echo [3/5] Checking git status...
git status

echo [4/5] Pushing code to GitHub...
git push -u origin main

if errorlevel 1 (
    echo ERROR: Push failed!
    echo Please check your credentials and try again.
    pause
    exit /b 1
)

echo [5/5] Verifying push...
git log --oneline -n 3

echo.
echo ========================================
echo SUCCESS! Project pushed to GitHub!
echo ========================================
echo.
echo Your repository is now live at:
echo https://github.com/%GITHUB_USERNAME%/pet-adoption-platform
echo.
echo Next steps:
echo 1. Visit the repository URL above
echo 2. Add a description (Settings - About)
echo 3. Add topics (java, pet-adoption, etc.)
echo 4. Check GitHub Actions (should show green checkmark)
echo.
echo You can now:
echo - Share the link with others
echo - Make updates and push with: git push
echo - Use GitHub for version control
echo.

pause
