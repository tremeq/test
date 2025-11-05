# 📦 Resource Pack Setup Guide - ChatHead Integration

## 🎯 Overview

The ChatHead feature requires a **resource pack** to be installed on the client to render player heads in chat. This pack defines custom Unicode characters (\uF001-\uF008) that map to individual pixels.

**Without the resource pack, players will see garbled text instead of heads!**

## 📂 Resource Pack Location

The resource pack is included in this repository at:
- **Unpacked**: `chathead-resourcepack/` (for development)
- **Packed**: `chathead-resourcepack.zip` (3.1 KB, ready for distribution)

## 🚀 Distribution Methods

### Method 1: Server-Level Auto-Distribution (RECOMMENDED)

This method automatically sends the resource pack to all players when they join.

#### Step 1: Host the Resource Pack

You need to host `chathead-resourcepack.zip` somewhere accessible via HTTPS. Options:

##### Option A: GitHub Releases (FREE, RECOMMENDED)

1. Go to your repository on GitHub
2. Click "Releases" → "Create a new release"
3. Upload `chathead-resourcepack.zip`
4. Publish the release
5. Right-click the uploaded file → "Copy link address"
6. URL format: `https://github.com/YOUR_USERNAME/YOUR_REPO/releases/download/TAG/chathead-resourcepack.zip`

##### Option B: Own Web Server

1. Upload `chathead-resourcepack.zip` to your web server
2. Ensure it's accessible via HTTPS (required by Minecraft)
3. Get the direct download URL
4. Example: `https://yourserver.com/downloads/chathead-resourcepack.zip`

##### Option C: Dropbox

1. Upload to Dropbox
2. Get share link
3. Change `?dl=0` to `?dl=1` at the end
4. Example: `https://www.dropbox.com/s/abc123/chathead-resourcepack.zip?dl=1`

##### Option D: Google Drive

1. Upload to Google Drive
2. Right-click → "Get link" → "Anyone with the link"
3. Extract the FILE_ID from the link
4. Use format: `https://drive.google.com/uc?export=download&id=FILE_ID`

#### Step 2: Generate SHA1 Hash

The SHA1 hash verifies pack integrity. Generate it:

```bash
# On Linux/Mac
sha1sum chathead-resourcepack.zip

# On Windows (PowerShell)
Get-FileHash chathead-resourcepack.zip -Algorithm SHA1

# Example output:
# a1b2c3d4e5f6789012345678901234567890abcd  chathead-resourcepack.zip
```

#### Step 3: Configure server.properties

Add these lines to your `server.properties`:

```properties
# Resource Pack Settings
resource-pack=https://github.com/YOUR_USERNAME/YOUR_REPO/releases/download/v1.0/chathead-resourcepack.zip
resource-pack-sha1=YOUR_SHA1_HASH_HERE
resource-pack-prompt=§aSupremeChat ChatHead Pack§r\n§7Required for displaying player heads in chat
require-resource-pack=false
```

**Configuration Explained:**

| Option | Description | Recommended Value |
|--------|-------------|-------------------|
| `resource-pack` | Direct download URL (HTTPS only) | Your hosted URL |
| `resource-pack-sha1` | SHA1 hash for verification | Generated hash |
| `resource-pack-prompt` | Message shown to player | Custom message with formatting |
| `require-resource-pack` | Kick players who decline | `false` (optional feature) |

**Example with real values:**

```properties
resource-pack=https://github.com/yourname/supremechat/releases/download/v1.0/chathead-resourcepack.zip
resource-pack-sha1=a1b2c3d4e5f6789012345678901234567890abcd
resource-pack-prompt=§6§lSupremeChat§r §7ChatHead Pack\n§7Enables player head rendering in chat\n§e§lRecommended!
require-resource-pack=false
```

#### Step 4: Restart Server

```bash
# Stop the server gracefully
stop

# Start it again
java -jar spigot.jar
```

Players will now be prompted to download the pack when joining!

---

### Method 2: Manual Client Installation

For testing or single players:

#### For Players:

1. Download `chathead-resourcepack.zip`
2. Open Minecraft
3. Go to **Options** → **Resource Packs**
4. Click **Open Pack Folder**
5. Copy `chathead-resourcepack.zip` into this folder
6. Back in Minecraft, the pack should appear in "Available"
7. Click the arrow to move it to "Selected"
8. Click **Done**

---

### Method 3: Development Mode (For Developers)

When actively developing or testing:

1. Locate your Minecraft resourcepacks folder:
   - Windows: `%APPDATA%\.minecraft\resourcepacks\`
   - Mac: `~/Library/Application Support/minecraft/resourcepacks/`
   - Linux: `~/.minecraft/resourcepacks/`

2. Create a symbolic link to the unpacked folder:
   ```bash
   # Linux/Mac
   ln -s /path/to/project/chathead-resourcepack ~/.minecraft/resourcepacks/chathead-dev

   # Windows (as Administrator)
   mklink /D "%APPDATA%\.minecraft\resourcepacks\chathead-dev" "C:\path\to\project\chathead-resourcepack"
   ```

3. Changes to the pack are immediately reflected after `/reload` or F3+T

---

## 🔍 Verification

### Check Pack is Active

1. Join the server
2. Press **F3** (debug screen)
3. Look for resource pack information on the right side
4. Should show: "chathead-resourcepack.zip" or similar

### Test Head Rendering

1. Type in chat: `\uF001\uF002\uF003\uF004\uF005\uF006\uF007\uF008`
2. You should see colored pixels (if pack is active)
3. If you see weird Unicode chars, the pack isn't loaded

### Using SupremeChat Test

If you've implemented heads in chat formatting:
1. Send a normal chat message
2. Your head should appear before your name
3. Example: `[HEAD] PlayerName: Hello!`

---

## 🛠️ Troubleshooting

### Problem: Players see Unicode characters instead of heads

**Cause**: Resource pack not loaded

**Solutions**:
1. Check `resource-pack` URL is accessible (try opening in browser)
2. Verify URL uses HTTPS (HTTP won't work)
3. Ensure SHA1 hash is correct
4. Check player accepted the pack (look in chat when joining)
5. Try manual installation to verify pack works

### Problem: "Failed to download resource pack"

**Cause**: URL not accessible or incorrect

**Solutions**:
1. Test URL in browser - should download immediately
2. For Dropbox, ensure URL ends with `?dl=1` not `?dl=0`
3. For Google Drive, use the `uc?export=download&id=` format
4. Check firewall/security settings

### Problem: SHA1 verification failed

**Cause**: Hash mismatch (file changed or incorrect hash)

**Solutions**:
1. Regenerate SHA1 hash from the exact file you're hosting
2. Make sure you're hashing the `.zip` file, not the folder
3. Copy the ENTIRE hash (40 characters)
4. Hash is case-insensitive but use lowercase

### Problem: Pack loads but heads still don't show

**Cause**: Code not implemented in Formatting.java

**Solution**: Follow the `CHATHEAD_IMPLEMENTATION_EXAMPLE.md` guide to add heads to your chat format

### Problem: "Incompatible pack" error

**Cause**: `pack_format` doesn't match Minecraft version

**Solution**: Edit `pack.mcmeta` in the pack:
```json
{
  "pack": {
    "pack_format": 22,  // Adjust for your MC version
    "description": "ChatHead Unicode Font"
  }
}
```

**Pack format versions:**
- Minecraft 1.20.2-1.20.4: `pack_format: 22`
- Minecraft 1.20-1.20.1: `pack_format: 15`
- Minecraft 1.19.4: `pack_format: 13`
- Minecraft 1.19-1.19.3: `pack_format: 12`
- See: https://minecraft.wiki/w/Pack_format

---

## 📊 Pack Contents Explained

### File Structure

```
chathead-resourcepack/
├── pack.mcmeta                          # Pack metadata
└── assets/
    ├── chathead/
    │   ├── font/
    │   │   └── playerhead.json          # Unicode char definitions
    │   └── textures/
    │       ├── pixel1.png ... pixel8.png # 1x1 white pixels
    └── minecraft/
        └── font/
            └── default.json             # Links to playerhead.json
```

### How It Works

1. **Unicode Private Use Area**: Characters \uF001-\uF008 are "private use" (not standard Unicode)
2. **Bitmap Mapping**: Each char maps to a 1x1 white pixel texture
3. **Color Override**: Minecraft chat colors override the white pixel
4. **Spacing**: \uF101 (newline), \uF102 (negative space) control positioning
5. **8x8 Grid**: 8 columns × 8 rows = 64 colored pixels = player head

### playerhead.json Key Content

```json
{
  "providers": [
    {
      "type": "space",
      "advances": {
        "\uF101": -1,   // Newline (move down)
        "\uF102": -2    // Negative space (move left)
      }
    },
    {
      "type": "bitmap",
      "file": "chathead:pixel1.png",
      "ascent": 8,
      "height": 8,
      "chars": ["\uF001"]  // First column
    }
    // ... same for \uF002-\uF008
  ]
}
```

---

## 🎨 Customization

### Change Pack Description

Edit `pack.mcmeta`:
```json
{
  "pack": {
    "pack_format": 22,
    "description": "§6Your Custom Description Here"
  }
}
```

### Change Pixel Textures

Replace `pixel1.png` through `pixel8.png` with your own 1x1 images.
**Note**: Color will be overridden by chat color codes, so white/gray works best.

### Add Pack Icon

Add `pack.png` (64x64) to root of pack folder for a custom icon.

---

## 🔐 Security Notes

- Resource packs can only change textures, sounds, and text rendering
- They **cannot** execute code or access files
- Always host on HTTPS to prevent tampering
- SHA1 verification ensures integrity

---

## 📈 Performance Impact

- **Pack size**: 3.1 KB (negligible)
- **Client RAM**: < 1 MB additional
- **Download time**: < 1 second on most connections
- **Rendering performance**: No measurable impact

---

## 🔄 Updating the Pack

If you modify the pack:

1. Make changes to `chathead-resourcepack/` folder
2. Re-zip the contents:
   ```bash
   cd chathead-resourcepack
   zip -r ../chathead-resourcepack.zip .
   ```
3. Upload new version to hosting
4. **Regenerate SHA1 hash** (important!)
5. Update `server.properties` with new hash
6. Players will be prompted to re-download on next join

---

## 📞 Support

### Common Player Questions

**Q: "Is this resource pack safe?"**
A: Yes, it only contains font definitions and small 1x1 pixel textures. No executable code.

**Q: "Will it affect my other resource packs?"**
A: Generally no, but if another pack also modifies `minecraft:default` font, there may be conflicts. This pack should load last.

**Q: "Can I decline the pack?"**
A: Yes (if `require-resource-pack=false`), but you won't see player heads in chat.

**Q: "Can I use my own resource pack too?"**
A: Yes! Merge the contents of `chathead-resourcepack/assets/` into your pack, or load both packs.

---

## 🎯 Quick Start Checklist

- [ ] Upload `chathead-resourcepack.zip` to GitHub Releases or web host
- [ ] Generate SHA1 hash
- [ ] Add `resource-pack` and `resource-pack-sha1` to `server.properties`
- [ ] Restart server
- [ ] Join and accept the pack prompt
- [ ] Test by sending a chat message (if heads implemented)
- [ ] Verify heads display correctly

---

## 🔗 Additional Resources

- [Minecraft Wiki - Resource Pack](https://minecraft.wiki/w/Resource_pack)
- [Pack Format Versions](https://minecraft.wiki/w/Pack_format)
- [Font Format Documentation](https://minecraft.wiki/w/Resource_pack#Fonts)
- ChatHeadAPI Documentation: See `CHATHEAD_INTEGRATION_GUIDE.md`

---

**Created for SupremeChat** | ChatHead Offline Mode Integration
