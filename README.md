# Neumorphism UI with Jetpack Compose
This library is an experimentation with [Neumorphism UI](https://uxdesign.cc/neumorphism-in-user-interfaces-b47cef3bf3a6) (New + Skeuomorphism) on Android with **Jetpack Compose**.  

[![](https://img.shields.io/badge/mavencentral-1.0.0--alpha-brightgreen)]() ![List of Awesome List Badge](https://cdn.rawgit.com/sindresorhus/awesome/d7305f38d29fed78fa85652e3a63e154dd8e8829/media/badge.svg) [![Awesome Kotlin Badge](https://kotlin.link/awesome-kotlin.svg)](https://github.com/KotlinBy/awesome-kotlin)


<p align="center">
<img src="https://github.com/CuriousNikhil/neumorphic-compose/blob/main/static/complete_screen.png?raw=true" height=400>
</p>

## How to use?

### 1. Add the dependency

And your app level `build.gradle`. (Make sure you have compose dependencies as well)

```kotlin
  implementation("me.nikhilchaudhari:composeNeumorphism:1.0.0-alpha")
```

### 2. Use

Just add `modifier = Modifier.neumorphic(context)` to any of your UI element of Jetpack-Compose (just like you do for other modifiers).

![image](https://user-images.githubusercontent.com/16976114/120097042-12adfe00-c14c-11eb-861c-a826108194d6.png)


## How do I configure shape, color, etc.?

### 1. Shapes  
You've three basic **Neu** shapes available -   
1. `Punched`,  2. `Pressed`, 3. `Pot` 

These are not standard names for Neumorphism UI, I just named them based on how they look (at least to me).

And you can also configure the corner families of those shapes as **`Oval`** or **`Rounded`** 

![image](https://user-images.githubusercontent.com/16976114/120097777-03c94a80-c150-11eb-847b-3a9547f3a56d.png)

```kotlin
Card(
  backgroundColor = Color(236, 234, 235),
  shape = RoundedCornerShape(12.dp),
  modifier = Modifier
      .padding(16.dp)
      .size(300.dp, 100.dp)
      .neumorphic(
        context,
        neuShape = 
          // Punched shape
         Punched.Rounded(radius = 8.dp) 
         Punched.Oval()

         // Presssed Shape
         Pressed.Rounded(radius = 4.dp)
         Presssed.Oval()

        // Pot shape
        Pot.Rounded(radius = 8.dp)
        Pot.Oval()
      )
    ){/*card content */} 
```

For `Rounded` corner-family, you can configure the radius of how much rounded corner you want. The default Neu shape is `Punched.Rounded(radius = 12.dp)`.  
The `Punched` shape would work with Cards, Buttons, etc.  
You should use `Modifier.clip()` while using `Pressed` shape, because there's known issue of shadow placeents for Pressed shape. Check the sample app for more code snippets.

P.S: Even I'm yet to experiment it with all UI elements.

### 2. Shadows

The idea of Neumorphism UI is simple, just create two shadows light and dark and draw those around the UI component from top-left and bottom-right corners.  
**Just make sure you are using the same colors for your Surface and for the UI elements.**
You can configure the shadow `insets`, `color` and `elevation`

Params              | Description
------------------- | -------------
`lightShadowColor`  | Default value - `Color.White` - Set the light shadow color you want  
`darkShadowColor`   | Default value - `Color.Gray` - Set the dark shadow color you want.  
`elevation`         | Default value - `6.dp` - Set the elevation for the shadow. 
`neuInsets`         | Default values - `NeuInsets(6.dp, 6.dp)` - Insets = Horizontal, Vertical - Placements of your shadows i.e How do you want to place your                           | shadows vertically and horizontally. 
`strokeWidth`       | Default value -  `6.dp` - Stroke width of the internal shadows. Stroke width is for only `Pressed` and `Punched`.


 ```kotlin
   Button(
        onClick = { /*TODO*/ },
        modifier = Modifier
            .padding(12.dp)
            .neumorphic(
                context,
                // assing neuShape
                //...
                lightShadowColor = Color.White,
                darkShadowColor = Color.Gray,
                elevation = 4.dp,
                strokeWidth: Dp = 6.dp,
                neuInsets = NeuInsets(6.dp, 6.dp)
            )
    ) 
```

The overall configutation
![image](https://user-images.githubusercontent.com/16976114/120100461-780aea80-c15e-11eb-9626-10b684f9c982.png)


## ToDos 
 - [ ] Add `LightSource` option and place the shadows accordingly (It is, by default, top-left right now)
 - [ ] Migrate from RenderScript
 - [ ] Fix clipping and shadow positioning with insets
 - [ ] Update sample app and code with Jetpack-Compose guidelines

## FAQs
### What's the idea? Is there any performance overhead?
Idea is simple - just draw the two shadows light and dark around the UI element. I'm using [RenderScript](https://developer.android.com/guide/topics/renderscript/compute) to blur two GradientDrawables (that's why the `context` parameter). I know it's going to be deprecated in the Android 12. I'll surely update the code to migrate from RenderScript. If Renderscript throws any exception / not able to blur the drawable, I'm using [StackBlur](https://github.com/CuriousNikhil/neumorphic-compose/blob/main/library/src/main/java/me/nikhilchaudhari/library/internal/StackBlur.kt) algorithm from Mario Klingemann as a workaround.

### Does this library work with all the Jetpack Compose UI elements? 
I can't surely say. I'm already experimenting drawing shadow on UIs with Jetpack Compose and improving this library. I request you to try and please help to make it work.

### When can I use this?
You can use this right now if you want to play with shiny Neumorphism UI. The library is in alpha and there are some known issues regarding shadow rendering and clipping. Please raise issue if you found any.

## Contribution Guide
Please contribute! I'm just getting my hands dirty with Jetpack-Compose.  
There is heavy chance that the code may/may not be correct/holding best practices. I request you to contribute/ raise issues/ send PRs so I can learn too.
You can use the Github **Discussion** to discuss and ask questions. Or you can reach out to me on Twitter [@CuriousNikhil](https://twitter.com/curiousnikhil). 
 - Current release branch - `release/1.0.0-alpha02`

## License
Licensed under Apache License, Version 2.0 [here](https://github.com/CuriousNikhil/neumorphic-compose/blob/main/LICENSE)
