# capacitor-firebase-upload-file
Capacitor Firebase Upload File use the Firebase Storage Native, With the plugin you can upload any type of formats.
> especially for MP4 for Videos and PNG/JPG for Images also compress the file.
> Android Platform use the siliCompressor for compress MP4 and PNG/JPG files; [More information:](https://github.com/Tourenathan-G5organisation/SiliCompressor)

NOTE: The plugin its supports only for Android also the compress its only available for Video format, not iOS and Web for now.

| Platform |   |
|----------|:-:|
| Android  |✅|
| iOS      |🧠|
| Web      |🧠|

✅ - Ready
🧠 - Thinking about, date to be defined


## How to install
Install the plugin into your Capacitor project with npm.

```
npm install --save capacitor-firebase-upload-file@0.0.1
```

## How to use on JavaAndroid

Import the class in the MainActivity.java:

```java:
import com.jdesigner.firebaseuploadfile.plugin.FirebaseUploadFile;
```

Add class inside the public MainActivity class:

```java:
public class MainActivity extends BridgeActivity {
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // Initializes the Bridge
    this.init(savedInstanceState, new ArrayList<Class<? extends Plugin>>() {{
      // Additional plugins you've installed go here
      // Ex: add(TotallyAwesomePlugin.class);
      add(FirebaseUploadFile.class);
    }});
  }
}
```

IMPORTANT NOTE: Make sure you added google-services.json in the android/app/... 

## How to use on JS

Import the capacitor plugin: 
```js:

import "capacitor-firebase-upload-file";
const { FirebaseUploadFile } = Plugins;
```

Use the Plugin with a simple Promise:
```js:
   FirebaseUploadFile.putStorageFile({ 
       fileLocalName: yourLocalFileName,
       fileNewStorageName: "newfile.mp4",
       fileNewStorageUrl: "yourpath/firebase/",
       fileCompress: true // only for Video formats
    }).then(res => {
        alert(JSON.stringify(res))
    }).catch(err => {
        alert(JSON.stringify(err))
    });
```

## Parameters
| Parameters | Details |
|----------|:-:|
| fileLocalName      |Name with which previously saved due                         |
| fileNewStorageName        |Name of the file to be saved to storage, example: myfile.mp4 |
| fileNewStorageUrl |Firebase Storage URL of the file to be saved to storage, example: /myfiles/ |
| fileCompress | only for Video formats like MP4, example: true or false |

## Response

| Parameters | Details  |
|----------|:-:|
| status      | return success or error  |
| response    | return the message       |
| downloadUrl | return the public url    |


