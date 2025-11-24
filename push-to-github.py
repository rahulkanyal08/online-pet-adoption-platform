#!/usr/bin/env python3
"""
Automated GitHub Push Script for Pet Adoption Platform
This script automatically pushes your project to GitHub
"""

import subprocess
import sys
import os
import getpass
from pathlib import Path

def run_command(cmd, description=""):
    """Run a shell command and handle errors"""
    try:
        if description:
            print(f"\n{description}")
        result = subprocess.run(cmd, shell=True, capture_output=True, text=True)
        if result.stdout:
            print(result.stdout)
        if result.returncode != 0 and result.stderr:
            print(f"Warning: {result.stderr}", file=sys.stderr)
        return result.returncode
    except Exception as e:
        print(f"Error running command: {e}", file=sys.stderr)
        return 1

def main():
    print("\n" + "="*40)
    print("Pet Adoption Platform - GitHub Push")
    print("="*40 + "\n")
    
    # Get GitHub username
    github_username = input("Enter your GitHub username: ").strip()
    if not github_username:
        print("ERROR: GitHub username cannot be empty!")
        sys.exit(1)
    
    # Get Personal Access Token
    print("\n" + "─"*40)
    print("IMPORTANT: Go to https://github.com/settings/tokens")
    print("Click 'Generate new token (classic)'")
    print("Check 'repo' scope and click 'Generate token'")
    print("Copy the token from GitHub (you won't see it again)")
    print("─"*40 + "\n")
    
    github_token = getpass.getpass("Paste your Personal Access Token: ")
    if not github_token:
        print("ERROR: Personal Access Token cannot be empty!")
        sys.exit(1)
    
    print("\n" + "="*40)
    print("Starting Automated Push...")
    print("="*40 + "\n")
    
    # Navigate to project directory
    project_dir = r"d:\Java Pro"
    if not os.path.exists(project_dir):
        print(f"ERROR: Project directory not found: {project_dir}")
        sys.exit(1)
    
    os.chdir(project_dir)
    print(f"📁 Working directory: {os.getcwd()}\n")
    
    # Step 1: Link repository
    print("[1/5] Linking repository to GitHub...")
    remote_url = f"https://{github_username}:{github_token}@github.com/{github_username}/pet-adoption-platform.git"
    
    result = run_command(f'git remote add origin "{remote_url}"', "")
    if result != 0:
        print("Remote already exists, updating...")
        run_command(f'git remote set-url origin "{remote_url}"')
    print("✅ Repository linked")
    
    # Step 2: Rename branch
    print("\n[2/5] Renaming branch to 'main'...")
    run_command("git branch -M main", "")
    print("✅ Branch renamed to main")
    
    # Step 3: Check status
    print("\n[3/5] Checking git status...")
    run_command("git status", "")
    
    # Step 4: Push code
    print("\n[4/5] Pushing code to GitHub...")
    result = run_command("git push -u origin main", "")
    
    if result != 0:
        print("\n❌ Push failed!")
        print("Please check your credentials and try again.")
        sys.exit(1)
    
    print("✅ Code pushed successfully")
    
    # Step 5: Verify
    print("\n[5/5] Verifying push...")
    run_command("git log --oneline -n 3", "")
    
    # Success message
    print("\n" + "="*40)
    print("✅ SUCCESS! Project pushed to GitHub!")
    print("="*40 + "\n")
    
    print("Your repository is now live at:")
    print(f"https://github.com/{github_username}/pet-adoption-platform\n")
    
    print("Next steps:")
    print("1. Visit the repository URL above")
    print("2. Add a description (Settings → About)")
    print("3. Add topics (java, pet-adoption, web-application, etc.)")
    print("4. Check GitHub Actions (should show green checkmark)\n")
    
    print("You can now:")
    print("- Share the link with others")
    print("- Make updates and push with: git push")
    print("- Use GitHub for version control\n")
    
    # Clean up sensitive data
    del github_token

if __name__ == "__main__":
    try:
        main()
    except KeyboardInterrupt:
        print("\n\nOperation cancelled by user.")
        sys.exit(0)
    except Exception as e:
        print(f"\nError: {e}", file=sys.stderr)
        sys.exit(1)
