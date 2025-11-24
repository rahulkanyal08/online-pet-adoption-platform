#!/usr/bin/env pwsh
# Automated GitHub Push Script for Pet Adoption Platform
# This script automatically pushes your project to GitHub

Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "Pet Adoption Platform - GitHub Push" -ForegroundColor Cyan
Write-Host "========================================`n" -ForegroundColor Cyan

# Get GitHub username
$GITHUB_USERNAME = Read-Host "Enter your GitHub username"

if ([string]::IsNullOrEmpty($GITHUB_USERNAME)) {
    Write-Host "ERROR: GitHub username cannot be empty!" -ForegroundColor Red
    exit 1
}

# Get Personal Access Token
Write-Host ""
Write-Host "IMPORTANT: Go to https://github.com/settings/tokens" -ForegroundColor Yellow
Write-Host "Click 'Generate new token (classic)'" -ForegroundColor Yellow
Write-Host "Check 'repo' scope and click 'Generate token'" -ForegroundColor Yellow
Write-Host "Copy the token from GitHub (you won't see it again)" -ForegroundColor Yellow
Write-Host ""

$GITHUB_TOKEN = Read-Host "Paste your Personal Access Token" -AsSecureString
$GITHUB_TOKEN_PLAIN = [System.Runtime.InteropServices.Marshal]::PtrToStringAuto([System.Runtime.InteropServices.Marshal]::SecureStringToCoTaskMemUnicode($GITHUB_TOKEN))

if ([string]::IsNullOrEmpty($GITHUB_TOKEN_PLAIN)) {
    Write-Host "ERROR: Personal Access Token cannot be empty!" -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Green
Write-Host "Starting Automated Push..." -ForegroundColor Green
Write-Host "========================================`n" -ForegroundColor Green

# Navigate to project directory
Set-Location "d:\Java Pro"

if ($LASTEXITCODE -ne 0) {
    Write-Host "ERROR: Cannot navigate to project directory!" -ForegroundColor Red
    exit 1
}

# Step 1: Link repository to GitHub
Write-Host "[1/5] Linking repository to GitHub..." -ForegroundColor Cyan
$remoteUrl = "https://${GITHUB_USERNAME}:${GITHUB_TOKEN_PLAIN}@github.com/${GITHUB_USERNAME}/pet-adoption-platform.git"

try {
    git remote add origin $remoteUrl 2>$null
    if ($LASTEXITCODE -ne 0) {
        Write-Host "Remote already exists, updating..." -ForegroundColor Yellow
        git remote set-url origin $remoteUrl
    }
    Write-Host "✅ Repository linked" -ForegroundColor Green
} catch {
    Write-Host "❌ Failed to link repository" -ForegroundColor Red
    exit 1
}

# Step 2: Rename branch to main
Write-Host "[2/5] Renaming branch to 'main'..." -ForegroundColor Cyan
git branch -M main
Write-Host "✅ Branch renamed to main" -ForegroundColor Green

# Step 3: Check git status
Write-Host "[3/5] Checking git status..." -ForegroundColor Cyan
git status
Write-Host ""

# Step 4: Push code to GitHub
Write-Host "[4/5] Pushing code to GitHub..." -ForegroundColor Cyan
git push -u origin main

if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ Push failed!" -ForegroundColor Red
    Write-Host "Please check your credentials and try again." -ForegroundColor Red
    exit 1
}

Write-Host "✅ Code pushed successfully" -ForegroundColor Green

# Step 5: Verify push
Write-Host "[5/5] Verifying push..." -ForegroundColor Cyan
git log --oneline -n 3

Write-Host ""
Write-Host "========================================" -ForegroundColor Green
Write-Host "✅ SUCCESS! Project pushed to GitHub!" -ForegroundColor Green
Write-Host "========================================`n" -ForegroundColor Green

Write-Host "Your repository is now live at:" -ForegroundColor Cyan
Write-Host "https://github.com/${GITHUB_USERNAME}/pet-adoption-platform`n" -ForegroundColor Yellow

Write-Host "Next steps:" -ForegroundColor Cyan
Write-Host "1. Visit the repository URL above" -ForegroundColor White
Write-Host "2. Add a description (Settings → About)" -ForegroundColor White
Write-Host "3. Add topics (java, pet-adoption, web-application, etc.)" -ForegroundColor White
Write-Host "4. Check GitHub Actions (should show green checkmark)" -ForegroundColor White
Write-Host ""

Write-Host "You can now:" -ForegroundColor Cyan
Write-Host "- Share the link with others" -ForegroundColor White
Write-Host "- Make updates and push with: git push" -ForegroundColor White
Write-Host "- Use GitHub for version control" -ForegroundColor White
Write-Host ""

# Clean up sensitive data
$GITHUB_TOKEN_PLAIN = $null
