# @tbvjaos510/react-native-paste-input

React Native `TextInput` component with the ability to paste images and files from the clipboard.

This is a fork of `@mattermost/react-native-paste-input` with support for React Native 0.76+ New Architecture (Fabric).

## Features

- Paste images from clipboard (context menu)
- Paste images/GIFs from keyboard (Gboard, etc.)
- Drag & drop images (Android 12+)
- Disable copy/paste functionality
- Full New Architecture (Fabric) support

## Installation

```sh
npm install @tbvjaos510/react-native-paste-input
# or
yarn add @tbvjaos510/react-native-paste-input
```

## Requirements

- React Native 0.76+
- New Architecture enabled

### Android Permissions

To paste images from the clipboard on Android 13+, your app needs the `READ_MEDIA_IMAGES` permission:

```xml
<!-- android/app/src/main/AndroidManifest.xml -->
<uses-permission android:name="android.permission.READ_MEDIA_IMAGES" />
```

For Android 12 and below, use:
```xml
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
```

**Note:** Without these permissions, pasting images copied from the gallery will fail with a SecurityException. Images from other sources (like web browsers) may still work.

## Usage

```tsx
import React, { useRef } from 'react';
import PasteInput, { PastedFile, PasteInputRef } from "@tbvjaos510/react-native-paste-input";

const YourTextInput = () => {
    const inputRef = useRef<PasteInputRef>(null);

    const onPaste = (
        error: string | null | undefined,
        files: Array<PastedFile>
    ) => {
        console.log('ERROR', error);
        console.log('PASTED FILES', files);
    };

    return (
        <PasteInput
            ref={inputRef}
            disableCopyPaste={false}
            onPaste={onPaste}
            multiline={true}
            blurOnSubmit={false}
            underlineColorAndroid="transparent"
            keyboardType="default"
        />
    );
}
```

## Properties

All properties of the [TextInput](https://reactnative.dev/docs/textinput) component plus:

### `disableCopyPaste: boolean`
Indicates if the menu items for *cut*, *copy*, *paste* and *share* should not be present in the context menu.

### `onPaste: (error, files) => void`
Callback that is called when pasting files into the text input.

**Note:** On Android, this callback is also called when selecting an image/GIF from the soft keyboard.

## Known Limitations

- **Samsung Keyboard Clipboard:** Images from Samsung keyboard's clipboard panel are not supported due to Samsung's proprietary implementation.
- **Android Media Permissions:** Pasting images copied from the gallery requires `READ_MEDIA_IMAGES` permission on Android 13+.

## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT
