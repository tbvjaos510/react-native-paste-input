require "json"

package = JSON.parse(File.read(File.join(__dir__, "package.json")))

Pod::Spec.new do |s|
  s.name         = "react-native-paste-input"
  s.version      = package["version"]
  s.summary      = package["description"]
  s.homepage     = package["homepage"]
  s.license      = package["license"]
  s.authors      = package["author"]

  s.platforms    = { :ios => "12.4" }
  s.source       = { :git => "https://github.com/mattermost/react-native-paste-input.git", :tag => "#{s.version}" }

  s.source_files = "ios/**/*.{h,m,mm,swift,cpp}"
  s.private_header_files = "ios/**/*.h"

  # Header search paths for React Native internal headers
  s.pod_target_xcconfig = {
    "HEADER_SEARCH_PATHS" => [
      "\"$(PODS_ROOT)/../../node_modules/react-native/ReactCommon\"",
      "\"$(PODS_ROOT)/../../node_modules/react-native/ReactCommon/react/renderer/components/textinput\"",
      "\"$(PODS_ROOT)/../../node_modules/react-native/ReactCommon/react/renderer/components/textinput/platform/ios\"",
      "\"$(PODS_ROOT)/../../node_modules/react-native/ReactCommon/react/renderer/components/text\"",
      "\"$(PODS_ROOT)/../../node_modules/react-native/ReactCommon/react/renderer/textlayoutmanager\"",
      "\"$(PODS_ROOT)/../../node_modules/react-native/ReactCommon/react/renderer/textlayoutmanager/platform/ios\"",
      "\"$(PODS_ROOT)/../../node_modules/react-native/ReactCommon/react/renderer/components/view\"",
    ].join(" ")
  }

  install_modules_dependencies(s)
end
