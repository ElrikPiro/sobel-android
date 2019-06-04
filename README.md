SoberAndroidApp

The project per se would not compile as opencv is not included into the project.

to make it work the subsequent steps will be necesary:
- download OpenCV Android SDK
- import the sdk/ folder as a gradle project module and name it "opencv"
- create a symbolic link that points to opencv/ with the name sdk
