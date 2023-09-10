<br/>
<p align="center">
  <a href="https://github.com/selcannarin/SchoolBusTracker-Driver">
    <img src="https://github.com/selcannarin/SchoolBusTracker-Driver/assets/72921635/b90699e2-3041-41af-b00f-f13bbb907224" alt="Logo" width="100" height="100">
  </a>

  <h3 align="center">School Bus Tracker - Driver</h3>

  <p align="center">
    It is the application of the project called School Bus Tracker for drivers using student buses. It cooperates with the <a href="https://github.com/selcannarin/SchoolBusTracker-Parent">School Bus Tracker - Parent</a> application, provides communication between parents and drivers.
    <br/>
    <br/>
    <a href="https://github.com/selcannarin/SchoolBusTracker-Driver"><strong>Explore the docs »</strong></a>
    <br/>
    <br/>
  </p>
</p>

![Downloads](https://img.shields.io/github/downloads/selcannarin/SchoolBusTracker-Driver/total) ![Contributors](https://img.shields.io/github/contributors/selcannarin/SchoolBusTracker-Driver?color=dark-green) ![Stargazers](https://img.shields.io/github/stars/selcannarin/SchoolBusTracker-Driver?style=social) 

## Table Of Contents

* [About the Project](#about-the-project)
* [Built With](#built-with)
* [Usage](#usage)
* [Getting Started](#getting-started)
  * [Prerequisites](#prerequisites)
  * [Installation](#installation)

## About The Project

This application, aimed at drivers using student buses, is designed to optimize student transportation processes, increase safety and provide better communication with parents by working in collaboration with the <a href="https://github.com/selcannarin/SchoolBusTracker-Parent">School Bus Tracker - Parent</a> application.

 <h4><strong>Main Features of the Project: </strong></h4>

* <strong>Student Tracking and Directions:</strong> Allows drivers to instantly view students' locations and get directions via Google Maps. In this way, drivers can transport students to school or home via the fastest and safest route.

* <strong>Effective Communication with Parents:</strong> Provides powerful communication between drivers and parents with automatic notifications, so parents can be sure that their children are safe.

* <strong>Student Information Management:</strong> Drivers can add or correct the student information they carry, so itinerary changes and new student additions can be made easily.

* <strong>Profile Management:</strong> Drivers can easily create and update their profile information, increasing the customizability of the project.

## Built With

Android: Clean Architecture, MVVM

Dagger Hilt (Dependency Injection)

Google Play Services (Location, Maps)

Firebase (Firestore, Authentication, Storage, Cloud Messaging, InAppMessaging)

Coroutines (Kotlin Asynchronous Programming)

Retrofit, OkHttp (For Firebase messaging response.)

Glide (Image Loading)

GIF Support

Android Navigation Components

## Usage

![AUTHENTICATION](https://github.com/selcannarin/SchoolBusTracker-Driver/assets/72921635/01f058ae-e908-47fa-aba3-0f498e98754f)
![UI](https://github.com/selcannarin/SchoolBusTracker-Driver/assets/72921635/28d9e4ee-1ec4-4814-a5d7-6743bb22923d)


## Getting Started

To get a local copy up and running follow these simple example steps.

### Prerequisites

You can get a free Google Maps API key with the help of this [document](https://developers.google.com/maps/documentation/android-sdk/get-api-key).

### Installation

1. [Get a free Map API Key](https://console.cloud.google.com/project/_/google/maps-apis/credentials?utm_source=Docs_CreateAPIKey&utm_content=Docs_maps-android-backend&_gl=1*1gil097*_ga*MTA2MzY5OTU2LjE2ODEzNDA0Mzc.*_ga_NRWSTWS78N*MTY5NDM3MTg3MS4xMS4xLjE2OTQzNzE5OTIuMC4wLjA.) 

2. Clone the repo

```sh
git clone https://github.com/selcannarin/SchoolBusTracker-Driver.git
```

3. Enter your API in `strings.xml`

```JS
 <string name="google_api_key">YOUR API KEY</string>
```
