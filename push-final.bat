@echo off
setlocal enabledelayedexpansion

echo.
echo ========================================
echo Git Remote Setup
echo ========================================
echo.

cd /d "d:\Java Pro"

REM Remove existing remote
git remote rm origin 2>nul

REM Get token from user input
set /p TOKEN="Enter your GitHub Personal Access Token: "

REM Check if token is empty
if "%TOKEN%"=="" (
    echo Error: Token cannot be empty
    pause
    exit /b 1
)

REM Add remote with token
git remote add origin "https://rahulkanyal07:%TOKEN%@github.com/rahulkanyal07/pet-adoption-platform.git"

if errorlevel 1 (
    echo Error adding remote
    pause
    exit /b 1
)

echo.
echo Pushing to GitHub...
echo.

REM Push to GitHub
git push -u origin main

if errorlevel 1 (
    echo Push failed
    pause
    exit /b 1
)

echo.
echo ========================================
echo SUCCESS! Project pushed to GitHub!
echo ========================================
echo.
echo Repository: https://github.com/rahulkanyal07/pet-adoption-platform
echo.

pause
