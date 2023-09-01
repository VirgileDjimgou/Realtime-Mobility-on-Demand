
The purpose of this project is to specify software requirements of the Real-time and Autonome Online Moto-Taxi / Taxi Booking in Cameroon (Central Africa ) or in specificaly in regions where urban transport is not properly structured or is done mainly with motorcycle taxi (Sud-Asia, Africa or Sud-America).

this Solution is an on-demand taxi booking software that helps expand business reach by providing taxi services to customers anytime. Any services like taxi booking, carpooling, taxi renting, car sharing, etc., are all managed within the platform.thiss software is a ride-hailing app solution that allows a company to launch its taxi-booking support online. It supports customers to find rides at their convenience and helps the drivers earn by completing the trips that users have requested.

this platform is built with native Android with the required functions and latest features for riders convenience. Prospering the freakishly growing taxi booking industry becomes super easy. The solution aims to connect the business with the customers seamlessly. 


### . This Solution includes passenger app, driver app and web admin dashboard.

### Driver App
![layer_1](https://github.com/chichikolon/Bee_Realtime_Vehicle_Booking_System/blob/master/Ressource/markdown/Untitled%20collage-1.png)
![layer_2](https://github.com/chichikolon/Bee_Realtime_Vehicle_Booking_System/blob/master/Ressource/markdown/Untitled%20collage-2.png)
![layer_3](https://github.com/chichikolon/Bee_Realtime_Vehicle_Booking_System/blob/master/Ressource/markdown/Untitled%20collage-4.png)
![layer_4](https://github.com/chichikolon/Bee_Realtime_Vehicle_Booking_System/blob/master/Ressource/markdown/Untitled%20collage-5.png)
![layer_5](https://github.com/chichikolon/Bee_Realtime_Vehicle_Booking_System/blob/master/Ressource/markdown/Untitled%20collage-6.png)


### Passenger App
![layer_6](https://github.com/chichikolon/Bee_Realtime_Vehicle_Booking_System/blob/master/Ressource/markdown/Untitled%20collage.png)
![layer_7](https://github.com/chichikolon/Bee_Realtime_Vehicle_Booking_System/blob/master/Ressource/markdown/Untitled%20collage-8.png)
![layer_8](https://github.com/chichikolon/Bee_Realtime_Vehicle_Booking_System/blob/master/Ressource/markdown/Untitled%20collage-9.png)


### Web Admin and Visualisation
![download 2](https://github.com/chichikolon/Bee_Realtime_Vehicle_Booking_System/blob/master/Ressource/markdown/WebAdmin/Screenshot%20from%202018-02-05%2006-22-37.png)
![download 1](https://github.com/chichikolon/Bee_Realtime_Vehicle_Booking_System/blob/master/Ressource/markdown/WebAdmin/Screenshot%20from%202018-02-05%2006-22-50.png)
![download](https://github.com/chichikolon/Bee_Realtime_Vehicle_Booking_System/blob/master/Ressource/markdown/WebAdmin/Screenshot%20from%202018-02-05%2005-40-33.png)


# Cloud Messaging Panel and Event Big Query 
![download](https://github.com/chichikolon/Bee_Realtime_Vehicle_Booking_System/blob/master/Ressource/markdown/WebAdmin/Screenshot%20from%202018-02-05%2006-26-22.png)
![download](https://github.com/chichikolon/Bee_Realtime_Vehicle_Booking_System/blob/master/Ressource/markdown/WebAdmin/Screenshot%20from%202018-02-05%2006-27-36.png)

# Crashlytics Testlab 
![download](https://github.com/chichikolon/Bee_Realtime_Vehicle_Booking_System/blob/master/Ressource/markdown/WebAdmin/Screenshot%20from%202018-02-05%2006-32-13.png)



## How It Works

### Start The App

For This Project, I created Two Different Apps. The Client App and The Driver App. After creating An Account, Adding A Phone Number, Your Location Is Automatically detected via the google maps geolocation, But You Could Point The Marker Elsewhere To Choose Another Location.

### Request A Taxi

When A User Books A ride, Then User's Details Are Sent To The Database With The Notification Id Of The First Driver Available, Then After 60 seconds And No reply, Then it is shifted To Another driver.

### Get Accepted By A Driver

Once You Get A Driver Then Your Phone Vibrates And The Credentials Of The Driver Appears on Your Screen, As well as The Distance Between You And Your Driver Are Displayed As well.

### Get To Your Destination And Pay Up

On Completion Of the ride your payment reciept is shown to you, then you can check out and rate your ride and then, you are ready for another booking.


## Features in Customer(client) App

* Plotting of places to google map with distance and minutes calculation between routes.
* Integration with Stripe Payment gateway. Option to set cab types with rates from backend Firebase . 
* Stylish animation between views with Facebook style slide menu. Display all booking with scroll to load and clean UI.
* Enable Auto refresh when driver accept job on driver arrival, journey begin , journey completed / dropped etc. Support Push messages for all the status as well. 
* Live tracking Driver who are assigned for your booking.
* Intro splash screen to give appealing look to the app. 
* Google api integration for autocomplete. 
* Option to Cancel the Job till driver is not assigned. Rate card screen. 
* User can rate driver after completion of Ride , ALSO driver can rate user as well. 100% Java native Project.

## Features in Driver App Feature 

* 3 Step registration form with all required field capture and validation done to make app ready to go live .. 
* Facebook style sliding menu. 
* Option to set status available and unavailable. 
* Support Firebase Realtime for tracking Driver. 
* Support FCM notification Interface when new booking is arrived and Phone will start beeping . 
* Backend in Compute Engine to  Auto assigning of Driver for Jobs on the basis of availability of Driver avaibality , 
* car type and nearest available driver using Robust Spatial GEO Queries. 
* Support Push messages to driver if app is in background.
* Calculating Tariff on the basis of Miles + Minutes require to travel.

## Features in Web admin: 

Clean and Easy to understand Dashboard which display all relevant statistics.

View Realtime driver and passenger activity on map. 
View all Driver’s .. View all User’s .. View Flagged Driver and Option to block them.. 
View Flagged User and Option to block users.. 
Option to view all users.. Set Prices for Car types .. 
Set Currencies .. And much more …


## Technology i used to create this Application.

  * [Firebase Realtime DB / Firestore / Cloud messaging](https://github.com/firebase/quickstart-android)
  * Google Maps SDK 
  * Paystack
  * geofire.
  * [Volley](https://github.com/google/volley)
  * [Picasso](https://github.com/square/picasso)
  * App Engine.
  * Compute Engine .
  * NodeJs
  * [Sweet Alert](https://github.com/pedant/sweet-alert-dialog)
  * [FlatUI](https://github.com/eluleci/FlatUI)
  

[![License: GPL v3](https://img.shields.io/badge/License-GPL%20v3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)


 Who made this
--------------

| <a href="https://github.com/VirgileDjimgou"><img src="https://avatars1.githubusercontent.com/u/8148300?s=400&v=4" alt="IoEngine" align="left" height="100" width="100" /></a>
|---
| [VirgileDjimgou](https://github.com/VirgileDjimgou)



Contribute
----------

1. Create an issue to discuss about your idea
2. [Fork it] (https://github.com/VirgileDjimgou/P125_RealtimeMoD.git)
3. Create your feature branch (`git checkout -b my-new-feature`)
4. Commit your changes (`git commit -am 'Add some feature'`)
5. Push to the branch (`git push origin my-new-feature`)
6. Create a new Pull Request
7. Profit! :white_check_mark:


