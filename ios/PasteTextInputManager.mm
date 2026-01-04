#import <React/RCTLog.h>
#import <React/RCTUIManager.h>
#import <React/RCTViewManager.h>

#import <React/RCTBaseTextInputShadowView.h>
#import <React/RCTBaseTextInputView.h>
#import <React/RCTConvert+Text.h>

#import <React/RCTUIManagerObserverCoordinator.h>
#import <React/RCTUIManagerUtils.h>
#import <React/RCTShadowView+Layout.h>
#import <React/RCTShadowView.h>

#import "PasteTextInput.h"
#import "PasteInputView.h"

#ifdef RCT_NEW_ARCH_ENABLED
@interface PasteTextInputManager : RCTViewManager
@end

@implementation PasteTextInputManager

RCT_EXPORT_MODULE(PasteTextInput)

// Export TextInput events for Fabric Paper interop
// These are inherited from TextInputEventEmitter in native code
RCT_EXPORT_VIEW_PROPERTY(onContentSizeChange, RCTDirectEventBlock)
RCT_EXPORT_VIEW_PROPERTY(onSelectionChange, RCTDirectEventBlock)
RCT_EXPORT_VIEW_PROPERTY(onScroll, RCTDirectEventBlock)
RCT_EXPORT_VIEW_PROPERTY(onBlur, RCTBubblingEventBlock)
RCT_EXPORT_VIEW_PROPERTY(onFocus, RCTBubblingEventBlock)
RCT_EXPORT_VIEW_PROPERTY(onEndEditing, RCTBubblingEventBlock)
RCT_EXPORT_VIEW_PROPERTY(onSubmitEditing, RCTBubblingEventBlock)
RCT_EXPORT_VIEW_PROPERTY(onKeyPress, RCTBubblingEventBlock)
RCT_EXPORT_VIEW_PROPERTY(onChange, RCTBubblingEventBlock)
RCT_EXPORT_VIEW_PROPERTY(onPaste, RCTBubblingEventBlock)

RCT_EXPORT_METHOD(focus : (nonnull NSNumber *)viewTag)
{
  [self.bridge.uiManager addUIBlock:^(RCTUIManager *uiManager, NSDictionary<NSNumber *, UIView *> *viewRegistry) {
    UIView *view = viewRegistry[viewTag];
    [view reactFocus];
  }];
}

RCT_EXPORT_METHOD(blur : (nonnull NSNumber *)viewTag)
{
  [self.bridge.uiManager addUIBlock:^(RCTUIManager *uiManager, NSDictionary<NSNumber *, UIView *> *viewRegistry) {
    UIView *view = viewRegistry[viewTag];
    [view reactBlur];
  }];
}

RCT_EXPORT_METHOD(setTextAndSelection
                  : (nonnull NSNumber *)viewTag mostRecentEventCount
                  : (NSInteger)mostRecentEventCount value
                  : (NSString *)value start
                  : (NSInteger)start end
                  : (NSInteger)end)
{
  [self.bridge.uiManager addUIBlock:^(RCTUIManager *uiManager, NSDictionary<NSNumber *, UIView *> *viewRegistry) {
    RCTBaseTextInputView *view = (RCTBaseTextInputView *)viewRegistry[viewTag];
    NSInteger eventLag = view.nativeEventCount - mostRecentEventCount;
    if (eventLag != 0) {
      return;
    }
    RCTExecuteOnUIManagerQueue(^{
      RCTBaseTextInputShadowView *shadowView =
          (RCTBaseTextInputShadowView *)[self.bridge.uiManager shadowViewForReactTag:viewTag];
      if (value) {
        [shadowView setText:value];
      }
      [self.bridge.uiManager setNeedsLayout];
      RCTExecuteOnMainQueue(^{
        [view setSelectionStart:start selectionEnd:end];
      });
    });
  }];
}

// Note: For Fabric, the -view method is NOT needed.
// The PasteTextInput component is created via PasteTextInputCls() in PasteTextInput.mm

@end

#else
#import <React/RCTMultilineTextInputViewManager.h>

@interface PasteTextInputManager : RCTMultilineTextInputViewManager
@end

@implementation PasteTextInputManager

RCT_EXPORT_MODULE(PasteTextInput)

RCT_EXPORT_VIEW_PROPERTY(disableCopyPaste, BOOL)
RCT_EXPORT_VIEW_PROPERTY(smartPunctuation, NSString)
RCT_EXPORT_VIEW_PROPERTY(onPaste, RCTBubblingEventBlock)

- (UIView *)view
{
  return [[PasteInputView alloc] initWithBridge:self.bridge];
}

@end
#endif
