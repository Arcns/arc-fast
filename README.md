# Arc Fast
[![](https://jitpack.io/v/com.gitee.arcns/arc-fast.svg)](https://jitpack.io/#com.gitee.arcns/arc-fast)

## 一、介绍
本项目包含一系列开箱即用的便携工具，能够让你快速、优雅的享受安卓便捷开发～

## 二、Fast Core
- 核心类库
#### 1.集成方式：
```
allprojects {
	repositories {
		...
		maven { url 'https://www.jitpack.io' }
	}
}
```
```
 implementation 'com.gitee.arcns.arc-fast:core:latest.release'
```

## 三、Fast Permission
- 一行代码实现基于Activity Result API的动态权限获取`
#### 1.集成方式：
```
allprojects {
	repositories {
		...
		maven { url 'https://www.jitpack.io' }
	}
}
```
```
 implementation 'com.gitee.arcns.arc-fast:permission:latest.release'
```
#### 2.使用方式：
- 简单获取权限
```
 FastPermissionUtil.request(
            activity, // or fragment
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            ... 
        ) { allGranted: Boolean, result: Map<String, FastPermissionResult> ->
            // allGranted：是否全部权限获取成功
            // result：各个权限的获取结果，key为permission，value为获取结果（Granted:同意；Denied:拒绝；DeniedAndDonTAskAgain:拒绝且不再询问）
             if (allGranted) {
                // 全部权限获取成功
            }
        }
```
- 获取权限，并在必要时弹出权限解释说明
```
 FastPermissionUtil.request(
             fragment = this,
            FastPermissionRequest(Manifest.permission.CAMERA,"应用需要相机权限用于扫描"),
            FastPermissionRequest(Manifest.permission.READ_EXTERNAL_STORAGE,"应用需要储存权限用于选择扫描图片"),
        ) { allGranted, result ->
             if (allGranted) {
                // 全部权限获取成功
            }
        }
```
- 使用自定义的弹出权限解释说明弹窗
```
 FastPermissionUtil.showAlertDialog = {activity,message,positiveButton,onPositiveButton,negativeButton,onNegativeButton ->
           // 弹出自定义弹窗
          MyAlertDialogBuilder(activity)
                    .setMessage(message)
                    .setNegativeButton(negativeButton) { _, _ -> onNegativeButton.invoke() }
                    .setPositiveButton(positiveButton) { _, _ -> onPositiveButton.invoke() }
                    .show()
}
```

## 三、Immersive Dialog
- 一行代码简单实现Android沉浸式Dialog
#### 1.集成方式：
```
allprojects {
	repositories {
		...
		maven { url 'https://www.jitpack.io' }
	}
}
```
```
 implementation 'com.gitee.arcns.arc-fast:immersive:latest.release'
```
#### 2.使用方式
>第一步：Dialog改为继承ImmersiveDialog
第二步：实现layoutId（Dialog的布局文件）和immersiveDialogConfig（沉浸式配置）
第三步：在onViewCreated实现您自己的业务逻辑
```
/**
 * 第一步：Dialog改为继承ImmersiveDialog
 */
class TestDialog : ImmersiveDialog() {

    /**
     * 第二步：实现layoutId（Dialog的布局文件）
     * 注意：您不需要再自行实现onCreateView方法
     */
    override val layoutId: Int get() = R.layout.dialog_test

    /**
     * 第二步：实现immersiveDialogConfig（沉浸式配置），为简化配置，我们内置了三种常用配置：
     * 1. createFullScreenDialogConfig 全屏弹窗配置
     * 2. createBottomDialogConfig 底部弹窗配置
     * 3. createSoftInputAdjustResizeDialogConfig 带输入框的弹窗配置
     * 如果您有更多自定义需求，您可以自行创建自己的ImmersiveDialogConfig
     * 注意：您不需要再自行配置Dialog和Window，通过ImmersiveDialogConfig即可简单完成配置
     */
    override val immersiveDialogConfig: ImmersiveDialogConfig
        get() = ImmersiveDialogConfig.createFullScreenDialogConfig()

    /**
     * 第三步：在onViewCreated实现您自己的业务逻辑
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ... ...
    }
}
```
常用案例一：全屏弹窗
```
override val immersiveDialogConfig
        get() = ImmersiveDialogConfig.createFullScreenDialogConfig().apply {
            height = 500 // 可选：设置弹窗宽度
            width = 500 // 可选：设置弹窗高度
            backgroundDimEnabled = false // 可选：禁用默认弹窗背景
            backgroundColor = 0x99000000.toInt() // 可选：设置弹窗背景的颜色
        }
```
常用案例二：底部弹窗
```
override val immersiveDialogConfig
        get() = ImmersiveDialogConfig.createFullScreenDialogConfig().apply {
            height = 500 // 可选：设置弹窗高度
            navigationColor = Color.BLACK  //可选：设置底部导航栏背景为黑色
            isLightNavigationBarForegroundColor = true //可选：设置设置底部导航栏上的图标为白色
        }
```
常用案例三：带输入框同时需要弹出键盘时自动更改布局的弹窗
```
override val immersiveDialogConfig
        get() = ImmersiveDialogConfig.createSoftInputAdjustResizeDialogConfig().apply {
            backgroundDimEnabled = false // 可选：该配置下除非禁用backgroundDimEnabled否则navigationColor会无效
            backgroundColor = 0x99000000.toInt() // 可选：设置弹窗背景的颜色
            animations = 0
        }

override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // SoftInputAdjustResizeDialogConfig需要与applyWindowInsetIMEAnimation配合使用
        binding.clContent.applyWindowInsetIMEAnimation(
            dispatchMode = WindowInsetsAnimationCompat.Callback.DISPATCH_MODE_CONTINUE_ON_SUBTREE,
            rootView = view
        )
    }
```
#### 3.ImmersiveDialogConfig支持的配置参数

| 配置参数 | 类型 | 说明 |
| ------ | ------ | ------ |
| width | Int| Dialog的宽度，支持MATCH_PARENT、WRAP_CONTENT或具体数值 |
| height | Int | Dialog的高度，支持MATCH_PARENT、WRAP_CONTENT或具体数值 |
| gravity | Int | 弹窗的位置，支持Gravity.CENTER、Gravity.BOTTOM等位置 |
| backgroundDimEnabled | Boolean | 启用系统自带的弹窗半透明黑色背景 |
| backgroundDimAmount | Float | 系统自带背景的透明值，范围为0[完全透明]-1[不透明] |
| backgroundColor | Int | 自定义的弹窗背景颜色，只有backgroundDimEnabled为false时才有效果 |
| navigationColor | Int | 弹窗系统导航栏处/底部小白条的颜色 |
| animations | Int | 弹窗动画的资源文件，为0时表示无动画 |
| canceledOnTouchOutside | Boolean | 触摸弹窗之外的地方是否关闭弹窗 |
| cancelable | Boolean | 点击返回按键是否关闭弹窗 |
| isLightStatusBarForegroundColor | Boolean | 系统状态栏上的图标与文字是否显示为白色 |
| isLightNavigationBarForegroundColor | Boolean | 系统导航栏上的图标是否显示为白色  |
| enableWrapDialogContentView | Boolean | 启用弹窗内容根视图包裹。注意如果不启用，则由背景实现视图包裹功能（navigationColor由背景包裹控制，而且除非禁用backgroundDimEnabled否则navigationColor无效） |
| enableSoftInputAdjustResize | Boolean | 启用打开键盘时自动重置弹窗布局大小，避免布局被键盘遮挡。注意启用后，内容无法扩展到全屏，通常R版本以下带输入框同时需要弹出键盘时自动更改布局的弹窗需设置该项为true，否则键盘打开后无法重置布局|
| updateCustomDialogConfig | (dialog, window) -> Unit | 更新dialog更多自定义配置 |

## 四、Immersive PopupWindow
- 一行代码简单实现Android沉浸式PopupWindow
#### 1.集成方式：
```
allprojects {
	repositories {
		...
		maven { url 'https://www.jitpack.io' }
	}
}
```
```
 implementation 'com.gitee.arcns.arc-fast:immersive:latest.release'
```

#### 2.使用方式
>第一步：PopupWindow改为继承ImmersivePopupWindow
第二步：实现getImmersivePopupWindowConfig

```
/**
 * 第一步：PopupWindow改为继承ImmersivePopupWindow
 */
class TestPopupWindow : ImmersivePopupWindow(ViewGroup.LayoutParams.MATCH_PARENT, 500)  {

    /**
     * 第二步：实现getImmersivePopupWindowConfig（沉浸式配置），为简化配置，我们内置了三种常用配置：
     * 1. createBottomPopupWindow 底部PopupWindows
     * 2. createTopToAnchorBottomPopupWindow 顶部锚点PopupWindows（例如顶部下拉菜单）
     * 3. createBottomToAnchorTopPopupWindow 底部锚点PopupWindows（例如底部上拉菜单）
     * 如果您有更多自定义需求，您可以自行创建自己的ImmersivePopupWindowConfig
     */
   override fun getImmersivePopupWindowConfig(context: Context) =
        ImmersivePopupWindowConfig.createBottomPopupWindow(context)
}
```

#### 3.ImmersivePopupWindowConfig支持的配置参数

| 配置参数 | 类型 | 说明 |
| ------ | ------ | ------ |
| backgroundColor | Int | 背景颜色 |
| navigationColor | Int | 系统导航栏处/底部小白条的颜色 |
| canceledOnTouchOutside | Boolean | 触摸PopupWindow之外的地方是否关闭PopupWindow |
| cancelable | Boolean | 点击返回按键是否关闭PopupWindow |
| isLightStatusBarForegroundColor | Boolean | 系统状态栏上的图标与文字是否显示为白色 |
| isLightNavigationBarForegroundColor | Boolean | 系统导航栏上的图标是否显示为白色  |
| backgroundConstraint | ImmersivePopupWindowBackgroundConstraint | 相对于锚点的背景布局约束 |
| enableBackgroundAnimator | Boolean | 是否启用背景渐变动画 |

## 五、Fast Span
- 一行代码简单实现Android TextView常用样式Span
#### 1.集成方式：
```
allprojects {
	repositories {
		...
		maven { url 'https://www.jitpack.io' }
	}
}
```
```
 implementation 'com.gitee.arcns.arc-fast:span:latest.release'
```

#### 2.使用方式
```
 val spannableStringBuilder = SpannableStringBuilder()
// 添加图片使用appendFastImage与FastImageSpan
spannableStringBuilder.appendFastImage(
    FastImageSpan(
        context,
        R.mipmap.ic_launcher_round, // 图片资源
        width = 60.dp, // 图片宽度
        height = 60.dp, // 图片高度
        leftMargin = 8.dp, // 图片的左间距
        rightMargin = 8.dp // 图片的右间距
    )
)
// 添加带圆角边框的文字使用appendFastSpan与FastTextWrapSpan
spannableStringBuilder.appendFastSpan(
    "满99元减10元", 
    FastTextWrapSpan(
        radius = 4f.dp, // 边框的圆角
        borderSize = 1f.dp, // 边框的大小
        borderColor = R.color.main.color,// 边框的颜色
        textSize = 12f.sp, // 文字的大小
        textColor = R.color.main.color, // 文字的颜色
        textRightMargin = 6f.dp, // 文字的右外边距
        topPadding = 2f.dp, // 文字的上内边距
        bottomPadding = 2f.dp,// 文字的下内边距
        leftPadding = 6f.dp,// 文字的左内边距
        rightPadding = 6f.dp// 文字的右内边距
    )
)
spannableStringBuilder.append(
    "华为平板MatePad 11 平板电脑120Hz高刷全面屏 鸿蒙HarmonyOS 6G+128GB WIFI 曜石灰 WIFI海岛蓝"
)
// 添加文字之间的间距使用appendFastSpacing
spannableStringBuilder.appendFastSpacing(6.dp)
// 添加文字的颜色、大小、加粗等常用样式使用appendFastTextStyle
spannableStringBuilder.appendFastTextStyle("10月31日-11月3日的订单，预计在2日内发货") {
    textColor = 0xFF999999.toInt() // 文字颜色
    textSize = 14.sp // 文字大小
    textStyle = Typeface.BOLD // 文字加粗
}
binding.tvTitle.text = spannableStringBuilder
```