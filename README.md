# android-path-drawable

A custom drawable that can modify individual vector path colors at runtime.

## Using

1. Import the library into your project.
    ```groovy
    dependencies {
        implementation "com.aureusapps.android:path-drawable:1.0.0"
    }
    ```
2. Create `PathDrawable`.
   ```kotlin
   val pathDrawable = PathDrawable(context, R.drawable.ic_android)
   ```
3. Control individual path segment colors.
   ```kotlin
   pathDrawable.setSegmentFillColor(index, color)
   ```

## Appreciate my work!

If you find this library useful, please consider buying me a coffee.

<a href="https://www.buymeacoffee.com/udarawanasinghe" target="_blank"><img src="https://cdn.buymeacoffee.com/buttons/default-orange.png" alt="Buy Me A Coffee" height="41" width="174"></a>