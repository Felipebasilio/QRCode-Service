# QR Code Service

## Overview
QR Code Service is a Spring Boot application that generates QR codes based on user input. It provides a REST API that allows clients to generate QR codes with configurable size, format, and error correction levels.

## Features
- Generate QR codes from user-defined content
- Configurable image size (150x150 to 350x350 pixels)
- Support for PNG, JPEG, and GIF formats
- Customizable error correction levels (L, M, Q, H)
- Proper error handling for invalid parameters

## Technologies Used
- **Java 21**
- **Spring Boot 3**
- **ZXing (Zebra Crossing) Library** for QR code generation
- **Gradle** for dependency management

## Installation

### Prerequisites
- Java 21
- Gradle
- IntelliJ IDEA (recommended)

### Clone the Repository
```sh
git clone https://github.com/your-repo/qrcode-service.git
cd qrcode-service
```

### Build and Run the Application
```sh
./gradlew bootRun
```
The application will start on **port 8080** by default.

## API Endpoints

### Health Check
**GET** `/api/health`

- **Response:** `200 OK`
- **Body:** `"OK"`

### Generate QR Code
**GET** `/api/qrcode`

#### Request Parameters
| Parameter    | Type   | Required | Description |
|-------------|--------|----------|-------------|
| `contents`  | String | Yes      | The text to encode in the QR code. Must not be empty or blank. |
| `size`      | int    | No       | The size of the QR code in pixels (150 to 350). Default: 250. |
| `type`      | String | No       | The format of the generated image (`png`, `jpeg`, `gif`). Default: `png`. |
| `correction`| String | No       | The error correction level (`L`, `M`, `Q`, `H`). Default: `L`. |

#### Example Requests

1. **Valid request:**
   ```sh
   curl -X GET "http://localhost:8080/api/qrcode?contents=HelloWorld&size=250&type=png&correction=M" --output qrcode.png
   ```
   **Response:** `200 OK`, returns a PNG image of the QR code.

2. **Invalid size parameter:**
   ```sh
   curl -X GET "http://localhost:8080/api/qrcode?contents=HelloWorld&size=100&type=png"
   ```
   **Response:** `400 BAD REQUEST`
   ```json
   {"error": "Image size must be between 150 and 350 pixels"}
   ```

3. **Invalid correction level:**
   ```sh
   curl -X GET "http://localhost:8080/api/qrcode?contents=HelloWorld&correction=S"
   ```
   **Response:** `400 BAD REQUEST`
   ```json
   {"error": "Permitted error correction levels are L, M, Q, H"}
   ```

## Project Structure
```
qrcode-service/
├── src/
│   ├── main/
│   │   ├── java/qrcodeapi/
│   │   │   ├── Application.java  # Main Spring Boot application
│   │   │   ├── controller/QRCodeController.java  # Handles API requests
│   │   ├── resources/
│   │   │   ├── application.properties  # Configuration file
├── build.gradle  # Dependencies and build configuration
├── README.md  # Project documentation
```

## Error Handling
If a client sends an invalid request, the service returns a `400 BAD REQUEST` with an appropriate JSON error message. The priority order for handling multiple invalid parameters is:
1. **Invalid contents**
2. **Invalid size**
3. **Invalid correction**
4. **Invalid type**

## Dependencies
The project uses ZXing to generate QR codes. Ensure the following dependencies are in `build.gradle`:
```gradle
dependencies {
    implementation 'com.google.zxing:core:3.5.2'
    implementation 'com.google.zxing:javase:3.5.2'
}
```

## License
This project is licensed under the MIT License.

## Author - Felipe Basilio
Developed as part of the **Hyperskill QR Code Service** project.
