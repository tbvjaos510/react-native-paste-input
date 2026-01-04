import { StatusBar } from 'expo-status-bar';
import { useState, useRef } from 'react';
import {
    StyleSheet,
    Text,
    View,
    Button,
    TextInput,
    SafeAreaView,
    ScrollView,
} from 'react-native';
import PasteInput, {
    type PastedFile,
    type PasteInputRef,
} from '@tbvjaos510/react-native-paste-input';

export default function App() {
    const inputRef = useRef<PasteInputRef>(null);
    const [controlledText, setControlledText] = useState('');
    const [pastedFile, setPastedFile] = useState<PastedFile | null>(null);

    const onPaste = (
        error: string | null | undefined,
        files: Array<PastedFile>
    ) => {
        console.log('ERROR', error);
        console.log('PASTED FILES', files);
        if (!error && files.length > 0) {
            setPastedFile(files[0]);
        }
    };

    return (
        <SafeAreaView style={styles.container}>
            <StatusBar style="auto" />
            <ScrollView
                style={styles.scrollView}
                contentContainerStyle={styles.scrollContent}
            >
                <Text style={styles.title}>PasteInput Test</Text>

                {/* Controlled PasteInput */}
                <Text style={styles.label}>Controlled PasteInput:</Text>
                <PasteInput
                    ref={inputRef}
                    disableCopyPaste={false}
                    onPaste={onPaste}
                    style={styles.input}
                    multiline={true}
                    placeholder="Type here (controlled)"
                    submitBehavior="newline"
                    underlineColorAndroid="transparent"
                    keyboardType="default"
                    disableFullscreenUI={true}
                    textContentType="none"
                    autoComplete="off"
                    smartPunctuation="disable"
                    value={controlledText}
                    onChangeText={(newText) => {
                        console.log('onChangeText:', newText);
                        setControlledText(newText);
                    }}
                />
                <Text style={styles.valueText}>Value: "{controlledText}"</Text>

                {/* Normal TextInput for comparison */}
                <Text style={styles.label}>
                    Normal TextInput (for comparison):
                </Text>
                <TextInput
                    style={styles.input}
                    multiline={true}
                    placeholder="Type here (normal)"
                    submitBehavior="newline"
                    underlineColorAndroid="transparent"
                    keyboardType="default"
                    disableFullscreenUI={true}
                    textContentType="none"
                    autoComplete="off"
                />

                {/* Pasted file info */}
                {pastedFile && (
                    <View style={styles.pastedInfo}>
                        <Text style={styles.label}>Pasted File:</Text>
                        <Text>Name: {pastedFile.fileName}</Text>
                        <Text>Size: {pastedFile.fileSize} bytes</Text>
                        <Text>Type: {pastedFile.type}</Text>
                        <Text numberOfLines={1}>URI: {pastedFile.uri}</Text>
                    </View>
                )}

                <Button
                    title="Clear Text"
                    onPress={() => setControlledText('')}
                />
                <Button
                    title="Focus PasteInput"
                    onPress={() => inputRef.current?.focus()}
                />
            </ScrollView>
        </SafeAreaView>
    );
}

const styles = StyleSheet.create({
    container: {
        flex: 1,
        backgroundColor: '#fff',
    },
    scrollView: {
        flex: 1,
    },
    scrollContent: {
        padding: 20,
        gap: 12,
    },
    title: {
        fontSize: 24,
        fontWeight: 'bold',
        marginBottom: 20,
        textAlign: 'center',
    },
    label: {
        fontSize: 16,
        fontWeight: '600',
        marginTop: 10,
    },
    input: {
        fontSize: 16,
        lineHeight: 22,
        paddingHorizontal: 12,
        paddingVertical: 10,
        minHeight: 44,
        maxHeight: 150,
        borderColor: '#ccc',
        borderWidth: 1,
        borderRadius: 8,
        backgroundColor: '#f9f9f9',
    },
    valueText: {
        fontSize: 14,
        color: '#666',
        fontStyle: 'italic',
    },
    pastedInfo: {
        backgroundColor: '#e0f0ff',
        padding: 12,
        borderRadius: 8,
        marginTop: 10,
    },
});
