/**
 * @type {import('@react-native-community/cli-types').UserDependencyConfig}
 */
module.exports = {
    dependency: {
        platforms: {
            android: {
                sourceDir: './android',
                // cmakeListsPath removed - using ViewManager-only approach without C++ ShadowNode
                cxxModuleCMakeListsPath: null,
                componentDescriptorsCMakeListsPath: null,
            },
        },
    },
    // Disable codegen for Android to avoid C++ compatibility issues
    codegenConfig: null,
};
