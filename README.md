# Activity Monitoring App

## Problem Statement

Develop a comprehensive sensor and activity monitoring system for child Android devices, aiming to provide real-time insights into user behavior, app usage, and environmental context. Utilize device sensors, power management, usage statistics, battery information, location data, network connectivity, and additional data collection to capture diverse user interactions. Additionally, implement data attributes for accessibility events to enhance data granularity.

  ```
  Project Timeline:
  The project is set to be completed within a two-week timeframe, starting from Feb 13th, 2024 to 27th, 2024.
  ```

## Overview
The Advanced Sensor and Activity Monitoring System is a cutting-edge solution designed to provide real-time insights into various aspects of device behavior and user interactions on Android devices. By leveraging advanced sensor technologies, comprehensive backend APIs, and intuitive frontend visualization, the system empowers users to gain actionable insights and optimize their device usage patterns. This documentation offers an extensive overview of both the Android application and the backend system, covering functionalities, implementation details, deployment processes, and integration methods.

(Flow diagram for the complete architecture)

![WhatsApp Image 2024-02-27 at 21 35 12_91f09d82](https://github.com/purulokendrasingh/activity-monitoring-app/assets/29207426/6f9be043-c9c7-4728-8889-0fc2dbd734eb)


## Android App

### Features
- **Sensor Data Retrieval**: The app captures real-time data changes from a wide range of sensors including accelerometer, gyroscope, proximity, and magnetic field sensors, providing granular insights into device orientation, motion, and environmental conditions.
- **Battery Usage Monitoring**: Comprehensive monitoring of battery-related metrics such as status, voltage, temperature, and charging state enables users to optimize battery usage and identify potential issues.
- **Location Data Tracking**: The app records device location changes using GPS coordinates, facilitating location-based services and context-aware applications.
- **Network Connectivity Monitoring**: Continuous monitoring of network connectivity status including WiFi connection, mobile data availability, and network availability ensures uninterrupted connectivity for users.
- **Device State Monitoring**: The app detects and logs device state changes such as screen on/off events, providing valuable insights into device usage patterns and power management.
- **Usage Statistics Tracking**: Detailed tracking of app usage, screen-on time, and other usage patterns helps users understand their digital habits and optimize productivity.
- **Additional Data Collection**: Capturing various user interactions and activities such as screen unlocks, button presses, touch interactions, and hardware key usage enhances the depth of data collected for analysis.
- **Data Attributes for Accessibility Events**: Implementation of data attributes for accessibility events enriches the collected data with additional context including timestamp, package name, event type, node class type, and more.

### Implementation Details
- The app employs background services to continuously monitor sensor data and device state changes, ensuring seamless data collection even when the app is not in the foreground.
- Data collected by the app is securely transmitted to corresponding backend APIs via REST requests for storage, processing, and analysis.
- A foreground service with a persistent notification ensures that the app and background services remain active and operational, providing users with uninterrupted monitoring capabilities.

(The folder structure for the app code)

![image](https://github.com/purulokendrasingh/activity-monitoring-app/assets/29207426/361f8d72-7274-483b-8857-536f53b51343)


### Testing
- Rigorous testing has been conducted on the app, specifically targeting compatibility and functionality on Android version 11 (version R) using a Pixel 3 phone emulator.

(The app screen just shows the Device ID which needs to be noted down and used while querying the frontend dashboard for activity logs)

![app-snip-2](https://github.com/purulokendrasingh/activity-monitoring-app/assets/29207426/f9c4dac0-ccb7-4c15-b3b0-a740785f43ca)

(The app runs a foreground service which ensure the app runs even after the user has closed it by showing notification banner)

![app-snip-3](https://github.com/purulokendrasingh/activity-monitoring-app/assets/29207426/459019a0-798e-4f88-b9b9-f02593aed6d9)
  

## Web App

### Architecture Overview
- The backend system comprises a Flask web server hosting REST APIs for performing CRUD operations on sensor data, battery usage metrics, location data, network connectivity status, device state information, usage statistics, and additional user interactions.
- Separate controller files have been implemented for each service to handle request routing and data processing logic, ensuring modularity and maintainability.
- Azure Cosmos DB serves as the primary data storage solution, with separate containers partitioned based on the device ID for efficient data organization and retrieval.
- Azure App Configuration is utilized to manage sensitive data and access keys, ensuring secure access to backend resources.

(Used POSTMAN to test the backend APIs)

![image](https://github.com/purulokendrasingh/activity-monitoring-app/assets/29207426/6c95d526-ec41-4045-85a3-3e45136c2f83)

(The folder structure for the Flask App)

![image](https://github.com/purulokendrasingh/activity-monitoring-app/assets/29207426/f4e8a0b7-ff10-4ff4-b033-9e14d7ede27a)

(The CosmosDB and service specific containers)

![image](https://github.com/purulokendrasingh/activity-monitoring-app/assets/29207426/c3fcddcb-a584-4c71-981a-233b0830a708)

(Azure App Configuration to ensure sensitive access keys and information)

![image](https://github.com/purulokendrasingh/activity-monitoring-app/assets/29207426/94b5107d-abbb-42f0-ac4e-876c50e37d0c)



### Deployment Process
- Docker containers are employed for seamless deployment on Azure App Service, providing scalability and portability across different environments.
- Docker images are pushed to Azure Container Registry, and containers are provisioned on Azure App Service for reliable and scalable deployment.
- Continuous integration and continuous deployment (CI/CD) pipelines may be implemented to automate the deployment process and streamline development workflows.

(Docker images on the Docker-Hub)

![image](https://github.com/purulokendrasingh/activity-monitoring-app/assets/29207426/f30b0459-ee8e-4808-9dfe-74d78077319c)


### Frontend Integration
- The frontend dashboard, built using React, offers an intuitive user interface for visualizing collected data.
- Separate routes are available for each service, facilitating easy navigation and access to specific data sets.
- Search functionality enables users to input device IDs and retrieve corresponding data using the fetch_records API, with pagination implemented to optimize data retrieval and display.

(Frontend React App folder structure)

![image](https://github.com/purulokendrasingh/activity-monitoring-app/assets/29207426/1e14f83e-bf15-4b30-9103-2b85fc3d9269)

(Dashboard View)

![image](https://github.com/purulokendrasingh/activity-monitoring-app/assets/29207426/4f02b442-3e16-4018-ba86-5205628d04c9)

(Sensor Data View for a specific Device ID)

![image](https://github.com/purulokendrasingh/activity-monitoring-app/assets/29207426/ef71d2cd-b4f1-46f7-a236-f4e92c67e25a)

(Location Data View for a specific Device ID)

![image](https://github.com/purulokendrasingh/activity-monitoring-app/assets/29207426/9fb95b69-532c-4c51-b2dc-bce074517140)

(Connectivity Data View for a specific Device ID)

![image](https://github.com/purulokendrasingh/activity-monitoring-app/assets/29207426/96f88d76-bbd4-472d-a506-01dfad2d6206)

(Battery Usage View for a specific Device ID)

![image](https://github.com/purulokendrasingh/activity-monitoring-app/assets/29207426/aded23dc-b009-4a50-9d3b-81f848df2412)

