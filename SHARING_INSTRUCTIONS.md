# How to Share Your Repository with Your Friend

## Option 1: Push to GitHub/GitLab/Bitbucket (Recommended)

1. **Push your main branch to remote:**
   ```bash
   git push origin main
   ```

2. **Share the repository URL** with your friend:
   - If using GitHub: `https://github.com/yourusername/portfolio`
   - If using GitLab: `https://gitlab.com/yourusername/portfolio`
   - If using Bitbucket: `https://bitbucket.org/yourusername/portfolio`

3. **Your friend can clone it:**
   ```bash
   git clone <repository-url>
   cd portfolio
   ```

## Option 2: Create a Zip File

1. **Create a zip of your project** (excluding node_modules and target folders):
   ```bash
   # On Windows, you can use File Explorer to zip the folder
   # Or use PowerShell:
   Compress-Archive -Path portfolio -DestinationPath portfolio.zip -Exclude "node_modules","target",".vite"
   ```

2. **Share the zip file** via:
   - Email
   - Google Drive
   - Dropbox
   - USB drive
   - Any file sharing service

3. **Your friend should:**
   - Extract the zip file
   - Follow the SETUP_GUIDE.md instructions

## Option 3: Share via Git Bundle

1. **Create a bundle:**
   ```bash
   git bundle create portfolio.bundle main
   ```

2. **Share the bundle file** with your friend

3. **Your friend can clone from bundle:**
   ```bash
   git clone portfolio.bundle portfolio
   cd portfolio
   ```

## Important Notes for Sharing

### What Your Friend Needs:

1. **The repository code** (via any method above)
2. **SETUP_GUIDE.md** - Detailed setup instructions (already included)
3. **README.md** - Project documentation (already included)

### What's Already Configured:

✅ MongoDB Atlas connection string (in `backend/src/main/resources/application.properties`)
✅ All dependencies and configurations
✅ Frontend and backend code

### What Your Friend Needs to Install:

- Java 17+ (JDK)
- Node.js and npm
- Git (if cloning from repository)

### Security Note:

⚠️ **Important**: The MongoDB connection string in `application.properties` contains your database credentials. If you're sharing publicly:
- Consider using environment variables instead
- Or create a separate database user for your friend
- Or ask them to use their own MongoDB Atlas cluster

## After Your Friend Clones/Pulls:

Tell them to:
1. Read `SETUP_GUIDE.md` for detailed instructions
2. Follow the setup steps
3. Run backend and frontend as described

## Quick Share Checklist

- [ ] All code is committed to main branch
- [ ] SETUP_GUIDE.md is included
- [ ] README.md is updated
- [ ] MongoDB connection is working
- [ ] Push to remote repository (if using Git)
- [ ] Share repository URL or zip file
- [ ] Tell your friend to read SETUP_GUIDE.md

