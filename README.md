# Arc Fast
[![](https://jitpack.io/v/com.gitee.arcns/arc-fast.svg)](https://jitpack.io/#com.gitee.arcns/arc-fast)

#### 目录
- [一、介绍](#一介绍)
- [二、Fast Resource:一行代码简单实现Android dp2px、sp2px、常用Resource值(string/color/drawable)获取](#二fast-resource)
- [三、Fast Permission:一行代码实现基于Activity Result API的动态权限获取](#三fast-permission)
- [四、Immersive Dialog:一行代码简单实现Android沉浸式Dialog](#四immersive-dialog)
- [五、Immersive PopupWindow:一行代码简单实现Android沉浸式PopupWindow](#五immersive-popupwindow)
- [六、Fast Span:一行代码简单实现Android TextView常用样式Span](#六fast-span)
- [七、Fast Mask:一行代码简单实现Android遮罩镂空视图](#七fast-mask)
- [八、Fast View:一行代码简单实现Android常用View的圆角边框](#八fast-view)
- [九、Fast TextView:一行代码实现TextView中粗、四个方向drawable的不同Padding和宽高](#九fast-textview)
- [十、Fast NestedScrollCompat:一行代码解决Android滚动控件嵌套产生的滑动事件冲突](#十fast-nestedscrollcompat)
- [十一、Fast DragExitLayout:一行代码实现Android仿小红书、Lemon8拖拽退出效果](#十一fast-dragexitlayout)


## 一、介绍
本项目包含一系列开箱即用的便携工具，主要包括Fast Permission、Immersive Dialog、Immersive PopupWindow、Fast Span、Fast Mask等，能够让你快速、优雅的享受安卓便捷开发～

## 二、Fast Resource
- 一行代码简单实现Android dp2px、sp2px、常用Resource值(string/color/drawable)获取
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

#### 2.使用方式
（1）dp2px、px2dp
| 方法 | 功能 | 用法 |
| ------ | ------ | ------ |
| Float.dpToPx | 把dp转换为px(Float格式) | 100f.dpToPx |
| Int.dpToPx | 把dp转换为px(Int格式) | 100.dpToPx |
| Float.pxToDp | 把px转换为dp(Float格式) | 100f.pxToDp |
| Int.pxToDp | 把px转换为dp(Int格式) | 100.pxToDp |

（2）sp2px
| 方法 | 功能 | 用法 |
| ------ | ------ | ------ |
| Float.spToPx | 把sp转换为px(Float格式) | 100f.spToPx |
| Int.spToPx | 把sp转换为px(Int格式) | 100.spToPx |

（3）获取String资源
| 方法 | 功能 | 用法 |
| ------ | ------ | ------ |
| Int.resToString | 通过StringRes获取String值 | R.string.test.resToString |
| Int.resToStringOrNull | 通过StringRes获取String值，获取失败时返回null | R.string.test.resToStringOrNull |
| Int.resToString(vararg values: Any?) | 通过StringRes获取String值，并替换格式参数(例如%1$s) | R.string.test.resToString("1","2") |
| Int.resToStringOrNull(vararg values: Any?) | 通过StringRes获取String值，并替换格式参数(例如%1$s)，获取失败时返回null | R.string.test.resToStringOrNull("1","2") |

（4）Drawable资源
| 方法 | 功能 | 用法 |
| ------ | ------ | ------ |
| Int.resToDrawable | 通过DrawableRes获取Drawable值 | R.drawable.test.resToDrawable |
| Int.resToDrawableOrNull | 通过DrawableRes获取Drawable值，获取失败时返回null | R.drawable.test.resToDrawableOrNull |
| Drawable.applyTint(color: Int?) | 为Drawable实现着色效果 | R.drawable.test.applyTint(0x24000000) |
| Drawable.applyRipple(context: Context,rippleColor: Int? = null,rippleColorStateList: ColorStateList? = null) | 为Drawable实现Ripple效果，ColorStateList优先级高于rippleColor | R.drawable.test.applyRipple(context,0x00000000) |

（5）Color资源
| 方法 | 功能 | 用法 |
| ------ | ------ | ------ |
| Int.resToColor | 通过ColorRes获取Color值 | R.color.test.resToColor |
| Int.resToColorOrNull | 通过ColorRes获取Color值，获取失败时返回null  | R.color.test.resToColorOrNull |
| String.hexToColor | 把Hex Color转换为Color(Int格式) | "#00000000".hexToColor |
| String.hexToColorOrNull | 把Hex Color转换为Color(Int格式)，获取失败时返回null  | "#000000".hexToColorOrNull |
| Int.colorToHex | 把Color转换为Hex Color(String格式) | 0x00000000.colorToHex |
| Int.colorToHexOrNull | 把Color转换为Hex Color(String格式)，获取失败时返回null  | 0x00000000.colorToHexOrNull |
| Int.lightColorNess | 获取Color的亮度(0-1) | 0x00000000.lightColorNess |
| Int.isLightColor | 判断Color是否为亮色调 | 0x00000000.isLightColor |

（6）Dimension资源
| 方法 | 功能 | 用法 |
| ------ | ------ | ------ |
| Int.resToDimenValue | 通过DimenRes获取Dimen值 | R.dimen.test.resToDimenValue |
| Int.resToDimenValueOrNull | 通过DimenRes获取Dimen值，获取失败时返回null  | R.dimen.test.resToDimenValueOrNull |

（7）Attr资源（Attr的Res资源与Context的theme相关，因此此处必须手动传入Attr对应的Context）
| 方法 | 功能 | 用法 |
| ------ | ------ | ------ |
| Context.getAttributeResource(attr: Int, defResId: Int? = null) | 通过AttrResId获取Res资源 | context.getAttributeResource(R.attr.test) |
| Context.selectableItemBackgroundRes | 获取selectableItemBackground资源 | context.selectableItemBackgroundRes |
| Context.selectableItemBackgroundBorderlessRes | 获取selectableItemBackgroundBorderless资源 | context.selectableItemBackgroundBorderlessRes |
| Context.actionBarItemBackgroundRes | 获取actionBarItemBackground资源 | context.actionBarItemBackgroundRes |

## 三、Fast Permission
- 一行代码实现基于Activity Result API的动态权限获取`
> 众所周知，在Android中如果我们想要实现动态权限获取，只需要调用`ActivityCompat.requestPermissions(activity/fragment,permissions,requestCode)`，然后在`activity/fragment`中重写`onRequestPermissionsResult`来响应请求结果即可，当然我们也可以通过`RxPermissions`、`easypermissions`等第三方库实现，简单而便捷。可如今却有一个小问题，就是在新的API中，`onRequestPermissionsResult`已被弃用，取而代之的是`Activity Result API`，但大多数第三方库仍然使用旧的解决方案。因此，假设你想要不使用已被弃用的`onRequestPermissionsResult`，大概率就只能自己通过`Activity Result API`实现了，经过实践，我发现如果项目中全部使用`Activity Result API`代替原有方法，会存在大量样板代码，而且`registerForActivityResult`要求必须在`fragment`或`activity`的`Lifecycle`达到`CREATED`之前创建，所以也无法进行按需加载，于是便做了一个开源Library项目，方便大家集成后，一行代码实现基于Activity Result API的动态权限获取。

#### 1.实现思路：
相对比原来的`onRequestPermissionsResult`，`Activity Result API`使用起来更加的便捷友好，我们只需要简单的封装和实现权限理由相关的逻辑，即可方便的调用。
唯一的难度在于：根据https://developer.android.com/training/basics/intents/result?hl=zh-cn，`registerForActivityResult`要求必须在`fragment`或`activity`的`Lifecycle`达到`CREATED`之前创建，这意味着我们无法进行按需加载，而且每次都要创建实例，无法通过静态方法进行一键调用。
经过研究，这里我们通过创建一个专用的`fragment`来解决上述的问题：
在调用动态权限获取的时候，我们创建一个空布局的`fragment`，并在这个`fragment`的onCreate中进行`registerForActivityResult`，从而满足创建条件并实现按需加载；另外为了提高效率，我们也做了实例池避免这个专用`fragment`的重复创建问题，并实现了自动的生命周期管理。

#### 2.集成方式：
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
#### 3.使用方式：
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

## 四、Immersive Dialog
- 一行代码简单实现Android沉浸式Dialog
> 随着全面屏时代的来临，沉浸式的体验对于APP变得越来越重要，Dialog作为APP一种重要的交互方式，如果不实现沉浸式的话，那么Dialog显示时便会在`状态栏/系统导航栏/小白条`上会出现丑陋的黑边，或出现上下一边有黑边一边没有黑边的情况，影响体验。但是，想要在Android中实现理想的沉浸式Dialog，并不是一件容易的事情，不仅Android不同版本的系统实现方式不同，而且Android提供的设置API也并不友好，大多数情况下，若我们想要达到理想的沉浸式Dialog，往往需要花费大量的时间。
由于每个项目都会或多或少遇到这个问题，解决起来也较为繁琐，存在着大量样板代码，所以我做了一个开源Library项目，不仅能够方便大家快速实现Android沉浸式Dialog，也能够大大简化Dialog的配置，让大家聚焦于业务功能代码。

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

## 五、Immersive PopupWindow
- 一行代码简单实现Android沉浸式PopupWindow
> 随着全面屏时代的来临，沉浸式的体验对于APP变得越来越重要，PopupWindow作为APP一种重要的交互方式，如果不实现沉浸式的话，那么PopupWindow显示时便会在`状态栏/系统导航栏/小白条`上会出现丑陋的黑边，或出现上下一边有黑边一边没有黑边的情况，影响体验。但是，想要在Android中实现理想的沉浸式PopupWindow，并不是一件容易的事情，不仅Android不同版本的系统实现方式不同，而且Android提供的设置API也并不友好，大多数情况下，若我们想要达到理想的沉浸式PopupWindow，往往需要花费大量的时间。
由于每个项目都会或多或少遇到这个问题，解决起来也较为繁琐，存在着大量样板代码，所以我做了一个开源Library项目，不仅能够方便大家快速实现Android沉浸式PopupWindow，让大家聚焦于业务功能代码。

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

## 六、Fast Span
- 一行代码简单实现Android TextView常用样式Span
> 在日常开发中，我们经常需要使用到TextView Span的各种常用样式，但Android提供的设置API也并不友好，无法快速使用，所以我做了一个开源Library项目，方便大家集成后，一行代码简单实现Android TextView常用样式Span。

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
// 添加图片的宽高、间距、点击事件等常用样式使用appendFastImageStyle
spannableStringBuilder.appendFastImageStyle(
            context = requireContext(),
            drawableRes = R.mipmap.ic_launcher_round
        ) {
            width = 20.dp //图片宽度
            height = 20.dp //图片高度
            padding = 8.dp //图片间距
            onClick = {
                // 点击图片回调
            }
        }
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
// 添加文字的颜色、大小、加粗、点击事件等常用样式使用appendFastTextStyle
spannableStringBuilder.appendFastTextStyle("10月31日-11月3日的订单，预计在2日内发货") {
    textColor = 0xFF999999.toInt() // 文字颜色
    textSize = 14.sp // 文字大小
    textStyle = Typeface.BOLD // 文字加粗
     onClick = {
                // 点击文字回调
      }
}
binding.tvTitle.text = spannableStringBuilder
binding.tvTitle.enableClickableSpan() // 启用点击事件
```

## 七、Fast Mask
- 一行代码简单实现Android遮罩镂空视图
> 在日常开发中，我们经常需要实现遮罩镂空的功能，例如扫码、引导页、遮挡层等等。通常我们会在每个需要的地方自定义一个View来实现，但如果项目有多个地方存在类似需求，就会产生大量样板代码，于是我做了一个开源Library项目，方便大家集成后，一行代码实现Android遮罩镂空视图。

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
 implementation 'com.gitee.arcns.arc-fast:mask:latest.release'
```

#### 2.使用方式
```
<com.arc.fast.mask.MaskHollowView
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:gravity="center"
            app:maskHollowView_hollow_border_color="#FFFFFF"
            app:maskHollowView_hollow_border_size="4dp"
            app:maskHollowView_hollow_height="80dp"
            app:maskHollowView_hollow_margin_top="40dp"
            app:maskHollowView_hollow_radius="16dp"
            app:maskHollowView_hollow_width="80dp"
            app:maskHollowView_mask_background="#66000000" />
```
-MaskHollowView支持的参数
| 参数 | 说明 | 类型 | 默认值 |
| ------ | ------ | ------ | ------ |
| maskHollowView_hollow_width | 镂空区域的宽度，优先级最高 | dimension，例如100dp | 控件宽度的一半 |
| maskHollowView_hollow_height | 镂空区域的高度，优先级最高 | dimension，例如100dp | 控件与宽度一致 |
| maskHollowView_hollow_width_ratio | 镂空区域的宽度相对与控件宽度的比例 | float，例如0.5 | 空 |
| maskHollowView_hollow_height_ratio | 镂空区域的高度相对与控件高度的比例 | float，例如0.5 | 空 |
| maskHollowView_hollow_dimension_ratio | 镂空区域的宽高比例 | string，例如w,1:1 | 空 |
| maskHollowView_hollow_margin_top | 镂空区域的上间距 | dimension，例如10dp | 0 |
| maskHollowView_hollow_margin_bottom | 镂空区域的下间距 | dimension，例如10dp | 0 |
| maskHollowView_hollow_margin_left | 镂空区域的左间距 | dimension，例如10dp | 0 |
| maskHollowView_hollow_margin_right | 镂空区域的右间距 | dimension，例如10dp | 0 |
| maskHollowView_hollow_margin_radius | 镂空区域的圆角 | dimension，例如10dp | 0 |
| maskHollowView_hollow_margin_border_size | 镂空区域的边框大小 | dimension，例如10dp | 0 |
| maskHollowView_hollow_margin_border_color | 镂空区域的边框颜色 | color，例如#FFFFFF | 空 |
| maskHollowView_hollow_border_rect | 镂空区域的边框矩形边长，如果该值大于0，则只会在镂空区域四个角的边框矩形内显示边框 | dimension，例如10dp | 0 |
| maskHollowView_mask_background | 遮罩区域的背景颜色 | color，例如#66000000 | #66000000 |
| android:gravity | 镂空区域相对于遮罩区域的位置 | gravity，例如top | center |


## 八、Fast View
- 一行代码简单实现Android常用View的圆角边框
> 在日常开发中，我们经常需要为各种View实现圆角边框，例如圆角边框的ImageView、圆角边框的TextView、圆角边框的ConstraintLayout等等。通常情况下我们会使用shape drawable或自定义View去实现，使用shape drawable会造成项目中存在大量的drawable文件，使用自定义View会造成相同代码的冗余，所以我做了一个开源Library项目，方便大家集成后，一行代码简单实现Android常用View的圆角边框。

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
 implementation 'com.gitee.arcns.arc-fast:view:latest.release'
```

#### 2.使用方式
##### 方式一：通过Library内置的View实现圆角边框
为方便使用，Library中内置了RoundedView、RoundedImageView、RoundedConstraintLayout、FastTextView四款支持圆角边框的View，适应绝大多数的使用场景。以RoundedView为例（其他View的使用方式相同），使用方式如下：
```
<com.arc.fast.view.rounded.RoundedView
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            app:rounded_background_color="@android:color/holo_red_light" //背景颜色
            app:rounded_border_color="@android:color/holo_blue_light" //边框颜色
            app:rounded_border_size="2dp"//边框大小
            app:rounded_radius="16dp" />//圆角大小
```
-支持的圆角边框参数
| 参数 | 说明 | 类型 | 默认值 |
| ------ | ------ | ------ | ------ |
| rounded_radius | 圆角的大小，优先度最低 | dimension，例如100dp | 0 |
| rounded_radius_top_left | 左上的圆角大小 | dimension，例如100dp | 0 |
| rounded_radius_top_right | 右上的圆角大小 | dimension，例如100dp | 0 |
| rounded_radius_bottom_left | 左下的圆角大小 | dimension，例如100dp | 0 |
| rounded_radius_bottom_right | 右下的圆角大小 | dimension，例如100dp | 0 |
| rounded_background_color | 背景颜色 | color，例如#FFFFFF | 空 |
| rounded_border_color | 边框颜色 | color，例如#FFFFFF | 空 |
| rounded_border_size | 边框大小 | dimension，例如10dp | 0 |
##### 方式二：通过IRoundedView实现任意的圆角边框控件
如果Library中内置的View无法满足项目的使用需求，那么你也可以通过IRoundedView实现任意的圆角边框控件。
>要让控件支持圆角边框只需4个步骤：
第一步:在控件中实现IRoundedView
第二步:实现IRoundedView的参数
第三步:在init中调用initRoundedRadius(context, attrs)获取xml中配置的参数
第四步:在draw中调用onDrawBefore和onDrawAfter

具体实现方式如下：
```
class CustomView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr), IRoundedView { //第一步:在控件中实现IRoundedView

   // 第二步:实现IRoundedView的参数
    override var _config = RoundedViewConfig()
    override var _temporarilyConfig: RoundedViewConfig? = null

    init {
        // 第三步:在init中调用initRoundedRadius(context, attrs)获取xml中配置的参数
        if (attrs != null) initRoundedRadius(context, attrs)
    }

  // 第四步:在draw中调用onDrawBefore和onDrawAfter
    override fun draw(canvas: Canvas) {
        onDrawBefore(canvas)
        super.draw(canvas)
        onDrawAfter(canvas)
    }
}

// 使用方式与Library内置的View相同
<yourpackage.CustomView
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            app:rounded_background_color="@android:color/holo_red_light" //背景颜色
            app:rounded_border_color="@android:color/holo_blue_light" //边框颜色
            app:rounded_border_size="2dp"//边框大小
            app:rounded_radius="16dp" />//圆角大小
```


## 九、Fast TextView
- 一行代码实现TextView中粗、四个方向drawable的不同Padding和宽高
> 在日常开发中，TextView是我们经常使用的控件，但是原生的TextView却无法帮我们实现一些常用的功能，例如：设置中粗、设置TextView drawable的宽高、分别设置TextView不同方向drawable的padding，所以我做了一个开源Library项目，方便大家集成后，一行代码简单实现TextView中粗、四个方向drawable的不同Padding和宽高。

#### 1.实现思路：
- （1）设置TextView中粗：通过设置画笔的边框来实现中粗效果（`paint.style = Paint.Style.FILL_AND_STROKE`和`paint.strokeWidth=1`）
- （2）设置TextView drawable的宽高：`drawable.setBounds(0,0,width,height)`
- （3）分别设置TextView不同方向drawable的padding：TextView虽然提供了`drawablePadding`，但只能为不同方向drawable设置一个相同的padding，因此如果需要为TextView不同方向drawable设置不同的padding，需要使用其他的方式来实现。经过研究对比，这里我们使用`InsetDrawable(drawable, paddingLeft, paddingTop, paddingRight, paddingBottom)`来实现


#### 2.集成方式：
```
allprojects {
	repositories {
		...
		maven { url 'https://www.jitpack.io' }
	}
}
```
```
 implementation 'com.gitee.arcns.arc-fast:view:latest.release'
```

#### 3.使用方式
```
<com.arc.fast.view.FastTextView
            android:id="@+id/tvTest"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:gravity="center_vertical"
            android:text="TEST"
            android:textColor="@color/main"
            android:textSize="16sp"
            android:drawableLeft="@mipmap/ic_left" //左边方向drawable
            app:fastTextView_leftImageWidth="20dp" //左边方向drawable宽度
            app:fastTextView_leftImageHeight="20dp" //左边方向drawable高度
            app:fastTextView_leftImagePadding="10dp" //左边方向drawable padding
            android:drawableRight="@mipmap/ic_right"//右边边方向drawable
            app:fastTextView_rightImageWidth="20dp" //右边方向drawable宽度
            app:fastTextView_rightImageHeight="20dp"//右边方向drawable高度
            app:fastTextView_rightImagePadding="20dp">//右边方向drawable padding
```
-FastTextView不仅支持支持四个方向drawable的不同Padding和宽高，也支持设置中粗和圆角边框，所有支持的参数如下：
| 参数 | 说明 | 类型 | 默认值 |
| ------ | ------ | ------ | ------ |
| fastTextView_textMediumBold | 设置中粗 | boolean，例如true | false |
| fastTextView_leftImageHeight | 左边方向drawable高度 | dimension，例如10dp | 0 |
| fastTextView_leftImageWidth | 左边方向drawable宽度 | dimension，例如10dp | 0 |
| fastTextView_leftImagePadding | 左边方向drawable padding | dimension，例如10dp | 0 |
| fastTextView_rightImageHeight | 右边方向drawable高度 | dimension，例如10dp | 0 |
| fastTextView_rightImageWidth | 右边方向drawable宽度 | dimension，例如10dp | 0 |
| fastTextView_rightImagePadding | 右边方向drawable padding | dimension，例如10dp | 0 |
| fastTextView_topImageHeight | 上边方向drawable高度 | dimension，例如10dp | 0 |
| fastTextView_topImageWidth | 上边方向drawable宽度 | dimension，例如10dp | 0 |
| fastTextView_topImagePadding | 上边方向drawable padding | dimension，例如10dp | 0 |
| fastTextView_bottomImageHeight | 下边方向drawable高度 | dimension，例如10dp | 0 |
| fastTextView_bottomImageWidth | 下边方向drawable宽度 | dimension，例如10dp | 0 |
| fastTextView_bottomImagePadding | 下边方向drawable padding | dimension，例如10dp | 0 |
| rounded_radius | 圆角的大小，优先度最低 | dimension，例如100dp | 0 |
| rounded_radius_top_left | 左上的圆角大小 | dimension，例如100dp | 0 |
| rounded_radius_top_right | 右上的圆角大小 | dimension，例如100dp | 0 |
| rounded_radius_bottom_left | 左下的圆角大小 | dimension，例如100dp | 0 |
| rounded_radius_bottom_right | 右下的圆角大小 | dimension，例如100dp | 0 |
| rounded_background_color | 背景颜色 | color，例如#FFFFFF | 空 |
| rounded_border_color | 边框颜色 | color，例如#FFFFFF | 空 |
| rounded_border_size | 边框大小 | dimension，例如10dp | 0 |

## 十、Fast NestedScrollCompat
- 一行代码解决Android滚动控件嵌套产生的滑动事件冲突
> 在日常开发中，我们经常需要解决NestedScrollView、ScrollView、RecyclerView、ViewPager、ViewPager2、Banner等各种滚动控件之间相互嵌套带来的滑动事件冲突问题，修复起来往往也比较麻烦，所以我做了一个开源Library项目，方便大家集成后，一行代码解决Android绝大多数场景下的滑动冲突。

#### 1.实现思路：
之所以会出现滚动控件嵌套后的滑动冲突，主要是因为里面嵌套的滚动控件不知道在什么时候需要把`TouchEvent`交给外层的滚动控件处理，所以会产生滑动冲突。
因此我们可以考虑在里面嵌套的每个滚动控件的外面都添加上一个`处理控件`，根据TouchEvent机制，`处理控件`会优先于里面的滚动控件接收到`TouchEvent`，我们就可以在处理控件中判断是否需要把`TouchEvent`交给外层的滚动控件处理。
在`处理控件`中，我们通过重写`onInterceptTouchEvent`来处理TouchEvent，处理流程如下：
- （1）在`TouchEvent`开始时(`ACTION_DOWN`)，先通过`parent.requestDisallowInterceptTouchEvent(true)`暂时禁止所有`parent`拦截`TouchEvent`，以便`处理控件`进行判断处理
- （2）在`TouchEvent`移动时(`ACTION_MOVE`)，根据TouchEvent判断用户意图的滑动方向
- （3）判断里面的滚动控件是否支持用户意图的滑动方向，如果不支持则调用`parent.requestDisallowInterceptTouchEvent(false)`允许所有`parent`拦截，如果支持则跳到（4）
- （4）根据用户意图
的滑动方向，判断里面的滚动控件是否能够在该方向进行滑动(通过`canScrollHorizontally`/`canScrollVertically`)，如果不能滑动则调用`parent.requestDisallowInterceptTouchEvent(false)`允许所有`parent`拦截
这样处理之后，如果里面的滚动控件能够滑动则交由里面的滚动控件处理，如果里面的滚动控件不能滑动则交由上级的滚动控件处理，因此能够解决绝大多数场景下滚动控件嵌套产生的滑动事件冲突。

#### 2.集成方式：
```
allprojects {
	repositories {
		...
		maven { url 'https://www.jitpack.io' }
	}
}
```
```
 implementation 'com.gitee.arcns.arc-fast:view:1.16.1'
```

#### 3.使用方式
使用时，只需要在里面嵌套的每个滚动控件的外面都添加上FastNestedScrollCompat即可
（1）NestedScrollView、ScrollView、RecyclerView、ViewPager、ViewPager2等滚动控件
```
<com.arc.fast.view.FastNestedScrollCompat
            android:layout_width="match_parent"
            android:layout_height="match_parent">
      // 注意：使用时，滚动控件必须是FastNestedScrollCompat直接且唯一的子元素
      // 此处RecyclerView仅为示例，你可以替换为NestedScrollView、ScrollView、RecyclerView、ViewPager、ViewPager2等滚动控件
      <androidx.recyclerview.widget.RecyclerView
                android:layout_width="match_parent"
                android:layout_height="match_parent"/> 
</com.arc.fast.view.FastNestedScrollCompat>
```
（2）Banner
```
<com.arc.fast.view.FastBannerNestedScrollCompat
            android:layout_width="match_parent"
            android:layout_height="match_parent">
      // 注意：使用时，Banner必须是FastNestedScrollCompat直接且唯一的子元素
      <com.youth.banner.Banner
                android:layout_width="match_parent"
                android:layout_height="match_parent"/> 
</com.arc.fast.view.FastBannerNestedScrollCompat>
```

## 十一、Fast DragExitLayout
- 一行代码实现Android仿小红书、Lemon8拖拽退出效果
> 最近小伙伴有个需求，就是实现类似于小红书、Lemon8的拖拽退出效果，查了一圈发现并没有实现该功能的Library，于是便做了一个开源Library项目，方便大家集成后，一行代码实现Android仿小红书、Lemon8的拖拽退出效果。

#### 1.实现思路：
- （1）创建一个`自定义Layout`，作为实现拖拽退出的视图
- （2）在`自定义Layout中`，重写`onInterceptTouchEvent`，用来检查`TouchEvent`的滑动方向是否可以执行退拽退出效果，如果可以执行退拽退出效果则返回`true`表示拦截`TouchEvent`
- （3）在`自定义Layout中`，重写`onTouchEvent`，在可以执行退拽退出效果时，先根据`TouchEvent`计算出滑动距离，然后使用滑动距离来设置leftMargin和topMargin以实现`自定义Layout`拖拽时移动的效果，同时设置`scaleX`和`scaleY`以实现`自定义Layout`拖拽时缩放的效果。

#### 2.集成方式：
```
allprojects {
	repositories {
		...
		maven { url 'https://www.jitpack.io' }
	}
}
```
```
 implementation 'com.gitee.arcns.arc-fast:view:1.16.1'
```

#### 3.使用方式
使用时，只需要在布局文件的最外层包裹`FastDragExitLayout`，然后在代码文件中使用`FastDragExitLayout.enableDragExit()`启用拖拽退出效果即可。
布局文件：
```
 <com.arc.fast.view.FastDragExitLayout
        android:id="@+id/dragExitLayout"
        android:layout_width="match_parent"
        android:layout_height="match_parent">
      // 你自己的布局
      ... ...
</com.arc.fast.view.FastDragExitLayout>
```
代码文件：
```
// 简单调用方式一：在拖拽退出时关闭activity
dragExitLayout.enableDragExit(activity)

// 简单调用方式二：在拖拽退出时自定义处理操作
dragExitLayout.enableDragExit{
        // 自定义处理操作
}

// 完整的调用方式：
dragExitLayout.enableDragExit(
        bindExitActivity = activity, // 可选项：绑定在拖拽退出时的关闭activity
        onDragCallback = {isDrag:Boolean-> 
                 // 可选项：在开始或取消拖拽时的回调
        },
        onExitWaitCallback = {currentScale: Float, continueExit: (() -> Unit) ->
                 // 可选项：在拖拽退出之前的回调，你可以在此处进行耗时的操作，完成后调用        continueExit即可继续退出
        },
        onExitCallback = { currentScale: Float ->
                 // 可选项：在拖拽退出时的回调
        }
)
```
