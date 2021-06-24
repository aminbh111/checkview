# Check View
An animating check mark

![Sample App](https://github.com/cdflynn/crossView/blob/master/sample/img/check_sample.gif?raw=true)


## Usage
Add a `CrossView` to your layout

```xml
    <nl.schmit.animationView.crossView.CrossView
        android:id="@+id/check"
        android:layout_width="200dp"
        android:layout_height="200dp"
        app:crossView_strokeColor="@color/green"
        app:crossView_strokeWidth="@dimen/check_stroke_width"/>
```

Note that you can specify a stroke width and color with xml attributes `crossView_strokeWidth` and `crossView_strokeColor` respectively.

Call `check()`:

```java
    mCrossView.check();
```


## Install

Add jitpack to your root `build.gradle`

```gradle
    allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```

Add as a dependency
```gradle
	dependencies {
	        implementation 'com.github.cdflynn:crossView:v1.1'
	}
```
