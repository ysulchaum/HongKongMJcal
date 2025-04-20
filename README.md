# HongKongMJcal

HongKongMJcal is an Android application designed to streamline the Hong Kong Mahjong experience by providing a score calculator, game tracking, and player management features. Built with Java and Android SDK, it supports players in calculating scores, recording game rounds, and managing game state, making it ideal for both casual and competitive Mahjong sessions.

## Table of Contents

- Features
- Screenshots
- Installation
- Usage
- Contributing
- License
- Authors

## Features

- **Score Calculator**: Calculates scores based on Hong Kong Mahjong rules, supporting fan values (0-10) and base values (32, 64, 128). Handles special hands like Thirteen Orphans, All Pungs, and Three Dragons.
- **Tile Input**: Allows players to input their hand via a tile selection interface for accurate fan calculation (e.g., Ping Hu, Dui Dui Hu, Mixed One Suit).
- **Game Tracking**: Records round details, including winners, win types (Self-Draw, Eat Player, Draw), and score changes, with an undo feature for correcting mistakes.
- **Player Management**: Supports adding and replacing players, assigning them to seats (East, South, West, North), and tracking scores.
- **Statistics**: Displays player statistics, such as self-draw wins, eat player wins, and deal-in losses.
- **Dealer and Round Management**: Tracks the dealer (莊家) and round seat, with support for consecutive wins (連莊) and seat rotation.
- **Dice Rolling**: Includes a virtual dice roller for determining game actions.
- **Cash Out Settlement**: Calculates final transactions for settling scores among players.
- **Share Results**: Captures and shares game results as screenshots with a text summary.
- **Persistent State**: Saves game state to a JSON file, allowing resumption across app sessions.

## Screenshots

Below are placeholders for screenshots of the app. Replace these with actual images from the app.
![螢幕擷取畫面 2025-04-20 141232](https://github.com/user-attachments/assets/96bbe513-a772-4d72-95bd-43f483667d37)

*Main screen showing player seats, scores, and game controls.*
![螢幕擷取畫面 2025-04-20 141409](https://github.com/user-attachments/assets/1ae201ea-9838-40ef-8c36-727e9fd42d27)

*Tile selection interface for entering a winning hand.*
![螢幕擷取畫面 2025-04-20 141300](https://github.com/user-attachments/assets/6493b303-34c5-4c86-ac4b-482a72a5cdb3)

*Record screen displaying game rounds and player statistics.*

## Installation

### Prerequisites

- Android Studio 4.0 or later
- Android SDK (API level 21 or higher recommended)
- Git
- A GitHub account (for cloning the repository)

### Steps

1. **Clone the Repository**:

   ```bash
   git clone https://github.com/ysulchaum/HongKongMJcal.git
   ```
2. **Open in Android Studio**:
   - Launch Android Studio.
   - Select `File > Open` and navigate to the cloned `HongKongMJcal` directory.
   - Allow Android Studio to sync the project with Gradle.
3. **Build the Project**:
   - Click `Build > Make Project` or press `Ctrl+F9` to build the app.
   - Resolve any missing dependencies by following Android Studio’s prompts.
4. **Run the App**:
   - Connect an Android device or start an emulator.
   - Click `Run > Run 'app'` or press `Shift+F10` to deploy the app.

### Dependencies

The project uses the following libraries (check `build.gradle` for exact versions):

- AndroidX libraries (e.g., `androidx.appcompat`, `androidx.recyclerview`)
- Google Material Components (`com.google.android.material`)
- Flexbox Layout (`com.google.android.flexbox`)
- JSON processing (`org.json`)

## Usage

1. **Start a New Game**:
   - Launch the app and add four players by selecting seats (East, South, West, North) and entering their names.
   - Set the base fan value (32, 64, or 128) for score calculations.
2. **Play a Round**:
   - Roll the dice to determine actions (optional).
   - When a player wins, select their seat, choose the win type (Self-Draw, Eat Player, or Wrong Draw), and input the fan value or enter the winning hand via the tile selection screen.
   - Confirm the round to update scores and record the result.
3. **Track Rounds**:
   - View round history and player statistics in the Record screen.
   - Use the undo feature to revert the last round if needed.
4. **Manage Game State**:
   - Change the dealer or reset consecutive wins via the Master Setting panel.
   - Save the game state automatically when pausing or closing the app.
5. **Settle Scores**:
   - At the end of the game, use the Cash Out feature to calculate who pays whom.
   - Share game results via screenshot and text summary.

For detailed instructions, refer to the in-app UI prompts or the Wiki (if available).

## Contributing

Contributions are welcome! To contribute:

1. Fork the repository.
2. Create a new branch for your feature or bug fix:

   ```bash
   git checkout -b feature/your-feature-name
   ```
3. Commit your changes:

   ```bash
   git commit -m "Add your feature description"
   ```
4. Push to your fork:

   ```bash
   git push origin feature/your-feature-name
   ```
5. Open a pull request on the `main` branch of this repository.

Please follow the Code of Conduct and ensure your code adheres to the project’s coding standards (e.g., Java conventions, Android best practices).

## License

This project is licensed under the MIT License. See the LICENSE file for details.

## Authors

- Yu Sui Chung (GitHub: ysulchaum)

---

**Contact**: For questions or feedback, open an issue on GitHub or contact the maintainers via GitHub.
