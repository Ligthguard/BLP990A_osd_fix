# BLP990A_osd_fix
A simple android app for toggling the HVAC screen on a BLP-990A head unit in Volvo P1 (C30/S40/V50/C70) cars in case you have the wrong firmware installed or for some reason lost the OSD button.

There's an APK file you can use for testing, but note that it was created for Android 7.1 and might not work on later versions without some simple modifications.
The app was tested on a rooted Android 7.1.2 TW2-FD firmware.

Some background info:

The BLP-990A is a cheap chinese Topway head unit based on the Allwinner T8 soc somewhat modified by some ignorant swedish guys.
Correct me if i'm wrong, but they probably use the same hardware since the beginning, i mean we should be able to update the 7.1 version to 12, but either they modified the BROM code so that FEL mode does not work correctly, or i just couldn't find the appropriate memory freq.
At least in my case, the head unit is equipped with a Bluesoleil IVT i140 bluetooth module which wasn't supported by the TW2-FD software i had on it, so i had to find the appropriate gocsdk.

Bluetooth:

For this one, you'll need root access. You have to extract the contents of gocsdk.zip, then copy gocsdk binary to /system/bin directory, gocsdk6, gocsdk8 and config.ini to /system/etc/goc directory.