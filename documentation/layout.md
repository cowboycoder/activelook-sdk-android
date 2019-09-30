# Layout System

To simplify the various low-level drawing procedures the SDK exposes a high-level layout system. The layout system can be instantiated directly in code but is usually defined in a JSON file.

This document describes the various layout components and their JSON attributes. 

## Components

### `Screen`

A `Screen` is the root JSON object and defines a padding, foreground and background colors, and an updatable text. It can also contain various static `Widget` objects and an array of bitmaps that can be shown or hidden dynamically.

**Attributes**

- `version`: This is the version of the layout system. 1 is the only valid value. Reserved for future use
- `id`: The screen number. Used to refer to the screen for modification operations. Valid values are 1 - 50.
- `padding`: An object specifying the padded bounds of the `Screen`
    - `left`
    - `top`
    - `right`
    - `bottom`
- `textOrigin`: The origin point for the updatable text.
- `textOrientation`: The display orientation for the updatable text. See the documentation on orientation for acceptable values
- `foregroundColor`: The grayscale level of the foreground color (0 - 15)
- `backgroundColor`: The grayscale level of the background color (0 - 15) 
- `font`: The font size of the updatable text. See the documentation on fonts for more information.
    - `SMALL`
    - `MEDIUM`
    - `LARGE`
- `widgets`: A JSONArray of `Widget` objects. See the `Widget` section for more information

**JSON Example**

```json
{
  "version": 1,
  "id": 1,
  "padding": {
    "left": 0,
    "top": 25,
    "right": 0,
    "bottom": 0
  },
  "textOrigin": {
    "x": 200,
    "y": 200
  },
  "textOrientation": "R4",
  "foregroundColor": 15,
  "backgroundColor": 0,
  "font": "MEDIUM",
  "widgets": [
    {}
  ]
}
```

### `Widget`

A `Widget` is a static element inside of a `Screen`. `Widget` objects represent low-level shapes, text, and images. All widgets have a corresponding `type` value that must be specified in JSON

#### `PointWidget`

Draw a point

**Attributes**

- `type`: `point`
- `position`: A JSON object representing the x and y values on the display
- `color`: The color of the point (#RRGGBB hexidecimal format).

**JSON Example**

```json
{
  "type": "point",
  "position": {
    "x": 150,
    "y": 150
  },
  "color": "#FFFFFF"
}
```

#### `LineWidget`

Draw a line

**Attributes**

- `type`: `line`
- `start`: The starting point of the line
- `end`: The ending point of the line

**JSON Example**

```json
{
  "type": "line",
  "start": {
    "x": 160,
    "y": 160
  },
  "end": {
    "x": 50,
    "y": 170
  },
  "color": "#FFFFFF"
}
```

#### `RectangleWidget`

Draw a rectangle

**Attributes**

- `type`: `rectangle`
- `position`: the origin (x,y)
- `height`
- `width`
- `color`: The color as #RRGGBB hexadecimal string
- `style`: The fill style.
    - `filled`
    - `outline`

**JSON Example**

```json
{
  "type": "rectangle",
  "position": {
    "x": 250,
    "y": 250
  },
  "height": 12,
  "width": 12,
  "color": "#FFFFFF",
  "style": "filled"
}
```

#### `CircleWidget`

Draw a circle

**Attributes**

- `type`: `circle`
- `position`: the center coordinates (x,y) 
- `radius`
- `color`: circle color (#RRGGBB hex string)
- `style`: The fill style
    - `filled`
    - `outline`

**JSON Example**

```json
{
  "type": "circle",
  "position": {
    "x": 200,
    "y": 200
  },
  "radius": 20,
  "color": "#FFFFFF",
  "style": "filled"
}
```

#### `TextWidget`

Draw static text

**Attributes**

- `type`: `text`
- `position`: the origin (x,y)
- `color`: Text color (#RRGGBB hex string)
- `font`: The font size
- `value`: The text string value (ASCII)

**JSON Example**

```json
{
  "type": "text",
  "position": {
    "x": 200,
    "y": 250
  },
  "color": "#FFFFFF",
  "font": "SMALL",
  "value": "Test Static"
}
```

## Usage

In an `Activity` with the screen file `screen1.json1` in the `assets` folder:

```kotlin
val jsonStr = assets.open("screen1.json").bufferedReader().use(BufferedReader::readText)
val screen = Screen.Builder(jsonStr)

// Send the new screen to the device
sdkInstance.enqueueOperation(ActiveLookOperation.AddScreen(screen, contentResolver))

// Display the new screen
sdkInstance.enqueueOperation(ActiveLookOperation.ClearScreen)
sdkInstance.enqueueOperation(ActiveLookOperation.DisplayScreen(screen.id, "Start text"))

// Update the dynamic text
sdkInstance.enqueueOperation(ActiveLookOperation.ClearScreen)
sdkInstance.enqueueOperation(ActiveLookOperation.DisplayScreen(screen.id, "New text"))
```