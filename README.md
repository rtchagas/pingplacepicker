# PING - Because Ping Is Not Google's Place Picker 😉
[![](https://jitpack.io/v/rtchagas/pingplacepicker.svg)](https://jitpack.io/#rtchagas/pingplacepicker) [![](https://img.shields.io/badge/MinSDK-19-blue)](#)

 
If you're here looking for a place picker you have probably read this:

![Google Place Picker was deprecated](https://github.com/rtchagas/pingplacepicker/blob/master/images/google_picker_deprecated.png?raw=true)

As of the end of January 2019, Google deprecated the so useful Place Picker bundled in the Places SDK for Android.
The main reason was due the new pricing model of the [Places API](https://developers.google.com/places/android-sdk/usage-and-billing).

**PING** Place Picker is here to help you to (almost) plug-and-play replace the original Google's Place Picker.

<img src="images/screenshot_1.jpg" alt="Map expanded" width="210"/> <img src="images/screenshot_4.jpg" alt="Place selected" width="210"/> <img src="images/screenshot_2.jpg" alt="Results expanded" width="210"/> <img src="images/screenshot_3.jpg" alt="Search result" width="210"/>

## A key difference

Different than Google's Place Picker, PING by default **doesn't** search for places according to where the user is pointing the map to. Instead, it shows only the nearby places in the **current** location.

This was intentional and the reason is simple. By using the **/nearbysearch** from [Google Places Web API](https://developers.google.com/places/web-service/search#PlaceSearchRequests) we are going to be charged *a lot* for each map movement.

![NearbySearch warning](https://github.com/rtchagas/pingplacepicker/blob/master/images/nearby_search_warning.png?raw=true)

According to [Nearby Search pricing](https://developers.google.com/maps/billing/understanding-cost-of-use#nearby-search) each request to the API is going to cost 0.04 USD per each (40.00 USD per 1000).

To avoid the extra cost of **/nearbysearch**, PING relies on Place API's **findCurrentPlace()** that is going to cost 0.030 USD per each  (30.00 USD per 1000).

Moreover, we don't fire a new request when the user moves the map.

## Enabling nearby searches

If you do want to fetch places from a custom location or refresh them when the user moves the map, you must enable /nearbysearch queries in PING.

To do that, enable this flag in your project:
```xml  
 <bool name="enable_nearby_search">true</bool>
```

By doing so, PING behaviour will be slightly changed:
- All places will be fetched by /nearbysearch queries.
- You get a button to refresh the places for the current location.
- You can set the initial map position to get the places from via `pingBuilder.setLatLng(LatLng)`

## Why use PING?

PING is based entirely on Google Places and MAPs APIs. Google has the biggest places database available to us developers with most up to date and curated places information.

It is worth to notice that Google provides US$ 200 (free) per month to be used with Places API. This should be more than enough for small applications that rely on Places data.

## Download

Add Jitpack in your root build.gradle at the end of repositories:

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
  
Step 2. Add the dependency

	dependencies {
	        // Places library  
            implementation 'com.google.android.libraries.places:places:2.0.0'
	        // PING Place Picker
	        implementation 'com.github.rtchagas:pingplacepicker:1.1.+'
	}

## Setup

 1. Add Google Play Services to your project - [How to](https://developers.google.com/android/guides/setup)
 2. Sign up for API keys - [How to](https://developers.google.com/places/android-sdk/signup)
 3. Add the Android API key to your **AndroidManifest** file as in the [sample project](https://github.com/rtchagas/pingplacepicker/blob/master/sample/src/main/AndroidManifest.xml#L15).
 4. Optional but strongly recommended to enable R8 in you *[gradle.properties](https://github.com/rtchagas/pingplacepicker/blob/master/gradle.properties#L12)* file

## Hands on

Check the [sample](https://github.com/rtchagas/pingplacepicker/tree/master/sample) project for a full working example.

### - Kotlin
```kotlin
    private fun showPlacePicker() {  
        val builder = PingPlacePicker.IntentBuilder()
	builder.setAndroidApiKey("YOUR_ANDROID_API_KEY")  
        	.setMapsApiKey("YOUR_MAPS_API_KEY")
	
	// If you want to set a initial location rather then the current device location.
	// NOTE: enable_nearby_search MUST be true.
        // builder.setLatLng(LatLng(37.4219999, -122.0862462))
	
        try {
            val placeIntent = pingBuilder.build(this)
            startActivityForResult(placeIntent, REQUEST_PLACE_PICKER)
        }
        catch (ex: Exception) {  
            toast("Google Play Services is not Available")  
        }
    }
    
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {  
	super.onActivityResult(requestCode, resultCode, data)  
	if ((requestCode == REQUEST_PLACE_PICKER) && (resultCode == Activity.RESULT_OK)) {  
	    val place: Place? = PingPlacePicker.getPlace(data!!)  
	    toast("You selected: ${place?.name}")  
	}  
    }
```

### - Java
```java
    private void showPlacePicker() {
	PingPlacePicker.IntentBuilder builder = new PingPlacePicker.IntentBuilder();
	builder.setAndroidApiKey("YOUR_ANDROID_API_KEY")
	       .setMapsApiKey("YOUR_MAPS_API_KEY");
	
	// If you want to set a initial location rather then the current device location.
	// NOTE: enable_nearby_search MUST be true.
        // builder.setLatLng(new LatLng(37.4219999, -122.0862462))
	
	try {
	    Intent placeIntent = builder.build(getActivity());  
	    startActivityForResult(placeIntent, REQUEST_PLACE_PICKER);  
	}  
	catch (Exception ex) {  
	    // Google Play services is not available... 
	}
    }
    
    @Override  
    public void onActivityResult(int requestCode, int resultCode, Intent data) {  
        if ((requestCode == REQUEST_PLACE_PICKER) && (resultCode == RESULT_OK)) {  
            Place place = PingPlacePicker.getPlace(data);  
	    if (place != null) {  
                Toast.makeText(this, "You selected the place: " + place.getName(), Toast.LENGTH_SHORT).show();
            }  
        }
    }
```

## API Keys

PING needs two API keys in order to work.

It was decided to split the API keys to clearly distinguish what you're going to be charged for. Also, the Places Web API and the Geocoding API don't allow an Android API key to be used. To not expose an unrestricted key for all APIs, the Maps API key is now required.

| Key | Restriction | Purpose
|--|--|--|
| Android key | [Android Applications](https://developers.google.com/places/android-sdk/signup#restrict-key) | Used as the Places API key. Main purpose is to retrieve the current places and place details.
| Maps key | [APIs: Geocoding, Maps Static and Places API only](https://cloud.google.com/docs/authentication/api-keys#api_key_restrictions) | Used to fetch static maps, nearby places through Places Web API and perform reverse geocoding on the current user position. That is, discover the address that the user is current pointing to. Your key should look [like this](https://raw.githubusercontent.com/rtchagas/pingplacepicker/master/images/maps_api_key.png).

**TIP:** It is strongly recommended to **not expose** your Maps API key in your resource files. Anyone could decompile your apk and have access to that key. To avoid this, the key should be at least obfuscated.
A nice approach is to save the key in the cloud through "Firebase remote config" and fetch it at runtime.

## Configuration

As some features are charged by Google, you can alter the default **PING** Place Picker behaviour by overriding below resources:

```xml  
<!-- 0.002 USD per each (2.00 USD per 1000) -->  
<bool name="show_confirmation_photo">true</bool>  

<!-- 0.007 USD per each (7.00 USD per 1000) -->  
<bool name="show_confirmation_map">true</bool>

<!-- If true, the map will automatically center (pan) to
     the selected marker -->
<bool name="auto_center_on_marker_click">false</bool>
```

## Contribute

Let's together make PING awesome!

Please feel free to contribute with improvements.

<!--stackedit_data:
eyJoaXN0b3J5IjpbLTE2OTEwOTMwMTIsNjk1MDQ1MzY0LDE4NT
I5NDk5MDUsMjAwMTQ0MzM5OSwyODIyMTI1MjFdfQ==
-->
