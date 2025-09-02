# Demo Application to generate Random String using the provided Conent Generator apk

A simple Android app built with **Jetpack Compose**, **Room**, **ViewModel**, and a **Content Provider** that allows users to generate random strings of a given length, store them with metadata, and manage (delete single or all) generated strings.  

---
### Application apk Link
[**Download App (APK)**] - (https://i.diawi.com/HaJfMb)

## Features mentionedd in Document and Implementation Status

- **IAV-1**: The user should be able to set the length of the string to be generated.- **Done**  
- **IAV-2**: After a button click (or similar action), the app queries the Content Provider for a random string with the specified length.- **Done**  
- **IAV-3**: Every generated string is shown to the user along with:- **Done**  
  - The generated string  
  - The specified length of the string  
  - The time and date when the string was created  
- **IAV-4**: Old strings remain visible even after generating a new one.- **Done**  
- **IAV-5**: User can delete **all** generated strings from the app.- **Done**  
- **IAV-6**: User can delete a **single generated string** from the app.- **Done**  
- **IAV-7**: Proper error handling is implemented (e.g., invalid input for length).- **Done**  
---

## How It Works

1. **Launch the App**  
   The main screen shows all previously generated strings (if any).  

2. **Generate a Random String**  
   - Tap the `+` floating action button.  
   - Enter the desired string length in the dialog box.  
   - Tap **Generate**.  
   - The generated string will be displayed in the list with its metadata.  

3. **Delete a Single String**  
   - Swipe an item **right to left** to delete it.  

4. **Delete All Strings**  
   - Tap the red **Delete All** button at the bottom of the screen.  

5. **Error Handling**  
   - If the entered length is invalid (e.g., empty or not a number), the app shows a Toast message.
   - Toast messages for other errors

## Tech Stack

- **UI**: Jetpack Compose (Material 3)  
- **State Management**: ViewModel + Kotlin Flows  
- **Persistence**: Room Database  
- **Data Sharing**: Content Provider  
- **Language**: Kotlin  

---

## Future Improvements can be done

- Improved error messages with Snackbar or Other Dialog based on Priority 

---
   ```bash
   git clone https://github.com/your-username/random-text-generator.git
